package org.iplantc.de.server;

/**
 * A dispatch service servlet that all client-side service facade make "unsecured" requests regarding
 * data that is not sensitive.
 */
public class UnsecuredDEServiceDispatcher extends BaseDEServiceDispatcher {
    private static final long serialVersionUID = 1L;

    /**
     * @param serviceResolver resolves aliased URLs.
     */
    public UnsecuredDEServiceDispatcher(ServiceCallResolver serviceResolver) {
        super(serviceResolver);
        setUrlConnector(new UnauthenticatedUrlConnector());
    }
}
