package org.iplantc.de.client.services.impl;

import org.iplantc.de.client.models.DEProperties;
import org.iplantc.de.client.models.UserSession;
import org.iplantc.de.client.services.DEServiceFacade;
import org.iplantc.de.client.services.UserSessionServiceFacade;
import org.iplantc.de.shared.services.ServiceCallWrapper;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;
import com.google.web.bindery.autobean.shared.Splittable;

/**
 * A service facade to save and retrieve user session
 * 
 * @author sriram
 * 
 */
public class UserSessionServiceFacadeImpl implements UserSessionServiceFacade {

    /* (non-Javadoc)
     * @see org.iplantc.de.client.services.UserSessionServiceFacade#getUserSession(com.google.gwt.user.client.rpc.AsyncCallback)
     */
    @Override
    public void getUserSession(AsyncCallback<String> callback) {
        String address = DEProperties.getInstance().getMuleServiceBaseUrl() + "sessions"; //$NON-NLS-1$
        ServiceCallWrapper wrapper = new ServiceCallWrapper(address);
        DEServiceFacade.getInstance().getServiceData(wrapper, callback);
    }

    /* (non-Javadoc)
     * @see org.iplantc.de.client.services.UserSessionServiceFacade#saveUserSession(org.iplantc.de.client.models.UserSession, com.google.gwt.user.client.rpc.AsyncCallback)
     */
    @Override
    public void saveUserSession(UserSession userSession, AsyncCallback<String> callback){
        String address = DEProperties.getInstance().getMuleServiceBaseUrl() + "sessions"; //$NON-NLS-1$
        ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.POST, address,
                AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(userSession)).getPayload());
        DEServiceFacade.getInstance().getServiceData(wrapper, callback);
    }

    /* (non-Javadoc)
     * @see org.iplantc.de.client.services.UserSessionServiceFacade#clearUserSession(com.google.gwt.user.client.rpc.AsyncCallback)
     */
    @Override
    public void clearUserSession(AsyncCallback<String> callback) {
        String address = DEProperties.getInstance().getMuleServiceBaseUrl() + "sessions"; //$NON-NLS-1$
        ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.DELETE, address);
        DEServiceFacade.getInstance().getServiceData(wrapper, callback);
    }

    /* (non-Javadoc)
     * @see org.iplantc.de.client.services.UserSessionServiceFacade#getUserPreferences(com.google.gwt.user.client.rpc.AsyncCallback)
     */
    @Override
    public void getUserPreferences(AsyncCallback<String> callback) {
        String address = DEProperties.getInstance().getMuleServiceBaseUrl() + "preferences"; //$NON-NLS-1$
        ServiceCallWrapper wrapper = new ServiceCallWrapper(address);
        DEServiceFacade.getInstance().getServiceData(wrapper, callback);
    }

    /* (non-Javadoc)
     * @see org.iplantc.de.client.services.UserSessionServiceFacade#saveUserPreferences(com.google.web.bindery.autobean.shared.Splittable, com.google.gwt.user.client.rpc.AsyncCallback)
     */
    @Override
    public void saveUserPreferences(Splittable json, AsyncCallback<String> callback) {
        String address = DEProperties.getInstance().getMuleServiceBaseUrl() + "preferences"; //$NON-NLS-1$
        ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.POST, address, json.getPayload());
        DEServiceFacade.getInstance().getServiceData(wrapper, callback);
    }

    /* (non-Javadoc)
     * @see org.iplantc.de.client.services.UserSessionServiceFacade#postClientNotification(com.google.gwt.json.client.JSONObject, com.google.gwt.user.client.rpc.AsyncCallback)
     */
    @Override
    public void postClientNotification(JSONObject notification, AsyncCallback<String> callback) {
        String address = DEProperties.getInstance().getUnproctedMuleServiceBaseUrl()
                + "send-notification";

        ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.POST, address,
                notification.toString());

        DEServiceFacade.getInstance().getServiceData(wrapper, callback);

    }

    /* (non-Javadoc)
     * @see org.iplantc.de.client.services.UserSessionServiceFacade#getSearchHistory(com.google.gwt.user.client.rpc.AsyncCallback)
     */
    @Override
    public void getSearchHistory(AsyncCallback<String> callback) {
        String address = DEProperties.getInstance().getMuleServiceBaseUrl() + "search-history";
        ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.GET, address);
        DEServiceFacade.getInstance().getServiceData(wrapper, callback);
    }

    /* (non-Javadoc)
     * @see org.iplantc.de.client.services.UserSessionServiceFacade#saveSearchHistory(com.google.gwt.json.client.JSONObject, com.google.gwt.user.client.rpc.AsyncCallback)
     */
    @Override
    public void saveSearchHistory(JSONObject body, AsyncCallback<String> callback) {
        String address = DEProperties.getInstance().getMuleServiceBaseUrl() + "search-history";
        ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.POST, address,
                body.toString());
        DEServiceFacade.getInstance().getServiceData(wrapper, callback);

    }

}
