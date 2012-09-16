package org.iplantc.de.server;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.iplantc.de.shared.services.PropertyService;

public class PropertyServlet extends RemoteServiceServlet implements PropertyService{

    /**
     * {@inheritDoc}
     */
    private static final long serialVersionUID = 1L;

    /**
     * The configuration settings.
     */
    private final Properties props;

    /**
     * @param props the configuration properties.
     */
    public PropertyServlet(Properties props) {
        this.props = props;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, String> getProperties() throws SerializationException {
        HashMap<String, String> propertyMap = new HashMap<String, String>();
        for (Object key : props.keySet()) {
            propertyMap.put(key.toString(), props.get(key).toString());
        }
        return propertyMap;
    }
}
