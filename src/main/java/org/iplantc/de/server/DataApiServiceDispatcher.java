package org.iplantc.de.server;

import java.io.IOException;

import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;

/**
 * A dispatch service servlet that is used to dispatch requests to the data API.
 * 
 * @author Dennis Roberts
 */
public class DataApiServiceDispatcher extends BaseDEServiceDispatcher {
    /**
     * The version number used to identify serialized instances of this version of this class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Initializes the new service dispatcher.
     */
    public DataApiServiceDispatcher() {
        setUrlConnector(new DataApiUrlConnector());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addAdditionalParts(MultipartEntity entity) throws IOException {
        entity.addPart("user", new StringBody(getUsername()));
    }
}
