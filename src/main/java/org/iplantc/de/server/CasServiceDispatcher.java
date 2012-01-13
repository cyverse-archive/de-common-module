package org.iplantc.de.server;

/**
 * A servlet that is used to dispatch requests to services that are secured by CAS.  The service must be configured to
 * accept proxy tickets from this server.  The proxy tickets will be sent to the service in the query-string parameter,
 * <code>proxyTicket</code>.
 * 
 * @author Dennis Roberts
 */
public class CasServiceDispatcher extends BaseDEServiceDispatcher {

    /**
     * Initializes the new service dispatcher.
     */
    public CasServiceDispatcher() {
        setUrlConnector(new CasUrlConnector());
    }
}
