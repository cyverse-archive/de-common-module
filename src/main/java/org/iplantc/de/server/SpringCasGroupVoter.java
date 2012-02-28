package org.iplantc.de.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jasig.cas.client.authentication.AttributePrincipal;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;

/**
 * 
 * @author Dennis Roberts
 */
public class SpringCasGroupVoter implements AccessDecisionVoter {

    /**
     * Used to log error and informational messages.
     */
    private static final Logger LOG = Logger.getLogger(SpringCasGroupVoter.class);

    /**
     * The pattern used to extract list contents from the string representation of a list.
     */
    private static final Pattern LIST_CONTENTS_PATTERN = Pattern.compile("\\A\\[([^\\]]*)\\]\\z");

    /**
     * The pattern used to separate list elements in the string representation of a list.
     */
    private static final Pattern LIST_DELIMITER_PATTERN = Pattern.compile(",\\s*");

    /**
     * The name of the attribute containing the list of groups that the user belongs to.
     */
    private String groupAttributeName;

    /**
     * The list of groups that are authorized to access the resource.
     */
    private List<String> authorizedGroups;

    /**
     * @param groupAttributeName the name of the attribute containing the list of groups that the user belongs to.
     */
    public void setGroupAttributeName(String groupAttributeName) {
        this.groupAttributeName = groupAttributeName;
    }

    /**
     * @param authorizedGroups The list of groups that are authorized to access the resource.
     */
    public void setAuthorizedGroups(String authorizedGroups) {
        this.authorizedGroups = Arrays.asList(LIST_DELIMITER_PATTERN.split(authorizedGroups));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supports(ConfigAttribute attribute) {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supports(Class type) {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int vote(Authentication authn, Object object, Collection attributes) {
        AttributePrincipal principal = CasUtils.attributePrincipalFromUserPrincipal(authn.getPrincipal());
        if (principal == null) {
            return ACCESS_ABSTAIN;
        }
        else {
            return userInAuthorizedGroup(principal) ? ACCESS_GRANTED : ACCESS_DENIED;
        }
    }

    /**
     * Determines whether or not a user is in an authorized group.
     * 
     * @param principal the AttributePrincipal obtained from the CAS assertion.
     * @return true if the user is in one of the authorized groups.
     */
    private boolean userInAuthorizedGroup(AttributePrincipal principal) {
        for (String group : getUserGroups(principal)) {
            if (authorizedGroups.contains(group)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Extracts the list of groups that a user belongs to from the user's attributes.
     *
     * @param principal the principal.
     * @return the list of groups that the user belongs to.
     */
    private List<String> getUserGroups(AttributePrincipal principal) {
        final List<String> groups = new ArrayList<String>();
        final String groupsContents = extractListContents((String) principal.getAttributes().get(groupAttributeName));
        LOG.debug("Groups obtained from principal: " + groupsContents);
        if (!StringUtils.isEmpty(groupsContents)) {
            for (String group : LIST_DELIMITER_PATTERN.split(groupsContents)) {
                if (!StringUtils.isEmpty(group)) {
                    groups.add(group);
                }
            }
        }
        else {
            LOG.debug("no groups received in principal: groupAttributeName = " + groupAttributeName);
        }
        return groups;
    }

    /**
     * Extracts the list contents string from a string representation of a list.
     *
     * @param listString the string representation of the list.
     * @return the list contents or null
     */
    public String extractListContents(String listString) {
        String result = null;
        if (listString != null) {
            Matcher m = LIST_CONTENTS_PATTERN.matcher(listString);
            result = m.find() ? m.group(1) : null;
        }
        return result;
    }
}
