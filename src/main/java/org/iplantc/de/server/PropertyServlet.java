package org.iplantc.de.server;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.iplantc.de.shared.services.PropertyService;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import java.util.Iterator;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

public class PropertyServlet extends RemoteServiceServlet implements PropertyService{
    /**
     * {@inheritDoc}
     */
    private static final long serialVersionUID = 1L;
    private static final String APP_PROPERTY_FILE = "appPropertyFile";
    private String propertyFile ;
    
    @Override
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
        Configuration config = loadConfiguration();
        HashMap<String, String> propertyMap = new HashMap<String, String>();
        for (Iterator i = config.getKeys(); i.hasNext(); ) {
            String key = (String) i.next();
            propertyMap.put(key, config.getString(key));
        }
        return propertyMap;
    }

    /**
     * Loads the configuration from the application property file.
     * 
     * @return the configuration.
     * @throws SerializationException if the configuration can't be loaded.
     */
    private Configuration loadConfiguration() throws SerializationException {
        try {
            return new PropertiesConfiguration(propertyFile);
        }
        catch (ConfigurationException e) {
            throw new SerializationException(e);
        }
    }
}
