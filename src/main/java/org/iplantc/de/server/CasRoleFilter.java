package org.iplantc.de.server;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jasig.cas.client.authentication.AttributePrincipal;

/**
 * An authentication filter that verifies that the user has thee role that is permitted to access a resource.  The
 * list of roles that are assigned to the user must be stored as a comma-delimited string with optional whitespace
 * in the format @{code [value1, value2, ..., valuen]}.  This filter requires two initialization parameters:
 * @{code authorizedRole} and @{code roleAttributeName}.  The @{code authorizedRole} parameter contains the name of the
 * role that is authorized to access the resource.  The @{code roleAttributeName} parameter contains the name of the
 * attribute that is used to transfer the list of roles that the user fills.
 * 
 * @author Dennis Roberts
 */
public class CasRoleFilter implements Filter {

    /**
     * Used to log debugging messages.
     */
    private static final Logger LOG = Logger.getLogger(CasRoleFilter.class);

    /**
     * The pattern used to extract list contents from the string representation of a list.
     */
    private static final Pattern LIST_CONTENTS_PATTERN = Pattern.compile("\\A\\[([^\\]]*)\\]\\z");

    /**
     * The pattern used to separate list elements in the string representation of a list.
     */
    private static final Pattern LIST_DELIMITER_PATTERN = Pattern.compile(",\\s*");

    /**
     * The role that is authorized to access a resource.
     */
    private String authorizedRole;

    /**
     * The name of the attribute containing the roles.  This attribute must contain a string array.
     */
    private String roleAttributeName;

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        authorizedRole = getRequiredInitParameter(filterConfig, "authorizedRole");
        roleAttributeName = getRequiredInitParameter(filterConfig, "roleAttributeName");
    }

    /**
     * Gets a required initialization parameter from the filter configuration, throwing an exception if the parameter
     * is undefined or empty.
     * 
     * @param filterConfig the filter configuration.
     * @param name the initialization parameter name.
     * @return the initialization parameter value.
     * @throws IllegalArgumentException if the parameter is undefined or empty.
     */
    private String getRequiredInitParameter(FilterConfig filterConfig, String name) {
        String value = filterConfig.getInitParameter(name);
        if (StringUtils.isEmpty(value)) {
            throw new IllegalArgumentException("missing required init parameter: " + name);
        }
        return value;
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
            for (String role : getUserRoles(principal)) {
                if (role.equals(authorizedRole)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Extracts the roles that the user fills from an {@link AttributePrincipal}.
     * 
     * @param principal the principal.
     * @return the list of roles that the user fills.
     */
    private List<String> getUserRoles(AttributePrincipal principal) {
        final List<String> roles = new ArrayList<String>();
        final String rolesContents = extractListContents((String) principal.getAttributes().get(roleAttributeName));
        if (!StringUtils.isEmpty(rolesContents)) {
            LOG.debug("Roles obtained from principal: " + rolesContents);
            for (String role : LIST_DELIMITER_PATTERN.split(rolesContents)) {
                if (!StringUtils.isEmpty(role)) {
                    roles.add(role);
                }
            }
        }
        else {
            LOG.debug("no roles received in principal: roleAttributeName = " + roleAttributeName);
        }
        return roles;
    }

    /**
     * Extracts the list contents string from a string representation of a list.
     * 
     * @param listString the string representation of the list.
     * @return the list contents or null 
     */
    public String extractListContents(String listString) {
        String result = null;
        if (listString != null) {
            Matcher m = LIST_CONTENTS_PATTERN.matcher(listString);
            result = m.find() ? m.group(1) : null;
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void destroy() {
    }
}
