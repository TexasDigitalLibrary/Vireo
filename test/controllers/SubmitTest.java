package controllers;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import play.Play;
import play.mvc.Http.Response;
import play.mvc.Router;
import play.test.FunctionalTest;

/**
 * Test all the actions in the Submit controller.
 * 
 * @author Dan Galewsky</a>
 */

public class SubmitTest extends FunctionalTest {
	
	// Make sure that we can get the VerifyInfo page
	
	 @Test
	 public void testGetVerifyInfoPage() {

		 shibLogin();
		 
		 final String VERIFY_URL = Router
				 .reverse("Submit.verifyPersonalInformation").url;
		 Response response = GET(VERIFY_URL);
		 assertStatus(200, response);
	 }
	 
	 // Test posting VerifyPersonalInformation to the license page
	 
	 @Test
	 public void testPostVerifyInfo(){
		 
		 shibLogin();
		 
		 final String DO_VERIFY_URL = Router
				 .reverse("Submit.doVerifyPersonalInformation").url;
		 
		 Map<String,String> verifyArgs = new HashMap<String,String>();
		 
		 verifyArgs.put("middleName","TestStudentFirstName");
		 verifyArgs.put("yearOfBirth","1996");
		 verifyArgs.put("department","science");
		 verifyArgs.put("degree","bs");
		 verifyArgs.put("major","computer science");
		 verifyArgs.put("permPhone","555-1212");
		 verifyArgs.put("permAddress","2222 Fake Street");
		 verifyArgs.put("permEmail","noreply@noreply.org");
		 verifyArgs.put("currentPhone","555-1212");
		 verifyArgs.put("currentAddress","2222 Fake Street");

			
		 Response response = POST(DO_VERIFY_URL, verifyArgs);
		 
		 assertStatus(200, response);
	 }
	 
	 private void shibLogin() {
		 Map<String, Object> routeArgs = new HashMap<String, Object>();
		 routeArgs.put("methodName", "ShibbolethAuthentication");
		 	 
		 final String LOGIN_SHIB_RETURN_URL = Router.reverse(
				 "Authentication.loginReturn", routeArgs).url;
		 
		 // Click on the shibboleth URL	 
		 Response response = GET(LOGIN_SHIB_RETURN_URL);
		 
	 }
}