package edu.tamu.auth.controller;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.tdl.vireo.config.constant.ConfigurationName;
import org.tdl.vireo.model.repo.ConfigurationRepo;

import com.fasterxml.jackson.core.JsonProcessingException;

import edu.tamu.framework.aspect.annotation.SkipAop;
import edu.tamu.framework.model.jwt.Jwt;
import edu.tamu.framework.util.JwtUtility;

/**
 *
 *
 * @author
 *
 */
@RestController
@RequestMapping("/mockauth")
public class MockTokenController {

    @Autowired
    private Environment env;

    @Autowired
    private JwtUtility jwtUtility;

    @Autowired
    private ConfigurationRepo configurationRepo;

    @Value("${auth.security.jwt.secret-key}")
    private String secret_key;

    @Value("${auth.security.jwt-expiration}")
    private Long expiration;

    @Value("${shib.keys}")
    private String[] shibKeys;

    private static final Logger logger = Logger.getLogger(MockTokenController.class);

    /**
     * Token endpoint. Returns a token with credentials from Shibboleth in payload.
     *
     * @param params
     * @RequestParam() Map<String,String>
     * @param headers
     * @RequestHeader() Map<String,String>
     *
     * @return ModelAndView
     *
     * @exception InvalidKeyException
     * @exception NoSuchAlgorithmException
     * @exception IllegalStateException
     * @exception UnsupportedEncodingException
     * @exception JsonProcessingException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     * @throws NoSuchPaddingException
     *
     */
    @RequestMapping("/token")
    @SkipAop
    public ModelAndView token(@RequestParam() Map<String, String> params, @RequestHeader() Map<String, String> headers) throws InvalidKeyException, NoSuchAlgorithmException, IllegalStateException, UnsupportedEncodingException, JsonProcessingException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {

        String referer = params.get("referer");
        if (referer == null) {
            System.err.println("No referer in header!!");
        }

        ModelAndView tokenResponse = null;
        try {
            tokenResponse = new ModelAndView("redirect:" + referer + "?jwt=" + makeToken(params, headers).getTokenAsString());
        } catch (InvalidKeyException | JsonProcessingException | NoSuchAlgorithmException | IllegalStateException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return tokenResponse;
    }

    /**
     * Refresh endpoint. Returns a new token with credentials from Shibboleth in payload.
     *
     * @param params
     * @RequestParam() Map<String,String>
     * @param headers
     * @RequestHeader() Map<String,String>
     *
     * @return Jwt
     *
     * @exception InvalidKeyException
     * @exception NoSuchAlgorithmException
     * @exception IllegalStateException
     * @exception UnsupportedEncodingException
     * @exception JsonProcessingException
     *
     */
    @RequestMapping("/refresh")
    @SkipAop
    public Jwt refresh(@RequestParam() Map<String, String> params, @RequestHeader() Map<String, String> headers) throws InvalidKeyException, NoSuchAlgorithmException, IllegalStateException, UnsupportedEncodingException, JsonProcessingException {
        return makeToken(params, headers);
    }

    /**
     * Constructs a token from selected Shibboleth headers.
     *
     * @param headers
     *            Map<String, String>
     *
     * @return Jwt
     *
     * @exception InvalidKeyException
     * @exception NoSuchAlgorithmException
     * @exception IllegalStateException
     * @exception UnsupportedEncodingException
     * @exception JsonProcessingException
     *
     */
    private synchronized Jwt makeToken(Map<String, String> params, Map<String, String> headers) throws InvalidKeyException, JsonProcessingException, NoSuchAlgorithmException, IllegalStateException, UnsupportedEncodingException {
        Jwt newToken = null;

        String token = params.get("token");

        // get shib headers out of DB
        String netIdHeader = configurationRepo.getValue(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_NETID, "netid");
        String birthYearHeader = configurationRepo.getValue(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_BIRTH_YEAR, "birthYear");
        String middleNameHeader = configurationRepo.getValue(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_MIDDLE_NAME, "middleName");
        String firstNameHeader = configurationRepo.getValue(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_FIRST_NAME, "firstName");
        String lastNameHeader = configurationRepo.getValue(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_LAST_NAME, "lastName");
        String emailHeader = configurationRepo.getValue(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_EMAIL, "email");
        String orcidHeader = configurationRepo.getValue(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_ORCID, "orcid");
        String institutionIdentifierHeader = configurationRepo.getValue(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_INSTITUTION_IDENTIFIER, "institutionid");
        String institutionalIdentifierHeader = configurationRepo.getValue(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_INSTITUTIONAL_IDENTIFIER, "uin");
        String permEmailHeader = configurationRepo.getValue(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_PERMANENT_EMAIL_ADDRESS, "permanentEmailAddress");
        String permPhoneHeader = configurationRepo.getValue(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_PERMANENT_PHONE_NUMBER, "permanentPhoneNumber");
        String permAddressHeader = configurationRepo.getValue(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_PERMANENT_POSTAL_ADDRESS, "permanentPostalAddress");

        newToken = jwtUtility.craftToken();

        if (token != null) {
            for (Map.Entry<String, String> entry : jwtUtility.validateJWT(token).entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                if (key.equals("exp")) {
                    continue;
                }
                newToken.makeClaim(key, value);
            }
        } else {

            String mockUser = params.get("mock");

            if (mockUser != null) {
                if (mockUser.equals("assumed")) {
                    for (String k : shibKeys) {
                        String p = headers.get(env.getProperty("shib." + k, ""));
                        newToken.makeClaim(k, p);
                        logger.info("Adding " + k + ": " + p + " to jwt.");
                    }
                } else if (mockUser.equals("admin")) {
                    newToken.makeClaim(netIdHeader, "aggieJack");
                    newToken.makeClaim(institutionIdentifierHeader, "inst-id-123");
                    newToken.makeClaim(institutionalIdentifierHeader, "123456789");
                    newToken.makeClaim(lastNameHeader, "Daniels");
                    newToken.makeClaim(firstNameHeader, "Jack");
                    newToken.makeClaim(emailHeader, "aggieJack@tamu.edu");

                    newToken.makeClaim(birthYearHeader, "1977");
                    newToken.makeClaim(middleNameHeader, "Jay");
                    newToken.makeClaim(orcidHeader, "0000-0000-0000-0000");
                    newToken.makeClaim(permEmailHeader, "aggieJack@tamu.edu");
                    newToken.makeClaim(permPhoneHeader, "800-555-1234");
                    newToken.makeClaim(permAddressHeader, "5000 TAMU");
                } else {
                    newToken.makeClaim(netIdHeader, "bobBoring");
                    newToken.makeClaim(institutionIdentifierHeader, "inst-id-123");
                    newToken.makeClaim(institutionalIdentifierHeader, "987654321");
                    newToken.makeClaim(lastNameHeader, "Boring");
                    newToken.makeClaim(firstNameHeader, "Bob");
                    newToken.makeClaim(emailHeader, "bobBoring@tamu.edu");

                    newToken.makeClaim(birthYearHeader, "1978");
                    newToken.makeClaim(middleNameHeader, "Be");
                    newToken.makeClaim(orcidHeader, "0000-0000-0000-0001");
                    newToken.makeClaim(permEmailHeader, "bobBoring@tamu.edu");
                    newToken.makeClaim(permPhoneHeader, "800-555-4321");
                    newToken.makeClaim(permAddressHeader, "5000 TAMU");
                }
            }
        }

        return newToken;
    }

}
