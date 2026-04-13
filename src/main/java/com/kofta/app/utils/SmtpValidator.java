package com.kofta.app.utils;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;

public class SmtpValidator {
    public static Session validateAndCreateSession(
            String host, String port, String email, String password) throws Exception {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);

        Session session =
                Session.getInstance(
                        props,
                        new Authenticator() {
                            @Override
                            protected PasswordAuthentication getPasswordAuthentication() {
                                return new PasswordAuthentication(email, password);
                            }
                        });

        Transport transport = session.getTransport("smtp");
        transport.connect(host, Integer.parseInt(port), email, password);
        transport.close();

        return session;
    }
}
