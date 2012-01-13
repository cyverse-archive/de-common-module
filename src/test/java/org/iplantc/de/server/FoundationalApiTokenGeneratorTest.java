package org.iplantc.de.server;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for FoundationalApiTokenGenerator.
 * 
 * @author Dennis Roberts
 */
public class FoundationalApiTokenGeneratorTest {
    /**
     * The token generator used for each of the unit tests.
     */
    FoundationalApiTokenGenerator tokenGenerator;

    /**
     * Sets up each of the unit tests.
     * 
     * @throws IOException if the token generator can't be initialized.
     */
    @Before
    public void setUp() throws IOException {
        tokenGenerator = new FoundationalApiTokenGenerator("test.key");
    }

    /**
     * Verifies that the token generator can parse its own tokens.
     * 
     * @throws Exception if an error occurs.
     */
    @Test
    public void shouldGenerateParseableTokens() throws Exception {
        testGeneratedToken("dooley");
        testGeneratedToken("vaughn");
        testGeneratedToken("galaxy");
    }

    /**
     * Verifies that the token generator can parse a token that it generates for the given username.
     * 
     * @param username the username to include in the token.
     * @throws Exception if an error occurs.
     */
    private void testGeneratedToken(String username) throws Exception {
        String token = tokenGenerator.generateToken(username);
        assertTrue(tokenGenerator.decryptToken(token).matches(username + "\\|\\d+"));
    }

    /**
     * Verifies that we get an exception if we try to load a missing key file.
     * 
     * @throws Exception if an error occurs.
     */
    @Test(expected = IOException.class)
    public void shouldGetExceptionForMissingKeyFile() throws Exception {
        new FoundationalApiTokenGenerator("missing.key");
    }

    /**
     * Verifies that we get an exception if we try to load a key file that doesn't contain a key pair.
     * 
     * @throws Exception if an error occurs.
     */
    @Test(expected = IOException.class)
    public void shouldGetExceptionForBogusKeyFile() throws Exception {
        new FoundationalApiTokenGenerator("test.crt");
    }

    /**
     * Verifies that we get an exception if we try to load a key file without specifying a path.
     * 
     * @throws Exception if an error occurs.
     */
    @Test(expected = IOException.class)
    public void shouldGetExceptionForNullKeyFilePath() throws Exception {
        new FoundationalApiTokenGenerator(null);
    }
}
