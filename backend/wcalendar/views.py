import datetime
from django.http import HttpResponse
from django.utils import simplejson
from django.contrib.auth.decorators import login_required
from backend.wcalendar.models import Calendar

# TODO: a user may have more than one calendar!
# Note: Had to calculate weekNr here ant send it to client

@login_required    
def get_cal(request):
    calendars = request.user.calendars.filter()
    if calendars.count() == 0:
        response_dict = {'error': 'No calendars found!',
                         'result': None}
    else:
        result = []
        cal = request.user.calendars.filter()[0]
        appointments = cal.appointments.all()
        
        for app in appointments.order_by('start_timestamp'):
            d = datetime.date.fromtimestamp(app.start_timestamp/1000)
            week_nr = d.isocalendar()[1]
            item = {'subject': app.subject,
                    'description': app.description,
                    'location': app.location,
                    'startTimeStamp': app.start_timestamp,
                    'endTimeStamp': app.end_timestamp,
                    'weekNr': week_nr}
            result.append(item)
        
            response_dict = {'error': None, 'result': result}
    
    return HttpResponse(simplejson.dumps(response_dict),
                    mimetype='application/javascript')

@login_required
def add_app(request):
    kwargs = simplejson.loads(request.raw_post_data)
    subj = kwargs['subject']
    descr = kwargs['description']
    loc = kwargs['location']
    start = kwargs['startTimeStamp']
    end = kwargs['endTimeStamp']

#    try:
#        cal = Calendar.objects.get(user=request.user)
#    except(cal.DoesNotExist):
#        cal = Calendar(user=request.user)
#        cal.save()
    calendars = request.user.calendars.filter()
    if calendars.count() == 0:
        cal = Calendar(user=request.user)
        cal.save()
    else:
        cal = request.user.calendars.filter()[0]
    
    cal.appointments.create(subject=subj, description=descr, location=loc,
                            start_timestamp=start, end_timestamp=end)
    cal.save()
    d = datetime.date.fromtimestamp(start/1000)
    week_nr = d.isocalendar()[1]
    response_dict = {'error': None,
                     'result': 'Appointment added!',
                     'weekNr': week_nr}

    return HttpResponse(simplejson.dumps(response_dict),
                    mimetype='application/javascript')

# TODO: do proper checks
def rem_app(request):
    kwargs = simplejson.loads(request.raw_post_data)
    subj = kwargs['subject']
    start = kwargs['startTimeStamp']
    end = kwargs['endTimeStamp']
    
    try:
        cal = request.user.calendars.filter()[0]
        cal.appointments.filter(subject=subj, start_timestamp=start,
                             end_timestamp=end)[0].delete()
        response_dict = {'error': None,
                     'result': 'Appointment deleted!'} 
    except:
        response_dict = {'error': 'Something went wrong when deleting!',
                     'result': None} 

    return HttpResponse(simplejson.dumps(response_dict),
                    mimetype='application/javascript')                        
     
                        