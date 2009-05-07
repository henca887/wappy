from django.http import HttpResponse
from django.utils import simplejson
from backend.wcalendar.models import Calendar

def add_appointment(request):
    if request.user.is_authenticated():
        #subject = request.POST.get('subject', False)
        kwargs = simplejson.loads(request.raw_post_data)
        subject = kwargs['subject']
        description = kwargs['description']
        year = kwargs['year']
        month = kwargs['month']
        day = kwargs['day']
        week_day = kwargs['week_day']
        start_hour = kwargs['start_hour']
        start_min = kwargs['start_min']
        end_hour = kwargs['end_hour']
        end_min = kwargs['end_min']
##        property1 = kwargs['property1']
##        property2 = kwargs['property2']
##        property3 = kwargs['property3']
        try:
            cal = Calendar.objects.get(user=request.user.username)
        except(cal.DoesNotExist):
            cal = Calendar(user=request.user.username)
            cal.save()
        cal.appointment_set.create(subject=subject, description=description, \
                                   year=year, month=month, day=day, \
                                   week_day=week_day, start_hour=start_hour, \
                                   start_min=start_min, end_hour=end_hour, \
                                   end_min=end_min)
        cal.save()

##        date = request.POST['date']
##        start_time = request.POST['start_time']
##        end_time = request.POST['end_time']
##        property1 = request.POST['property1']
##        property2 = request.POST['property2']
##        property3 = request.POST['property3']

        #app = Appointment(subject=subject, description=description, day=day)      
        response_dict = {"error": None,
                         "result": "Appointment added!"}
    else:
        response_dict = {"error": None,
                         "result": "you are not logged in!"}

    return HttpResponse(simplejson.dumps(response_dict),
                        mimetype='application/javascript')