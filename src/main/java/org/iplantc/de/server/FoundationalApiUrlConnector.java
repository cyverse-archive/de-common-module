package org.iplantc.de.server;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.GeneralSecurityException;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.iplantc.security.SecurityConstants;

/**
 * Used to establish connections to the foundational API. The foundational API requires us to pass an
 * encrypted token containing the username and a timestamp in the Authorization header. This header is
 * encrypted using the private key from a file in OpenSSL's PEM format. We do it that way because adding
 * a new public key to the foundational API every time the DE is deployed would be a pain and it will be
 * easier to simply place the key file in the appropriate directory than to have to maintain multiple
 * keystores or import a PEM file into the main DE keystore. The path to the PEM file is specified by the
 * configuration property, <code>org.iplantc.discoveryenvironment.foundationalApiKeyFile</code>. The path
 * to the file should be relative to one of the paths in the classpath. At this time the PEM file
 * containing the key is not expected to be encrypted. If it becomes necessary to encrypt the file at a
 * later time then support for a password encrypted file can be added fairly easily.
 * <p>
 * When security is disabled, the username can't be obtained from the attributes of the incoming HTTP
 * request, so this class resorts to using a default username for testing. The default username is
 * obtained from the configuration property,
 * <code>org.iplantc.discoveryenvironment.defaultUsername</code>. If this configuration property is not
 * defined then the username, <code>ipctest</code>, is used.
 * 
 * @author Dennis Roberts
 */
public class FoundationalApiUrlConnector implements UrlConnector {
    /**
     * The path to the file containing the certificate.
     */
    private static final String KEY_FILE = SecurityProperties.getFoundationalApiKeyFile();

    /**
     * The username to use when security is disabled and the default username is not specified.
     */
    private static final String DEFAULT_DEFAULT_USERNAME = "ipctest";

    /**
     * Used to generate the tokens used to authenticate to the foundational API.
     */
    private FoundationalApiTokenGenerator tokenGenerator;

    /**
     * Initializes a new foundational API URL connector.
     * 
     * @throws IOException if the private key can't be loaded.
     */
    public FoundationalApiUrlConnector() throws IOException {
        tokenGenerator = new FoundationalApiTokenGenerator(KEY_FILE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HttpURLConnection getUrlConnection(HttpServletRequest request, String address)
            throws IOException {
        try {
            URL url = new URL(address);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            String token = buildRequestToken(request);
            connection.setRequestProperty(SecurityConstants.FOUNDATIONAL_API_TOKEN_HEADER, "Basic "
                    + token);
            return connection;
        } catch (Throwable t) {
            throw new IOException("SEVERE ERROR", t);
        }
    }

    /**
     * Builds the encrypted token to place in the outgoing HTTP request.
     * 
     * @param request the incoming HTTP request.
     * @return the encrypted token.
     * @throws GeneralSecurityException if the token can't be encrypted.
     */
    private String buildRequestToken(HttpServletRequest request) throws GeneralSecurityException {
        return tokenGenerator.generateToken(getUsername(request));
    }

    /**
     * Gets the username to use for the authorization token. If security is enabled then the username is
     * obtained from an HTTP request attribute. Otherwise, a default username is used.
     * 
     * @param request the incoming HTTP request.
     * @return the username.
     */
    private String getUsername(HttpServletRequest request) {
        String username = null;
        if (SecurityProperties.isWebSecurityEnabled()) {
            username = getRequestAttribute(request, DESecurityConstants.LOCAL_SHIB_UID);
        } else {
            username = getDefaultUsername();
        }
        return username;
    }

    /**
     * Gets the default username to use when security is disabled.
     * 
     * @return the default username.
     */
    private String getDefaultUsername() {
        String username = SecurityProperties.getDefaultUsername();
        if (username == null) {
            username = DEFAULT_DEFAULT_USERNAME;
        }
        return username;
    }

    /**
     * Extracts an attribute from the given HTTP request. If the attribute is missing, an
     * IllegalArgumentException is thrown.
     * 
     * @param request the HTTP request to extract the attribute from.
     * @param name the name of the attribute.
     * @return the attribute value as a string.
     */
    private String getRequestAttribute(HttpServletRequest request, String name) {
        Object attr = request.getAttribute(name);
        if (attr == null) {
            throw new IllegalArgumentException("HTTP request missing required attribute");
        }
        return attr.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HttpEntityEnclosingRequestBase getRequest(HttpServletRequest request, String address,
            String method) throws IOException {
        try {
            HttpEntityEnclosingRequestBase clientRequest = RequestFactory.buildRequest(method, address);
            String token = buildRequestToken(request);
            clientRequest.addHeader(SecurityConstants.FOUNDATIONAL_API_TOKEN_HEADER, "Basic " + token);
            return clientRequest;
        } catch (Throwable t) {
            throw new IOException("SEVERE ERROR", t);
        }
    }
}
