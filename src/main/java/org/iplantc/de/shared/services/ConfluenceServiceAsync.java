package org.iplantc.de.shared.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * A service interface for interfacing with Confluence (asynchronous part).
 * 
 * @author hariolf
 * 
 */
public interface ConfluenceServiceAsync {

    /**
     * Creates a new page in the iPlant wiki as a child of the "List of Applications" page.
     * 
     * @param toolName the name of the tool which is used as the page title
     * @param description a tool description
     * @param callback called after the service call finishes
     */
    void addPage(String toolName, String description, AsyncCallback<String> callback);

}
