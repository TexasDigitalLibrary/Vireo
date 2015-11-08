/* 
 * JWTtoken.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.auth.model.jwt;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.fasterxml.jackson.core.JsonProcessingException;

import edu.tamu.auth.util.jwt.JWTservice;

/** 
 * JSON Web Token.
 * 
 * @author
 *
 */
public class JWTtoken {
	
	private JWTheader header;
	private JWTclaim claim;
	private String secret;
		
	/**
	 * Constructor.
	 *
	 * @param       content    		Map<String, String>
	 * @param       secret    		String
	 *
	 * @exception   JsonProcessingException
	 * @exception   InvalidKeyException
	 * @exception   NoSuchAlgorithmException
	 * @exception   IllegalStateException
	 * @exception   UnsupportedEncodingException
	 * 
	 */
	public JWTtoken(Map<String, String> content, String secret, Long expiration) throws JsonProcessingException, InvalidKeyException, NoSuchAlgorithmException, IllegalStateException, UnsupportedEncodingException {
		
		JWTheader newHeader = new JWTheader(new HashMap<String, String>());
		
		JWTclaim newClaim = new JWTclaim(content);
			
		this.header = newHeader;
		this.claim = newClaim;
		this.secret = secret;
		
		makeClaim("exp", Objects.toString(Calendar.getInstance().getTime().getTime()+expiration, null));
		
	}
	
	/**
	 * Constructor.
	 *
	 * @param       secret			String
	 *
	 * @exception   JsonProcessingException
	 * @exception   InvalidKeyException
	 * @exception   NoSuchAlgorithmException
	 * @exception   IllegalStateException
	 * @exception   UnsupportedEncodingException
	 * 
	 */
	public JWTtoken(String secret, Long expiration) throws JsonProcessingException, InvalidKeyException, NoSuchAlgorithmException, IllegalStateException, UnsupportedEncodingException {			
		this.header = new JWTheader(new HashMap<String, String>());;
		this.claim = new JWTclaim(new HashMap<String, String>());
		this.secret = secret;
				
		makeClaim("exp", Objects.toString(Calendar.getInstance().getTime().getTime()+expiration, null));	
	}
	
	/**
	 * Add claim to token.
	 *
	 * @param       key    			String
	 * @param       value    		String
	 *
	 */
	public void makeClaim(String key, String value) {
		this.claim.putClaim(key, value);
	}
	
	/**
	 * Retrieve token as a String.
	 *
	 * @return      String
	 *
	 * @exception   InvalidKeyException
	 * @exception   NoSuchAlgorithmException
	 * @exception   IllegalStateException
	 * @exception   UnsupportedEncodingException
	 * @exception   JsonProcessingException
	 * 
	 */
	public String getTokenAsString() throws InvalidKeyException, NoSuchAlgorithmException, IllegalStateException, UnsupportedEncodingException, JsonProcessingException {
		
		Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
		SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
		sha256_HMAC.init(secret_key);
		
		JWTservice jwtService = new JWTservice();
		
		String encodedHeader = jwtService.encodeJSON(header.getHeaderAsJSON());
		String encodedClaim = jwtService.encodeJSON(claim.getClaimAsJSON());
				
		return encodedHeader+"."+encodedClaim+"."+jwtService.hashSignature(encodedHeader+"."+encodedClaim, secret);
				
	}
	
}
