package org.iplantc.de.server;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.iplantc.de.server.util.UrlUtils;

/**
 * An authentication filter that allows or denies access based on the presence or absence of a valid SAML
 * assertion that is known by the local Shibboleth service provider. Our login page is the only page that
 * is guarded by Shibboleth; when it is accessed it stores assertion URLs in a session property. If this
 * property is defined and at least one of the URLs stored in this property produces a SAML assertion
 * then the user is authenticated. Otherwise, the user is not authenticated.
 * 
 * Because this class is used to secure pages that are normally only called via AJAX, performing a
 * redirection at this point is inappropriate (redirecting an AJAX requests always results in the AJAX
 * connection throwing an exception). Because of this, we always block the connection and let the client
 * handle the redirection.
 * 
 * @see javax.servlet.Filter
 * 
 * @author Sriram Srinivasan
 * @author Dennis Roberts
 */
@SuppressWarnings("unused")
public class AuthFilter implements Filter {

    /**
     * The logger for error and informational messages.
     */
    private static Logger LOG = Logger.getLogger(AuthFilter.class);

    /**
     * Bogus session attributes to set when security is disabled.
     */
    @SuppressWarnings("serial")
    private static HashMap<String, String> FAKE_ATTRIBUTE_VALUES = new HashMap<String, String>() {
        {
            put(DESecurityConstants.LOCAL_SHIB_EPPN, "ipctest@iplantcollaborative.org");
            put(DESecurityConstants.LOCAL_SHIB_UID, "ipctest");
            put(DESecurityConstants.LOCAL_SHIB_MAIL, "ipctest@iplantcollaborative.org");
        }
    };

