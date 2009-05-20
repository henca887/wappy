import re
# from backend.mail.imap import IMAPSession
from backend.mail.models import MailAccount, MailFolder, MailBody
from backend.mail.models import MailTransportDetails


def build_tree_from_paths(paths, sep='/'):
    root = []
    for path in paths:
        insert_path_into_tree(root, path.split(sep))
    return root

def insert_path_into_tree(tree, path):
    name = path.pop(0)
    for child in tree:
        if child['name'] == name:
            if path:
                insert_path_into_tree(child['childs'], path)
            break
    else:
        child = {'name': name, 'childs': []}
        tree.append(child)
        if path:
            insert_path_into_tree(child['childs'], path)

def match_at_least_one_pattern(text, patterns):
    for pattern in patterns:
        if re.search(pattern, text) is not None:
            return True
    return False

def html_message_filter(html):
    """Transform html messages so that they are safe to view in browser."""
    danger_message = """
    The requested mail might be malicious and are there for blocked!
    """
    patterns = [r'\<script', r'\<iframe']
    if match_at_least_one_pattern(html, patterns):
        return danger_message
    return html

def fetch_local_message_body(account, folder_path, uid):
    """Try to fetch the requested message from local database."""
    folder = account.folders.get(path=folder_path)
    header = folder.headers.get(uid=uid)
    return header.body.text

def store_local_message_body(account, folder_path, uid, text):
    """Store message body in local database."""
    folder = account.folders.get(path=folder_path)
    header = folder.headers.get(uid=uid)
    body = MailBody()
    body.header = header
    body.text = text
    body.save()

def ensure_private_message_account_exists(user):
    account_name = '%s@wappy' % user.username
    try:
        user.mail_accounts.get(name=account_name)
    except:
        mail_account = MailAccount()
        mail_account.user = user
        mail_account.name = account_name
        incoming = MailTransportDetails()
        incoming.protocol = 'pm'
        incoming.save()
        mail_account.incoming = incoming
        outgoing = MailTransportDetails()
        outgoing.protocol = 'pm'
        outgoing.save()
        mail_account.outgoing = outgoing
        mail_account.save()
        inbox = MailFolder()
        inbox.account = mail_account
        inbox.path = 'Inbox'
        inbox.save()
        sent = MailFolder()
        sent.account = mail_account
        sent.path = 'Sent'
        sent.save()

def create_mail_account(user, parameters):
    """Constuct mail account from dictionary with parameters."""
    account = MailAccount()
    account.user = user
    account.name = parameters['name']

    incoming = MailTransportDetails()
    incoming.protocol = parameters['incoming_protocol']
    incoming.server_address = parameters['incoming_server_address']
    incoming.server_port = int(parameters['incoming_server_port'])
    incoming.username = parameters['incoming_username']
    incoming.password = parameters['incoming_password']
    incoming.save()
    account.incoming = incoming

    outgoing = MailTransportDetails()
    outgoing.protocol = parameters['outgoing_protocol']
    outgoing.server_address = parameters['outgoing_server_address']
    outgoing.server_port = int(parameters['outgoing_server_port'])
    outgoing.username = parameters['outgoing_username']
    outgoing.password = parameters['outgoing_password']
    outgoing.save()
    account.outgoing = outgoing

    account.save()

    return account
