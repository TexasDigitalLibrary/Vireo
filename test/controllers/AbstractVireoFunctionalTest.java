package controllers;

import java.util.HashMap;
import java.util.Map;

import play.mvc.Http.Response;
import play.mvc.Router;
import play.test.FunctionalTest;

/**
 * Common functions used by all Vireo functional tests.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public abstract class AbstractVireoFunctionalTest extends FunctionalTest {

	/**
	 * Log in as a default administrator with full privileges to the system.
	 * 
	 * User: bthornton@gmail.com
	 * Password: password
	 * 
	 * These values are derived from the TestDataLoader
	 */
	public void LOGIN() {
		LOGIN("bthornton@gmail.com","password");
	}

	/**
	 * Log the user in using the default password 'password'
	 * 
	 * @param email
	 *            The email address identifying the Person object to login.
	 */
	public void LOGIN(String email) {
		LOGIN(email, "password");
	}
	
	/**
	 * Log the user in using PasswordAuthentication. This method will fail if
	 * another user has already authenticated, or if the username and password
	 * pair is incorrect.
	 * 
	 * @param email
	 *            The email address identifying the Person object to login.
	 * @param password
	 *            The password to authenticate with.
	 */
	public void LOGIN(String email, String password) {
		// Get our urls
		Map<String,Object> routeArgs = new HashMap<String,Object>();
		routeArgs.put("methodName", "PasswordAuthentication");
		final String LOGIN_PASSWORD_URL = Router.reverse("Authentication.loginMethod",routeArgs).url;
		
		// First, Check that no one is currently logged in.
		Response response = GET(LOGIN_PASSWORD_URL);
		assertFalse(response.cookies.get("PLAY_SESSION").value.contains("PasswordAuthentication"));		
		
		// Second, Login
		Map<String,String> loginForm = new HashMap<String,String>();
		loginForm.put("username", email);
		loginForm.put("password", password);
		loginForm.put("submit_login","Login");
		response = POST(LOGIN_PASSWORD_URL,loginForm);
		
		assertTrue(response.cookies.get("PLAY_SESSION").value.contains("PasswordAuthentication"));		
	}

	/**
	 * Log out the current user 
	 */
	public void LOGOUT() {

		// Our one url
		final String LOGOUT_URL = Router.reverse("Authentication.logout").url;
		
		// Logout
		Response response = GET(LOGOUT_URL);
		assertFalse(response.cookies.get("PLAY_SESSION").value.contains("Authentication"));
	}
}
