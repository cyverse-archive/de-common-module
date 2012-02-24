package org.iplantc.de.shared.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * A service for sending simple emails (asynchronous part).
 * 
 * @author hariolf
 * 
 */
public interface EmailServiceAsync {

    /**
     * Sends an email to one recipient.
     * 
     * @param subject the email subject
     * @param message the email message
     * @param fromAddress the from address
     * @param toAddress the recipient
     * @param callback called after the service call finishes
     */
    void sendEmail(String subject, String message, String fromAddress, String toAddress,
            AsyncCallback<String> callback);
}
