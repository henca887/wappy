from django.http import HttpResponse
from django.utils.simplejson import loads, dumps
from django.contrib.auth.models import User
from backend.utils.decorators import login_required_json
from backend.shareables.models import ItemSharedWithUser


@login_required_json
def remove_item(request):
    item_id = loads(request.raw_post_data)
    try:
        item = request.user.my_shareable_items.get(id=item_id)
        item.delete()
        result = {'error': None}
    except:
        result = {'error': 'You are not allowed to remove this item!'}
    return HttpResponse(dumps(result), mimetype='application/javascript')

@login_required_json
def share_item(request):
    kwargs = loads(request.raw_post_data)
    try:
        item = request.user.my_shareable_items.get(id=int(kwargs['item_id']))
        user_to_share_with = User.objects.get(username=kwargs['username'])
        share = ItemSharedWithUser()
        share.item = item
        share.user = user_to_share_with
        share.save()
        result = {'error': None}
    except Exception as e:
        print e
        result = {'error': 'You are not allowed to share this item!'}
    return HttpResponse(dumps(result), mimetype='application/javascript')
