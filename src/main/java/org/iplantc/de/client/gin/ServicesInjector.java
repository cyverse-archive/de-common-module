package org.iplantc.de.client.gin;

import org.iplantc.de.client.services.AnalysisServiceFacade;
import org.iplantc.de.client.services.AppServiceFacade;
import org.iplantc.de.client.services.AppTemplateServices;
import org.iplantc.de.client.services.AppUserServiceFacade;
import org.iplantc.de.client.services.CollaboratorsServiceFacade;
import org.iplantc.de.client.services.DEFeedbackServiceFacade;
import org.iplantc.de.client.services.DeployedComponentServices;
import org.iplantc.de.client.services.DiskResourceServiceFacade;
import org.iplantc.de.client.services.FileEditorServiceFacade;
import org.iplantc.de.client.services.MessageServiceFacade;
import org.iplantc.de.client.services.SearchServiceFacade;
import org.iplantc.de.client.services.SystemMessageServiceFacade;
import org.iplantc.de.client.services.ToolRequestProvider;
import org.iplantc.de.client.services.UUIDServiceAsync;
import org.iplantc.de.client.services.UserSessionServiceFacade;

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

    UserSessionServiceFacade getUserSessionServiceFacade();

    MessageServiceFacade getMessageServiceFacade();

    FileEditorServiceFacade getFileEditorServiceFacade();

    DEFeedbackServiceFacade getDeFeedbackServiceFacade();

    AnalysisServiceFacade getAnalysisServiceFacade();

    CollaboratorsServiceFacade getCollaboratorsServiceFacade();

    DeployedComponentServices getDeployedComponentServices();

    AppTemplateServices getAppTemplateServices();

    UUIDServiceAsync getUUIDService();

    SystemMessageServiceFacade getSystemMessageServiceFacade();


}
