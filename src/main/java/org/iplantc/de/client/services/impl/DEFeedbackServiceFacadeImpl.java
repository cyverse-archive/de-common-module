package org.iplantc.de.client.services.impl;

import org.iplantc.de.client.models.DEProperties;
import org.iplantc.de.client.services.DEFeedbackServiceFacade;
import org.iplantc.de.client.services.DEServiceFacade;
import org.iplantc.de.shared.services.ServiceCallWrapper;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Provides access to remote services for submitting user feedback.
 */
@SuppressWarnings("nls")
public class DEFeedbackServiceFacadeImpl implements DEFeedbackServiceFacade {

    private static String FEEDBACK_SERVICE_PATH = "feedback";

    /* (non-Javadoc)
     * @see org.iplantc.de.client.services.impl.DEFeedbackServiceFacade#submitFeedback(java.lang.String, com.google.gwt.user.client.rpc.AsyncCallback)
     */
    @Override
    public void submitFeedback(String feedback, AsyncCallback<String> callback) {
        String addr = DEProperties.getInstance().getMuleServiceBaseUrl() + FEEDBACK_SERVICE_PATH;
        ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.PUT, addr, feedback);
        DEServiceFacade.getInstance().getServiceData(wrapper, callback);
    }
}