    /**
     * A certificate trust store that trusts all certificates. This trust store will be used exclusively
     * for connections that have been forced to point to the loopback interface, so we can be sure that
     * they're secure. Important note: be sure to reinstall the default trust manager after the
     * connection is established.
     */
    private static TrustManager[] trustAllCertificates = new TrustManager[] {new X509TrustManager() {
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        @Override
        public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
        }
    }};

    /**
     * A host name verifier that accepts all host names, no matter what's in the certificate. Important
     * note: be sure to reinstall the default host name verifier after the connection is established.
     */
    private static HostnameVerifier trustAllHostNames = new HostnameVerifier() {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };

    /**
     * The servlet filter configuration.
     */
    private FilterConfig config;

    /**
     * Called when the filter is being destroyed.
     */
    @Override
    public void destroy() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        if (SecurityProperties.isWebSecurityEnabled()) {
            if (!isUserAuthenticated(request)) {
                HttpServletResponse httpResponse = (HttpServletResponse)response;
                httpResponse.sendError(HttpURLConnection.HTTP_FORBIDDEN);
            } else {
                filterChain.doFilter(request, response);
            }
        } else {
            setFakeSessionProperties((HttpServletRequest)request);
            filterChain.doFilter(request, response);
        }
    }

    /**
     * Sets fake session properties for use when security is disabled.
     * 
     * @param request the HTTP servlet request.
     */
    private void setFakeSessionProperties(HttpServletRequest request) {
        HttpSession session = request.getSession(true);
        for (String name : FAKE_ATTRIBUTE_VALUES.keySet()) {
            String value = FAKE_ATTRIBUTE_VALUES.get(name);
            session.setAttribute(name, value);
        }
    }

    /**
     * Determines whether or not the user is authenticated. The user is authenticated if our Shibboleth
     * service provider knows of at least one session that the user is associated with.
     * 
     * @param request the servlet request.
     * @return true if the user is authenticated.
     * @throws ServletException if an unexpected error is encountered.
     */
    private boolean isUserAuthenticated(ServletRequest request) throws ServletException {
        boolean authenticated = false;
        try {
            HttpServletRequest httpRequest = (HttpServletRequest)request;
            List<String> assertionUrls = getAssertionUrls(httpRequest);
            authenticated = validAssertionAvailable(assertionUrls);
            if (LOG.isDebugEnabled()) {
                String msg = authenticated ? "user was authenticated" : "user was not authenticated";
                LOG.debug(msg);
            }
        } catch (Throwable t) {
            String msg = "unexpected exception";
            LOG.debug(msg, t);
            throw new ServletException(msg, t);
        }
        return authenticated;
    }

    /**
     * Initializes the filter.
     * 
     * @param fconfig a filter configuration object
     * @throws ServletException is thrown if initialization fails.
     */
    @Override
    public void init(FilterConfig fconfig) throws ServletException {
        this.config = fconfig;
    }

    /**
     * Obtains the list of assertion URLs from the request session.
     * 
     * @param request the request to fetch the URLs from.
     * @return the list of URLs.
     */
    @SuppressWarnings("unchecked")
    private List<String> getAssertionUrls(HttpServletRequest request) {
        String attributeName = DESecurityConstants.LOCAL_ASSERTION_URL_LIST;
        return (List<String>)request.getSession().getAttribute(attributeName);
    }

    /**
     * Determines whether or not a valid assertion is available for this session.
     * 
     * @param assertionUrls the list of URLs to use when attempting to fetch assertions.
     * @return true if at least one URL in the list references a valid assertion.
     */
    private boolean validAssertionAvailable(List<String> assertionUrls) {
        if (assertionUrls != null) {
            for (String urlString : assertionUrls) {
                if (isValidAssertionUrl(urlString)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Determines whether or not the given URL references a valid SAML assertion.
     * 
     * @param urlString the string representation of the URL to use when attempting to fetch the
     *            assertion.
     * @return true if the URL references a SAML assertion.
     */
    private boolean isValidAssertionUrl(String urlString) {
        InputStream in = null;
        try {
            URL url = buildAssertionRetrievalUrl(urlString);
            URLConnection connection = getTrustingUrlConnection(url);

            in = connection.getInputStream();
            return true;
        } catch (IOException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("valid session check failed for url " + urlString, e);
            }
        } finally {
            IOUtils.closeQuietly(in);
        }
        return false;
    }

    /**
     * Obtains a URL connection for which SSL certificates and host names are not verified. It is safe to
     * do so in this case because we're forcing the URL to point to the loopback interface, meaning that
     * someone would have had to infiltrate the discovery environment server to stage an attack using any
     * of these URLs. Note that this method changes the default trust store and host name verifier for
     * SSL connections. It is essential that the original trust store and host name verifier are restored
     * before this method exits.
     * 
     * @param url the URL to connect to.
     * @return The URL connection.
     * @throws IOException if the connection can't be established.
     */
    private URLConnection getTrustingUrlConnection(URL url) throws IOException {
        SSLSocketFactory originalSocketFactory = HttpsURLConnection.getDefaultSSLSocketFactory();
        HostnameVerifier originalHostnameVerifier = HttpsURLConnection.getDefaultHostnameVerifier();
        try {
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCertificates, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(trustAllHostNames);
            return url.openConnection();
        } catch (GeneralSecurityException e) {
            throw new IOException("unable to establish a trusting HTTPS connection", e);
        } finally {
            HttpsURLConnection.setDefaultSSLSocketFactory(originalSocketFactory);
            HttpsURLConnection.setDefaultHostnameVerifier(originalHostnameVerifier);
        }
    }

    /**
     * Determines the URL to use when fetching the SAML assertion. This URL is always based on the URL
     * that was provided to us by Shibboleth, but the host name may be replaced by the loopback IP
     * address if the DE is configured to do so. The use of the loopback interface is governed by the
     * property, org.iplantc.discoveryenvironment.useLoopbackInterface, in security.properties. The
     * loopback interface should be used when the Shibboleth service provider is configured to accept
     * SAML assertion requests only over the loopback interface.
     * 
     * @param originalUrlString URL that was provided to us by Shibboleth.
     * @return the updated URL.
     * @throws IOException if one of the URLs is invalid.
     */
    private URL buildAssertionRetrievalUrl(String originalUrlString) throws IOException {
        URL url = new URL(originalUrlString);
        if (SecurityProperties.useLoopbackInterface()) {
            url = UrlUtils.replaceHost(url, DESecurityConstants.ASSERTION_QUERY_HOST);
        }
        return url;
    }
}
