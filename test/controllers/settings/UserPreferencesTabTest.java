package controllers.settings;

import org.junit.Test;

import play.mvc.Http.Response;
import play.mvc.Router;

import controllers.AbstractVireoFunctionalTest;
/**
 * Test for the user preferences tab. 
 * 
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public class UserPreferencesTabTest extends AbstractVireoFunctionalTest {

	/**
	 * Just test that the page is displayed without error.
	 */
	@Test
	public void testDisplayOfUserPreferenceTab() {
		
		LOGIN();
		
		final String URL = Router.reverse("settings.UserPreferencesTab.userPreferences").url;

		Response response = GET(URL);
		assertIsOk(response);
	}
}
