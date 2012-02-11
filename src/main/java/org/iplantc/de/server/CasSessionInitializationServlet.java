package org.iplantc.de.server;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang.StringUtils;
import org.jasig.cas.client.authentication.AttributePrincipal;

/**
 * A servlet used to initialize HTTP sessions for CAS-secured web applications.
 *
 * @author Dennis Roberts
 */
public class CasSessionInitializationServlet extends HttpServlet {

    /**
     * The parameter name used to refer to the initial page.
     */
    private static final String INITIAL_PAGE_PARAMETER_NAME = "org.iplantc.initial-page";

    /**
     * The name of the HTTP session attribute used to store the CAS principal.
     */
    private static final String CAS_PRINCIPAL_ATTR = "casPrincipal";

    /**
     * The domain name to use for the EPPN.
     * 
     * TODO: this will have to go away when we federate.
     */
    private static final String EPPN_DOMAIN_NAME = "@iplantcollaborative.org";

    /**
     * The names of the HTTP session attribute used to store the remote username.
     */
    private static final String[] CAS_USERNAME_ATTR = {"username", DESecurityConstants.LOCAL_SHIB_UID};

    /**
     * A map that translates CAS user attribute names to the names used by the DE.
     */
    private static final Map<String, String> ATTR_NAME_MAP = new HashMap<String, String>();

    static {
        ATTR_NAME_MAP.put("email", DESecurityConstants.LOCAL_SHIB_MAIL);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            copyCasAttributes(req);
            redirectUser(req, resp);
        }
        catch (Exception e) {
            resp.setContentType("text/plain");
            resp.getWriter().println(e.getMessage());
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Copies the attributes from the CAS Attribute Principal into the user's session. some attributes have an alternate
     * name for backward compatibility. Those attributes will be stored under both the original name and the alternate
     * name.
     *
     * @param req the HTTP servlet request.
     */
    private void copyCasAttributes(HttpServletRequest req) {
        HttpSession session = req.getSession();
        AttributePrincipal principal = (AttributePrincipal) req.getUserPrincipal();
        Map<String, Object> attrs = principal.getAttributes();
        session.setAttribute(CAS_PRINCIPAL_ATTR, req.getRemoteUser());
        session.setAttribute(DESecurityConstants.LOCAL_SHIB_EPPN, req.getRemoteUser() + EPPN_DOMAIN_NAME);
        session.setAttribute(CAS_PRINCIPAL_ATTR, principal);
        for (String name : attrs.keySet()) {
            Object value = attrs.get(name);
            session.setAttribute(name, value);
            String alternateName = ATTR_NAME_MAP.get(name);
            if (alternateName != null) {
                session.setAttribute(alternateName, value);
            }
        }
    }

    /**
     * Redirects the user to the web application's initial page.
     *
     * @param req the HTTP servlet request.
     * @param resp the HTTP servlet response.
     * @throws IOException if the redirect can't be sent to the client.
     */
    private void redirectUser(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        ServletContext application = getServletConfig().getServletContext();
        StringBuffer urlBuffer = req.getRequestURL();
        int pos = StringUtils.lastOrdinalIndexOf(urlBuffer.toString(), "/", 2);
        urlBuffer.delete(pos + 1, urlBuffer.length());
        urlBuffer.append(application.getInitParameter(INITIAL_PAGE_PARAMETER_NAME));
        resp.sendRedirect(urlBuffer.toString());
    }
}
