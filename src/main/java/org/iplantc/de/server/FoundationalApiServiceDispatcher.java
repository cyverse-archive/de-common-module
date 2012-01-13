package org.iplantc.de.server;

import java.io.IOException;

/**
 * A dispatch service servlet that is used to dispatch requests to the foundational API.
 * 
 * @author Dennis Roberts
 */
public class FoundationalApiServiceDispatcher extends BaseDEServiceDispatcher {
    private static final long serialVersionUID = 1L;

    /**
     * Initializes the new service dispatcher.
     * 
     * @throws IOException if the URL connector encounters a problem.
     */
    public FoundationalApiServiceDispatcher() throws IOException {
        setUrlConnector(new FoundationalApiUrlConnector());
    }
}
