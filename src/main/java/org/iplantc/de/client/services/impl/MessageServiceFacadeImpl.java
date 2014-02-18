package org.iplantc.de.client.services.impl;

import org.iplantc.de.client.models.DEProperties;
import org.iplantc.de.client.models.UserInfo;
import org.iplantc.de.client.models.notifications.Counts;
import org.iplantc.de.client.models.notifications.NotificationAutoBeanFactory;
import org.iplantc.de.client.services.AsyncCallbackConverter;
import org.iplantc.de.client.services.DEServiceFacade;
import org.iplantc.de.client.services.MessageServiceFacade;
import org.iplantc.de.client.services.callbacks.NotificationCallback;
import org.iplantc.de.shared.services.BaseServiceCallWrapper.Type;
import org.iplantc.de.shared.services.ServiceCallWrapper;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.http.client.URL;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

/**
 * Provides access to remote services to acquire messages and notifications.
 *
 * @author amuir
 *
 */
public class MessageServiceFacadeImpl implements MessageServiceFacade {

    private static final class CountsCB extends AsyncCallbackConverter<String, Counts> {
        public CountsCB(final AsyncCallback<Counts> callback) {
            super(callback);
        }

        @Override
        protected Counts convertFrom(final String json) {
            return AutoBeanCodex.decode(notesFactory, Counts.class, json).as();
        }
    }

    private static final NotificationAutoBeanFactory notesFactory = GWT.create(NotificationAutoBeanFactory.class);

    /* (non-Javadoc)
     * @see org.iplantc.de.client.services.impl.MessageServiceFacade#getNotifications(int, int, java.lang.String, java.lang.String, C)
     */
    @Override
    public <C extends NotificationCallback> void getNotifications(int limit, int offset, String filter, String sortDir, C callback) {
        String address = DEProperties.getInstance().getMuleServiceBaseUrl(); //$NON-NLS-1$

        StringBuilder builder = new StringBuilder("notifications/messages?limit=" + limit + "&offset="
                + offset);
        if (filter != null && !filter.isEmpty()) {
            builder.append("&filter=" + URL.encodeQueryString(filter));
        }

        if (sortDir != null && !sortDir.isEmpty() && !sortDir.equalsIgnoreCase("NONE")) {
            builder.append("&sortDir=" + URL.encodeQueryString(sortDir));
        }

        address = address + builder.toString();
        ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.GET, address);
        DEServiceFacade.getInstance().getServiceData(wrapper, callback);
    }

    /* (non-Javadoc)
     * @see org.iplantc.de.client.services.impl.MessageServiceFacade#getRecentMessages(com.google.gwt.user.client.rpc.AsyncCallback)
     */
    @Override
    public void getRecentMessages(AsyncCallback<String> callback) {
        String address = DEProperties.getInstance().getMuleServiceBaseUrl()
                + "notifications/last-ten-messages"; //$NON-NLS-1$
        ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.GET, address);

        DEServiceFacade.getInstance().getServiceData(wrapper, callback);
    }

    /* (non-Javadoc)
     * @see org.iplantc.de.client.services.impl.MessageServiceFacade#markAsSeen(com.google.gwt.json.client.JSONObject, com.google.gwt.user.client.rpc.AsyncCallback)
     */
    @Override
    public void markAsSeen(final JSONObject seenIds, AsyncCallback<String> callback) {
        String address = DEProperties.getInstance().getMuleServiceBaseUrl() + "notifications/seen"; //$NON-NLS-1$
        ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.POST, address,
                seenIds.toString());

        DEServiceFacade.getInstance().getServiceData(wrapper, callback);
    }

    /* (non-Javadoc)
     * @see org.iplantc.de.client.services.impl.MessageServiceFacade#deleteMessages(com.google.gwt.json.client.JSONObject, com.google.gwt.user.client.rpc.AsyncCallback)
     */
    @Override
    public void deleteMessages(final JSONObject deleteIds, AsyncCallback<String> callback) {
        String address = DEProperties.getInstance().getMuleServiceBaseUrl() + "notifications/delete"; //$NON-NLS-1$
        ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.POST, address,
                deleteIds.toString());

        DEServiceFacade.getInstance().getServiceData(wrapper, callback);
    }

    /* (non-Javadoc)
     * @see org.iplantc.de.client.services.impl.MessageServiceFacade#getRecentMessages(C)
     */
    @Override
    public <C extends NotificationCallback> void getRecentMessages(C callback) {
        String address = DEProperties.getInstance().getMuleServiceBaseUrl()
                + "notifications/last-ten-messages"; //$NON-NLS-1$
        ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.GET, address);
        DEServiceFacade.getInstance().getServiceData(wrapper, callback);

    }

    /* (non-Javadoc)
     * @see org.iplantc.de.client.services.impl.MessageServiceFacade#getMessageCounts(com.google.gwt.user.client.rpc.AsyncCallback)
     */
    @Override
    public void getMessageCounts(final AsyncCallback<Counts> callback) {
        final String addr = DEProperties.getInstance().getMuleServiceBaseUrl()
                + "notifications/count-messages?seen=false"; //$NON-NLS-1$
        final ServiceCallWrapper wrapper = new ServiceCallWrapper(Type.GET, addr);
        final AsyncCallback<String> convCB = new CountsCB(callback);
        DEServiceFacade.getInstance().getServiceData(wrapper, convCB);
    }

    /* (non-Javadoc)
     * @see org.iplantc.de.client.services.impl.MessageServiceFacade#deleteAll(com.google.gwt.user.client.rpc.AsyncCallback)
     */
    @Override
    public void deleteAll(AsyncCallback<String> callback) {
        String address = DEProperties.getInstance().getMuleServiceBaseUrl() + "notifications/delete-all"; //$NON-NLS-1$

        ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.DELETE, address);

        DEServiceFacade.getInstance().getServiceData(wrapper, callback);
    }

    /* (non-Javadoc)
     * @see org.iplantc.de.client.services.impl.MessageServiceFacade#acknowledgeAll(com.google.gwt.user.client.rpc.AsyncCallback)
     */
    @Override
    public void acknowledgeAll(AsyncCallback<String> callback) {
        String address = DEProperties.getInstance().getMuleServiceBaseUrl()
                + "notifications/mark-all-seen"; //$NON-NLS-1$

        ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.POST, address,
                UserInfo.getInstance().getUsername());

        DEServiceFacade.getInstance().getServiceData(wrapper, callback);
    }
    
}
