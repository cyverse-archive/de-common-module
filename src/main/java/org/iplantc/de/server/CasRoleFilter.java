package org.iplantc.de.server;

import java.io.IOException;
import java.net.HttpURLConnection;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jasig.cas.client.authentication.AttributePrincipal;

/**
 * An authentication filter that verifies that the user has thee role that is permitted to access a resource.  The
 * list of roles that are assigned to the user must be stored as a comma-delimited string in the <code>roles</code>
 * attribute of the principal.
 * 
 * @author Dennis Roberts
 */
public class CasRoleFilter implements Filter {

    /**
     * The role that is authorized to access a resource.
     */
    private String authorizedRole;

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        authorizedRole = filterConfig.getInitParameter("authorizedRole");
        if (authorizedRole == null) {
            throw new IllegalArgumentException("missing required init parameter: authorizedRole");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        if (!isUserAuthorized((HttpServletRequest) request)) {
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpResponse.sendError(HttpURLConnection.HTTP_FORBIDDEN);
        }
        else {
            chain.doFilter(request, response);
        }
    }

    /**
     * Determines if the user is authorized to access the resource.
     * 
     * @param request the servlet request.
     * @return true if the user is authorized to access the resource.
     */
    private boolean isUserAuthorized(HttpServletRequest request) {
        if (request.getUserPrincipal() instanceof AttributePrincipal) {
            AttributePrincipal principal = (AttributePrincipal) request.getUserPrincipal();
            String roles = (String) principal.getAttributes().get("roles");
            if (roles != null) {
                for (String role : roles.split(",")) {
                    if (role.equals(authorizedRole)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void destroy() {
    }
}
