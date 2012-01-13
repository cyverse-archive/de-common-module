package org.iplantc.de.server;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;

/**
 * Used to establish connections to URLs over which authentication information will not be sent.
 * 
 * @author Dennis Roberts
 */
public class UnauthenticatedUrlConnector implements UrlConnector {
    /**
     * {@inheritDoc}
     */
    @Override
    public HttpURLConnection getUrlConnection(HttpServletRequest request, String address)
            throws IOException {
        return (HttpURLConnection)new URL(address).openConnection();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HttpEntityEnclosingRequestBase getRequest(HttpServletRequest request, String address,
            String method) throws IOException {
        return RequestFactory.buildRequest(method, address);
    }
}
