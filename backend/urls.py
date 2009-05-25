from django.conf.urls.defaults import *
from django.conf import settings


urlpatterns = patterns('',
    (r'^frontend/(?P<path>.*)$', 'django.views.static.serve',
        {'document_root': settings.MEDIA_ROOT}),
    (r'^accounts/login/$', 'django.contrib.auth.views.login',
        {'template_name': 'accounts/login.html'}),
    (r'^accounts/logout/$', 'backend.accounts.views.logout'),
    (r'^accounts/register/$', 'backend.accounts.views.register'),
    (r'^$', 'backend.welcome.views.index'),
    (r'^groups/', include('backend.groups.urls')),
    (r'^mail/', include('backend.mail.urls')),
    (r'^wcalendar/', include('backend.wcalendar.urls')),
    (r'^shareables/', include('backend.shareables.urls')),
    (r'^bookmarks/', include('backend.bookmarks.urls')),
    (r'^welcome/', include('backend.welcome.urls')),
)
