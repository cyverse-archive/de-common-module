package org.iplantc.de.client.gin;

import org.iplantc.de.client.models.UserInfo;
import org.iplantc.de.client.services.DEServiceFacade;
import org.iplantc.de.client.services.DiskResourceServiceFacade;
import org.iplantc.de.client.services.ToolRequestProvider;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

final class ServicesModule extends AbstractGinModule {

    @Override
    protected void configure() {
        bind(DiskResourceServiceFacade.class).in(Singleton.class);
        bind(ToolRequestProvider.class).in(Singleton.class);
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
