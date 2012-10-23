package org.iplantc.de.server.service;

/**
 * Thrown by the proxy servlet when no service name was provided by the caller.
 *
 * @author Dennis Roberts
 */
public class NoServiceNameProvidedException extends RuntimeException {

    /**
     * The default constructor.
     */
    public NoServiceNameProvidedException() {
        super("no service name was provided");
    }
}
