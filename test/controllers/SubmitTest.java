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
	
	 @Test
	 public void testGetVerifyInfoPage() {

		 Map<String, Object> routeArgs = new HashMap<String, Object>();
		 routeArgs.put("methodName", "ShibbolethAuthentication");
		 	 
		 final String LOGIN_SHIB_RETURN_URL = Router.reverse(
				 "Authentication.loginReturn", routeArgs).url;
		 
		 // Click on the shibboleth URL	 
		 Response response = GET(LOGIN_SHIB_RETURN_URL);

		 final String VERIFY_URL = Router
				 .reverse("Submit.verifyPersonalInformation").url;
		 response = GET(VERIFY_URL);
		 assertStatus(200, response);
	 }
}