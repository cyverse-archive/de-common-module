package org.iplantc.de.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;
import java.util.Map.Entry;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.iplantc.de.shared.services.BaseServiceCallWrapper;
import org.iplantc.de.shared.services.ServiceCallWrapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestDefaultServiceCallResolver {
    /**
     * These are the *actual* expected values taken from the current version of
     * discoveryenvironment.properties
     */
    private static final String[] EXPECTED = {
            "org.iplantc.services.acctmgmt.fetchStates=http://emma.iplantcollaborative.org/accountmanagementv2/fetch-states",
            "org.iplantc.services.acctmgmt.fetchPositions=http://emma.iplantcollaborative.org/accountmanagementv2/fetch-positions",
            "org.iplantc.services.acctmgmt.fetchResearchAreas=http://emma.iplantcollaborative.org/accountmanagementv2/fetch-research-areas",
            "org.iplantc.services.acctmgmt.fetchFundingAgencies=http://emma.iplantcollaborative.org/accountmanagementv2/fetch-funding-agencies",
            "org.iplantc.services.acctmgmt.fetchAcctDetails=http://emma.iplantcollaborative.org/accountmanagementv2/fetch-account-details",
            "org.iplantc.services.acctmgmt.updateAccounts=http://emma.iplantcollaborative.org/accountmanagementv2/update-accounts",
            "org.iplantc.services.acctmgmt.updatePassword=http://emma.iplantcollaborative.org/accountmanagementv2/update-password",
            "org.iplantc.services.acctmgmt.fetchUserOptDetails=http://emma.iplantcollaborative.org/accountmanagementv2/fetch-user-options-details"};

    private Properties testProps;
    private DefaultServiceCallResolver resolver;

    /**
     * This was intended to test the default constructor for DefaultServiceCallResolver. Since the values
     * in discoveryenvironments.properties will be influx, a version of the test using the EXPECTED
     * values will be checked in. To test this class's ability to pull in values from the expect
     * properties file, just uncomment the default constructor and modify the EXPECTED array.
     */
    @Test
    public void testActualPropertiesValues() { // wipe everything other
        testProps = null;
        resolver = null;
        // use the default ctor to grab discoveryenvironment.properties
        // resolver = new DefaultServiceCallResolver();
        resolver = new DefaultServiceCallResolver(createFromExpected());
        // ensure it was set
        assertNotNull(resolver);
        verifyExpectedValues(EXPECTED);

    }

    private void verifyExpectedValues(String[] expectedValues) {
        for (int i = 0; i < expectedValues.length; i++) {
            String[] pieces = expectedValues[i].split("=");
            String actual = resolver.resolveAddress(wrapper(pieces[0]));
            assertEquals(pieces[1], actual);
            verifyURLParses(actual);
        }
    }

    @Test
    public void testKnownPropertiesResolveCorrectly() {
        String srvKey = "org.iplantc.services.acctmgmt.fetchStates";
        String expected = "http://ndy.sixfifty.org/accountmanagementv2/fetch-states";
        assertNotNull(testProps);
        assertNotNull(resolver);
        String valid = resolver.resolveAddress(wrapper(srvKey));
        String prop = testProps.getProperty(srvKey);
        System.out.println(prop);
        // expected should equal the returned value
        assertEquals(expected, valid);
        // we should be able to parse the result as a valid URL
        verifyURLParses(valid);
    }

    @Test
    public void testAllKnownPropertiesResolveCorrectly() {
        assertNotNull(testProps);
        assertNotNull(resolver);

        String srvKey = "org.iplantc.services.acctmgmt.fetchPositions";
        String expected = "http://ndy.sixfifty.org/accountmanagementv2/fetch-positions";
        String actual = resolver.resolveAddress(wrapper(srvKey));
        assertEquals(expected, actual);
        verifyURLParses(actual);

        srvKey = "org.iplantc.services.acctmgmt.fetchResearchAreas";
        expected = "http://ndy.sixfifty.org/accountmanagementv2/fetch-research-areas";
        actual = resolver.resolveAddress(wrapper(srvKey));
        assertEquals(expected, actual);
        verifyURLParses(actual);

        srvKey = "org.iplantc.services.acctmgmt.fetchFundingAgencies";
        expected = "http://ndy.sixfifty.org/accountmanagementv2/fetch-funding-agencies";
        actual = resolver.resolveAddress(wrapper(srvKey));
        assertEquals(expected, actual);
        verifyURLParses(actual);

        srvKey = "org.iplantc.services.acctmgmt.fetchAcctDetails";
        expected = "http://ndy.sixfifty.org/accountmanagementv2/fetch-account-details";
        actual = resolver.resolveAddress(wrapper(srvKey));
        assertEquals(expected, actual);
        verifyURLParses(actual);
    }

    @Test
    public void testKnownURLPassesThroughCorrectly() {
        String srvUrl = "http://ndy.sixfifty.org/accountmanagementv2/fetch-states";
        String expected = srvUrl;
        assertNotNull(testProps);
        assertNotNull(resolver);
        String actual = resolver.resolveAddress(wrapper(srvUrl));
        assertEquals(expected, actual);
        // it's a url before resolving, so it better still be valid
        verifyURLParses(actual);
    }

    @Test
    public void testGarbageValuePassesThrough() {
        String garbage = "@$@&(!&@!(*&*&*(**!#!#!#!$!%";
        String expected = garbage;
        assertNotNull(testProps);
        assertNotNull(resolver);
        String actual = resolver.resolveAddress(wrapper(garbage));
        assertEquals(expected, actual);
        verifyURLParses(actual, true);
    }

    @Test
    public void testBasicVariableInterpolationWithinPropertiesFile() {
        Properties expected = createExpectedPropertiesForInterpolationTest();
        PropertiesConfiguration actual = createPropertiesConfigurationForInterpolationTest();
        for (Entry<Object, Object> expectedPair : expected.entrySet()) {
            String key = (String)expectedPair.getKey();
            assertEquals(expectedPair.getValue(), actual.getString(key));
        }
    }

    @Test
    public void testVariableInterpolationResolution() {
        String expected;
        String actual;

        PropertiesConfiguration propConfig = createPropertiesConfigurationForInterpolationTest();
        propConfig.addProperty("org.iplantc.services.zoidberg.components.foo",
                "http://emma.iplantcollaborative.org:10000/components");
        propConfig.addProperty("zoidberg.port", ":10000");
        propConfig.addProperty("org.iplantc.services.zoidberg.components.port",
                "http://${zoidberg.hostname}${zoidberg.port}/components");

        DefaultServiceCallResolver interpolRes = new DefaultServiceCallResolver(propConfig);

        expected = "http://emma.iplantcollaborative.org/nibblonian/home?user=ndy";
        actual = interpolRes
                .resolveAddress(wrapper("org.iplantc.services.zoidberg.getuserhomedir?user=ndy"));
        assertEquals(expected, actual);

        expected = "http://emma.iplantcollaborative.org/in-progress?user=ndy";
        actual = interpolRes
                .resolveAddress(wrapper("org.iplantc.services.zoidberg.inprogress?user=ndy"));
        assertEquals(expected, actual);

        expected = "http://emma.iplantcollaborative.org/in-progress?user=ndy&summary=true";
        actual = interpolRes
                .resolveAddress(wrapper("org.iplantc.services.zoidberg.inprogress?user=ndy&summary=true"));
        assertEquals(expected, actual);

        expected = "http://emma.iplantcollaborative.org/in-progress?tito=650650n";
        actual = interpolRes
                .resolveAddress(wrapper("org.iplantc.services.zoidberg.inprogress?tito=650650n"));
        assertEquals(expected, actual);

        expected = "http://emma.iplantcollaborative.org:10000/components?user=ndy&bar=quux";
        actual = interpolRes
                .resolveAddress(wrapper("org.iplantc.services.zoidberg.components.foo?user=ndy&bar=quux"));
        assertEquals(expected, actual);

        expected = "http://emma.iplantcollaborative.org:10000/components?user=ndy&bar=quux";
        actual = interpolRes
                .resolveAddress(wrapper("org.iplantc.services.zoidberg.components.port?user=ndy&bar=quux"));
        assertEquals(expected, actual);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testResolverFailsWithoutPrefix() {
        testProps.remove("prefix");
        String tmp = testProps.getProperty("prefix");
        assertNull(tmp);
        // this should fail.
        resolver = new DefaultServiceCallResolver(testProps);
    }

    @Before
    public void setUp() {
        testProps = createProperties();
        resolver = new DefaultServiceCallResolver(testProps);
    }

    @After
    public void tearDown() {
        testProps = null;
        resolver = null;
    }

    private void verifyURLParses(String valid) {
        verifyURLParses(valid, false);
    }

    private void verifyURLParses(String valid, boolean shouldFail) {
        try {
            new URL(valid);
            if (shouldFail) { // if it *should* fail - then reaching here is failure
                fail();
            }
        } catch (MalformedURLException e) {
            if (!shouldFail) { // if it should not fail - then an exception is treated as failure
                fail();
            }
        }
    }

    private BaseServiceCallWrapper wrapper(String serviceKey) {
        return new ServiceCallWrapper(serviceKey);
    }

    private Properties createExpectedPropertiesForInterpolationTest() {
        Properties expectedProps = new Properties();

        expectedProps.put("prefix", "org.iplantc.services");
        expectedProps.put("zoidberg.hostname", "emma.iplantcollaborative.org");
        expectedProps.put("org.iplantc.services.zoidberg.components",
                "http://emma.iplantcollaborative.org/components");
        expectedProps.put("org.iplantc.services.zoidberg.formats",
                "http://emma.iplantcollaborative.org/formats");
        expectedProps.put("org.iplantc.services.zoidberg.propertytypes",
                "http://emma.iplantcollaborative.org/property-types");
        expectedProps.put("org.iplantc.services.zoidberg.ruletypes",
                "http://emma.iplantcollaborative.org/rule-types");
        expectedProps.put("org.iplantc.services.zoidberg.uuid",
                "http://emma.iplantcollaborative.org/uuid");
        expectedProps.put("org.iplantc.services.zoidberg.infotypes",
                "http://emma.iplantcollaborative.org/info-types");
        expectedProps.put("org.iplantc.services.zoidberg.inprogress",
                "http://emma.iplantcollaborative.org/in-progress");
        expectedProps.put("org.iplantc.services.zoidberg.getuserhomedir",
                "http://emma.iplantcollaborative.org/nibblonian/home");
        expectedProps.put("org.iplantc.services.zoidberg.fileupload",
                "http://emma.iplantcollaborative.org/nibblonian/file/upload");
        return expectedProps;
    }

    private PropertiesConfiguration createPropertiesConfigurationForInterpolationTest() {
        PropertiesConfiguration propsConfig = new PropertiesConfiguration();

        propsConfig.addProperty("prefix", "org.iplantc.services");
        propsConfig.addProperty("zoidberg.hostname", "emma.iplantcollaborative.org");
        propsConfig.addProperty("org.iplantc.services.zoidberg.components",
                "http://${zoidberg.hostname}/components");
        propsConfig.addProperty("org.iplantc.services.zoidberg.formats",
                "http://${zoidberg.hostname}/formats");
        propsConfig.addProperty("org.iplantc.services.zoidberg.propertytypes",
                "http://${zoidberg.hostname}/property-types");
        propsConfig.addProperty("org.iplantc.services.zoidberg.ruletypes",
                "http://${zoidberg.hostname}/rule-types");
        propsConfig
                .addProperty("org.iplantc.services.zoidberg.uuid", "http://${zoidberg.hostname}/uuid");
        propsConfig.addProperty("org.iplantc.services.zoidberg.infotypes",
                "http://${zoidberg.hostname}/info-types");
        propsConfig.addProperty("org.iplantc.services.zoidberg.inprogress",
                "http://${zoidberg.hostname}/in-progress");
        propsConfig.addProperty("org.iplantc.services.zoidberg.getuserhomedir",
                "http://${zoidberg.hostname}/nibblonian/home");
        propsConfig.addProperty("org.iplantc.services.zoidberg.fileupload",
                "http://${zoidberg.hostname}/nibblonian/file/upload");
        return propsConfig;
    }

    private Properties createFromExpected() {
        Properties props = new Properties();

        // prefix is a required key/prop.
        props.put("prefix", "org.iplantc.services");
        // the rest of the values that were in the properties values as of the creation of this file
        props.put("org.iplantc.services.acctmgmt.fetchStates",
                "http://emma.iplantcollaborative.org/accountmanagementv2/fetch-states");
        props.put("org.iplantc.services.acctmgmt.fetchPositions",
                "http://emma.iplantcollaborative.org/accountmanagementv2/fetch-positions");
        props.put("org.iplantc.services.acctmgmt.fetchResearchAreas",
                "http://emma.iplantcollaborative.org/accountmanagementv2/fetch-research-areas");
        props.put("org.iplantc.services.acctmgmt.fetchFundingAgencies",
                "http://emma.iplantcollaborative.org/accountmanagementv2/fetch-funding-agencies");
        props.put("org.iplantc.services.acctmgmt.fetchAcctDetails",
                "http://emma.iplantcollaborative.org/accountmanagementv2/fetch-account-details");
        props.put("org.iplantc.services.acctmgmt.updateAccounts",
                "http://emma.iplantcollaborative.org/accountmanagementv2/update-accounts");
        props.put("org.iplantc.services.acctmgmt.updatePassword",
                "http://emma.iplantcollaborative.org/accountmanagementv2/update-password");
        props.put("org.iplantc.services.acctmgmt.fetchUserOptDetails",
                "http://emma.iplantcollaborative.org/accountmanagementv2/fetch-user-options-details");

        return props;
    }

    private Properties createProperties() {
        Properties props = new Properties();
        // prefix is a required key/prop.
        props.put("prefix", "org.iplantc.services");
        props.put("org.iplantc.services.acctmgmt.fetchStates",
                "http://ndy.sixfifty.org/accountmanagementv2/fetch-states");
        props.put("org.iplantc.services.acctmgmt.fetchPositions",
                "http://ndy.sixfifty.org/accountmanagementv2/fetch-positions");
        props.put("org.iplantc.services.acctmgmt.fetchResearchAreas",
                "http://ndy.sixfifty.org/accountmanagementv2/fetch-research-areas");
        props.put("org.iplantc.services.acctmgmt.fetchFundingAgencies",
                "http://ndy.sixfifty.org/accountmanagementv2/fetch-funding-agencies");
        props.put("org.iplantc.services.acctmgmt.fetchAcctDetails",
                "http://ndy.sixfifty.org/accountmanagementv2/fetch-account-details");

        return props;
    }
}
