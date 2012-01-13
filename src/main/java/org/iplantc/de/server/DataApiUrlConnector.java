package org.iplantc.de.server;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;

/**
 * A URL connector that can be used to establish connections to our internal data API.
 * 
 * @author Dennis Roberts
 */
public class DataApiUrlConnector implements UrlConnector {
    /**
     * {@inheritDoc}
     */
    @Override
    public HttpURLConnection getUrlConnection(HttpServletRequest request, String address)
            throws IOException {
        HttpURLConnection connection = (HttpURLConnection)new URL(updateAddress(request, address)).openConnection();
        
        // Set Content-Type to application/json for all POST and PUT requests that are not multipart
        String method = request.getMethod();
        if ("POST".equalsIgnoreCase(method) || "PUT".equalsIgnoreCase(method)) { //$NON-NLS-1$ //$NON-NLS-2$
            String contentType = request.getHeader("Content-Type"); //$NON-NLS-1$
            if (contentType == null || !contentType.toLowerCase().contains("multipart")) { //$NON-NLS-1$
                connection.setRequestProperty("Content-Type", "application/json"); //$NON-NLS-1$ //$NON-NLS-2$
            }
        };
        
        return connection;
    }

    /**
     * Updates the given address by adding the username to the query string.
     * 
     * @param request the servlet request.
     * @param address the original address.
     * @return the updated address.
     * @throws IOException if the address can't be updated.
     */
    protected String updateAddress(HttpServletRequest request, String address) throws IOException {
        String encodedUsername = URLEncoder.encode(getUsername(request), "UTF-8"); //$NON-NLS-1$
        URL url = new URL(address);
        validateAnchor(url);
        String delim = StringUtils.isEmpty(url.getQuery()) ? "?" : "&"; //$NON-NLS-1$ //$NON-NLS-2$
        String updatedAddress = address + delim + "user=" + encodedUsername; //$NON-NLS-1$
        return updatedAddress;
    }

    /**
     * Verifies that the anchor portion of the URL is correct. For the time being, this means that the
     * URL has no anchor.
     * 
     * @param url the URL to validate.
     * @throws IOException if the anchor is invalid.
     */
    private void validateAnchor(URL url) throws IOException {
        if (!StringUtils.isEmpty(url.getRef())) {
            throw new IOException("URLs with anchors are not currently supported"); //$NON-NLS-1$
        }
    }

    /**
     * Gets the name of the authenticated user.
     * 
     * @param request the HTTP servlet request.
     * @return the username.
     */
    private String getUsername(HttpServletRequest request) throws IOException {
        Object username = request.getSession().getAttribute(DESecurityConstants.LOCAL_SHIB_UID);
        if (username == null) {
            throw new IOException("user is not authenticated"); //$NON-NLS-1$
        }
        return username.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HttpEntityEnclosingRequestBase getRequest(HttpServletRequest request, String address,
            String method) throws IOException {
        return RequestFactory.buildRequest(method, updateAddress(request, address));
    }
}
