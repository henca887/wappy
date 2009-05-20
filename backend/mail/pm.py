from backend.mail.utils import fetch_local_message_body


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
        pass # @todo: implement.
