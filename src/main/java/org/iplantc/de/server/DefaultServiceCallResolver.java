package org.iplantc.de.server;

import java.io.InputStream;
import java.util.Map.Entry;
import java.util.Properties;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.iplantc.de.shared.services.BaseServiceCallWrapper;

public class DefaultServiceCallResolver implements ServiceCallResolver {
    private static final Logger LOG = Logger.getLogger(DefaultServiceCallResolver.class);
    private static final String PREFIX_KEY = "prefix";

    private PropertiesConfiguration appProperties;
    private String prefix;

    public DefaultServiceCallResolver(PropertiesConfiguration propsConfig) {
        appProperties = propsConfig;
        setPrefix();
        validatePrefix();
    }

    public DefaultServiceCallResolver(Properties prop) {
        appProperties = new PropertiesConfiguration();
        for (Entry<Object, Object> propPair : prop.entrySet()) {
            appProperties.addProperty((String)propPair.getKey(), propPair.getValue());
        }
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
        prefix = appProperties.getString(PREFIX_KEY);
    }

    private void loadProperties(String propertyFile) {
        appProperties = new PropertiesConfiguration();
        try {
            InputStream is = Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream(propertyFile);
            appProperties.load(is);
        } catch (ConfigurationException e) {
            LOG.error(e.toString(), e);
            e.printStackTrace();
        }
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
            components[0] = appProperties.getString(serviceName);
            if (components[0] == null) {
                throw new RuntimeException("unknown service name: " + serviceName);
            }
            address = StringUtils.join(components, "?");
        }
        return address;
    }
}
