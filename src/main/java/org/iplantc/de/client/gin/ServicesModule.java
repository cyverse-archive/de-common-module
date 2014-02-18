package org.iplantc.de.client.gin;

import org.iplantc.de.client.models.UserInfo;
import org.iplantc.de.client.services.DEServiceFacade;
import org.iplantc.de.client.services.DiskResourceServiceFacade;
import org.iplantc.de.client.services.SearchServiceFacade;
import org.iplantc.de.client.services.ToolRequestProvider;
import org.iplantc.de.client.services.impl.SearchServiceFacadeImpl;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

final class ServicesModule extends AbstractGinModule {

    @Override
    protected void configure() {
        // TODO JDS May want to perform binding here instead of via deferred binding.
        // That will help ensure that only one DiskResource service is active in the app (no one can
        // GWT.create one with deferred binding).
        bind(DiskResourceServiceFacade.class).in(Singleton.class);
        bind(ToolRequestProvider.class).in(Singleton.class);

        bind(SearchServiceFacade.class).to(SearchServiceFacadeImpl.class);
    }

    @Provides
    @Singleton
    public DEServiceFacade createDeServiceFacade() {
        return DEServiceFacade.getInstance();
    }

    @Provides
    @Singleton
    public UserInfo createUserInfo() {
        return UserInfo.getInstance();
    }

}
