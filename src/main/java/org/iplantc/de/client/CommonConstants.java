package org.iplantc.de.client;

import com.google.gwt.i18n.client.Constants;

public interface CommonConstants extends Constants {
    /**
     * URL for landing page.
     *
     * @return a string representing the URL.
     */
    String iplantHome();

    /**
     * The name of the request string parameter for selecting an application on startup
     * 
     * @return application ID parameter name
     */
    String appIdParam();

    /**
     * Request parameter for loading a tool on Tito startup
     * 
     * @return the URL parameter name
     */
    String titoId();

}
