package org.iplantc.de.client.gin;

import org.iplantc.de.client.services.AppServiceFacade;
import org.iplantc.de.client.services.AppUserServiceFacade;
import org.iplantc.de.client.services.DiskResourceServiceFacade;
import org.iplantc.de.client.services.SearchServiceFacade;
import org.iplantc.de.client.services.ToolRequestProvider;

import com.google.gwt.core.client.GWT;
import com.google.gwt.inject.client.GinModules;
import com.google.gwt.inject.client.Ginjector;

@GinModules(ServicesModule.class)
public interface ServicesInjector extends Ginjector {

    final ServicesInjector INSTANCE = GWT.create(ServicesInjector.class);

    DiskResourceServiceFacade getDiskResourceServiceFacade();

    ToolRequestProvider getToolRequestServiceProvider();

    SearchServiceFacade getSearchServiceFacade();

    AppServiceFacade getAppServiceFacade();

    AppUserServiceFacade getAppUserServiceFacade();

}
