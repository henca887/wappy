import smtplib
import email.mime.text


class SMTP:

    def send(self, account, recipient, subject, body):
        msg = email.mime.text.MIMEText(body.replace('<br>', '\r\n'))
        msg['Subject'] = subject
        msg['From'] = account.name
        msg['To'] = recipient
        
        session = smtplib.SMTP(account.outgoing.server_address,
                               account.outgoing.server_port)
        session.ehlo()
        session.starttls()
        session.ehlo()
        session.login(account.outgoing.username, account.outgoing.password)
        session.sendmail(account.name, [recipient], msg.as_string())
        session.close()
