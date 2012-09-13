package org.iplantc.de.server.spring;

import java.util.Properties;
import java.util.TreeSet;
import javax.servlet.ServletContextEvent;
import org.apache.log4j.Logger;
import org.springframework.util.Assert;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;

/**
 * An abstract ServletContextListener for services that need to authenticate using CAS and obtain configuration
 * settings using a Clavin client.
 *
 * @author Dennis Roberts
 */
public abstract class AbstractServletContextListener extends ContextLoaderListener {

    /**
     * Used to log configuration settings.
     */
    private static final Logger LOG = Logger.getLogger(AbstractServletContextListener.class);

    /**
     * Loads the configuration and initializes the web application context.
     *
     * @param event the ServletContextEvent containing the ServletContext being initialized.
     */
    @Override
    public void contextInitialized(ServletContextEvent event) {
        super.contextInitialized(event);
        ClavinPropertyPlaceholderConfigurer configurer = getPropertyPlaceholderConfigurer();
        Assert.notNull(configurer, "a ClavinPropertyPlaceholderConfigurer bean must be defined");
        loadConfigs(configurer);
        registerServlets();
    }

    /**
     * @return the property placeholder configurer.
     */
    private ClavinPropertyPlaceholderConfigurer getPropertyPlaceholderConfigurer() {
        WebApplicationContext wac = getCurrentWebApplicationContext();
        return (ClavinPropertyPlaceholderConfigurer) wac.getBean(ClavinPropertyPlaceholderConfigurer.class);
    }

    /**
     * Loads any configurations that still need to be loaded.  The configurations are assumed to be stored in a
     * {@link ClavinPropertyPlaceholderConfigurer} instance.
     *
     * @param configurer the Clavin property placeholder configurer to get the configurations from.
     */
    protected abstract void loadConfigs(ClavinPropertyPlaceholderConfigurer configurer);

    /**
     * Registers any servlets that need to be registered by the context listener.
     */
    protected abstract void registerServlets();

    /**
     * Loads a single configuration from the configurer bean.
     *
     * @param configurer the configurer bean.
     * @param configName the name of the configuration to load.
     * @return the configuration settings as a {@link Properties} instance.
     */
    protected Properties loadConfig(ClavinPropertyPlaceholderConfigurer configurer, String configName) {
        LOG.warn("CONFIGURATION: retrieving config - " + configName);
        Properties props = configurer.getConfig(configName);
        if (props != null) {
            for (String propName : new TreeSet<String>(props.stringPropertyNames())) {
                LOG.warn("CONFIGURATION: " + propName + " = " + props.getProperty(propName));
            }
        }
        else {
            LOG.warn("CONFIGURATION: no configuration found - " + configName);
        }
        return props;
    }
}
