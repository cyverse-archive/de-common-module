package org.iplantc.de.server;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.log4j.Logger;
import org.iplantc.saml.Saml2Exception;
import org.iplantc.security.SecurityConstants;
import org.opensaml.xml.io.MarshallingException;

/**
 * Used to establish connections to remote URLs over which authentication information will be passed. The
 * authentication information will be passed in a signed, encrypted SAML assertion stored in a custom
 * HTTP header.
 * 
 * @author Dennis Roberts
 */
public class AuthenticatedUrlConnector implements UrlConnector {
    private static final Logger LOGGER = Logger.getLogger(AuthenticatedUrlConnector.class);

    private static final String KEYSTORE_PATH = SecurityProperties.getKeystorePath();
    private static final String KEYSTORE_TYPE = SecurityProperties.getKeystoreType();
    private static final String KEYSTORE_PASSWORD = SecurityProperties.getKeystorePassword();
    private static final String SIGNING_KEY_ALIAS = SecurityProperties.getSigningKeyAlias();
    private static final String SIGNING_KEY_PASSWORD = SecurityProperties.getSigningKeyPassword();
    private static final String ENCRYPTING_KEY_ALIAS = SecurityProperties.getEncryptingKeyAlias();

    /**
     * The certificate used to sign SAML assertions.
     */
    private X509Certificate signingCertificate = null;

    /**
     * The key used to sign SAML assertions.
     */
    private PrivateKey signingKey = null;

    /**
     * The key used to encrypt SAML assertions.
     */
    private PublicKey encryptingKey = null;

    /**
     * {@inheritDoc}
     */
    @Override
    public HttpURLConnection getUrlConnection(HttpServletRequest request, String address)
            throws IOException {
        return isSecurityEnabled() ? getAuthenticatedUrlConnection(request, address)
                : getUnauthenticatedUrlConnection(address);
    }

    /**
     * Loads the signing and encrypting keys and certificates.
     * 
     * @throws IOException if the keystore can't be loaded.
     * @throws GeneralSecurityException if the keys and certificates can't be loaded.
     */
    private void loadKeys() throws IOException, GeneralSecurityException {
        LOGGER.debug("inside loadKeys");
        if (signingCertificate == null) {
            LOGGER.debug("laoding the keystore");
            URL keystoreUrl = getKeystoreUrl();
            KeyLoader keyLoader = new KeyLoader(keystoreUrl.getFile(), KEYSTORE_TYPE, KEYSTORE_PASSWORD);
            signingCertificate = keyLoader.getCertificate(SIGNING_KEY_ALIAS);
            signingKey = keyLoader.getPrivateKey(SIGNING_KEY_ALIAS, SIGNING_KEY_PASSWORD);
            encryptingKey = keyLoader.getCertificate(ENCRYPTING_KEY_ALIAS).getPublicKey();
        }
    }

    /**
     * Obtains a URL that can be used to load the keystore.
     * 
     * @return the URL.
     * @throws FileNotFoundException if the keystore can't be found.
     */
    private URL getKeystoreUrl() throws FileNotFoundException {
        URL keystoreUrl = Thread.currentThread().getContextClassLoader().getResource(KEYSTORE_PATH);
        if (keystoreUrl == null) {
            String msg = "unable to find the keystore: " + KEYSTORE_PATH;
            LOGGER.error(msg);
            throw new FileNotFoundException(msg);
        }
        return keystoreUrl;
    }

    /**
     * Obtains an unauthenticated URL connection.
     * 
     * @param address the address to connect to.
     * @return the URL connection.
     * @throws IOException if the connection can't be established.
     */
    private HttpURLConnection getUnauthenticatedUrlConnection(String address) throws IOException {
        return (HttpURLConnection)new URL(address).openConnection();
    }

    /**
     * Creates a URL connection with a SAML assertion in the custom header defined by
     * SecurityConstants.ASSERTION_HEADER.
     * 
     * @param request the servlet request.
     * @param address the address to connect to.
     * @return the new URL connection.
     * @throws IOException if the connection can't be established or the assertion can't be built.
     */
    private HttpURLConnection getAuthenticatedUrlConnection(HttpServletRequest request, String address)
            throws IOException {
        try {
            URL url = new URL(address);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestProperty(SecurityConstants.ASSERTION_HEADER,
                    buildSamlAssertion(request));
            return connection;
        } catch (Saml2Exception e) {
            String msg = "unable to build the SAML assertion";
            LOGGER.error(msg, e);
            throw new IOException(msg, e);
        } catch (MarshallingException e) {
            String msg = "unable to marshall the SAML assertion";
            LOGGER.error(msg, e);
            throw new IOException(msg, e);
        } catch (IOException e) {
            String msg = "unable to build assertion or set request property";
            LOGGER.error(msg, e);
            throw new IOException(msg, e);
        } catch (Throwable e) {
            String msg = "severe error";
            LOGGER.error(msg, e);
            throw new IOException(msg, e);
        }
    }

    /**
     * Builds, signs, encrypts and encodes a SAML assertion.
     * 
     * @param request the servlet request.
     * @return the SAML assertion.
     * @throws Saml2Exception if the assertion can't be built, signed or encrypted.
     * @throws MarshallingException if the assertion can't be converted to XML.
     * @throws IOException if any of the cryptography keys can't be loaded.
     */
    private String buildSamlAssertion(HttpServletRequest request) throws Saml2Exception,
            MarshallingException, IOException {
        LOGGER.debug("building the SAML assertion");
        try {
            loadKeys();
            return AssertionHelper.createEncodedAssertion(request, signingCertificate, signingKey,
                    encryptingKey);
        } catch (GeneralSecurityException e) {
            String msg = "unable to load the encryption keys";
            LOGGER.debug(msg, e);
            throw new IOException(msg, e);
        }
    }

    /**
     * Is security enabled?
     * 
     * @return
     */
    private boolean isSecurityEnabled() {
        return SecurityProperties.isWebSecurityEnabled();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HttpEntityEnclosingRequestBase getRequest(HttpServletRequest request, String address,
            String method) throws IOException {
        HttpEntityEnclosingRequestBase clientRequest = RequestFactory.buildRequest(method, address);
        if (isSecurityEnabled()) {
            addSamlAssertion(clientRequest, request);
        }
        return clientRequest;
    }

    /**
     * Adds the SAML assertion to the given client request object.
     * 
     * @param clientRequest the client request object.
     * @param request the servlet request.
     * @throws IOException if the connection can't be established.
     */
    private void addSamlAssertion(HttpEntityEnclosingRequestBase clientRequest,
            HttpServletRequest request) throws IOException {
        try {
            clientRequest.setHeader(SecurityConstants.ASSERTION_HEADER, buildSamlAssertion(request));
        } catch (Saml2Exception e) {
            String msg = "unable to build the SAML assertion";
            LOGGER.error(msg, e);
            throw new IOException(msg, e);
        } catch (MarshallingException e) {
            String msg = "unable to marshall the SAML assertion";
            LOGGER.error(msg, e);
            throw new IOException(msg, e);
        } catch (IOException e) {
            String msg = "unable to build assertion or set request property";
            LOGGER.error(msg, e);
            throw new IOException(msg, e);
        } catch (Throwable e) {
            String msg = "severe error";
            LOGGER.error(msg, e);
            throw new IOException(msg, e);
        }
    }
}
