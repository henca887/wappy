import imaplib
import email
import time
from backend.mail.models import MailFolder, MailHeader
from backend.mail.utils import fetch_local_message_body
from backend.mail.utils import store_local_message_body


class IMAP:
    """IMAP implementation of the incoming interface."""

    def synchronize(self, account):
        session = IMAPSession(account)
        session.login()
        session.synchronize_folders()
        session.synchronize_headers()
        session.logout()

    def fetch(self, account, folder, uid):
        try:
            body = fetch_local_message_body(account, folder, uid)
        except:
            session = IMAPSession(account)
            session.login()
            body = session.fetch_message(folder, uid)
            session.logout()
            store_local_message_body(account, folder, uid, body)
        return body


class IMAPSession:
    """Helper class that make it simple to work with imap for this app."""

    def __init__(self, account):
        self.account = account
        self.session = None

    def login(self):       
        self.session = imaplib.IMAP4_SSL(self.account.incoming.server_address,
                                         self.account.incoming.server_port)
        self.session.login(self.account.incoming.username,
                           self.account.incoming.password)

    def logout(self):
        self.session.logout()
        self.session = None

    def synchronize_folders(self):
        status, response = self.session.list()
        if status == 'OK':
            remote_paths = set()
            for folder in response:
                trash, parsed = parse_list('(' + folder + ')')
                if len(parsed) == 3 and '\\Noselect' not in parsed[0]:
                    remote_paths.add(parsed[2])
            local_paths = set(
                [folder.path for folder in self.account.folders.all()])
            added_paths = remote_paths - local_paths
            for path in added_paths:
                self.account.folders.create(path=path)
            removed_paths = local_paths - remote_paths
            for path in removed_paths:
                self.account.folders.filter(path=path).delete()

    def synchronize_headers(self):
        for folder in self.account.folders.all():
            self._synchronize_folder_headers(folder)

    def fetch_message(self, path, uid):
        self.session.select('"' + path + '"')
        status, data = self.session.uid('FETCH', uid, 'RFC822')
        message = email.message_from_string(data[0][1])
        message_text = ''
        for part in message.walk():
            if part.get_content_type() == 'text/html':
                message_text = part.get_payload(decode=True)
                break # use text/html when available
            elif part.get_content_type() == 'text/plain':
                message_text = part.get_payload(decode=True)
                # convert message to html.
                message_text = message_text.replace('\r\n', '<br>')
        self.session.close()
        message_text = message_text.decode('latin-1', 'ignore')
        return message_text if message_text else 'Unknown message type!'

    def _synchronize_folder_headers(self, folder):
        try:
            last_uid = folder.headers.order_by('uid').reverse()[:1][0].uid
            search_command = '(UID %d:*)' % (last_uid + 1, )
        except:
            last_uid = 0
            search_command = 'ALL'
        self.session.select('"' + folder.path + '"')
        status, uids = self.session.uid('SEARCH', None, search_command)
        if status == 'OK':
            for uid in uids[0].split():
                if int(uid) > last_uid:
                    status, data = self.session.uid('FETCH', uid, 'ALL')
                    header = parse_header(data)
                    if header:
                        header.folder = folder
                        header.uid = uid
                        header.save()
                    else:
                        print 'could not parse:', data
        self.session.close()


# @todo: parse sender and recipients fields.
def parse_header(data):
    """Parses imap headers fetched with the imap all macro."""
    parsed = create_dict(parse_list(data[0].partition(' ')[2])[1])
    envelope = parsed['ENVELOPE']
    if len(envelope) == 10:
        header = MailHeader()
        header.subject = envelope[1]
        header.flags = ' '.join(parsed['FLAGS'])
        header.timestamp = time.mktime(
            time.strptime(parsed['INTERNALDATE'], '%d-%b-%Y %H:%M:%S +0000'))
        return header
    return None

def create_dict(list_):
    """Create dictionary from list where keys and values are alternating."""
    if len(list_) % 2:
        raise Exception("Even number of list items expected.")
    result = {}
    for i in range(0, len(list_), 2):
        result[list_[i]] = list_[i + 1]
    return result

def parse_list(data, index=0):
    """Generic helper routine for parsing imap responses."""
    if index < len(data) and data[index] == '(':
        result = []
        index += 1
        while index < len(data):
            if data[index] == '(':
                index, lst = parse_list(data, index)
                result.append(lst)
            elif data[index] == '"':
                index, s = parse_string(data, index)
                result.append(s)
            elif data[index] == ' ':
                index += 1
            elif data[index] == ')':
                index += 1
                break
            else:
                index, literal = parse_literal(data, index)
                result.append(literal)
        return index, result
    else:
        return index, []

def parse_literal(data, index):
    """Generic helper routine for parsing imap responses."""
    literal = ''
    while index < len(data):
        if data[index] in (' ', ')'):
            break
        else:
            literal += data[index]
            index += 1
    if literal.lower() == "nil":
        return index, None
    else:
        return index, literal

def parse_string(data, index):
    """Generic helper routine for parsing imap responses."""
    if index < len(data) and data[index] == '"':
        s = ''
        index += 1
        while index < len(data):
            if data[index] == '"':
                index += 1
                break
            else:
                s += data[index]
                index += 1
        return index, s
    else:
        return index, ''
