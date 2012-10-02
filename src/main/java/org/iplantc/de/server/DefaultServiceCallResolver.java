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
     */
    @Override
    public String resolveAddress(BaseServiceCallWrapper wrapper) {
        String address = wrapper.getAddress();
        if (address.startsWith(prefix)) {
            String[] components = address.split("\\?", 2);
            String serviceName = components[0];
            components[0] = appProperties.getProperty(serviceName);
            if (components[0] == null) {
                LOG.error("unknown service name: " + serviceName);
                if (LOG.isDebugEnabled()) {
                    for (String prop : new TreeSet<String>(appProperties.stringPropertyNames())) {
                        LOG.debug("configuration setting: " + prop + " = " + appProperties.getProperty(prop));
                    }
                }
                throw new RuntimeException("unknown service name: " + serviceName);
            }
            address = StringUtils.join(components, "?");
        }
        return address;
    }
}
