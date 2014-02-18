package org.iplantc.de.client.services.impl;

import org.iplantc.de.client.models.DEProperties;
import org.iplantc.de.client.models.UserInfo;
import org.iplantc.de.client.models.sysMsgs.IdList;
import org.iplantc.de.client.models.sysMsgs.MessageFactory;
import org.iplantc.de.client.models.sysMsgs.MessageList;
import org.iplantc.de.client.models.sysMsgs.User;
import org.iplantc.de.client.services.AsyncCallbackConverter;
import org.iplantc.de.client.services.DEServiceFacade;
import org.iplantc.de.client.services.StringToVoidCallbackConverter;
import org.iplantc.de.client.services.SystemMessageServiceFacade;
import org.iplantc.de.shared.services.BaseServiceCallWrapper.Type;
import org.iplantc.de.shared.services.ServiceCallWrapper;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;
import com.google.web.bindery.autobean.shared.Splittable;

/**
 * Provides access to remote services to acquire system messages.
 */
public final class SystemMessageServiceFacadeImpl implements SystemMessageServiceFacade {
	
    private static final class MsgListCB extends AsyncCallbackConverter<String, MessageList> {
        public MsgListCB(final AsyncCallback<MessageList> callback) {
            super(callback);
        }

        @Override
        protected MessageList convertFrom(final String json) {
            return AutoBeanCodex.decode(MessageFactory.INSTANCE, MessageList.class, json).as();
        }
    }
	
    /**
     * @see SystemMessageServiceFacade#getAllMessages(AsyncCallback)
     */
    @Override
    public final void getAllMessages(final AsyncCallback<MessageList> callback) {
        getMessages("/messages", callback); //$NON-NLS-1$
    }	

    /**
     * @see SystemMessageServiceFacade#getNewMessages(AsyncCallback)
     */
    @Override
    public final void getNewMessages(final AsyncCallback<MessageList> callback) {
        getMessages("/new-messages", callback); //$NON-NLS-1$
    }

    /**
     * @see SystemMessageServiceFacade#getUnseenMessages(AsyncCallback)
     */
    @Override
    public final void getUnseenMessages(final AsyncCallback<MessageList> callback) {
        getMessages("/unseen-messages", callback); //$NON-NLS-1$
    }

    /**
     * @see SystemMessageServiceFacade#markAllReceived(AsyncCallback)
     */
    @Override
    public void markAllReceived(final AsyncCallback<Void> callback) {
        final String address = makeAddress("/mark-all-received");  //$NON-NLS-1$
        final AutoBean<User> user = MessageFactory.INSTANCE.makeUser();
        user.as().setUser(UserInfo.getInstance().getUsername());
        final String payload = AutoBeanCodex.encode(user).getPayload();
        final ServiceCallWrapper wrapper = new ServiceCallWrapper(Type.POST, address, payload);
        final AsyncCallback<String> voidedCB = new StringToVoidCallbackConverter(callback);
        DEServiceFacade.getInstance().getServiceData(wrapper, voidedCB);
    }

    /**
     * @see SystemMessageServiceFacade#markReceived(IdList, AsyncCallback)
     */
    @Override
    public void markReceived(final IdList msgIds, final AsyncCallback<Void> callback) {
        final String address = makeAddress("/received");  //$NON-NLS-1$
        final Splittable split = AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(msgIds));
        final ServiceCallWrapper wrapper = new ServiceCallWrapper(Type.POST, address, split.getPayload());
        final AsyncCallback<String> voidedCB = new StringToVoidCallbackConverter(callback);
        DEServiceFacade.getInstance().getServiceData(wrapper, voidedCB);
    }

    /**
     * @see SystemMessageServiceFacade#acknowledgeMessages(IdList, AsyncCallback)
     */
    @Override
    public void acknowledgeMessages(final IdList msgIds, final AsyncCallback<Void> callback) {
        final String address = makeAddress("/seen"); //$NON-NLS-1$
        final Splittable split = AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(msgIds));
        final ServiceCallWrapper wrapper = new ServiceCallWrapper(Type.POST, address, split.getPayload());
        final AsyncCallback<String> voidedCB = new StringToVoidCallbackConverter(callback);
        DEServiceFacade.getInstance().getServiceData(wrapper, voidedCB);
    }

    /**
     * @see SystemMessageServiceFacade#hideMessages(IdList, AsyncCallback)
     */
    @Override
    public void hideMessages(final IdList msgIds, final AsyncCallback<Void> callback) {
        final String address = makeAddress("/delete");  //$NON-NLS-1$
        final Splittable split = AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(msgIds));
        final ServiceCallWrapper wrapper = new ServiceCallWrapper(Type.POST, address, split.getPayload());
        final AsyncCallback<String> voidedCB = new StringToVoidCallbackConverter(callback);
        DEServiceFacade.getInstance().getServiceData(wrapper, voidedCB);
    }

    private void getMessages(final String relSvcPath, final AsyncCallback<MessageList> callback) {
        final String address = makeAddress(relSvcPath);
        final ServiceCallWrapper wrapper = new ServiceCallWrapper(Type.GET, address);
        DEServiceFacade.getInstance().getServiceData(wrapper, new MsgListCB(callback));
    }

    private String makeAddress(final String relPath) {
        final String base = DEProperties.getInstance().getMuleServiceBaseUrl();
        return base + "notifications/system" + relPath;  //$NON-NLS-1$
    }

}
