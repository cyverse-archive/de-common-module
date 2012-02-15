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

    /**
     * Adds a user comment to a tool description page.
     * 
     * @param toolName the name of the tool which is also the page title
     * @param comment a comment
     * @param callback called after the service call finishes
     */
    void addComment(String toolName, String comment, AsyncCallback<String> callback);

    /**
     * Removes a user comment from a tool description page.
     * 
     * @param toolName the name of the tool which is also the page title
     * @param commentId the comment ID in Confluence
     * @param callback called after the service call finishes
     */
    void removeComment(String toolName, Long commentId, AsyncCallback<String> callback);

    /**
     * Changes an existing user comment on a tool description page.
     * 
     * @param toolName the name of the tool which is also the page title
     * @param commentId the comment ID in Confluence
     * @param newComment the new comment text
     * @param callback called after the service call finishes
     */
    void editComment(String toolName, Long commentId, String newComment, AsyncCallback<String> callback);
}
