package org.iplantc.de.server;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.iplantc.de.shared.services.PropertyService;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class PropertyServlet extends RemoteServiceServlet implements PropertyService{
    /**
     * {@inheritDoc}
     */
    private static final long serialVersionUID = 1L;
    private static final String APP_PROPERTY_FILE = "appPropertyFile";
    private String propertyFile ;
    
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        ServletContext context = config.getServletContext();
        propertyFile = context.getInitParameter(APP_PROPERTY_FILE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, String> getProperties() throws SerializationException {
        Properties properties = loadProperties();
        HashMap<String, String> propertyMap = new HashMap<String, String>();
        for (Object key : properties.keySet()) {
            propertyMap.put(key.toString(), properties.get(key).toString());
        }
        return propertyMap;
    }

    /**
     * Loads the properties from discoveryenvironment.properties.
     * 
     * @return the properties.
     * @throws SerializationException if the properties can't be loaded.
     */
    private Properties loadProperties() throws SerializationException {
        Properties properties = null;
        InputStream in = null;
        try {
            in = getClass().getClassLoader().getResourceAsStream(propertyFile); //$NON-NLS-1$
            properties = new Properties();
            properties.load(in);
        } catch (IOException e) {
            throw new SerializationException(e);
        }
        return properties;
    }
}
