from django.conf.urls.defaults import *
from django.conf import settings


urlpatterns = patterns('backend.wcalendar.views',
    (r'^add_appointment/$', 'add_appointment'),
    (r'^get_calendar/$', 'get_calendar'),
)
