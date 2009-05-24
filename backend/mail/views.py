from django.http import HttpResponse
from django.utils import simplejson
from backend.utils.decorators import login_required_json
from backend.mail.utils import build_tree_from_paths, html_message_filter
from backend.mail.utils import ensure_private_message_account_exists
from backend.mail.utils import create_mail_account
from backend.mail.imap import IMAP, IMAPSession
from backend.mail.pm import PM
from backend.mail.smtp import SMTP


PROTOCOL_HANDLERS = {
    'imap': IMAP(),
    'pm': PM(),
    'smtp': SMTP()
}


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
        account = create_mail_account(request.user, kwargs)
        try:
            synchronizer = IMAPSession(account)
            synchronizer.login()
            synchronizer.synchronize_folders()
            synchronizer.logout()
        except Exception as e:
            account.incoming.delete()
            account.outgoing.delete()
            account.delete()
            return HttpResponse(
                simplejson.dumps({'error': 'Login failed!', 'result': None}),
                mimetype='application/javascript')
        return HttpResponse(
            simplejson.dumps({'error': None, 'result': 'Account created!'}),
            mimetype='application/javascript')

@login_required_json
def accounts_list(request):
    result = [account.name for account in request.user.mail_accounts.all()]
    return HttpResponse(simplejson.dumps({'error': None, 'result': result}),
                        mimetype='application/javascript')

@login_required_json
def synchronize(request):
    """Synchronizes all accounts fully."""
    error = None
    result = 'ok'
    for account in request.user.mail_accounts.all():
        try:
            ph = PROTOCOL_HANDLERS[account.incoming.protocol]
            ph.synchronize(account)
        except:
            error = '' if error is None else error
            error += 'Could not access: ' + account.name + '\n'
            result = None
    return HttpResponse(simplejson.dumps({'error': error, 'result': result}),
                        mimetype='application/javascript')

@login_required_json
def folders(request):
    """Fetch a tree with all mail folders that belong to the current user."""
    ensure_private_message_account_exists(request.user)
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
    """Fetch message headers."""
    # Validate request parameters.
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

    # Determine query result order.
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
        ph = PROTOCOL_HANDLERS[account.incoming.protocol]
        body = ph.fetch(account, folder_path, uid)
    except Exception as e:
        print e
        body = "failed to fetch message"
    result = {'content': html_message_filter(body)}    
    return HttpResponse(simplejson.dumps(result),
                        mimetype='application/javascript')

@login_required_json 
def send(request):
    """Send message."""
    kwargs = simplejson.loads(request.raw_post_data)
    try:
        account_name = kwargs['from']
        recipient = kwargs['to']
        subject = kwargs['subject']
        body = kwargs['body']
        account = request.user.mail_accounts.get(name=account_name)
        ph = PROTOCOL_HANDLERS[account.outgoing.protocol]
        ph.send(account, recipient, subject, body)
    except:
        return HttpResponse(simplejson.dumps({'error': 'Internal Error'}),
                            mimetype='application/javascript')
    return HttpResponse(simplejson.dumps({'error': None}),
                        mimetype='application/javascript')
