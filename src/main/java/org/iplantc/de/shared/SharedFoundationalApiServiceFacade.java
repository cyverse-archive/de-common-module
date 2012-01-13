package org.iplantc.de.shared;

/**
 * A singleton service that provides an asynchronous proxy to Foundational API services.
 * 
 * @author Dennis Roberts
 */
public class SharedFoundationalApiServiceFacade extends BaseSharedServiceFacade {
    /**
     * The name used to identify calls to the foundational API services.
     */
    public static final String DE_SERVICE = "foundationalapi";

    /**
     * The single instance of this class.
     */
    private static SharedFoundationalApiServiceFacade srvFacade;

    /**
     * Creates a new unsecured service facade.
     */
    private SharedFoundationalApiServiceFacade() {
        super(DE_SERVICE);
    }

    /**
     * Gets the single instance of the unsecured shared service facade.
     * 
     * @return the instance.
     */
    public static SharedFoundationalApiServiceFacade getInstance() {
        if (srvFacade == null) {
            srvFacade = new SharedFoundationalApiServiceFacade();
        }

        return srvFacade;
    }
}
