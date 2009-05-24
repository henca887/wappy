from django.conf.urls.defaults import *
from django.conf import settings


urlpatterns = patterns('backend.bookmarks.views',
    (r'^tree/$', 'bookmarks_tree'),
    (r'^add/$', 'add_bookmark'),
)
