package org.iplantc.de.server;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.regex.Pattern;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.stringtemplate.v4.ST;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.iplantc.clavin.spring.ConfigAliasResolver;

/**
 * A shared servlet for handling CAS logout.
 *
 * @author Dennis Roberts
 */
public class CasLogoutServlet extends HttpServlet {

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
     * The name of the property containing the relative URL to redirect the user to when the user chooses to log back
     * into the web application.
     */
    private static final String LOGIN_URL_PROPERTY = ".cas.login-url";

    /**
     * The name of the property containing the URL to redirect users to when they choose not to log out of the web
     * application.
     */
    private static final String NO_LOGOUT_URL_PROPERTY = ".cas.no-logout-url";

    /**
     * The name of the property containing the text that describes all of the applications whose sessions will be closed
     * if the user decides to log out of all applications.
     */
    private static final String APP_LIST_PROPERTY = ".cas.app-list";

    /**
     * The name of the file containing the template for the logout alert page.
     */
    private static final String TEMPLATE_FILENAME = "logout-alert-template.html";

    /**
     * We assume that URLs are absolute URLs if they contain "://".
     */
    private static final Pattern ABSOLUTE_URL_PATTERN = Pattern.compile("://");

    /**
     * The URL to redirect the user to when the user chooses to log out of all applications.
     */
    private String logoutUrl;

    /**
     * The name of the current web application.
     */
    private String appName;

    /**
     * The URL to redirect the user to when the user chooses to log back into the current web application.
     */
    private String loginUrl;

    /**
     * The URL to redirect the user to when the user chooses not to log out of all applications.
     */
    private String noLogoutUrl;

    /**
     * Text that describes all of the applications whose sessions will be closed if the user chooses to log out of all
     * applications. This could be a simple phrase such as "all applications" or it could be an actual list of
     * application names.
     */
    private String appList;

    /**
     * The text of the page template to return.
     */
    private String templateText;

    /**
     * True if the servlet has been initialized.
     */
    private boolean initialized;

    /**
     * The default constructor.
     */
    public CasLogoutServlet() {}

    /**
     * @param props the properties containing the app configuration settings.
     * @param propPrefix the prefix to use when determining property names.
     */
    public CasLogoutServlet(Properties props, String propPrefix) {
        loadConfig(props, propPrefix);
    }

    /**
     * Initializes the servlet.
     *
     * @throws ServletException if the servlet can't be initialized.
     * @throws IllegalStateException if a required configuration parameter is missing.
     */
    @Override
    public void init() throws ServletException {
        super.init();
        if (!initialized) {
            Properties config = ConfigAliasResolver.getRequiredAliasedConfigFrom(getServletContext(), "webapp");
            loadConfig(config, getPropertyPrefix());
        }
    }

    /**
     * Gets the property name prefix from a servlet initialization parameter.
     *
     * @return the property name prefix.
     * @throws ServletException if the property name prefix isn't defined.
     */
    private String getPropertyPrefix() throws ServletException {
        String prefix = getServletConfig().getInitParameter("propertyNamePrefix");
        if (prefix == null) {
            throw new ServletException("init parameter, propertyNamePrefix, is required");
        }
        return prefix;
    }

    /**
     * @param props the properties to extract the desired configuration settings from.
     * @param propPrefix the property name prefix.
     */
    private void loadConfig(Properties props, String propPrefix) {
        logoutUrl = getRequiredProp(props, propPrefix + LOGOUT_URL_PROPERTY);
        appName = getRequiredProp(props, propPrefix + APP_NAME_PROPERTY);
        loginUrl = getRequiredProp(props, propPrefix + LOGIN_URL_PROPERTY);
        noLogoutUrl = getRequiredProp(props, propPrefix + NO_LOGOUT_URL_PROPERTY);
        appList = getRequiredProp(props, propPrefix + APP_LIST_PROPERTY);
        templateText = loadTemplate();
        initialized = true;
    }

    /**
     * Loads the template for the logout alert page.
     *
     * @return the template text.
     * @throws RuntimeException if the template doesn't exist or can't be loaded.
     */
    private String loadTemplate() {
        try {
            InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(TEMPLATE_FILENAME);
            return IOUtils.toString(in);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
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
     * Converts a relative URL to an absolute URL. If the URL is already an absolute URL then this method does nothing.
     *
     * @param req the object representing the HTTP request.
     * @param originalUrl the original URL.
     * @return
     */
    private String convertRelativeUrl(HttpServletRequest req, String originalUrl) {
        if (ABSOLUTE_URL_PATTERN.matcher(originalUrl).find()) {
            return originalUrl;
        }
        else {
            return req.getContextPath().replaceAll("/$", "") + "/" + originalUrl.replaceAll("^/", "");
        }
    }

    /**
     * Generates the text of the page that will be returned by this servlet. The page text itself is static for any
     * given web application, so it only needs to be generated once upon startup.
     *
     * @throws ServletException if the page text can't be generated.
     */
    private String generatePageText(HttpServletRequest req) throws ServletException {
        ST st = new ST(templateText, '$', '$');
        st.add("logout_url", convertRelativeUrl(req, logoutUrl));
        st.add("app_name", appName);
        st.add("login_url", convertRelativeUrl(req, loginUrl));
        st.add("no_logout_url", convertRelativeUrl(req, noLogoutUrl));
        st.add("app_list", appList);
        return st.render();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        resp.getWriter().print(generatePageText(req));
    }
}
