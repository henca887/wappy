from django.conf.urls.defaults import *
from django.conf import settings


urlpatterns = patterns('backend.mail.views',
    (r'^accounts/create/$', 'accounts_create'),
    (r'^synchronize/$', 'synchronize'),
    (r'^folders/$', 'folders'),
    (r'^messages/$', 'messages'),
    (r'^messages/content/$', 'messages_content'),
)
