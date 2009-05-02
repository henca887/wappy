import imaplib


class IMAPSynchronizer:
    """Synchronize local mail account with imap server."""

    def __init__(self, account):
        self.account = account
        self.session = None

    def login(self):
        self.session = imaplib.IMAP4_SSL(self.account.server_address,
                                         self.account.server_port)
        self.session.login(self.account.username, self.account.password)

    def logout(self):
        self.session.logout()
        self.session = None

    def synchronize_folders(self):
        response = self.session.list()
        # @todo: check status code, parse response, create folders.


def parse_list(data, index):
    """Helper routine for parsing imap responses."""
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
    """Helper routine for parsing imap responses."""
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
    """Helper routine for parsing imap responses."""
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
