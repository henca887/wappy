from django.conf.urls.defaults import *
from django.conf import settings


urlpatterns = patterns('backend.welcome.views',
    (r'^username/$', 'username'),
)
