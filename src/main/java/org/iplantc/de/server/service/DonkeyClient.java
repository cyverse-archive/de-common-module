package org.iplantc.de.server.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.Properties;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.iplantc.de.server.CasUrlConnector;
import org.iplantc.de.server.UnauthenticatedUrlConnector;
import org.iplantc.de.server.UrlConnector;

/**
 * A client for iPlant's Donkey service.
 *
 * @author Dennis Roberts
 */
public class DonkeyClient {

    /**
     * Used to log debugging messages.
     */
    private static final Logger LOG = Logger.getLogger(DonkeyClient.class);

    /**
     * The name of the property containing the base URL for secured services.
     */
    private static final String SECURED_BASE_PROP = "org.iplantc.discoveryenvironment.muleServiceBaseUrl";

    /**
     * The name of the property containing the base URL for unsecured services.
     */
    private static final String UNSECURED_BASE_PROP = "org.iplantc.discoveryenvironment.unprotectedMuleServiceBaseUrl";

    /**
     * The connector used to obtain connections to Donkey.
     */
    private final UrlConnector urlConnector;

    /**
     * The base URL to use when connecting to Donkey.
     */
    private final String baseUrl;

    /**
     * @param urlConnector the connector used to obtain connections to Donkey.
     * @param baseUrl the base URL to use when connecting to Donkey.
     */
    private DonkeyClient(UrlConnector urlConnector, String baseUrl) {
        this.urlConnector = urlConnector;
        this.baseUrl = baseUrl;
    }

    /**
     * Validates a base URL.
     *
     * @param baseUrl the base URL to validate.
     */
    private static void validateBaseUrl(String baseUrl) {
        if (baseUrl == null) {
            throw new NullPointerException("the base URL may not be null");
        }
        if (StringUtils.isEmpty(baseUrl)) {
            throw new IllegalArgumentException("the base URL may not be empty");
        }
    }

    /**
     * Creates a Donkey client using the provided URL connector and the base URL from a set of properties.
     *
     * @param urlConnector the connector used to obtain connections to Donkey.
     * @param baseUrlProp the name of the property containing the base URL.
     * @param props the properties.
     * @return the new Donkey client.
     */
    private static DonkeyClient createDonkeyClient(UrlConnector urlConnector, String baseUrlProp, Properties props) {
        String baseUrl = props.getProperty(baseUrlProp);
        if (StringUtils.isEmpty(baseUrl)) {
            throw new IllegalStateException("base URL property, " + baseUrlProp + ", is empty or undefined");
        }
        return new DonkeyClient(urlConnector, baseUrl);
    }

    /**
     * Creates an unsecured Donkey client using the base URL from a set of properties.
     *
     * @param props the properties.
     * @return the new Donkey client.
     */
    public static DonkeyClient unsecuredClient(Properties props) {
        return createDonkeyClient(new UnauthenticatedUrlConnector(), UNSECURED_BASE_PROP, props);
    }

    /**
     * Creates an unsecured Donkey client using an explicitly specified base URL.
     *
     * @param baseUrl the base URL.
     * @return the Donkey client.
     */
    public static DonkeyClient unsecuredClient(String baseUrl) {
        validateBaseUrl(baseUrl);
        return new DonkeyClient(new UnauthenticatedUrlConnector(), baseUrl);
    }

    /**
     * Creates a secured Donkey client using the base URL from a set of properties.
     *
     * @param props the properties.
     * @return the new Donkey client.
     */
    public static DonkeyClient securedClient(Properties props) {
        return createDonkeyClient(new CasUrlConnector(), SECURED_BASE_PROP, props);
    }

    /**
     * Creates a secured Donkey client using an explicitly specified base URL.
     *
     * @param baseUrl the base URL.
     * @return the Donkey client.
     */
    public static DonkeyClient securedClient(String baseUrl) {
        validateBaseUrl(baseUrl);
        return new DonkeyClient(new CasUrlConnector(), baseUrl);
    }

