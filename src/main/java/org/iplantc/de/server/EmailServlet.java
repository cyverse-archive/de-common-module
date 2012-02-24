package org.iplantc.de.server;

import java.io.IOException;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.iplantc.de.shared.services.EmailService;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * A servlet for sending simple emails. The server address is read from the mail.smtp.host property.
 * 
 * @author hariolf
 * 
 */
public class EmailServlet extends RemoteServiceServlet implements EmailService {
    private static final long serialVersionUID = -3893564670515471591L;
    private static final String PROPERTIES_FILE = "email.properties"; //$NON-NLS-1$
    private Properties properties;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        properties = new Properties();
        try {
            properties.load(Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream(PROPERTIES_FILE));
        } catch (IOException e) {
            throw new ServletException("Can't load file " + PROPERTIES_FILE, e); //$NON-NLS-1$
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendEmail(String subject, String message, String fromAddress, String toAddress) {
        Properties props = System.getProperties();

        Session session = Session.getDefaultInstance(props, null);
        MimeMessage mimeMsg = new MimeMessage(session);
        try {
            mimeMsg.setFrom(new InternetAddress(fromAddress));
            mimeMsg.addRecipient(Message.RecipientType.TO, new InternetAddress(toAddress));
            mimeMsg.setSubject(subject);
            mimeMsg.setText(message);
            Transport.send(mimeMsg);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
