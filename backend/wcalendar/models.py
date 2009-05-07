import datetime
from django.db import models

class Calendar(models.Model):
    user = models.CharField(max_length=20, default='DEBUG')
    type = models.CharField(max_length=20, default='Standard')
    def __unicode__(self):
        return self.user

class Appointment(models.Model):
    calendar = models.ForeignKey(Calendar)
    subject = models.CharField(max_length=30)
    description = models.TextField(blank=True)
    year = models.CharField(max_length=4)
    month = models.CharField(max_length=2)
    day = models.CharField(max_length=2)
    week_day = models.CharField(max_length=10)
    start_hour = models.CharField(max_length=2)
    start_min = models.CharField(max_length=2)
    end_hour = models.CharField(max_length=2)
    end_min = models.CharField(max_length=2)
##    property1 = models.BooleanField(default=True)
##    property2 = models.BooleanField(default=False)
##    property3 = models.BooleanField(default=False)
    
##    date = models.DateTimeField(default=datetime.date.today())
##    date = models.CharField(max_length=10) #Kanske datetime
##    start_time = models.CharField(max_length=10)
##    end_time = models.CharField(max_length=10)
##    property1 = models.BooleanField(default=True)
##    property2 = models.BooleanField(default=False)
##    property3 = models.BooleanField(default=False)
    def __unicode__(self):
        return self.subject
    