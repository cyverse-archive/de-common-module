package org.iplantc.de.shared;

/**
 * A singleton service that provides an asynchronous proxy to unsecured data services.
 */
public class SharedUnsecuredServiceFacade extends BaseSharedServiceFacade {
    public static final String DE_SERVICE = "unsecureddeservice";

    private static SharedUnsecuredServiceFacade srvFacade;

    /**
     * Creates a new unsecured service facade.
     */
    private SharedUnsecuredServiceFacade() {
        super(DE_SERVICE);
    }

    /**
     * Gets the single instance of the unsecured shared service facade.
     * 
     * @return the instance.
     */
    public static SharedUnsecuredServiceFacade getInstance() {
        if (srvFacade == null) {
            srvFacade = new SharedUnsecuredServiceFacade();
        }

        return srvFacade;
    }
}
