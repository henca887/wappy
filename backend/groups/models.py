import datetime
from django.db import models
from django.contrib.auth.models import User

class Group(models.Model):
    name = models.CharField(max_length=64, unique=True)
    # User already has a 'groups' field -> related_name='wappy_groups'
    members = models.ManyToManyField(User, through='Membership', related_name='wappy_groups')
    creation_date = models.DateField(default=datetime.date.today())
    is_public = models.BooleanField(default=True)
    requests_allowed = models.BooleanField(default=True)
    
    def __unicode__(self):
        return self.name
    
class Membership(models.Model):
    user = models.ForeignKey(User, related_name='memberships')
    group = models.ForeignKey(Group, related_name='memberships')
    join_date = models.DateField(default=datetime.date.today())
    # The owner of the group has full rights
    is_owner = models.BooleanField(default=False)
    is_admin = models.BooleanField(default=False)
    
    def __unicode__(self):
        return 'User: %s, Group: %s, Joined: %s' % \
            (self.user.username, self.group.name, self.join_date) 
    
    
class Membership_Request(models.Model):
    user = models.ForeignKey(User, related_name='membership_requests')
    group = models.ForeignKey(Group, related_name='membership_requests')
    request_date = models.DateField(default=datetime.date.today())
    
    def __unicode__(self):
        return 'User: %s, Group: %s, Request date: %s' % \
            (self.user.username, self.group.name, self.request_date)