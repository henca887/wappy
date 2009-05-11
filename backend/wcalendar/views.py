import datetime
from django.http import HttpResponse
from django.utils import simplejson
from backend.wcalendar.models import Calendar

# TODO: a user may have more than one calendar!
# Note: Had to calculate weekNr here ant send it to client
def add_appointment(request):
    if request.user.is_authenticated():
        kwargs = simplejson.loads(request.raw_post_data)
        subject = kwargs['subject']
        description = kwargs['description']
        start_timestamp = kwargs['startTimeStamp']
        end_timestamp = kwargs['endTimeStamp']

#        try:
#            cal = Calendar.objects.get(user=request.user)
#        except(cal.DoesNotExist):
#            cal = Calendar(user=request.user)
#            cal.save()
        calendars = request.user.calendars.filter()
        if calendars.count() == 0:
            cal = Calendar(user=request.user)
            cal.save()
        else:
            cal = request.user.calendars.get(id=1)
        
        cal.appointments.create(subject=subject, description=description,
                                start_timestamp=start_timestamp,
                                end_timestamp=end_timestamp)
        cal.save()
        d = datetime.date.fromtimestamp(start_timestamp/1000)
        week_nr = d.isocalendar()[1]
        response_dict = {'error': None,
                         'result': 'Appointment added!',
                         'weekNr': week_nr}
    else:
        response_dict = {'error': None,
                         'result': 'you are not logged in!'}

    return HttpResponse(simplejson.dumps(response_dict),
                        mimetype='application/javascript')
    
def get_calendar(request):
    if request.user.is_authenticated():
        calendars = request.user.calendars.filter()
        if calendars.count() == 0:
            response_dict = {'error': 'No calendars found!',
                             'result': None}
        else:
            result = []
            cal = request.user.calendars.get(id=1)
            appointments = cal.appointments.all()
            
            for app in appointments.order_by('start_timestamp'):
                d = datetime.date.fromtimestamp(app.start_timestamp/1000)
                week_nr = d.isocalendar()[1]
                item = {'subject': app.subject,
                        'description': app.description,
                        'startTimeStamp': app.start_timestamp,
                        'endTimeStamp': app.end_timestamp,
                        'weekNr': week_nr}
                result.append(item)
            
                response_dict = {'error': None, 'result': result}
        
        return HttpResponse(simplejson.dumps(response_dict),
                        mimetype='application/javascript')
            
    