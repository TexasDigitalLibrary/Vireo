package controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;
import org.tdl.vireo.model.Person;
import org.tdl.vireo.model.PersonRepository;
import org.tdl.vireo.model.jpa.JpaEmailTemplateImpl;
import org.tdl.vireo.security.SecurityContext;

import play.Play;
import play.db.jpa.JPA;
import play.libs.Mail;
import play.modules.spring.Spring;
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
	
	public static SecurityContext context = Spring.getBeanOfType(SecurityContext.class);
	public static PersonRepository personRepo = Spring.getBeanOfType(PersonRepository.class);
	
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
	
	
	//public static Pattern EMAIL_TOKEN = Pattern.compile("http://localhost:9000/[a-z]+\\?token=([a-zA-Z0-9\\-_]+)[\\n\\r]{1}");
	
	public static Pattern EMAIL_TOKEN = Pattern.compile("http://[^/]*/[a-z]+\\?token=([a-zA-Z0-9\\-_]+)[\\n\\r]{1}");
	
	
	/**
	 * This is one big test through the whole process. From email verification
	 * to completed the registration process.
	 */
	@Test
	public void testUserRegistration() throws InterruptedException {
		
		// Clear out the mail queues.
		Mail.Mock.reset();
		
		// Get all our URLS
		final String INDEX_URL = Router.reverse("Application.index").url;
		final String REGISTER_URL = Router.reverse("Authentication.register").url;
		final String PROFILE_URL = Router.reverse("Authentication.profile").url;
		
		// Step 1: Visit the registration page
		Response response = GET(REGISTER_URL);
		assertIsOk(response);
		assertContentMatch("New User Registration",response);
		
		// Step 2: Fill out the registration form to verify email address.
		Map<String,String> params = new HashMap<String,String>();
		params.put("email", "newregistration@email.com");
		params.put("submit_register", "Register");
		response = POST(REGISTER_URL,params);
		assertIsOk(response);
		assertContentMatch("Registration Email Sent",response);
		
		// Step 2: Wait for the email and the token.
		String recieved = null;
		for (int i = 0; i < 1000; i++) {
			Thread.yield();
			Thread.sleep(100);
			recieved = Mail.Mock.getLastMessageReceivedBy("newregistration@email.com");
			if (recieved != null)
				break;
		}
		assertNotNull(recieved);
		assertTrue(recieved.contains("Subject: Vireo Account Registration"));
		Matcher tokenMatcher = EMAIL_TOKEN.matcher(recieved);
		assertTrue(tokenMatcher.find());
		String token = tokenMatcher.group(1);
		assertNotNull(token);
		assertTrue(token.length() > 5);
		
		// Step 3: View the page to complete the registration process
		response = GET(REGISTER_URL+"?token="+token);
		assertIsOk(response);
		assertContentMatch("Your email address has been verified.",response);
		assertContentMatch("name=\"email\"\\s+value=\"newregistration@email.com\"",response);
		
		// Step 4: Complete the registration process
		params.clear();
		params.put("token",token);
		params.put("firstname","first");
		params.put("lastname", "last");
		params.put("password1", "password1");
		params.put("password2", "password1");
		params.put("submit_register","Register");
		response = POST(REGISTER_URL,params);
		System.out.println(getContent(response));
		assertHeaderEquals("Location", INDEX_URL, response);
		assertTrue(response.cookies.get("PLAY_SESSION").value.contains("first"));
		assertTrue(response.cookies.get("PLAY_SESSION").value.contains("last"));
		
		// Step 5: Verify that we are logged in.
		response = GET(PROFILE_URL);
		assertIsOk(response);
		assertContentMatch("first",response); // first name
		assertContentMatch("last",response); // last name
		
		// cleanup the user we created.
		context.turnOffAuthorization();
		Person person = personRepo.findPersonByEmail("newregistration@email.com");
		assertNotNull(person);
		assertEquals("first",person.getFirstName());
		assertEquals("last",person.getLastName());
		person.delete();
		context.restoreAuthorization();
		
		JPA.em().getTransaction().commit();
		JPA.em().getTransaction().begin();
	}

}