    /**
     * Builds a full URL to a Donkey service.
     *
     * @param servicePath the relative path to the service.
     * @return the full URL.
     */
    private String buildUrl(String servicePath) {
        return baseUrl.replaceAll("/$", "") + "/" + servicePath.replaceAll("^/", "");
    }

    /**
     * Sends a request body over a URL connection.
     *
     * @param conn the URL connection.
     * @param body the request body.
     * @throws IOException
     */
    private void sendRequestBody(HttpURLConnection conn, String body) throws IOException {
        OutputStream out = conn.getOutputStream();
        try {
            IOUtils.write(body, out);
            out.flush();
        }
        finally {
            IOUtils.closeQuietly(out);
        }
    }

    /**
     * Reads the response body over a URL connection.
     *
     * @param conn the URL connection.
     * @return the response body.
     * @throws IOException
     */
    private String readResponseBody(HttpURLConnection conn) throws IOException {
        InputStream in = conn.getInputStream();
        try {
            String result = IOUtils.toString(in);
            LOG.debug("response: " + result);
            return result;
        }
        finally {
            IOUtils.closeQuietly(in);
        }
    }

    /**
     * Sends an HTTP request without a body.
     *
     * @param request the incoming HTTP servlet request (required by some URL connectors for authentication).
     * @param method the HTTP method.
     * @param servicePath the relative path to the service.
     * @return the response body.
     * @throws IOException
     */
    private String sendRequest(HttpServletRequest request, String method, String servicePath) throws IOException {
        String url = buildUrl(servicePath);
        LOG.debug("sending an HTTP " + method + " request to " + url);
        HttpURLConnection conn = urlConnector.getUrlConnection(request, url);
        conn.setRequestMethod(method);
        return readResponseBody(conn);
    }

    /**
     * Sends an HTTP request with a request body.
     *
     * @param request the incoming HTTP servlet request (required by some URL connectors for authentication).
     * @param method the HTTP method.
     * @param servicePath the relative path to the service.
     * @param body the request body.
     * @return the response body.
     * @throws IOException
     */
    private String sendRequest(HttpServletRequest request, String method, String servicePath, String body)
            throws IOException {
        String url = buildUrl(servicePath);
        LOG.debug("sending an HTTP " + method + " request to " + url);
        LOG.debug("request body: " + body);
        HttpURLConnection conn = urlConnector.getUrlConnection(request, url);
        conn.setRequestMethod(method);
        conn.setDoOutput(true);
        sendRequestBody(conn, body);
        return readResponseBody(conn);
    }

    /**
     * Sends an HTTP PUT request to a Donkey service.
     *
     * @param request the incoming HTTP servlet request (required by some URL connectors for authentication).
     * @param servicePath the relative path to the service.
     * @param body the request body.
     * @return the response body.
     * @throws IOException
     */
    public String put(HttpServletRequest request, String servicePath, String body) throws IOException {
        return sendRequest(request, "PUT", servicePath, body);
    }

    /**
     * Sends an HTTP POST request to a Donkey service.
     *
     * @param request the incoming HTTP servlet request (required by some URL connectors for authentication).
     * @param servicePath the relative path to the service.
     * @param body the request body.
     * @return the response body.
     * @throws IOException
     */
    public String post(HttpServletRequest request, String servicePath, String body) throws IOException {
        return sendRequest(request, "POST", servicePath, body);
    }

    /**
     * Sends an HTTP GET request to a Donkey service.
     *
     * @param request the incoming HTTP servlet request (required by some URL connectors for authentication).
     * @param servicePath the relative path to the service.
     * @return the response body.
     * @throws IOException
     */
    public String get(HttpServletRequest request, String servicePath) throws IOException {
        return sendRequest(request, "GET", servicePath);
    }

    /**
     * Sends an HTTP GET request to a Donkey service.
     *
     * @param request the incoming HTTP servlet request (required by some URL connectors for authentication).
     * @param servicePath the relative path to the service.
     * @return the response body.
     * @throws IOException
     */
    public String delete(HttpServletRequest request, String servicePath) throws IOException {
        return sendRequest(request, "DELETE", servicePath);
    }
}
