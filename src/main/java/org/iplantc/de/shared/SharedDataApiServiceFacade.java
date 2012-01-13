package org.iplantc.de.shared;

/**
 * A singleton service that provides an asynchronous proxy to our internal data API.
 * 
 * @author Dennis Roberts
 */
public class SharedDataApiServiceFacade extends SharedServiceFacade {
    /**
     * the name of the service.
     */
    private static final String DE_SERVICE = "data-api-service";

    /**
     * The single instance of this class.
     */
    private static SharedDataApiServiceFacade instance;

    /**
     * Initializes a new service facade.
     */
    protected SharedDataApiServiceFacade() {
        super(DE_SERVICE);
    }

    /**
     * Gets the single instance of this class, creating an instance if one hasn't been created yet.
     * 
     * @return the single instance of this class.
     */
    public static SharedDataApiServiceFacade getInstance() {
        if (instance == null) {
            instance = new SharedDataApiServiceFacade();
        }
        return instance;
    }
}
