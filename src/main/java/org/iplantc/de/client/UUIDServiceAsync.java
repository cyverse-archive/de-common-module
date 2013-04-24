package org.iplantc.de.client;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Async counterpart of <code>UUIDService</code>
 * 
 * @author jstroot
 *
 */
public interface UUIDServiceAsync {

    void getUUIDs(int num, AsyncCallback<List<String>> callback);

}
