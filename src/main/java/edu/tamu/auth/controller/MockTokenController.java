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

import com.fasterxml.jackson.core.JsonProcessingException;

import edu.tamu.framework.aspect.annotation.SkipAop;
import edu.tamu.framework.model.jwt.JWT;
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
     * @param       params          @RequestParam() Map<String,String>
     * @param       headers         @RequestHeader() Map<String,String>
     *
     * @return      ModelAndView
     *
     * @exception   InvalidKeyException
     * @exception   NoSuchAlgorithmException
     * @exception   IllegalStateException
     * @exception   UnsupportedEncodingException
     * @exception   JsonProcessingException
     * @throws BadPaddingException 
     * @throws IllegalBlockSizeException 
     * @throws NoSuchPaddingException 
     * 
     */
    @RequestMapping("/token")
    @SkipAop
    public ModelAndView token(@RequestParam() Map<String,String> params, @RequestHeader() Map<String,String> headers) throws InvalidKeyException, NoSuchAlgorithmException, IllegalStateException, UnsupportedEncodingException, JsonProcessingException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
            	
        String referer = params.get("referer");
        if(referer == null) {
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
     * @param       params          @RequestParam() Map<String,String>
     * @param       headers         @RequestHeader() Map<String,String>
     *
     * @return      JWT
     *
     * @exception   InvalidKeyException
     * @exception   NoSuchAlgorithmException
     * @exception   IllegalStateException
     * @exception   UnsupportedEncodingException
     * @exception   JsonProcessingException
     * 
     */
    @RequestMapping("/refresh")
    @SkipAop
    public JWT refresh(@RequestParam() Map<String,String> params, @RequestHeader() Map<String,String> headers) throws InvalidKeyException, NoSuchAlgorithmException, IllegalStateException, UnsupportedEncodingException, JsonProcessingException {            	    	    	
    	return makeToken(params, headers);
    }
    
    /**
     * Constructs a token from selected Shibboleth headers.
     *
     * @param       headers         Map<String, String>
     *
     * @return      JWT
     *
     * @exception   InvalidKeyException
     * @exception   NoSuchAlgorithmException
     * @exception   IllegalStateException
     * @exception   UnsupportedEncodingException
     * @exception   JsonProcessingException
     * 
     */    
    private JWT makeToken(@RequestParam() Map<String,String> params, Map<String, String> headers) throws InvalidKeyException, JsonProcessingException, NoSuchAlgorithmException, IllegalStateException, UnsupportedEncodingException {       

    	String token = params.get("token");
    	if(token != null) {    		
    		return jwtUtility.makeToken(jwtUtility.validateJWT(token));
    	}
    	
    	JWT newToken = new JWT(secret_key, expiration);
    	
    	String mockUser = params.get("mock");
    	    	
    	if(mockUser != null) {
    		 if(mockUser.equals("assumed")) {
	            for(String k : shibKeys) {
	                String p = headers.get(env.getProperty("shib."+k, ""));
	                newToken.makeClaim(k, p);
	                logger.info("Adding " + k +": " + p + " to JWT.");
	            }
	        }
	        else if(mockUser.equals("admin")) {        	
	        	newToken.makeClaim("netid", "aggieJack");
	        	newToken.makeClaim("uin", "123456789");
	        	newToken.makeClaim("lastName", "Daniels");
	        	newToken.makeClaim("firstName", "Jack");
	        	newToken.makeClaim("email", "aggieJack@tamu.edu");
	        }
	        else {
	        	newToken.makeClaim("netid", "bobBoring");
	        	newToken.makeClaim("uin", "987654321");
	        	newToken.makeClaim("lastName", "Boring");
	        	newToken.makeClaim("firstName", "Bob");
	        	newToken.makeClaim("email", "bobBoring@tamu.edu");
	        }
    	}
         
        return newToken;       
    }

}