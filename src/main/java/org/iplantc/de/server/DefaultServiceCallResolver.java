package org.iplantc.de.server;

import java.util.Properties;
import java.util.TreeSet;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.iplantc.clavin.spring.ConfigAliasResolver;
import org.iplantc.de.shared.services.BaseServiceCallWrapper;

public class DefaultServiceCallResolver extends ServiceCallResolver {
    private static final Logger LOG = Logger.getLogger(DefaultServiceCallResolver.class);
    private static final String PREFIX_KEY = "prefix";

    private Properties appProperties;
    private String prefix;

    public DefaultServiceCallResolver(ConfigAliasResolver configResolver) {
        appProperties = configResolver.getRequiredAliasedConfig("webapp");
        setPrefix();
        validatePrefix();
    }

    public DefaultServiceCallResolver(Properties prop) {
        appProperties = prop;
        setPrefix();
        validatePrefix();
    }

    private void validatePrefix() {
        if (StringUtils.isEmpty(prefix)) {
            throw new IllegalArgumentException("Properties argument must contain a property defining "
                    + "the prefix for service keys: " + PREFIX_KEY);
        }
    }

    private void setPrefix() {
        prefix = appProperties.getProperty(PREFIX_KEY);
    }

    /**
     * Resolves a service call to a valid service address.
     *
     * This implementation determines if the wrapper contains a "service key" instead of the actual
     * service address. If so, the service key is resolved with the properties. Otherwise, the wrapper's
     * address is passed through without change.
     *
     * @param wrapper service call wrapper containing metadata for a call.
     * @return a string representing a valid URL.
     * @throws UnresolvableServiceNameException if a service name that couldn't be resolved is passed to the resolver.
     */
    @Override
    public String resolveAddress(BaseServiceCallWrapper wrapper) {
        String address = wrapper.getAddress();
        if (address.startsWith(prefix)) {
            String[] components = address.split("\\?", 2);
            components[0] = resolveAddress(components[0]);
            address = StringUtils.join(components, "?");
        }
        return address;
    }

    /**
     * Resolves a call to a named service.
     *
     * @param serviceName the name of the service.
     * @return a string representing a valid URL.
     * @throws UnresolvableServiceNameException if the service name can't be resolved.
     */
    @Override
    public String resolveAddress(String serviceName) {
        String result = appProperties.getProperty(serviceName);
        if (result == null) {
            LOG.error("unknown service name: " + serviceName);
            if (LOG.isDebugEnabled()) {
                for (String prop : new TreeSet<String>(appProperties.stringPropertyNames())) {
                    LOG.debug("configuration setting: " + prop + " = " + appProperties.getProperty(prop));
                }

            }
            throw new UnresolvableServiceNameException(serviceName);
        }
        return result;
    }
}
