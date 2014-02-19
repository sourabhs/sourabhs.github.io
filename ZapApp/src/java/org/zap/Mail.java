package org.zap;

import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/*
 This class has the functionality to send a mail.
 */
public class Mail {

    //function to send a mail to given email
    public static void send(String email, String productName) throws AddressException,MessagingException{
        //init props to send mail via gmail's secure smtp server
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.auth", "true");
        props.put("mail.debug", "true");
        props.put("mail.smtp.port", 465);
        props.put("mail.smtp.socketFactory.port", 465);
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");

        Session session = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        //use this email id and password to authenticate and send the mail
                        return new PasswordAuthentication("amdocsproject", "mitamdocs");
                    }
                });

        Message msg = new MimeMessage(session);

        //sender email
        InternetAddress addressFrom = new InternetAddress("amdocsproject@gmail.com");
        msg.setFrom(addressFrom);

        //recipient email
        msg.setRecipient(Message.RecipientType.TO, new InternetAddress(email));

        // Setting the Subject and Content Type
        msg.setSubject("Discount available on a product you are following");
        msg.setContent("Greetings from Zappos!\n\nWe are mailing to let you "
                + "know that the product " + productName + " is now available at a Discount!\n\n"
                + "Visit Zappos and Shop today!", "text/plain");

        //send mail
        Transport.send(msg);

    }

}
