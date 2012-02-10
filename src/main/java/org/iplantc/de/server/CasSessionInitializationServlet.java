package org.iplantc.de.server;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A servlet used to initialize HTTP sessions for CAS-secured web applications.
 *
 * @author Dennis Roberts
 */
public class CasSessionInitializationServlet extends HttpServlet {

    /**
     * The parameter name used to refer to the initial page.
     */
    private static final String initialPageParameterName = "org.iplantc.initial-page";

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
     * Copies the attributes from the CAS Attribute Principal into the user's session.
     * 
     * @param req the HTTP servlet request.
     */
    private void copyCasAttributes(HttpServletRequest req) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Redirects the user to the web application's initial page.
     * 
     * @param req the HTTP servlet request.
     * @param resp the HTTP servlet response.
     */
    private void redirectUser(HttpServletRequest req, HttpServletResponse resp) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
