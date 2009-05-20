from django.http import HttpResponseForbidden

def login_required_json(inner):
    """Deny none authenticated requests in a json compatible way."""
    def authentication(*args, **kwargs):
        if args[0].user.is_authenticated():
            return inner(*args, **kwargs)
        else:
            return HttpResponseForbidden()
    return authentication
