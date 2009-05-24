from django.http import HttpResponse
from django.utils.simplejson import loads, dumps
from backend.utils.decorators import login_required_json
from backend.shareables.shortcuts import leaf_class_filter
from backend.bookmarks.models import Bookmark


@login_required_json
def bookmarks_tree(request):
    result = []

    my_bookmarks = [bookmark.as_tree_node_dict()
                    for bookmark in leaf_class_filter(
                        request.user.my_shareable_items.all(), Bookmark)]
    result.append({'name': 'My Bookmarks', 'leaf': 0, 'childs': my_bookmarks})

    shared_bookmarks = [bookmark.as_tree_node_dict()
                    for bookmark in leaf_class_filter(
                        [shared.item
                         for shared in request.user.others_shared_items.all()],
                        Bookmark)]
    result.append(
        {'name': 'Shared Bookmarks', 'leaf': 0, 'childs': shared_bookmarks})
    
    return HttpResponse(dumps(result), mimetype='application/javascript')

@login_required_json
def add_bookmark(request):
    kwargs = loads(request.raw_post_data)
    bookmark = Bookmark()
    bookmark.owner = request.user
    bookmark.name = kwargs['name']
    bookmark.url = kwargs['url']
    bookmark.save()
    result = bookmark.as_tree_node_dict()
    return HttpResponse(dumps(result), mimetype='application/javascript')
