package org.iplantc.de.client.models;

import org.iplantc.core.jsonutil.JsonUtil;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;

/**
 * Models data related to the application.
 * 
 * Used to provide the user with information about the Discovery Environment in the tradition "About"
 * fashion.
 * 
 * @author lenards
 * 
 */
public class AboutApplicationData extends BaseModelData {
    /**
     * Generated unique identifier for serialization
     */
    private static final long serialVersionUID = 7912554754256735981L;
    private final String RELEASE = "release"; //$NON-NLS-1$
    private final String BUILD_NUMBER = "buildnumber"; //$NON-NLS-1$
    private final String USER_AGENT = "useragent"; //$NON-NLS-1$

    /**
     * Constructs an instance of the model data object given JSON.
     * 
     * @param json the representation of the data in JSON format
     */
    public AboutApplicationData(String json) {
        JSONValue value = JSONParser.parseStrict(json);
        JSONObject obj = value.isObject();

        set(RELEASE, JsonUtil.trim(obj.get(RELEASE).toString()));
        set(BUILD_NUMBER, JsonUtil.trim(obj.get(BUILD_NUMBER).toString()));
        set(USER_AGENT, GXT.getUserAgent());
    }

    /**
     * Retrieves the version of the release.
     * 
     * @return a string representing the release version
     */
    public String getReleaseVersion() {
        return get(RELEASE);
    }

    /**
     * Retrieves the number assigned to the build of the application.
     * 
     * @return a number representing the specific build that has been deployed
     */
    public String getBuildNumber() {
        return get(BUILD_NUMBER);
    }

    /**
     * Retrieves the user agent string reported for the user's browser.
     * 
     * @return a string representing the user's browser, operating system, and preferred language
     */
    public String getUserAgent() {
        return get(USER_AGENT);
    }

    public String toString() {
        return RELEASE + ": " + get(RELEASE) + " " + BUILD_NUMBER + ": " + get(BUILD_NUMBER) + " " //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                + USER_AGENT + ": " + get(USER_AGENT); //$NON-NLS-1$
    }
}