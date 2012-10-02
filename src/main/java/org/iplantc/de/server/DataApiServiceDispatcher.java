package org.iplantc.de.server;

import java.io.IOException;
import java.net.HttpURLConnection;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;

/**
 * A dispatch service servlet that is used to dispatch requests to the data API.
 *
 * @author Dennis Roberts
 */
@SuppressWarnings("nls")
public class DataApiServiceDispatcher extends BaseDEServiceDispatcher {
    /**
     * The version number used to identify serialized instances of this version of this class.
     */
    private static final long serialVersionUID = 1L;

    private boolean forceJsonContentType = false;

    /**
     * The default constructor.
     */
    public DataApiServiceDispatcher() {
        super();
        setUrlConnector(new DataApiUrlConnector());
    }

    /**
     * @param serviceResolver resolves aliased URLs.
     */
    public DataApiServiceDispatcher(ServiceCallResolver serviceResolver) {
        super(serviceResolver);
        setUrlConnector(new DataApiUrlConnector());
    }

    /**
     * Sets an optional flag that will force the HttpURLConnection returned by getUrlConnection to set
     * its "Content-Type" header to "application/json", even if the request object given in setRequest
     * has a multipart Content-Type header.
     *
     * @param forceJson
     */
    public void setForceJsonContentType(boolean forceJson) {
        forceJsonContentType = forceJson;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addAdditionalParts(MultipartEntity entity) throws IOException {
        entity.addPart("user", new StringBody(getUsername()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected HttpURLConnection getUrlConnection(String address) throws IOException {
        HttpURLConnection connection = super.getUrlConnection(address);

        if (forceJsonContentType && connection != null) {
            connection.setRequestProperty("Content-Type", "application/json");
        }

        return connection;
    }
}
