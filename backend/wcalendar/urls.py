from django.conf.urls.defaults import *
from django.conf import settings


urlpatterns = patterns('backend.wcalendar.views',
    (r'^get_cal/$', 'get_cal'),
    (r'^add_app/$', 'add_app'),
    (r'^rem_app/$', 'rem_app'),
)
