from time import time
from backend.mail.utils import fetch_local_message_body
from backend.mail.models import MailAccount, MailHeader, MailBody
from backend.mail.models import MailSender, MailRecipient


class PM:
    """Private message implementation of the incoming+outgoing interfaces."""

    def synchronize(self, account):
        pass # This operation is not nessessary for private messages.

    def fetch(self, account, folder, uid):
        try:
            body = fetch_local_message_body(account, folder, uid)
        except:
            body = 'Could not find the requested private message!'
        return body

    def send(self, account, recipient, subject, body):
        try:
            self._put_message_into_recipient_inbox(recipient, subject, body)
            self._put_message_into_sent(account, recipient, subject, body)
        except Exception as e:
            print e

    def _put_message_into_recipient_inbox(self, recipient, subject, text):
        account_name = '%s@wappy' % recipient
        account = MailAccount.objects.get(user__username=recipient,
                                          name=account_name)
        inbox = account.folders.get(path='Inbox')
        self._put_message_into_folder(inbox, recipient, subject, text)

    def _put_message_into_sent(self, account, recipient, subject, text):
        sent = account.folders.get(path='Sent')
        self._put_message_into_folder(sent, recipient, subject, text)

    def _put_message_into_folder(self, folder, recipient, subject, text):
        header = MailHeader()
        header.folder = folder
        header.uid = self._next_uid(folder)
        header.subject = subject
        header.flags = ''
        header.timestamp = time()
        header.save()
        body = MailBody()
        body.header = header
        body.text = text
        body.save()

    def _next_uid(self, folder):
        if folder.headers.count():
            return max(folder.headers.all(), key=lambda x: x.uid).uid + 1
        else:
            return 1
