from django.http import HttpResponse, HttpResponseForbidden
from django.utils import simplejson
from backend.mail.models import MailAccount
from backend.mail.utils import build_tree_from_paths


# @todo: move to utility module!
def login_required_json(myfunc):
    """Deny none authenticated requests in a json compatible way."""
    def authentication(*args, **kwargs):
        if args[0].user.is_authenticated():
            return myfunc(*args, **kwargs)
        else:
            return HttpResponseForbidden()
    return authentication

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
