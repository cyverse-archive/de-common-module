/**
 * 
 */
package org.iplantc.de.client.services.impl;

import org.iplantc.de.client.models.DEProperties;
import org.iplantc.de.client.services.CollaboratorsServiceFacade;
import org.iplantc.de.client.services.DEServiceFacade;
import org.iplantc.de.shared.services.ServiceCallWrapper;

import com.google.gwt.http.client.URL;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.List;

/**
 * @author sriram
 * 
 */
public class CollaboratorsServiceFacadeImpl implements CollaboratorsServiceFacade {

    @Override
    public void searchCollaborators(String term, AsyncCallback<String> callback) {
        String address = DEProperties.getInstance().getMuleServiceBaseUrl()
                + "user-search/" + URL.encodeQueryString(term.trim()); //$NON-NLS-1$

        ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.GET, address);

        DEServiceFacade.getInstance().getServiceData(wrapper, callback);
    }

    @Override
    public void getCollaborators(AsyncCallback<String> callback) {
        String address = DEProperties.getInstance().getMuleServiceBaseUrl() + "collaborators";
        ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.GET, address);
        DEServiceFacade.getInstance().getServiceData(wrapper, callback);
    }

    @Override
    public void addCollaborators(JSONObject users, AsyncCallback<String> callback) {
        String address = DEProperties.getInstance().getMuleServiceBaseUrl() + "collaborators"; //$NON-NLS-1$

        ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.POST, address,
                users.toString());

        DEServiceFacade.getInstance().getServiceData(wrapper, callback);
    }

    @Override
    public void removeCollaborators(JSONObject users, AsyncCallback<String> callback) {
        String address = DEProperties.getInstance().getMuleServiceBaseUrl() + "remove-collaborators"; //$NON-NLS-1$

        ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.POST, address,
                users.toString());

        DEServiceFacade.getInstance().getServiceData(wrapper, callback);
    }

    @Override
    public void getUserInfo(List<String> usernames, AsyncCallback<String> callback) {
        StringBuilder address = new StringBuilder(DEProperties.getInstance().getMuleServiceBaseUrl());
        address.append("user-info"); //$NON-NLS-1$

        if (usernames != null && !usernames.isEmpty()) {
            address.append("?"); //$NON-NLS-1$
            boolean first = true;
            for (String user : usernames) {
                if (first) {
                    first = false;
                } else {
                    address.append("&"); //$NON-NLS-1$
                }

                address.append("username="); //$NON-NLS-1$
                address.append(URL.encodeQueryString(user.trim()));
            }
        }

        ServiceCallWrapper wrapper = new ServiceCallWrapper(address.toString());
        DEServiceFacade.getInstance().getServiceData(wrapper, callback);
    }

}
