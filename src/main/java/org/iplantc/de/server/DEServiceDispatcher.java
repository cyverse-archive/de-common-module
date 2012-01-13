package org.iplantc.de.server;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

/**
 * A dispatch service servlet that all client-side service facade make requests through.
 * 
 * A calls through this dispatcher as expecting a SAML authentication token. Therefore, communication
 * through this service are "secured."
 */
public class DEServiceDispatcher extends BaseDEServiceDispatcher {
    private static final long serialVersionUID = 5625374046154309665L;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    /**
     * Initializes the new service dispatcher.
     */
    public DEServiceDispatcher() {
        setUrlConnector(new AuthenticatedUrlConnector());
    }
}
