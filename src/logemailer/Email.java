/*
    Log Emailer sends log files to your email as an attachment
    Copyright (C) 2010  Jason Bryan

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
  */

package logemailer;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;


/**
 *
 * @author Jason
 */
public class Email  {

    private String smtpHostName;
    private String smtpAuthUser;
    private String smtpAuthPass;

    private String from;
    private String subject;
    private String body;
    private String[] recipients;
    private ArrayList<LogFile> logFiles;


    /**
     * Default Constructor
     */
    public Email() {
        smtpHostName = "";
        smtpAuthUser = "";
        smtpAuthPass = "";

        from = "";
        subject = "";
        body = "";
        recipients = null;
        logFiles = new ArrayList<LogFile>();
    }


    /**
     * Constructor - creates email object with the arraylist of logfiles as attachments
     * @param logFiles - arraylist of logfiles to add as attachments to the email
     * @throws Exception 
     */
    public Email(ArrayList<LogFile> logFiles) throws Exception {
        this.smtpHostName = "";
        this.smtpAuthUser = "";
        this.smtpAuthPass = "";

        this.from = "";
        this.subject = createSubject();
        this.body = createBody();
        this.recipients = null;
        this.logFiles = logFiles;
    }


    /*
     * ------ PUBLIC METHODS ------
     */


    /**
     * Sends logFile to recipients in mailList
     * @throws MessagingException
     */
    public void postMail() throws Exception {
        boolean debug = false;
        File file = new File("database.properties");

        /* Pulles properties in the config.properties file */
        BufferedReader br = new BufferedReader(new FileReader(file));
        Properties props = new Properties();
        props.load(br);
        smtpHostName = props.getProperty("mail.smtp.host");
        smtpAuthUser = props.getProperty("mail.smtp.user");
        smtpAuthPass = props.getProperty("mail.smtp.pass");

        from = props.getProperty("mail.smtp.user");
        recipients = props.getProperty("mailList").split(",");

        /* Sets up Authentication and Session for sending of email */
        Authenticator auth = new SMTPAuthenticator();
        Session session = Session.getDefaultInstance(props, auth);

        session.setDebug(debug);

        Message msg = new MimeMessage(session);

        InternetAddress addressFrom = new InternetAddress(from);
        msg.setFrom(addressFrom);

        /* Established email to be sent to all recipents in mailList */
        InternetAddress[] addressTo = new InternetAddress[recipients.length];

        for (int i = 0; i < recipients.length; ++i) {
            addressTo[i] = new InternetAddress(recipients[i]);
        }
        msg.setRecipients(Message.RecipientType.TO, addressTo);

        /* Puts Subject and Body on message and sends it through Transporter */
        msg.setSubject(subject);

        /* Create the message part */
        MimeBodyPart messageBodyPart = new MimeBodyPart();

        /* Fill message */
        messageBodyPart.setContent(body, "text/html");

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(messageBodyPart);

        /* Adds all Log File attachment parts */
        for (int i = 0; i < logFiles.size(); ++i) {
           if (logFiles.get(i).isExists() == true) {
                messageBodyPart = new MimeBodyPart();
                DataSource source = new FileDataSource(logFiles.get(i).getContents());
                messageBodyPart.setDataHandler(new DataHandler(source));
                messageBodyPart.setFileName(logFiles.get(i).getName());
                multipart.addBodyPart(messageBodyPart);
           }
        }
        
        /* Put parts in message */
        msg.setContent(multipart);
        Transport.send(msg);

        /* Closes Readers */
        br.close();
    }


    /*
     * ------ PRIVATE METHODS ------
     */


    /**
     * create the subject for the email
     * @return the subject as a string
     */
    private String createSubject() {
        LogFile file = logFiles.get(0);

        return (file.getHostName() + ": Log Files");
    }


    /**
     * create the body of the email.  list of log files attached and date
     * @return the body as a string
     */
    private String createBody() throws Exception {
        Calendar today = Calendar.getInstance();

        String date = today.get(Calendar.YEAR) + "." +
                      (today.get(Calendar.MONTH) + 1) + "." +
                      today.get(Calendar.DAY_OF_MONTH);
        
        String hostName = logFiles.get(0).getHostName();

        /* main text */
        body = "The log files for " + date + " on host <strong>" + hostName + "</strong> are attached.<br /><br />";
        body += "Attached files include:<br />";

        /* list of attached log files */
        for (int i = 0; i < logFiles.size(); ++i) {
            LogFile file = logFiles.get(i);
            body += "<strong>" + file.getName() + "</strong><br />";
            body += (file.keywordSearch() + "<br />");
        }

        return body;
    }


    /*
     * ----- PRIVATE CLASSES ------
     */


    /**
     * Private class to Email only which allows for Authentication of User
     */
    private class SMTPAuthenticator extends javax.mail.Authenticator{

        /**
         * Authenticates username and password
         * @return PasswordAuthentication
         */
        @Override
        public PasswordAuthentication getPasswordAuthentication(){
            String username = smtpAuthUser;
            String password = smtpAuthPass;
            return new PasswordAuthentication(username, password);
        }

    }
}

