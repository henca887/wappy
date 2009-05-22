import time
from django.http import HttpResponse
from django.utils import simplejson
from django.contrib.auth.models import User
from backend.groups.models import Group, Membership
#from groups.models import Group, Membership

def return_json_http(dict):
    return HttpResponse(simplejson.dumps(dict),
                        mimetype='application/javascript') 

def create_group(request):
    kwargs = simplejson.loads(request.raw_post_data)
    gr_name = kwargs['name']
    is_pub = kwargs['is_public']
    reqs_allowed = kwargs['requests_allowed']
    
    existing_group = Group.objects.filter(name=gr_name)
    if existing_group.count() == 0:
        try:
            gr = Group(name=gr_name, is_public=is_pub,
                       requests_allowed=reqs_allowed)
            gr.save()
            mship = Membership(user=request.user, group=gr,
                               is_owner=True, is_admin=True)
            mship.save()
            response_dict = {'error': None,
                             'result': 'Group created!'}
        except:
            response_dict = {'error': 'Something went wrong when creating group!',
                             'result': None}
    else:
        response_dict = {'error': 'Group name already exists!',
                         'result': None}
    return return_json_http(response_dict)

def get_group_members(gr, requester):
    members = gr.members.all().exclude(username=requester)
    users = []
    for member in members:
        mship = Membership.objects.get(group=gr, user=member)
        timestamp = time.mktime(mship.join_date.timetuple())
        timestamp = long(timestamp)
        usr = {'name': member.username,
               'email': member.email,
               'is_admin': mship.is_admin,
               'is_owner': mship.is_owner,
               'join_date': timestamp}
        users.append(usr)
    return users

def get_groups(request):
    groups = request.user.wappy_groups.filter()
    if groups.count() == 0:
        response_dict = {'error': 'No groups found!',
                         'result': None}
    else:
        result = []
        for gr in groups:
            group = {'name': gr.name,
                     'is_public': gr.is_public,
                     'requests_allowed': gr.requests_allowed}
            members = get_group_members(gr, request.user.username)
            object = {'group': group,
                      'members': members}
            
            result.append(object)
        response_dict = {'error': None,
                         'result': result}
    return return_json_http(response_dict)

def add_member(request):
    kwargs = simplejson.loads(request.raw_post_data)
    gr_name = kwargs['group_name']
    user_name = kwargs['user_name']
    try:
        usr = User.objects.get(username=user_name)
    except:
        response_dict = {'error': 'User does not exist!',
                         'result': None}
        return return_json_http(response_dict)
    try:
        gr = request.user.wappy_groups.get(name=gr_name)
        mship = Membership(user=usr, group=gr)
        mship.save()
        response_dict = {'error': None,
                         'result': 'Member added!'}
    except:
        response_dict = {'error': 'Something went wrong when adding member!',
                         'result': None}
    return return_json_http(response_dict)   