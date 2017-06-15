package controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;
import org.tdl.vireo.model.Person;
import org.tdl.vireo.model.PersonRepository;
import org.tdl.vireo.model.RoleType;
import org.tdl.vireo.security.SecurityContext;

import play.Play;
import play.db.jpa.JPA;
import play.libs.Mail;
import play.modules.spring.Spring;
import play.mvc.Http.Response;
import play.mvc.Router;

/**
 * Test all the actions in the Authentication controller such as loging in,
 * logging out, registering new users, your profile etc...
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public class AuthenticationTest extends AbstractVireoFunctionalTest {
	
	// Constants
	public static Pattern EMAIL_TOKEN = Pattern.compile("http://[^/]*/[a-z]+\\?token=([a-zA-Z0-9\\-_]+)[\\n\\r]{1}");
	
	// Spring dependencies
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
		assertContentMatch("newregistration@email.com",response);
		
		// Step 4: Complete the registration process
		params.clear();
		params.put("token",token);
		params.put("firstName","first");
		params.put("lastName", "last");
		params.put("password1", "password1");
		params.put("password2", "password1");
		params.put("submit_register","Register");
		response = POST(REGISTER_URL,params);
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

	
	/**
	 * Test that users are able to recover their passwords.
	 */
	@Test
	public void testPasswordRecovery() throws InterruptedException {
		
		// Create a user to recover their password.
		context.turnOffAuthorization();
		Person person = personRepo.createPerson("forgetful", "forgetfull@email.com", "Forgetfull", "Person", RoleType.STUDENT).save();
		person.setPassword("password");
		person.save();
		context.restoreAuthorization();
		
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		
		
		// Clear out the mail queues.
		Mail.Mock.reset();
		
		// Get all our URLS
		final String INDEX_URL = Router.reverse("Application.index").url;
		final String RECOVER_URL = Router.reverse("Authentication.recover").url;
		final String PROFILE_URL = Router.reverse("Authentication.profile").url;
		
		// Step 1: Visit the registration page
		Response response = GET(RECOVER_URL);
		assertIsOk(response);
		assertContentMatch("Forgot Password Recovery",response);
		
		// Step 2: Fill out the registration form to verify email address.
		Map<String,String> params = new HashMap<String,String>();
		params.put("email", "forgetfull@email.com");
		params.put("submit_recover", "Verify");
		response = POST(RECOVER_URL,params);
		assertIsOk(response);
		assertContentMatch("Verification Email Sent",response);
		
		// Step 2: Wait for the email and the token.
		String recieved = null;
		for (int i = 0; i < 1000; i++) {
			Thread.yield();
			Thread.sleep(100);
			recieved = Mail.Mock.getLastMessageReceivedBy("forgetfull@email.com");
			if (recieved != null)
				break;
		}
		assertNotNull(recieved);
		assertTrue(recieved.contains("Subject: Verify Email Address"));
		Matcher tokenMatcher = EMAIL_TOKEN.matcher(recieved);
		assertTrue(tokenMatcher.find());
		String token = tokenMatcher.group(1);
		assertNotNull(token);
		assertTrue(token.length() > 5);
		
		// Step 3: View the page to complete the registration process
		response = GET(RECOVER_URL+"?token="+token);
		assertIsOk(response);
		assertContentMatch("Your email address has been verified.",response);
		assertContentMatch("forgetfull@email.com",response);
		
		// Step 4: Complete the registration process
		params.clear();
		params.put("token",token);
		params.put("password1", "ChangedPassword");
		params.put("password2", "ChangedPassword");
		params.put("submit_recover","Reset Password");
		response = POST(RECOVER_URL,params);
		assertHeaderEquals("Location", INDEX_URL, response);
		assertTrue(response.cookies.get("PLAY_SESSION").value.contains("Forgetfull"));
		assertTrue(response.cookies.get("PLAY_SESSION").value.contains("Person"));
		
		// Step 5: Verify that we are logged in.
		response = GET(PROFILE_URL);
		assertIsOk(response);
		assertContentMatch("Forgetfull",response); // first name
		assertContentMatch("Person",response); // last name
		
		// cleanup the user we created.
		context.turnOffAuthorization();
		person = personRepo.findPerson(person.getId());
		assertNotNull(person);
		assertTrue(person.validatePassword("ChangedPassword"));
		person.delete();
		context.restoreAuthorization();
		
		JPA.em().getTransaction().commit();
		JPA.em().getTransaction().begin();
	}
	
	/**
	 * Test updating profile information (i.e. everything but the password)
	 */
	@Test
	public void testUpdateProfile() {
		
		// Create a user to recover their password.
		context.turnOffAuthorization();
		Person person = personRepo.createPerson("updatter", "updatter@email.com", "Updatable", "Person", RoleType.STUDENT).save();
		person.setPassword("password");
		person.save();
		context.restoreAuthorization();
		
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		
		// Get all our URLS
		final String PROFILE_URL = Router.reverse("Authentication.profile").url;
		final String UPDATE_PROFILE_URL = Router.reverse("Authentication.updateProfile").url;
		Map<String,Object> routeArgs = new HashMap<String,Object>();
		routeArgs.put("methodName", "PasswordAuthentication");
		final String LOGIN_PASSWORD_URL = Router.reverse("Authentication.loginMethod",routeArgs).url;

		// Step 1: Authenticate (With Password)
		Map<String,String> loginForm = new HashMap<String,String>();
		loginForm.put("username", "updatter@email.com");
		loginForm.put("password", "password"); // yeah, the password is password.
		loginForm.put("submit_login","Login");
		Response response = POST(LOGIN_PASSWORD_URL,loginForm);
		assertTrue(response.cookies.get("PLAY_SESSION").value.contains("Updatable"));
		assertTrue(response.cookies.get("PLAY_SESSION").value.contains("Person"));
				
		// Step 2: Visit the profile page
		response = GET(PROFILE_URL);
		assertIsOk(response);
		assertContentMatch("Updatable",response);
		assertContentMatch("Person",response);

		// Step 3: View the update profile page
		response = GET(UPDATE_PROFILE_URL);
		assertIsOk(response);
		assertContentMatch("name=\"firstName\"\\s+value=\"Updatable\"",response);
		assertContentMatch("name=\"lastName\"\\s+value=\"Person\"",response);

		// Step 4: Submit update profile
		Map<String,String> profileForm = new HashMap<String,String>();
		profileForm.put("firstName","ChangedFirstname");
		profileForm.put("lastName","ChangedLastName");
		profileForm.put("middleName","ChangedMiddleName");
		profileForm.put("birthYear","1902");
		profileForm.put("currentPhoneNumber","555-555-5555 ex 9");
		profileForm.put("currentPostalAddress","2807 Barron Bason \nAustin, TX 77802");
		profileForm.put("permanentPhoneNumber","555-555-5555 ex 8");
		profileForm.put("permanentPostalAddress","2807 Barron Bason \nCollege Station, TX 77802");
		profileForm.put("permanentEmailAddress","scott@scottphillips.com");
		profileForm.put("submit_update","Update Profile");
		response = POST(UPDATE_PROFILE_URL,profileForm);
		assertHeaderEquals("Location", PROFILE_URL, response);
		
		// Step 5: Confirm updates.
		response = GET(PROFILE_URL);
		assertIsOk(response);
		assertContentMatch("ChangedFirstname",response);
		assertContentMatch("ChangedLastName",response);
		assertContentMatch("ChangedMiddleName",response);
		assertContentMatch("1902",response);
		assertContentMatch("555-555-5555 ex 9",response);
		assertContentMatch("Austin, TX 77802",response);
		assertContentMatch("555-555-5555 ex 8",response);
		assertContentMatch("College Station, TX 77802",response);
		assertContentMatch("scott@scottphillips.com",response);

		
		// cleanup the user we created.
		context.turnOffAuthorization();
		person = personRepo.findPerson(person.getId());
		assertNotNull(person);
		person.delete();
		context.restoreAuthorization();
		
		JPA.em().getTransaction().commit();
		JPA.em().getTransaction().begin();
	}
	
	/**
	 * Test a user changing their password. This is not the password recovery
	 * tool, so it assumes they know their current password.
	 */
	@Test
	public void testUpdatePassword() {

		// Create a user to recover their password.
		context.turnOffAuthorization();
		Person person = personRepo.createPerson("MrChange", "changer@email.com", "Detecated", "Changer", RoleType.STUDENT).save();
		person.setPassword("password");
		person.save();
		context.restoreAuthorization();
		
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		
		// Get all our URLS
		final String PROFILE_URL = Router.reverse("Authentication.profile").url;
		final String UPDATE_PASSWORD_URL = Router.reverse("Authentication.updatePassword").url;
		Map<String,Object> routeArgs = new HashMap<String,Object>();
		routeArgs.put("methodName", "PasswordAuthentication");
		final String LOGIN_PASSWORD_URL = Router.reverse("Authentication.loginMethod",routeArgs).url;

		// Step 1: Authenticate (With Password)
		Map<String,String> loginForm = new HashMap<String,String>();
		loginForm.put("username", "changer@email.com");
		loginForm.put("password", "password"); // yeah, the password is password.
		loginForm.put("submit_login","Login");
		Response response = POST(LOGIN_PASSWORD_URL,loginForm);
		assertTrue(response.cookies.get("PLAY_SESSION").value.contains("Detecated"));
		assertTrue(response.cookies.get("PLAY_SESSION").value.contains("Changer"));
				
		// Step 2: Visit the profile page
		response = GET(PROFILE_URL);
		assertIsOk(response);
		assertContentMatch("Detecated",response);
		assertContentMatch("Changer",response);

		// Step 3: View the update profile page
		response = GET(UPDATE_PASSWORD_URL);
		assertIsOk(response);

		// Step 4: Submit update profile
		Map<String,String> passwordForm = new HashMap<String,String>();
		passwordForm.put("current", "password");
		passwordForm.put("password1", "ChangedPassword");
		passwordForm.put("password2", "ChangedPassword");
		passwordForm.put("submit_update", "Change Password");
		response = POST(UPDATE_PASSWORD_URL,passwordForm);
		assertHeaderEquals("Location", PROFILE_URL, response);
		
		// Step 5: Confirm updates.
		response = GET(PROFILE_URL);
		assertIsOk(response);
		assertContentMatch("Detecated",response);
		assertContentMatch("Changer",response);

		
		// cleanup the user we created.
		context.turnOffAuthorization();
		person = personRepo.findPerson(person.getId());
		assertNotNull(person);
		assertTrue(person.validatePassword("ChangedPassword"));
		person.delete();
		context.restoreAuthorization();
		
		JPA.em().getTransaction().commit();
		JPA.em().getTransaction().begin();
	}
}
