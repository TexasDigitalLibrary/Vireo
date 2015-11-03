/* 
 * JWTclaim.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.auth.model.jwt;

import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/** 
 * JSON Web Token Claims.
 * 
 * @author
 *
 */
public class JWTclaim {
	
	private Map<String, String> claim;
	
	/**
	 * Constructor.
	 *
	 * @param       claim    		Map<String, String>
	 *
	 * @exception   JsonProcessingException
	 * 
	 */
	public JWTclaim(Map<String, String> claim) throws JsonProcessingException {
		this.claim = claim;
	}

	/**
	 * Retrieve contents of claims as a Map.
	 *
	 * @return      Map<String, String>
	 *
	 */
	public Map<String, String> getContentAsMap() {
		return claim;
	}

	/**
	 * Set contents of claim.
	 *
	 * @param       claim    		Map<String, String>
	 *
	 */
	public void setContent(Map<String, String> claim) {
		this.claim = claim;
	}
	
	/**
	 * Add claims.
	 *
	 * @param       key    			String
	 * @param       value    		String
	 *
	 */
	public void putClaim(String key, String value) {
		this.claim.put(key, value);
	}
	
	/**
	 * Retrieve claim as a JSON.
	 *
	 * @return      String
	 *
	 * @exception   JsonProcessingException
	 * 
	 */
	public String getClaimAsJSON() throws JsonProcessingException {
		return new ObjectMapper().writeValueAsString(claim);
	}
	
}
