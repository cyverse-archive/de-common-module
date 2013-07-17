package org.iplantc.de.server;

import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;

/**
 * Performs actions common to most URL connectors.
 */
public abstract class BaseUrlConnector implements UrlConnector {

    /**
     * Adds a query string parameter to a URI.
     *
     * @param uriString the string representation URI to update.
     * @param name the name of query string parameter.
     * @param value the value of the query string parameter.
     * @return the string representation of the updated URI.
     * @throws IOException if a URI representation is invalid or an encoding error occurs.
     */
    protected String addQueryParam(String uriString, String name, String value) throws IOException {
        String param = name + "=" + URLEncoder.encode(value, "UTF-8");
        try {
            URI uri = new URI(uriString);
            String query = uri.getQuery();
            query = StringUtils.isEmpty(query) ? param : query + "&" + param;
            return new URI(uri.getScheme(), uri.getUserInfo(), uri.getHost(), uri.getPort(), uri.getPath(), query,
                    uri.getFragment()).toString();
        }
        catch (URISyntaxException e) {
            throw new IOException("unable to add query string parameter " + param, e);
        }
    }

    /**
     * Adds the remote IP address to the query string of a URI.
     *
     * @param uriString the string representation of the URI to update.
     * @param request the HTTP servlet request object.
     * @return the string representation of the updated URI.
     * @throws URISyntaxException if there's a syntax error in the updated URI.
     * @throws IOException if a URI representation is invalid or an encoding error occurs.
     */
    protected String addIpAddress(String uriString, HttpServletRequest request) throws IOException {
        return addQueryParam(uriString, "ip-address", request.getRemoteAddr());
    }
}
