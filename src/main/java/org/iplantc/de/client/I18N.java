package org.iplantc.de.client;

import com.google.gwt.core.client.GWT;

/**
 * Provides static access to localized strings.
 * 
 * @author lenards
 * 
 */
public class I18N {
    /** Strings displayed in the UI */
    public static final CommonDisplayStrings DISPLAY = (CommonDisplayStrings)GWT.create(CommonDisplayStrings.class);
    /** Error messages */
    public static final CommonErrorStrings ERROR = (CommonErrorStrings)GWT.create(CommonErrorStrings.class);
}