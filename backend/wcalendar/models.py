from django.db import models
from django.contrib.auth.models import User

class Calendar(models.Model):
    user = models.ForeignKey(User, related_name='calendars')
    type = models.CharField(max_length=20, default='Main')
    def __unicode__(self):
        return 'User: %s, Type: %s' % (self.user.username, self.type)

class Appointment(models.Model):
    calendar = models.ForeignKey(Calendar, related_name='appointments')
    subject = models.CharField(max_length=30)
    description = models.TextField(max_length=160, blank=True)
    start_timestamp = models.IntegerField()
    end_timestamp = models.IntegerField()
    def __unicode__(self):
        return '%d' % self.start_timestamp
    