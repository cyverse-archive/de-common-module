package org.iplantc.de.server;

import org.apache.log4j.Logger;
import org.json.JSONObject;

/**
 * Used to dispatch service requests in which a JSON document is being sent to a remote service and the
 * username has to be inserted into the document. This prevents the UI from having to obtain the username
 * prior to sending such requests.
 * 
 * @author Dennis Roberts
 */
public class AuthenticatedJsonServiceDispatcher extends BaseDEServiceDispatcher {
    /**
     * Used to associate serialized objects with a specific version of this class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Used to log error messages.
     */
    private static final Logger LOG = Logger.getLogger(AuthenticatedJsonServiceDispatcher.class);

    /**
     * @param serviceResolver resolves aliased URLs.
     */
    public AuthenticatedJsonServiceDispatcher(ServiceCallResolver serviceResolver) {
        super(serviceResolver);
        setUrlConnector(new UnauthenticatedUrlConnector());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String updateRequestBody(String body) {
        try {
            JSONObject json = new JSONObject(body);
            json.put("user", getUsername());
            return json.toString();
        } catch (Exception e) {
            String msg = "unable to add the username to the request";
            LOG.error(msg, e);
            throw new RuntimeException(msg, e);
        }
    }
}
