package controllers;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import play.Play;
import play.mvc.Http.Response;
import play.mvc.Router;
import play.test.FunctionalTest;

/**
 * Test all the actions in the Authentication controller such as loging in,
 * logging out, registering new users, your profile etc...
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public class AuthenticationTest extends FunctionalTest {
	
	/**
	 * Test logging in with a password.
	 */
	@Test
	public void testPasswordLogin() {
		// Get all our URLs
		final String INDEX_URL = Router.reverse("Application.index").url;
		final String LOGIN_LIST_URL = Router.reverse("Authentication.loginList").url;
		Map<String,Object> routeArgs = new HashMap<String,Object>();
		routeArgs.put("methodName", "PasswordAuthentication");
		final String LOGIN_PASSWORD_URL = Router.reverse("Authentication.loginMethod",routeArgs).url;
		final String PROFILE_URL = Router.reverse("Authentication.profile").url;
		
		// Step 1: Visit the login page to get a list of methods available.
		Response response = GET(LOGIN_LIST_URL);
		assertIsOk(response);
		assertContentMatch(Play.configuration.getProperty("auth.pass.name"), response);
		assertContentMatch(LOGIN_PASSWORD_URL,response);
		
		// Step 2: Go to the login page for password
		response = GET(LOGIN_PASSWORD_URL);
		assertIsOk(response);		
		assertContentMatch("<input[^>]*name=\"username\"",response);
		assertContentMatch("<input[^>]*name=\"password\"",response);
		
		// Step 3: Give correct username / password
		Map<String,String> loginForm = new HashMap<String,String>();
		loginForm.put("username", "bthornton@gmail.com");
		loginForm.put("password", "password"); // yeah, the password is password.
		loginForm.put("submit_login","Login");
		response = POST(LOGIN_PASSWORD_URL,loginForm);
		assertHeaderEquals("Location", INDEX_URL, response);
		assertTrue(response.cookies.get("PLAY_SESSION").value.contains("Billy"));
		assertTrue(response.cookies.get("PLAY_SESSION").value.contains("Thornton"));
		
		// Step 4: Verify by viewing profile page.
		response = GET(PROFILE_URL);
		assertIsOk(response);
		assertContentMatch("Billy",response); // first name
		assertContentMatch("Thornton",response); // last name
	}
	
	/**
	 * Test failing to login with an incorrect username or password.
	 */
	@Test
	public void testNegativePasswordLogin() {
		
		Map<String,Object> routeArgs = new HashMap<String,Object>();
		routeArgs.put("methodName", "PasswordAuthentication");
		final String LOGIN_PASSWORD_URL = Router.reverse("Authentication.loginMethod",routeArgs).url;
		
		// Check that if we don't provide anything the password login form fails.
		Map<String,String> loginForm = new HashMap<String,String>();
		loginForm.put("submit_login","Login");
		Response response = POST(LOGIN_PASSWORD_URL,loginForm);
		
		assertIsOk(response);
		assertContentMatch("The username and/or password does not exist. Please try again.",response);
		
		// Try providing a wrong password.
		loginForm = new HashMap<String,String>();
		loginForm.put("username", "bthornton@gmail.com");
		loginForm.put("password", "incorrect");
		loginForm.put("submit_login","Login");
		response = POST(LOGIN_PASSWORD_URL,loginForm);
		
		assertIsOk(response);
		assertContentMatch("The username and/or password does not exist. Please try again.",response);
		assertContentMatch("value=\"bthornton@gmail.com\"",response);
		assertContentMatch("value=\"incorrect\"",response);
	}
	
	/**
	 * Test logging in with shibboleth
	 */
	@Test
	public void testShibbolethLogin() {
		
		System.out.println("SHIB: testShibbolethLogin() ");

		
		// Get all our URLs
		final String INDEX_URL = Router.reverse("Application.index").url;
		final String LOGIN_LIST_URL = Router.reverse("Authentication.loginList").url;
		Map<String,Object> routeArgs = new HashMap<String,Object>();
		routeArgs.put("methodName", "ShibbolethAuthentication");
		final String LOGIN_SHIB_URL = Router.reverse("Authentication.loginMethod",routeArgs).url;
		final String LOGIN_SHIB_RETURN_URL = Router.reverse("Authentication.loginReturn",routeArgs).url;
		final String PROFILE_URL = Router.reverse("Authentication.profile").url;
		
		// Step 1: Visit the login page to get a list of methods available
		Response response = GET(LOGIN_LIST_URL);
		assertIsOk(response);
		assertContentMatch(Play.configuration.getProperty("auth.shib.name"), response);
		assertContentMatch(LOGIN_SHIB_URL,response);
		
		// Step 2: Click on the shibboleth URL
		response = GET(LOGIN_SHIB_URL);
		assertHeaderEquals("Location", LOGIN_SHIB_RETURN_URL, response);
		response = GET(LOGIN_SHIB_RETURN_URL);
		assertHeaderEquals("Location", INDEX_URL, response);
		System.out.println(response.cookies.get("PLAY_SESSION").value);
		assertTrue(response.cookies.get("PLAY_SESSION").value.contains("Billy"));
		assertTrue(response.cookies.get("PLAY_SESSION").value.contains("Thornton"));
		
		// Step 3: Verify by viewing profile page
		response = GET(PROFILE_URL);
		assertIsOk(response);
		assertContentMatch("Billy",response); // first name
		assertContentMatch("Thornton",response); // last name
	}
	
	/**
	 * Test logging.
	 */
	@Test
	public void testLogout() {
		
		// Get all our URLs
		Map<String,Object> routeArgs = new HashMap<String,Object>();
		routeArgs.put("methodName", "ShibbolethAuthentication");
		final String LOGIN_SHIB_RETURN_URL = Router.reverse("Authentication.loginReturn",routeArgs).url;
		final String LOGOUT_URL = Router.reverse("Authentication.logout").url;

		// Do a short cut mock-shib login
		Response response = GET(LOGIN_SHIB_RETURN_URL);
		assertTrue(response.cookies.get("PLAY_SESSION").value.contains("Billy"));
		assertTrue(response.cookies.get("PLAY_SESSION").value.contains("Thornton"));
		
		response = GET(LOGOUT_URL);
		assertFalse(response.cookies.get("PLAY_SESSION").value.contains("Billy"));
		assertFalse(response.cookies.get("PLAY_SESSION").value.contains("Thornton"));
	}
	
	/**
	 * Test that when visiting an restricted page you are interrupted asked to
	 * login, and then returned to where you were trying to access.
	 */
	@Test
	public void testPasswordInturrupt() {
		
		// Get all our URLs
		final String INDEX_URL = Router.reverse("Application.index").url;
		final String LOGIN_LIST_URL = Router.reverse("Authentication.loginList").url;
		Map<String,Object> routeArgs = new HashMap<String,Object>();
		routeArgs.put("methodName", "PasswordAuthentication");
		final String LOGIN_PASSWORD_URL = Router.reverse("Authentication.loginMethod",routeArgs).url;
		final String PROFILE_URL = Router.reverse("Authentication.profile").url;
		
		// Step 1: Visit a restricted page, profile.
		Response response = GET(PROFILE_URL);
		assertHeaderEquals("Location", LOGIN_LIST_URL, response);
		
		// Step 2: Follow the redirect.
		response = GET(LOGIN_LIST_URL);
		assertIsOk(response);
		assertContentMatch(Play.configuration.getProperty("auth.pass.name"), response);
		assertContentMatch(LOGIN_PASSWORD_URL,response);
		
		// Step 2: Go to the login page for password
		response = GET(LOGIN_PASSWORD_URL);
		assertIsOk(response);		
		assertContentMatch("<input[^>]*name=\"username\"",response);
		assertContentMatch("<input[^>]*name=\"password\"",response);
		
		// Step 3: Check that we are sent back to the profile after loging in.
		Map<String,String> loginForm = new HashMap<String,String>();
		loginForm.put("username", "bthornton@gmail.com");
		loginForm.put("password", "password"); // yeah, the password is password.
		loginForm.put("submit_login","Login");
		response = POST(LOGIN_PASSWORD_URL,loginForm);
		assertHeaderEquals("Location", PROFILE_URL, response);
		assertTrue(response.cookies.get("PLAY_SESSION").value.contains("Billy"));
		assertTrue(response.cookies.get("PLAY_SESSION").value.contains("Thornton"));
	}

}
