from django.db import models
from django.contrib.auth.models import User


class MailTransportDetails(models.Model):
    protocol = models.CharField(max_length=64)
    server_address = models.CharField(max_length=128)
    server_port = models.IntegerField(null=True)
    username = models.CharField(max_length=64)
    password = models.CharField(max_length=64)


class MailAccount(models.Model):
    user = models.ForeignKey(User, related_name='mail_accounts')
    name = models.CharField(max_length=64)
    # server_address = models.CharField(max_length=128)
    # server_port = models.IntegerField(null=True)
    # username = models.CharField(max_length=64)
    # password = models.CharField(max_length=64)
    incoming = models.ForeignKey(MailTransportDetails, related_name='iaccount')
    outgoing = models.ForeignKey(MailTransportDetails, related_name='oaccount')


class MailFolder(models.Model):
    account = models.ForeignKey(MailAccount, related_name='folders')
    path = models.CharField(max_length=128)


class MailHeader(models.Model):
    folder = models.ForeignKey(MailFolder, related_name='headers')
    uid = models.IntegerField()
    subject = models.CharField(max_length=128)
    flags = models.CharField(max_length=128)
    timestamp = models.IntegerField()


class MailSender(models.Model):
    header = models.ForeignKey(MailHeader, related_name='senders')
    name = models.CharField(max_length=64)
    email = models.CharField(max_length=128)


class MailRecipient(models.Model):
    header = models.ForeignKey(MailHeader, related_name='recipients')
    name = models.CharField(max_length=64)
    email = models.CharField(max_length=128)


class MailBody(models.Model):
    header = models.OneToOneField(MailHeader, related_name='body')
    text = models.TextField()
