package org.iplantc.de.server;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

public class SecurityProperties {

    private static final Logger LOGGER = Logger.getLogger(SecurityProperties.class);

    // The name of the properties file.
    public static final String PROPERTIES_FILE = "security.properties";

    // The prefix for all of the properties.
    public static final String PREFIX = "org.iplantc.discoveryenvironment";

    public static final String USE_LOOPBACK_INTERFACE = PREFIX + ".useLoopbackInterface";

    // The property names.
    public static final String SECURITY_ENABLED_PROPERTY = PREFIX + ".securityEnabled";
    public static final String KEYSTORE_PATH_PROPERTY = PREFIX + ".keystorePath";
    public static final String KEYSTORE_TYPE_PROPERTY = PREFIX + ".keystoreType";
    public static final String KEYSTORE_PASSWORD_PROPERTY = PREFIX + ".keystorePassword";
    public static final String SIGNING_KEY_ALIAS_PROPERTY = PREFIX + ".signingKeyAlias";
    public static final String SIGNING_KEY_PASSWORD_PROPERTY = PREFIX + ".signingKeyPassword";
    public static final String ENCRYPTING_KEY_ALIAS_PROPERTY = PREFIX + ".encryptingKeyAlias";
    public static final String FOUNDATIONAL_API_KEY_FILE_PROPERTY = PREFIX + ".foundationalApiKeyFile";
    public static final String DEFAULT_USERNAME_PROPERTY = PREFIX + ".defaultUsername";

    /**
     * The list of properties that are required only when security is enabled.
     */
    private static final String[] PROPERTIES_REQUIRED_FOR_SECURITY = {KEYSTORE_PATH_PROPERTY,
            KEYSTORE_TYPE_PROPERTY, KEYSTORE_PASSWORD_PROPERTY, SIGNING_KEY_ALIAS_PROPERTY,
            SIGNING_KEY_PASSWORD_PROPERTY, ENCRYPTING_KEY_ALIAS_PROPERTY, USE_LOOPBACK_INTERFACE};

    /**
     * The properties. Place any default values in the initializer.
     */
    private static Properties properties;

    static {
        initProperties();
        loadProperties();
        if (isWebSecurityEnabled()) {
            validateProperties(PROPERTIES_REQUIRED_FOR_SECURITY);
        }
    }

    private static void initProperties() {
        properties = new Properties();
        properties.put(SECURITY_ENABLED_PROPERTY, "true");
        properties.put(USE_LOOPBACK_INTERFACE, "true");
    }

    /**
     * Gets the default username to use when security is disabled.
     * 
     * @return the default username.
     */
    public static String getDefaultUsername() {
        return properties.getProperty(DEFAULT_USERNAME_PROPERTY);
    }

    /**
     * Gets the path to the file containing the private key used to encrypt the token used to
     * authenticate to the foundational API.
     * 
     * @return the path to the file.
     */
    public static String getFoundationalApiKeyFile() {
        return properties.getProperty(FOUNDATIONAL_API_KEY_FILE_PROPERTY);
    }

    /**
     * Gets the path to the keystore containing the certificates and keys used to communicate with
     * secured iPlant services.
     * 
     * @return the path to the keystore.
     */
    public static String getKeystorePath() {
        return properties.getProperty(KEYSTORE_PATH_PROPERTY);
    }

    /**
     * Gets the type of the keystore containing the certificates and keys used to communicate with
     * secured iPlant services.
     * 
     * @return the keystore type.
     */
    public static String getKeystoreType() {
        return properties.getProperty(KEYSTORE_TYPE_PROPERTY);
    }

    /**
     * Gets the password to use to access the keystore containing the certificates and keys used to
     * communicate with secured iPlant services.
     * 
     * @return the password.
     */
    public static String getKeystorePassword() {
        return properties.getProperty(KEYSTORE_PASSWORD_PROPERTY);
    }

    /**
     * Gets the alias of the key used to sign SAML assertions that are forwarded to secured services.
     * 
     * @return the alias.
     */
    public static String getSigningKeyAlias() {
        return properties.getProperty(SIGNING_KEY_ALIAS_PROPERTY);
    }

    /**
     * Gets the password to use to access the key used to sign SAML assertions that are forwarded to
     * secured services.
     * 
     * @return the password.
     */
    public static String getSigningKeyPassword() {
        return properties.getProperty(SIGNING_KEY_PASSWORD_PROPERTY);
    }

    /**
     * Gets the alias of the certificate used to encrypt SAML assertions that are forwarded to secured
     * services.
     * 
     * @return the alias.
     */
    public static String getEncryptingKeyAlias() {
        return properties.getProperty(ENCRYPTING_KEY_ALIAS_PROPERTY);
    }

    /**
     * Gets the flag indicating whether or not we should force the use of the loopback interface for SAML
     * assertion requests. This flag should be set to true if the Shibboleth SP is on the local host and
     * is configured to accept SAML assertion requests only over the loopback interface. This parameter
     * does not have to appear in the discovery environment properties file. If the parameter value is
     * not specified then it defaults to true.
     * 
     * @return true if the loopback interface should be used.
     */
    public static boolean useLoopbackInterface() {
        return Boolean.parseBoolean(properties.getProperty(USE_LOOPBACK_INTERFACE));
    }

    /**
     * Need a static because DEServiceDispatcher needs to know is security is enabled or not.
     * Unfortunately, the dispatcher is a servlet and the normal way to get the Spring application
     * context doesn't work.
     * 
     * @return true if security is enabled.
     */
    public static boolean isWebSecurityEnabled() {
        return Boolean.parseBoolean(properties.getProperty(SECURITY_ENABLED_PROPERTY));
    }

    /**
     * Validates that we have values for all required properties.
     */
    private static void validateProperties(String[] propertyNames) {
        for (String propertyName : propertyNames) {
            String propertyValue = properties.getProperty(propertyName);
            if (propertyValue == null || propertyValue.equals("")) {
                throw new ExceptionInInitializerError("missing required property: " + propertyName);
            }
        }
    }

    /**
     * Loads the discovery environment properties. If an error occurs while loading the file, we log the
     * message, but do not throw an exception; the property validation will catch any required properties
     * that are missing.
     */
    private static void loadProperties() {
        try {
            // String classpath = System.getProperty("java.class.path");
            // int jarPos = classpath.indexOf("de-common.jar");
            //
            // int jarPathPos = classpath.lastIndexOf(File.pathSeparator, jarPos) + 1;
            // String path = classpath.substring(jarPathPos, jarPos);
            // path = path + "../classes/" + PROPERTIES_FILE;

            InputStream is = Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream(PROPERTIES_FILE);
            System.out.println("is-->" + is);
            properties.load(is);
        } catch (IOException e) {
            String msg = "unable to load discovery environment properties";
            LOGGER.error(msg, e);
        }
    }
}
