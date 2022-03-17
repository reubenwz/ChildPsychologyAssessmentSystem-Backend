/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.helper;

import java.util.Date;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.util.ByteArrayDataSource;

/**
 *
 * @author Ong Bik Jeun
 * @author Ooi Jun Hao
 */
public class EmailManager {

    private final String emailServerName = "smtp.gmail.com";
    private final String mailer = "JavaMailer";
    private String smtpAuthUser;
    private String smtpAuthPassword;

    public EmailManager() {
    }

    public EmailManager(String smtpAuthUser, String smtpAuthPassword) {
        this.smtpAuthUser = smtpAuthUser;
        this.smtpAuthPassword = smtpAuthPassword;
    }

    public Boolean emailPasswordReset(String link, String fromEmail, String toEmail) {

        try {
            String emailBody = "";
            emailBody += "Dear User, \n\n";
            emailBody += "Click on the following link to reset your password: \n\n";
            emailBody += link + "\n\n";

            emailBody += "Thank you and Have a nice day!";

            StringBuffer sb = new StringBuffer();
            sb.append("<HTML>\n");
            sb.append("<HEAD>\n");
            sb.append("<TITLE>\n");
            sb.append("Password Reset" + "\n");
            sb.append("</TITLE>\n");
            sb.append("</HEAD>\n");

            sb.append("<BODY>\n");
            sb.append("<P>" + "Dear User," + "</P>" + "\n");
            sb.append("<P>" + "Please click ");
            sb.append("<A href=\"").append(link).append("\">here</A>");
            sb.append(" to reset your password.</P>" + "\n");
            sb.append("<P>If you did not make a request to reset your password, please ignore this email.</P>" + "\n");
            sb.append("<P>Thank you and have a nice day!</P>" + "\n");
            sb.append("<P>Best Regards,</P>" + "\n");
            sb.append("<P>CANS Assessment System Management Team</P>" + "\n");

            sb.append("</BODY>\n");
            sb.append("</HTML>\n");

            Properties props = new Properties();
            props.put("mail.transport.protocol", "smtp");
            props.put("mail.smtp.host", emailServerName);
            props.put("mail.smtp.port", "587");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.debug", "true");
            props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
            javax.mail.Authenticator auth = new SMTPAuthenticator(smtpAuthUser, smtpAuthPassword);

            Session session = Session.getInstance(props, auth);
            session.setDebug(true);
            Message msg = new MimeMessage(session);

            if (msg != null) {
                /*
                msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
                msg.addHeader("format", "flowed");
                msg.addHeader("Content-Transfer-Encoding", "8bit");
                msg.setFrom(new InternetAddress(fromEmail, "NoReply-JD"));
                msg.setSubject("Password Reset");
                msg.setSentDate(new Date());
                msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail, false));
                msg.setText(emailBody);
                 */
                msg.setFrom(InternetAddress.parse(fromEmail, false)[0]);
                msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail, false));
                msg.setSubject("Password Reset");
                msg.setDataHandler(new DataHandler(
                        new ByteArrayDataSource(sb.toString(), "text/html")));
                //msg.setText(emailBody); // This is for setting to plain string
                msg.setHeader("X-Mailer", mailer);

                Date timeStamp = new Date();
                msg.setSentDate(timeStamp);

                Transport.send(msg);
                return true;
            } else {
                return false;
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public Boolean emailNewPasswordResetAssessor(String link, String fromEmail, String toEmail) {

        try {
            String emailBody = "";
            emailBody += "Dear User, \n\n";
            emailBody += "Click on the following link to reset your password: \n\n";
            emailBody += link + "\n\n";

            emailBody += "Thank you and Have a nice day!";

            StringBuffer sb = new StringBuffer();
            sb.append("<HTML>\n");
            sb.append("<HEAD>\n");
            sb.append("<TITLE>\n");
            sb.append("New Account Creation (CANS Assessment System)" + "\n");
            sb.append("</TITLE>\n");
            sb.append("</HEAD>\n");

            sb.append("<BODY>\n");
            sb.append("<P>" + "Dear User," + "</P>" + "\n");
            sb.append("<P>" + "Welcome to CANS Assessment System! You have been assigned a new account using this email for use in the assessment system." + "</P>" + "\n");
            sb.append("<P>" + "Please click ");
            sb.append("<A href=\"").append(link).append("\">here</A>");
            sb.append(" to set your password.</P>" + "\n");
            sb.append("<P>If you are not expecting an email to set your password for a new account, please ignore this email.</P>" + "\n");
            sb.append("<P>Thank you and have a nice day!</P>" + "\n");
            sb.append("<P>Best Regards,</P>" + "\n");
            sb.append("<P>CANS Assessment System Management Team</P>" + "\n");

            sb.append("</BODY>\n");
            sb.append("</HTML>\n");

            Properties props = new Properties();
            props.put("mail.transport.protocol", "smtp");
            props.put("mail.smtp.host", emailServerName);
            props.put("mail.smtp.port", "587");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.debug", "true");
            props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
            javax.mail.Authenticator auth = new SMTPAuthenticator(smtpAuthUser, smtpAuthPassword);

            Session session = Session.getInstance(props, auth);
            session.setDebug(true);
            Message msg = new MimeMessage(session);

            if (msg != null) {
                /*
                msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
                msg.addHeader("format", "flowed");
                msg.addHeader("Content-Transfer-Encoding", "8bit");
                msg.setFrom(new InternetAddress(fromEmail, "NoReply-JD"));
                msg.setSubject("Password Reset");
                msg.setSentDate(new Date());
                msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail, false));
                msg.setText(emailBody);
                 */
                msg.setFrom(InternetAddress.parse(fromEmail, false)[0]);
                msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail, false));
                msg.setSubject("Password Reset");
                msg.setDataHandler(new DataHandler(
                        new ByteArrayDataSource(sb.toString(), "text/html")));
                //msg.setText(emailBody); // This is for setting to plain string
                msg.setHeader("X-Mailer", mailer);

                Date timeStamp = new Date();
                msg.setSentDate(timeStamp);

                Transport.send(msg);
                return true;
            } else {
                return false;
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public Boolean emailNewPasswordResetAdmin(String link, String fromEmail, String toEmail) {

        try {
            String emailBody = "";
            emailBody += "Dear User, \n\n";
            emailBody += "Click on the following link to reset your password: \n\n";
            emailBody += link + "\n\n";

            emailBody += "Thank you and Have a nice day!";

            StringBuffer sb = new StringBuffer();
            sb.append("<HTML>\n");
            sb.append("<HEAD>\n");
            sb.append("<TITLE>\n");
            sb.append("New Account Creation (CANS Assessment Admin System)" + "\n");
            sb.append("</TITLE>\n");
            sb.append("</HEAD>\n");

            sb.append("<BODY>\n");
            sb.append("<P>" + "Dear User," + "</P>" + "\n");
            sb.append("<P>" + "Welcome to CANS Assessment System! You have been assigned a new account using this email for use in the admin system." + "</P>" + "\n");
            sb.append("<P>" + "Please click ");
            sb.append("<A href=\"").append(link).append("\">here</A>");
            sb.append(" to set your password.</P>" + "\n");
            sb.append("<P>If you are not expecting an email to set your password for a new account, please ignore this email.</P>" + "\n");
            sb.append("<P>Thank you and have a nice day!</P>" + "\n");
            sb.append("<P>Best Regards,</P>" + "\n");
            sb.append("<P>CANS Assessment System Management Team</P>" + "\n");

            sb.append("</BODY>\n");
            sb.append("</HTML>\n");

            Properties props = new Properties();
            props.put("mail.transport.protocol", "smtp");
            props.put("mail.smtp.host", emailServerName);
            props.put("mail.smtp.port", "587");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.debug", "true");
            props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
            javax.mail.Authenticator auth = new SMTPAuthenticator(smtpAuthUser, smtpAuthPassword);

            Session session = Session.getInstance(props, auth);
            session.setDebug(true);
            Message msg = new MimeMessage(session);

            if (msg != null) {
                /*
                msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
                msg.addHeader("format", "flowed");
                msg.addHeader("Content-Transfer-Encoding", "8bit");
                msg.setFrom(new InternetAddress(fromEmail, "NoReply-JD"));
                msg.setSubject("Password Reset");
                msg.setSentDate(new Date());
                msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail, false));
                msg.setText(emailBody);
                 */
                msg.setFrom(InternetAddress.parse(fromEmail, false)[0]);
                msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail, false));
                msg.setSubject("Password Reset");
                msg.setDataHandler(new DataHandler(
                        new ByteArrayDataSource(sb.toString(), "text/html")));
                //msg.setText(emailBody); // This is for setting to plain string
                msg.setHeader("X-Mailer", mailer);

                Date timeStamp = new Date();
                msg.setSentDate(timeStamp);

                Transport.send(msg);
                return true;
            } else {
                return false;
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
}
