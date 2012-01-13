package org.iplantc.de.server;

import org.iplantc.de.shared.services.BaseServiceCallWrapper;

/**
 * Resolves service calls from the client usage of a service key to the actual service address, or URL.
 * 
 */
public interface ServiceCallResolver {
    /**
     * Resolves the wrapper information to a service address, or URL.
     * 
     * @param wrapper service call wrapper containing metadata for a call.
     * @return a string representing a valid URL.
     */
    public String resolveAddress(BaseServiceCallWrapper wrapper);
}
