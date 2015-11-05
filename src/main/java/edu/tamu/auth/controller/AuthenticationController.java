/* 
 * AuthenticationController.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.auth.controller;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.JsonProcessingException;

import edu.tamu.auth.model.jwt.JWTtoken;
import edu.tamu.framework.aspect.annotation.Auth;
import edu.tamu.framework.aspect.annotation.SkipAop;


/** 
 * Authorization Service Application Controller.
 * 
 * @author
 *
 */
@RestController
@RequestMapping("/auth")
public class AuthenticationController {
	
	@Autowired
	private Environment env;
	
	@Value("${auth.security.secret_key}")
    private String secret_key;
	
	@Value("${auth.shib.keys}")
	private String[] shibKeys;
	
	@Value("${auth.authority.admins}")
	private String[] admins;
	
	@Value("${auth.security.jwt_expiration}")
	private Long expiration;
		
	/**
	 * Root endpoint. Returns headers which contain all Shibboleth attributes.
	 *
	 * @param       headers    		@RequestHeader() Map<String,String>
	 *
	 * @return     	Map<String, String>
	 *
	 */
	@RequestMapping("/")
	@SkipAop
	@Auth
	public Map<String, String> index(@RequestHeader() Map<String,String> headers) {
		return headers;
	}
	
	/**
	 * Token endpoint. Returns a token with credentials from Shibboleth in payload.
	 *
	 * @param       params    		@RequestParam() Map<String,String>
	 * @param       headers    		@RequestHeader() Map<String,String>
	 *
	 * @return      ModelAndView
	 *
	 * @exception   InvalidKeyException
	 * @exception   NoSuchAlgorithmException
	 * @exception   IllegalStateException
	 * @exception   UnsupportedEncodingException
	 * @exception   JsonProcessingException
	 * 
	 */
	@RequestMapping("/token")
	@SkipAop
	@Auth
	public ModelAndView token(@RequestParam() Map<String,String> params, @RequestHeader() Map<String,String> headers) throws InvalidKeyException, NoSuchAlgorithmException, IllegalStateException, UnsupportedEncodingException, JsonProcessingException {
		String referer = params.get("referer");
		if(referer == null) System.err.println("No referer in header!!");
		return new ModelAndView("redirect:" + referer + "?jwt=" + makeToken(headers).getTokenAsString());
	}
	
	/**
	 * Refresh endpoint. Returns a new token with credentials from Shibboleth in payload.
	 *
	 * @param       params    		@RequestParam() Map<String,String>
	 * @param       headers    		@RequestHeader() Map<String,String>
	 *
	 * @return      JWTtoken
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
	@Auth
	public JWTtoken refresh(@RequestParam() Map<String,String> params, @RequestHeader() Map<String,String> headers) throws InvalidKeyException, NoSuchAlgorithmException, IllegalStateException, UnsupportedEncodingException, JsonProcessingException {
		return makeToken(headers);
	}
	
	/**
	 * Constructs a token from selected Shibboleth headers.
	 *
	 * @param       headers    		Map<String, String>
	 *
	 * @return      JWTtoken
	 *
	 * @exception   InvalidKeyException
	 * @exception   NoSuchAlgorithmException
	 * @exception   IllegalStateException
	 * @exception   UnsupportedEncodingException
	 * @exception   JsonProcessingException
	 * 
	 */
	private JWTtoken makeToken(Map<String, String> headers) throws InvalidKeyException, JsonProcessingException, NoSuchAlgorithmException, IllegalStateException, UnsupportedEncodingException {		
		JWTtoken token = new JWTtoken(secret_key, expiration);		
		for(String k : shibKeys) {
			String p = headers.get(env.getProperty("shib."+k, ""));
			token.makeClaim(k, p);
			System.out.println("Adding " + k +": " + p + " to JWT.");
		}
		return token;		
	}
	
}
