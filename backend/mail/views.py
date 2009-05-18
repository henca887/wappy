from email.Header import decode_header
from django.http import HttpResponse, HttpResponseForbidden
from django.utils import simplejson
from backend.mail.models import MailAccount, MailBody
from backend.mail.utils import build_tree_from_paths, html_message_filter
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
    if request.user.mail_accounts.filter(name=kwargs['name']).count():
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
    error = None
    result = 'ok'
    for account in request.user.mail_accounts.all():
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
    for account in request.user.mail_accounts.all():
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
        pattern = request.GET.get('pattern', '')
        callback = request.GET['callback']
        offset = int(request.GET['offset'])
        limit = int(request.GET['limit'])
        sort_dir = request.GET.get('sortDir', 'NONE')
        sort_dir = 'DESC' if sort_dir == 'NONE' else sort_dir
        sort_field = request.GET.get('sortField', 'timestamp')
        sort_field = 'timestamp' if sort_field == 'date' else sort_field

        account_name, sep, folder_path = request.GET['path'].partition('/')
        account = request.user.mail_accounts.get(name=account_name)
        folder = account.folders.get(path=folder_path)
    except:
        result = {'results': [], 'total': '0'}
        return HttpResponse(
            '%s(%s);' % (callback, simplejson.dumps(result)),
            mimetype='application/javascript')

    # Construct query that find messages that match the request.
    query = folder.headers.filter(subject__contains=pattern)

    if sort_field in ('subject', 'timestamp'):
        query = query.order_by(sort_field)
        if sort_dir == 'DESC':
            query = query.reverse()

    # Create response.
    messages = [{'uid': '%d' % header.uid,
                 'subject': header.subject,
                 'sender': 'unknown',
                 'date': '%d' % header.timestamp}
                for header in query[offset:offset+limit]]

    result = {'results': messages, 'total': '%d' % query.count()}    
    return HttpResponse(
        '%s(%s);' % (callback, simplejson.dumps(result)),
        mimetype='application/javascript')

@login_required_json 
def messages_content(request):
    """Load message content."""
    kwargs = simplejson.loads(request.raw_post_data)
    try:
        path = kwargs['path']
        uid = int(kwargs['uid'])
        account_name, sep, folder_path = path.partition('/')
        account = request.user.mail_accounts.get(name=account_name)
        try:
            body = _fetch_local_message_body(account, folder_path, uid)
        except:
            body = _fetch_imap_message_body(account, folder_path, uid)
            _store_local_message_body(account, folder_path, uid, body)
    except Exception as e:
        body = "failed to fetch message"
    result = {'content': html_message_filter(body)}    
    return HttpResponse(simplejson.dumps(result),
                        mimetype='application/javascript')

def _fetch_local_message_body(account, folder_path, uid):
    """Try to fetch the requested message from local database."""
    folder = account.folders.get(path=folder_path)
    header = folder.headers.get(uid=uid)
    return header.body.text

def _fetch_imap_message_body(account, folder_path, uid):
    """Helper routine that fetch the message body of a given message."""
    synchronizer = IMAPSynchronizer(account)
    synchronizer.login()
    body = synchronizer.fetch_message(folder_path, uid)
    synchronizer.logout()
    return body

def _store_local_message_body(account, folder_path, uid, text):
    """Try to fetch the requested message from local database."""
    folder = account.folders.get(path=folder_path)
    header = folder.headers.get(uid=uid)
    body = MailBody()
    body.header = header
    body.text = text
    body.save()
