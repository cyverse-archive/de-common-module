package org.iplantc.de.client;

import com.google.gwt.core.client.GWT;

/**
 * Static access to client constants.
 * 
 * @author lenards
 * 
 */
public class Constants {
    public static final CommonConstants CLIENT = (CommonConstants)GWT.create(CommonConstants.class);
}
