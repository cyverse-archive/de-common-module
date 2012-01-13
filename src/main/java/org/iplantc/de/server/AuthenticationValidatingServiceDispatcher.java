package org.iplantc.de.server;

/**
 * A service dispatcher that can be used for internal services that need prior authentication.
 * 
 * @author Dennis Roberts
 */
public class AuthenticationValidatingServiceDispatcher extends BaseDEServiceDispatcher {
    /**
     * The version number used to identify serialized instances of this version of this class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Initializes the new service dispatcher.
     */
    public AuthenticationValidatingServiceDispatcher() {
        setUrlConnector(new AuthenticationValidatingUrlConnector());
    }
}
