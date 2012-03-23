package org.iplantc.de.client;

import com.google.gwt.i18n.client.Messages;

/**
 * Constants used by the client to communicate system errors.
 * 
 * All values present here are subject to translation for internationalization.
 */
public interface CommonErrorStrings extends Messages {
    /**
     * Caption for error dialogs.
     * 
     * @return localized error string.
     */
    String error();

    /**
     * Localized error message to show when app groups couldn't be loaded.
     * 
     * @return string representing the text
     */
    String appGroupsLoadFailure();
}
