from django.conf.urls.defaults import *
from django.conf import settings


urlpatterns = patterns('backend.shareables.views',
    (r'^remove/$', 'remove_item'),
    (r'^share/$', 'share_item'),
)
