package edu.tamu.auth.controller;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
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

        // get shib headers
        Map<String,String> shibSettings = new HashMap<String,String>();
        Map<String,String> shibValues = new HashMap<String,String>();

        shibSettings.put(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_NETID, "netid");
        shibSettings.put(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_BIRTH_YEAR, "birthYear");
        shibSettings.put(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_MIDDLE_NAME, "middleName");
        shibSettings.put(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_FIRST_NAME, "firstName");
        shibSettings.put(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_LAST_NAME, "lastName");
        shibSettings.put(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_EMAIL, "email");
        shibSettings.put(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_ORCID, "orcid");
        shibSettings.put(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_INSTITUTION_IDENTIFIER, "institutionid");
        shibSettings.put(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_INSTITUTIONAL_IDENTIFIER, "uin");
        shibSettings.put(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_PERMANENT_EMAIL_ADDRESS, "permanentEmailAddress");
        shibSettings.put(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_PERMANENT_PHONE_NUMBER, "permanentPhoneNumber");
        shibSettings.put(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_PERMANENT_POSTAL_ADDRESS, "permanentPostalAddress");
        
        shibSettings.forEach((k,v) -> {
        	String overrideValue = configurationRepo.getValueByNameAndType(k,"shibboleth");
        	shibValues.put(k, overrideValue != null ? overrideValue:v);
        });        

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
                    newToken.makeClaim(shibValues.get(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_NETID), "aggieJack");
                    newToken.makeClaim(shibValues.get(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_INSTITUTION_IDENTIFIER), "inst-id-123");
                    newToken.makeClaim(shibValues.get(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_INSTITUTIONAL_IDENTIFIER), "123456789");
                    newToken.makeClaim(shibValues.get(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_LAST_NAME), "Daniels");
                    newToken.makeClaim(shibValues.get(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_FIRST_NAME), "Jack");
                    newToken.makeClaim(shibValues.get(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_EMAIL), "aggieJack@tamu.edu");

                    newToken.makeClaim(shibValues.get(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_BIRTH_YEAR), "1977");
                    newToken.makeClaim(shibValues.get(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_MIDDLE_NAME), "Jay");
                    newToken.makeClaim(shibValues.get(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_ORCID), "0000-0000-0000-0000");
                    newToken.makeClaim(shibValues.get(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_PERMANENT_EMAIL_ADDRESS), "aggieJack@tamu.edu");
                    newToken.makeClaim(shibValues.get(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_PERMANENT_PHONE_NUMBER), "800-555-1234");
                    newToken.makeClaim(shibValues.get(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_PERMANENT_POSTAL_ADDRESS), "5000 TAMU");
                } else {
                    newToken.makeClaim(shibValues.get(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_NETID), "bobBoring");
                    newToken.makeClaim(shibValues.get(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_INSTITUTION_IDENTIFIER), "inst-id-123");
                    newToken.makeClaim(shibValues.get(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_INSTITUTIONAL_IDENTIFIER), "987654321");
                    newToken.makeClaim(shibValues.get(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_LAST_NAME), "Boring");
                    newToken.makeClaim(shibValues.get(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_FIRST_NAME), "Bob");
                    newToken.makeClaim(shibValues.get(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_EMAIL), "bobBoring@tamu.edu");

                    newToken.makeClaim(shibValues.get(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_BIRTH_YEAR), "1978");
                    newToken.makeClaim(shibValues.get(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_MIDDLE_NAME), "Be");
                    newToken.makeClaim(shibValues.get(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_ORCID), "0000-0000-0000-0001");
                    newToken.makeClaim(shibValues.get(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_PERMANENT_EMAIL_ADDRESS), "bobBoring@tamu.edu");
                    newToken.makeClaim(shibValues.get(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_PERMANENT_PHONE_NUMBER), "800-555-4321");
                    newToken.makeClaim(shibValues.get(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_PERMANENT_POSTAL_ADDRESS), "5000 TAMU");
                }
            }
        }

        return newToken;
    }

}
