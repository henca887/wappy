from django.conf.urls.defaults import *
from django.conf import settings


urlpatterns = patterns('backend.groups.views',
    (r'^create_group/$', 'create_group'),
    (r'^get_groups/$', 'get_groups'),
    (r'^add_member/$', 'add_member'),
)