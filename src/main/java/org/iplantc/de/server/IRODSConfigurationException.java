package org.iplantc.de.server;

public class IRODSConfigurationException extends Exception {
    private static final long serialVersionUID = 1L;
    private static final String MESSAGE = "IRODS configuration error"; //$NON-NLS-1$

    public IRODSConfigurationException() {
        super(MESSAGE);
    }

}
