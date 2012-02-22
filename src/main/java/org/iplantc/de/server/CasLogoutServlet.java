package org.iplantc.de.server;

import java.io.IOException;
import java.util.Properties;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringUtils;

/**
 * A shared servlet for handling CAS logout.
 *
 * @author Dennis Roberts
 */
public class CasLogoutServlet extends HttpServlet {

    /**
     * The name of the initialization parameter containing the name of the application property file.
     */
    private static final String PROP_FILE_INIT_PARAM = "appPropertyFile";

    /**
     * The name of the initialization parameter containing the property name prefix.
     */
    private static final String PROP_PREFIX_INIT_PARAM = "appPropertyPrefix";

    /**
     * The name of the property containing the relative URL to redirect the user to when the user chooses to log out of
     * all applications.
     */
    private static final String LOGOUT_URL_PROPERTY = ".cas.logout-url";

    /**
     * The name of the property containing the name of the current web application.
     */
    private static final String APP_NAME_PROPERTY = ".cas.app-name";

    /**
     * The relative URL to redirect the user to when the user chooses to log out of all applications.
     */
    private String logoutUrl;

    /**
     * The name of the current web application.
     */
    private String appName;

    /**
     * {@inheritDoc}
     */
    @Override
    public void init() throws ServletException {
        super.init();
        try {
            Properties props = loadAppProperties();
            String propPrefix = getRequiredInitParameter(PROP_PREFIX_INIT_PARAM);
            logoutUrl = getRequiredProp(props, propPrefix + LOGOUT_URL_PROPERTY);
            appName = getRequiredProp(props, propPrefix + APP_NAME_PROPERTY);
        }
        catch (IOException e) {
            throw new ServletException(e);
        }
    }

    /**
     * Gets a required property from a set of properties.
     * 
     * @param props the properties.
     * @param name the name of the required property.
     * @return the value of the required property.
     * @throws IllegalStateException if the required property is missing or empty.
     */
    private String getRequiredProp(Properties props, String name) {
        String value = props.getProperty(name);
        if (StringUtils.isBlank(value)) {
            String msg = "configuration property, " + name + ", is missing or empty";
            throw new IllegalStateException(msg);
        }
        return value;
    }

    /**
     * Loads configuration properties from the application properties file, which must be on the classpath.
     * 
     * @return the application properties.
     * @throws IOException if an I/O error occurs.
     */
    private Properties loadAppProperties() throws IOException {
        Properties props = new Properties();
        String filename = getRequiredInitParameter(PROP_FILE_INIT_PARAM);
        props.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(filename));
        return props;
    }

    /**
     * Gets a required initialization parameter.
     * 
     * @param name the name of the initialization parameter.
     * @return the value of the initialization parameter.
     * @throws IllegalStateException if the initialization parameter is missing or empty.
     */
    private String getRequiredInitParameter(String name) {
        String value = getServletContext().getInitParameter(name);
        if (StringUtils.isBlank(value)) {
            String msg = "initialization parameter, " + name + ", is missing or empty";
            throw new IllegalStateException(msg);
        }
        return value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        resp.getWriter().print("<html>");
        resp.getWriter().print("<head>");
        resp.getWriter().print("<title>Logout Successful</title>");
        resp.getWriter().print("</head>");
        resp.getWriter().print("<body>");
        resp.getWriter().print("You have been logged out of " + appName + ". ");
        resp.getWriter().print("To log out of all applications, click ");
        resp.getWriter().print("<a href=\"" + req.getContextPath() + logoutUrl + "\">here</a>.");
        resp.getWriter().print("</body>");
        resp.getWriter().print("</head>");
    }
}
