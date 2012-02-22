package org.iplantc.de.server;

import javax.servlet.http.HttpServletRequest;
import org.jasig.cas.client.authentication.AttributePrincipal;
import org.springframework.security.cas.authentication.CasAuthenticationToken;

/**
 * Utility methods for dealing with CAS.
 * 
 * @author Dennis Roberts
 */
public class CasUtils {

    /**
     * Prevent instantiation.
     */
    private CasUtils() {
    }
    
    /**
     * Obtains an AttributePrincipal object from an HTTP servlet request.  The user must have been authenticated
     * either directly via the Java CAS client or indirectly via the Java CAS client and Spring Security.
     * 
     * @param req the HTTP servlet request.
     * @return the attribute principal.
     */
    public static AttributePrincipal attributePrincipalFromServletRequest(HttpServletRequest req) {
        Object authToken = req.getUserPrincipal();
        if (authToken instanceof AttributePrincipal) {
            return (AttributePrincipal) authToken;
        }
        else if (authToken instanceof CasAuthenticationToken) {
            return (AttributePrincipal) ((CasAuthenticationToken) authToken).getAssertion().getPrincipal();
        }
        throw new IllegalStateException("the user doesn't seem to have been authenticated via CAS.");
    }
}
