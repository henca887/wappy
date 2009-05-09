from email.Header import decode_header
from django.http import HttpResponse, HttpResponseForbidden
from django.utils import simplejson
from backend.mail.models import MailAccount
from backend.mail.utils import build_tree_from_paths
from backend.mail.imap import IMAPSynchronizer

# @todo: move to utility module!
def login_required_json(inner):
    """Deny none authenticated requests in a json compatible way."""
    def authentication(*args, **kwargs):
        if args[0].user.is_authenticated():
            return inner(*args, **kwargs)
        else:
            return HttpResponseForbidden()
    return authentication

@login_required_json
def accounts_create(request):
    """Create an account and fetch folders from server."""
    kwargs = simplejson.loads(request.raw_post_data)
    if MailAccount.objects.filter(user=request.user,
                                  name=kwargs['name']).count():
        return HttpResponse(
            simplejson.dumps({'error': 'Account name must be unique!',
                              'result': None}),
            mimetype='application/javascript')
    else:
        account = MailAccount()
        account.user = request.user
        account.name = kwargs['name']
        account.server_address = kwargs['server_address']
        account.server_port = int(kwargs['server_port'])
        account.username = kwargs['username']
        account.password = kwargs['password']
        account.save()
        try:
            synchronizer = IMAPSynchronizer(account)
            synchronizer.login()
            synchronizer.synchronize_folders()
            synchronizer.logout()
        except:
            account.delete()
            return HttpResponse(
                simplejson.dumps({'error': 'Login failed!',
                                  'result': None}),
                mimetype='application/javascript')
        return HttpResponse(
            simplejson.dumps({'error': None,
                              'result': 'Account created!'}),
            mimetype='application/javascript')

@login_required_json
def synchronize(request):
    """ Synchronizes all accounts fully. """
    accounts = MailAccount.objects.filter(user=request.user)
    error = None
    result = 'ok'
    for account in accounts:
        try:
            synchronizer = IMAPSynchronizer(account)
            synchronizer.login()
            synchronizer.synchronize_folders()
            synchronizer.synchronize_headers()
            synchronizer.logout()
        except:
            error = '' if error is None else error
            error += 'Could not access: ' + account.name + '\n'
            result = None
    return HttpResponse(simplejson.dumps({'error': error, 'result': result}),
                        mimetype='application/javascript')

@login_required_json
def folders(request):
    """Fetch a tree with all mail folders that belong to the current user."""
    tree = []
    accounts = MailAccount.objects.filter(user=request.user)
    for account in accounts:
        paths = [folder.path for folder in account.folders.all()]
        node = {'name': account.name,
                'childs': build_tree_from_paths(paths)}
        tree.append(node)
    return HttpResponse(simplejson.dumps({'error': None, 'result': tree}),
                        mimetype='application/javascript')

@login_required_json
def messages(request):
    """Fetch message headers"""    
    try:
        path = request.GET.get('path', None)
        callback = request.GET['callback']
        offset = int(request.GET['offset'])
        limit = int(request.GET['limit'])
        sort_dir = request.GET['sortDir']
    
        account_name, sep, folder_path = path.partition('/')
        account = MailAccount.objects.get(user=request.user, name=account_name)
        folder = account.folders.get(path=folder_path)
    except:
        result = {'results': [], 'total': '0'}
        return HttpResponse(
        '%s(%s);' % (callback, simplejson.dumps(result)),
        mimetype='application/javascript')

    messages = []
    headers = [h for h in folder.headers.all().order_by('timestamp').reverse()]
    
    for i in range(offset, min(offset+limit, len(headers))):
        header = headers[i]
        messages.append({
            'uid': '%d' % header.uid,
            'subject': header.subject,
            'sender': 'unknown',
            'date': '%d' % header.timestamp
        })
        header.subject

    result = {'results': messages, 'total': '%d' % len(headers)}    
    return HttpResponse(
        '%s(%s);' % (callback, simplejson.dumps(result)),
        mimetype='application/javascript')

@login_required_json 
def messages_content(request):
    """ @todo """
    kwargs = simplejson.loads(request.raw_post_data)
    try:
        path = kwargs['path']
        uid = int(kwargs['uid'])
        account_name, sep, folder_path = path.partition('/')
        account = request.user.mail_accounts.get(name=account_name)
        synchronizer = IMAPSynchronizer(account)
        synchronizer.login()
        message_text = synchronizer.fetch_message(folder_path, uid)
        synchronizer.logout()
    except Exception as e:
        print e
        message_text = "failed to fetch message"
    result = {'content': message_text}    
    return HttpResponse(simplejson.dumps(result),
                        mimetype='application/javascript')
