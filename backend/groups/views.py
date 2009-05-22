import time
from django.http import HttpResponse
from django.utils import simplejson
from django.contrib.auth.models import User
from backend.utils.decorators import login_required_json
from backend.groups.models import Group, Membership

def json_http_response(dict):
    return HttpResponse(simplejson.dumps(dict),
                        mimetype='application/javascript')

def unkown_error_dict():
    return {'error': 'Some unkown error occured at the server.',
            'result': None}
    
def get_timestamp(timetuple):
    timestamp = time.mktime(timetuple)
    return long(timestamp)

@login_required_json
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
            response_dict = unkown_error_dict()
    else:
        response_dict = {'error': 'Group name already exists!',
                         'result': None}
    return json_http_response(response_dict)



def get_group_members(gr, requester):
    members = gr.members.all().exclude(username=requester)
    if members.count() == 0:
        return None
    users = []
    for member in members:
        mship = Membership.objects.get(group=gr, user=member)
        timestamp = get_timestamp(mship.join_date.timetuple())
        usr = {'name': member.username,
               'email': member.email,
               'is_admin': mship.is_admin,
               'is_owner': mship.is_owner,
               'join_date': timestamp}
        users.append(usr)
    return users

@login_required_json
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
    return json_http_response(response_dict)

@login_required_json
def rem_group(request):
    kwargs = simplejson.loads(request.raw_post_data)
    gr_name = kwargs['name']
    
    try:
        gr = Group.objects.get(name=gr_name)
        if request.user.memberships.get(group=gr).is_owner:
            gr.delete()
            response_dict = {'error': None,
                             'result': 'Group removed!'}
        else:
            response_dict = {'error': 'Only the owner can remove a group!',
                             'result': None}
    except:
        response_dict = unkown_error_dict()
    return json_http_response(response_dict)
    
@login_required_json
def add_member(request):
    kwargs = simplejson.loads(request.raw_post_data)
    gr_name = kwargs['group_name']
    user_name = kwargs['user_name']
    try:
        usr = User.objects.get(username=user_name)
    except:
        response_dict = {'error': 'User does not exist!',
                         'result': None}
        return json_http_response(response_dict)
    try:
        gr = request.user.wappy_groups.get(name=gr_name)
        if Membership.objects.filter(user=usr, group=gr).count() > 0:
            response_dict = {'error': 'User is already a member!',
                             'result': None}
            return json_http_response(response_dict)
        mship = Membership(user=usr, group=gr)
        mship.save()
        timestamp = get_timestamp(mship.join_date.timetuple())
        member = {'name': user_name,
                  'email': usr.email,
                  'is_admin': mship.is_admin,
                  'is_owner': mship.is_owner,
                  'join_date': timestamp}
        response_dict = {'error': None,
                         'result': member}
    except:
        response_dict = unkown_error_dict()
    return json_http_response(response_dict)

@login_required_json
def rem_member(request):
    kwargs = simplejson.loads(request.raw_post_data)
    gr_name = kwargs['group_name']
    user_name = kwargs['user_name']

    try:
        gr = Group.objects.get(name=gr_name)
        if request.user.memberships.get(group=gr).is_admin:
            mmbr = User.objects.get(username=user_name)
            mship = Membership.objects.get(user=mmbr, group=gr)
            mship.delete()
            response_dict = {'error': None,
                             'result': 'Member removed!'}
        else:
            response_dict = {'error': 'Only an admin can remove members!',
                             'result': None}
    except:
        response_dict = unkown_error_dict()
    return json_http_response(response_dict)
