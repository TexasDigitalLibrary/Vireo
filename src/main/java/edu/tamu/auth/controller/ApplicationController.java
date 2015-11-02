/* 
 * ApplicationController.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.auth.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.tamu.auth.model.jwt.JWTtoken;
import edu.tamu.auth.service.HttpService;
import edu.tamu.framework.aspect.annotation.SkipAop;
import edu.tamu.framework.aspect.annotation.Auth;


/** 
 * Authorization Service Application Controller.
 * 
 * @author
 *
 */
@RestController
@RequestMapping("auth")
public class ApplicationController {
	
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
	
	@Autowired
	private HttpService httpService;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	/**
	 * Anonymous token endpoint. Returns anonymous token.
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
	@RequestMapping("/anonymous")
	@SkipAop
	@Auth
	protected String anonymous(@RequestParam() Map<String,String> params, @RequestHeader() Map<String,String> headers) throws InvalidKeyException, NoSuchAlgorithmException, IllegalStateException, UnsupportedEncodingException, JsonProcessingException {
		JWTtoken token = new JWTtoken(secret_key, expiration);
		token.makeClaim("lastName", "Anonymous");
		token.makeClaim("firstName", "Role");
		token.makeClaim("netid", "anonymous");
		token.makeClaim("uin", "000000000");
		token.makeClaim("email", "");
		token.makeClaim("role", "ROLE_ANONYMOUS");
		token.makeClaim("exp", String.valueOf(((new Date()).getTime() + 3155692597470L)));
		return token.getTokenAsString();
	}
	
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
	
	/**
	 * Admin endpoint. Checks if user uin is an admin. Queries LDAP with netid and returns jwt of assumed user.
	 *
	 * @param       params    		@RequestParam() Map<String,String>
	 * @param       headers    		@RequestHeader() Map<String,String>
	 *
	 * @return     	Map<String, JWTtoken>
	 * @throws Exception 
	 * 
	 */
	@RequestMapping("/admin")
	@SuppressWarnings("unchecked")
	@SkipAop
	@Auth
	public Map<String, Object> admin(@RequestParam() Map<String,String> params, @RequestHeader() Map<String,String> headers) throws Exception {
		
		boolean isAdmin = false;
		String uin = headers.get("tamuuin");
		for(String a : admins) {
			if(uin.equals(a)) {
				isAdmin = true;
				break;
			}
		}
		
		Map<String, Object> assumedJwt = new HashMap<String, Object>();
		
		if(!isAdmin) {
			assumedJwt.put("forbidden", null);
			return assumedJwt;
		}
		
		String netid = params.get("netid");
		
		
		Map<String, String> creds = new HashMap<String,String>();
		
		
		String response = httpService.makeHttpRequest("http://php.library.tamu.edu/utilities/get_person_info.php?netid=" + netid, "GET");
		
		Map<String, String> info = objectMapper.readValue(response, Map.class);
		
		
		if(info.get("result") != null) {
			assumedJwt.put("invalid", "netid not found");
			return assumedJwt;
		}
		
		creds.put("edupersonprincipalnameunscoped", netid);
		
		creds.put("tamuuin", info.get("uin"));
		
		creds.put("tdl-sn", info.get("last_name"));
		creds.put("tdl-givenname", info.get("first_name"));
		creds.put("tdl-mail", info.get("tamu_preferred_alias"));
				
		String affiliation = info.get("employee_type_name");
		
		if("".equals(affiliation) || "Student".equals(affiliation)) {
			String classification = info.get("classification_name").split(" ")[0].replace(",", "");
			creds.put("tdl-metadata-edupersonaffiliation", classification);
		}
		else {
			creds.put("tdl-metadata-edupersonaffiliation", affiliation);
		}
		assumedJwt.put("assumed", makeToken(creds));
		
		return assumedJwt;
	}

	
	/**
	 * 
	 * 
	 * @param 		netid			String
	 * 
	 * @return		String
	 * 
	 * @throws		Exception
	 * 
	 */
	protected String getUin(final String netid) throws Exception {
 
		String url = "http://php.library.tamu.edu/utilities/find_netid_uin.php?netid="+netid;
 
		URL obj = new URL(url);
		
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
 
		con.setRequestMethod("GET");
 
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		
		String inputLine;
		
		StringBuffer response = new StringBuffer();
 
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		
		in.close();
		
		String res = response.toString();
		res = res.substring(1, res.length()-1);
		res = res.split(",")[1];
		res = res.split(":")[1];
		
		return res;
		
	}	
	
}
