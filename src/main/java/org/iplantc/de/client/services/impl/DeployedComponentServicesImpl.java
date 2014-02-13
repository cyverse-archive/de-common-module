package org.iplantc.de.client.services.impl;

import org.iplantc.de.client.models.DEProperties;
import org.iplantc.de.client.models.HasId;
import org.iplantc.de.client.models.deployedComps.DeployedComponent;
import org.iplantc.de.client.models.deployedComps.DeployedComponentAutoBeanFactory;
import org.iplantc.de.client.services.DEServiceFacade;
import org.iplantc.de.client.services.DeployedComponentServices;
import org.iplantc.de.client.services.converters.GetAppTemplateDeployedComponentConverter;
import org.iplantc.de.client.services.converters.GetDeployedComponentsCallbackConverter;
import org.iplantc.de.shared.SharedAuthenticationValidatingServiceFacade;
import org.iplantc.de.shared.services.ServiceCallWrapper;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.List;

public class DeployedComponentServicesImpl implements DeployedComponentServices {

    private final DeployedComponentAutoBeanFactory factory = GWT.create(DeployedComponentAutoBeanFactory.class);

    @Override
    public void getAppTemplateDeployedComponent(HasId appTemplateId, AsyncCallback<DeployedComponent> callback) {
        String address = DEProperties.getInstance().getMuleServiceBaseUrl() + "get-components-in-analysis/" + appTemplateId.getId();
        ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.GET, address);

        DEServiceFacade.getInstance().getServiceData(wrapper, new GetAppTemplateDeployedComponentConverter(callback, factory));
    }

    @Override
    public void getDeployedComponents(AsyncCallback<List<DeployedComponent>> callback) {
        GetDeployedComponentsCallbackConverter callbackCnvt = new GetDeployedComponentsCallbackConverter(callback, factory);
        ServiceCallWrapper wrapper = new ServiceCallWrapper("org.iplantc.services.zoidberg.components"); //$NON-NLS-1$

        callService(callbackCnvt, wrapper);
    }

    @Override
    public void searchDeployedComponents(String searchTerm, AsyncCallback<List<DeployedComponent>> callback) {
        GetDeployedComponentsCallbackConverter callbackCnvt = new GetDeployedComponentsCallbackConverter(callback, factory);

        String address = DEProperties.getInstance().getUnproctedMuleServiceBaseUrl() + "search-deployed-components/" + URL.encodeQueryString(searchTerm);
        ServiceCallWrapper wrapper = new ServiceCallWrapper(address);
        DEServiceFacade.getInstance().getServiceData(wrapper, callbackCnvt);
    }

    private void callService(AsyncCallback<String> callback, ServiceCallWrapper wrapper) {
        SharedAuthenticationValidatingServiceFacade.getInstance().getServiceData(wrapper, callback);
    }



}
