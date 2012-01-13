package org.iplantc.de.shared;

/**
 * A singleton service that provides an asynchronous proxy to services that accept JSON documents
 * containing the username in a "user" attribute.
 * 
 * @author Dennis Roberts
 */
public class SharedAuthenticatedJsonServiceFacade extends SharedServiceFacade {
    /**
     * the name of the service.
     */
    private static final String DE_SERVICE = "authenticated-json-service";

    /**
     * The single instance of this class.
     */
    private static SharedAuthenticatedJsonServiceFacade instance;

    /**
     * Initializes a new service facade.
     */
    protected SharedAuthenticatedJsonServiceFacade() {
        super(DE_SERVICE);
    }

    /**
     * Gets the single instance of this class, creating an instance if one hasn't been created yet.
     * 
     * @return the single instance of this class.
     */
    public static SharedAuthenticatedJsonServiceFacade getInstance() {
        if (instance == null) {
            instance = new SharedAuthenticatedJsonServiceFacade();
        }
        return instance;
    }
}
