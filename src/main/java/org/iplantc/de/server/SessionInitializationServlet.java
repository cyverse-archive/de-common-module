package org.iplantc.de.server;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.LinkedList;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;

/**
 * A servlet used to initialize HTTP sessions for Shibboleth-secured web applications.
 * 
 * @author Dennis Roberts
 */
public class SessionInitializationServlet extends HttpServlet {
    /**
     * The version identifier applied to serialized versions of this class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The parameter name used to refer to the initial page.
     */
    private static final String initialPageParameterName = "org.iplantc.initial-page";

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            storeAssertionUrlsInSession(request);
            copyShibbolethAttributes(request);
            redirectUser(request, response);
        } catch (Exception e) {
            response.setContentType("text/plain");
            response.getWriter().println(e.getMessage());
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Determines the Shibboleth assertion count.
     * 
     * @param request the HTTP servlet request.
     * @return the assertion count.
     */
    private int getAssertionCount(HttpServletRequest request) {
        try {
            String count = (String)request.getAttribute(DESecurityConstants.REMOTE_SHIB_ASSERTION_COUNT);
            return Integer.parseInt(count);
        } catch (RuntimeException e) {
            throw new RuntimeException("inernal configuration error: no assertion count provided");
        }
    }

    /**
     * Stores the list of Shibboleth assertion URLs in the user's HTTP session.
     * 
     * @param request the HTTP servlet request.
     */
    public void storeAssertionUrlsInSession(HttpServletRequest request) {
        LinkedList<String> assertionUrls = new LinkedList<String>();
        DecimalFormat assertionNumberFormat = new DecimalFormat("00");
        for (int i = 1; i <= getAssertionCount(request); i++) {
            String assertionNumber = assertionNumberFormat.format(i);
            String headerName = DESecurityConstants.REMOTE_ASSERTION_URL_PREFIX + assertionNumber;
            assertionUrls.add(request.getHeader(headerName));
        }
        HttpSession session = request.getSession(true);
        session.setAttribute(DESecurityConstants.LOCAL_ASSERTION_URL_LIST, assertionUrls);
    }

    /**
     * Copies the Shibboleth attributes from the HTTP request to the user's session.
     * 
     * @param request the HTTP servlet request.
     */
    public void copyShibbolethAttributes(HttpServletRequest request) {
        for (String remoteName : DESecurityConstants.ATTRIBUTE_MAP_REMOTE_TO_LOCAL.keySet()) {
            String localName = DESecurityConstants.ATTRIBUTE_MAP_REMOTE_TO_LOCAL.get(remoteName);
            String value = (String)request.getAttribute(remoteName);
            if (value != null) {
                request.getSession(true).setAttribute(localName, value);
            }
        }
    }

    /**
     * Redirects the user to the initial page.
     * 
     * @param request the HTTP servlet request.
     * @param response the HTTP servlet response.
     * @throws IOException if an I/O error occurs.
     */
    public void redirectUser(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        ServletContext application = getServletConfig().getServletContext();
        StringBuffer urlBuffer = request.getRequestURL();
        int pos = StringUtils.lastOrdinalIndexOf(urlBuffer.toString(), "/", 2);
        urlBuffer.delete(pos + 1, urlBuffer.length());
        urlBuffer.append(application.getInitParameter(initialPageParameterName));
        response.sendRedirect(urlBuffer.toString());
    }
}
