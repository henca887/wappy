from django.http import HttpResponse
from django.utils import simplejson
from backend.groups.models import Group, Membership

def create_group(request):
    kwargs = simplejson.loads(request.raw_post_data)
    gr_name = kwargs['group_name']
    is_pub = kwargs['is_public']
    reqs_allowed = kwargs['requests_allowed']
    
    try:
        gr = Group(name=gr_name, is_public=is_pub,
                   requests_allowed=reqs_allowed)
        gr.save()
    
        mship = Membership(user=request.user, group=gr, is_owner=True)
        mship.save()
        response_dict = {'error': None,
                     'result': 'Group created!'} 
    except:
        response_dict = {'error': 'Something went wrong when creating group!',
                     'result': None}
    return HttpResponse(simplejson.dumps(response_dict),
                    mimetype='application/javascript')
    
def add_member(request):
    kwargs = simplejson.loads(request.raw_post_data)
    gr_name = kwargs['group_name']
    user = kwargs['user_name']
