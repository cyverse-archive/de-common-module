package org.iplantc.de.client.models;

import com.google.web.bindery.autobean.shared.Splittable;
import com.google.web.bindery.autobean.shared.impl.StringQuoter;

import java.util.List;

/**
 * Holds all the information about an user.
 *
 * Note: init() must be called when using this class for the first time in the application.
 *
 * @author sriram
 *
 */
@SuppressWarnings("nls")
public class UserInfo {

    /**
     * Defines an attribute for User Email
     */
    public static String ATTR_EMAIL = "email";

    /**
     * Defines an attribute for the User's First Name.
     */
    public static String ATTR_FIRSTNAME = "firstName";

    /**
     * Defines an attribute for the User's Last Name.
     */
    public static String ATTR_LASTNAME = "lastName";

    /**
     * Defines an attribute for the short username.
     */
    public static String ATTR_USERNAME = "username";

    /**
     * Defines an attribute for a users login Time
     *
     */
    public static String LOGIN_TIME = "loginTime";

    /**
     * Defines an attribute for new user identification
     */
    public static String NEW_USER ="newWorkspace";

    /**
     * Defines an attribute for the fully qualified username.
     */
    private static final String ATTR_FULL_USERNAME = "full_username";


    private static UserInfo instance;

    /**
     * Get an instance of UserInfo.
     *
     * @return a singleton instance of the object.
     */
    public static UserInfo getInstance() {
        if (instance == null) {
            instance = new UserInfo();
        }

        return instance;
    }
    private String email;
    private String firstName;
    private String fullUsername;
    private String homePath;
    private String lastName;
    private String loginTime;
    private Boolean newUser;
    private List<WindowState> savedOrderedWindowStates;
    private String trashPath;
    private String username;

    private String workspaceId;

    /**
     * Constructs a default instance of the object with all fields being set to null.
     */
    public UserInfo() {
        workspaceId = null;
        email = null;
    }

    /**
     * Initializes UserInfo object.
     *
     * This method must be called before using any other member functions of this class
     *
     * @param userInfoJson json to initialize user info.
     */
    public void init(String userInfoJson) {
        if (userInfoJson != null && !userInfoJson.equals("")) {
            Splittable split = StringQuoter.split(userInfoJson);
            workspaceId = split.get("workspaceId").asString();
            newUser = split.get(NEW_USER).asBoolean();
            loginTime = split.get(LOGIN_TIME).asString();
            setUsername(split.get(ATTR_USERNAME).asString());
            setEmail(split.get(ATTR_EMAIL).asString());
            setFullUsername(split.get(ATTR_FULL_USERNAME).asString());
            setFirstName(split.get(ATTR_FIRSTNAME).asString());
            setLastName(split.get(ATTR_LASTNAME).asString());
        }
    }

    /**
     * Get user's email address.
     *
     * @return email address.
     */
    public String getEmail() {
        return email;
    }

    /**
     * @return the firstName
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Gets the full username.
     *
     * @return the fully qualified username.
     */
    public String getFullUsername() {
        return fullUsername;
    }

    /**
     * @return the path to the user's home directory.
     */
    public String getHomePath() {
        return homePath;
    }

    /**
     * @return the lastName
     */
    public String getLastName() {
        return lastName;
    }

    public String getLoginTime() {
		return loginTime;
	}

    /**
     * @return the savedOrderedWindowStates
     */
    public List<WindowState> getSavedOrderedWindowStates() {
        return savedOrderedWindowStates;
    }

    /**
     * @return the trashPath
     */
    public String getTrashPath() {
        return trashPath;
    }

    /**
     * Gets the username for the user.
     *
     * This value corresponds to an entry in LDAP.
     *
     * @return a string representing the username for the user.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Gets the workspace id for the user.
     *
     * @return a string representing the identifier for workspace.
     */
    public String getWorkspaceId() {
        return workspaceId;
    }

    /**
     * @return the newUser
     */
    public Boolean isNewUser() {
        return newUser;
    }

    /**
     * Set user's email address.
     *
     * @param email email address.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @param firstName the firstName to set
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Sets the full username.
     *
     * @param fullUsername the fully qualified username.
     */
    public void setFullUsername(String fullUsername) {
        this.fullUsername = fullUsername;
    }

    /**
     * @param homePath the path to the user's home directory.
     */
    public void setHomePath(String homePath) {
        this.homePath = trim(homePath);
    }

    /**
     * @param lastName the lastName to set
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setLoginTime(String loginTime) {
		this.loginTime = loginTime;
	}

    /**
     * @param newUser the newUser to set
     */
    public void setNewUser(Boolean newUser) {
        this.newUser = newUser;
    }

    /**
     * @param savedOrderedWindowStates the savedOrderedWindowStates to set
     */
    public void setSavedOrderedWindowStates(List<WindowState> savedOrderedWindowStates) {
        this.savedOrderedWindowStates = savedOrderedWindowStates;
    }

    /**
     * @param trashPath the trashPath to set
     */
    public void setTrashPath(String trashPath) {
        this.trashPath = trim(trashPath);
    }

	/**
     * Sets the username for the user.
     *
     * @param usr a string representing the username
     */
    public void setUsername(String usr) {
        this.username = usr;
    }

	private String trim(String value) {
        StringBuilder temp = null;
        if (value != null && !value.isEmpty()) {
            final String QUOTE = "\"";

            temp = new StringBuilder(value);

            if (value.startsWith(QUOTE)) {
                temp.deleteCharAt(0);
            }

            if (value.endsWith(QUOTE)) {
                temp.deleteCharAt(temp.length() - 1);
            }

            return temp.toString();
        } else {
            return value;
        }
    }
}

