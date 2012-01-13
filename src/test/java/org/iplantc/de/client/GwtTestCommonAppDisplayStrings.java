package org.iplantc.de.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.junit.client.GWTTestCase;

public class GwtTestCommonAppDisplayStrings extends GWTTestCase {
    @Override
    public String getModuleName() {
        return "org.iplantc.de.DiscoveryEnvironmentCommon";
    }

    public void testDisplayStringsConstruction() {
        CommonDisplayStrings displayStrings = (CommonDisplayStrings)GWT
                .create(CommonDisplayStrings.class);
        assertNotNull(displayStrings);
        assertNotNull(displayStrings.save());
    }
}
