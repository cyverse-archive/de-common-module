package org.iplantc.de.server;

import javax.servlet.ServletContext;
import org.iplantc.de.shared.services.BaseServiceCallWrapper;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * Resolves service calls from the client usage of a service key to the actual service address, or URL.
 *
 */
public abstract class ServiceCallResolver {

    /**
     * Resolves the wrapper information to a service address, or URL.
     *
     * @param wrapper service call wrapper containing metadata for a call.
     * @return a string representing a valid URL.
     */
    public abstract String resolveAddress(BaseServiceCallWrapper wrapper);

    /**
     * Gets the service call resolver for a servlet context.
     *
     * @param context the servlet context.
     * @return the service call resolver.
     * @throws IllegalStateException if the service call resolver can't be found.
     */
    public static ServiceCallResolver getServiceCallResolver(ServletContext context) {
        WebApplicationContext appContext = WebApplicationContextUtils.getRequiredWebApplicationContext(context);
        ServiceCallResolver resolver = appContext.getBean(ServiceCallResolver.class);
        if (resolver == null) {
            throw new IllegalStateException("no service call resolver bean defined");
        }
        return resolver;
    }
}
