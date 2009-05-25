from django.shortcuts import render_to_response
from django.contrib import auth
from django.contrib.auth.forms import UserCreationForm
from django.http import HttpResponse, HttpResponseRedirect
from django.conf import settings
from django.utils import simplejson


def index(request):
    if request.user.is_authenticated():
        return HttpResponseRedirect(settings.LOGIN_REDIRECT_URL)
    else:
        return HttpResponseRedirect(settings.LOGIN_URL)

def username(request):
    return HttpResponse(simplejson.dumps(request.user.username),
                        mimetype='application/javascript')
