package controllers.settings;

import static org.tdl.vireo.constant.AppPref.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.tdl.vireo.model.Person;
import org.tdl.vireo.model.PersonRepository;
import org.tdl.vireo.security.SecurityContext;

import play.db.jpa.JPA;
import play.modules.spring.Spring;
import play.mvc.Http.Response;
import play.mvc.Router;
import controllers.AbstractVireoFunctionalTest;
/**
 * Test for the user preferences tab. 
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public class UserPreferencesTabTest extends AbstractVireoFunctionalTest {

	public static SecurityContext context = Spring.getBeanOfType(SecurityContext.class);
	public static PersonRepository personRepo = Spring.getBeanOfType(PersonRepository.class);
	
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
	
	/**
	 * Test that every user preference field can be set and unset.
	 */
	@Test
	public void testSettingAndUnsettingAllPreferences() {
		
		
		LOGIN();
		
		// Get our urls and a list of fields.
		final String URL = Router.reverse("settings.UserPreferencesTab.updateUserPreferencesJSON").url;

		final List<String> editableFields = new ArrayList<String>();
		editableFields.add(NOTES_EMAIL_STUDENT);
		editableFields.add(NOTES_CC_ADVISOR);
		editableFields.add(NOTES_FLAG_NEEDS_CORRECTIONS);
		editableFields.add(NOTES_MARK_PRIVATE);
			
		editableFields.add(ATTACHMENT_EMAIL_STUDENT);
		editableFields.add(ATTACHMENT_CC_ADVISOR);
		editableFields.add(ATTACHMENT_FLAG_NEEDS_CORRECTIONS);
		
		
		
		// Set each field.
		for (String field : editableFields) {
			
			Map<String,String> params = new HashMap<String,String>();
			params.put("field", field);
			params.put("value","checked");
			Response response = POST(URL,params);
			assertContentMatch("\"success\": \"true\"", response);
		}
		
		// Check that all the fields are set.
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();		Person person = personRepo.findPersonByEmail("bthornton@gmail.com");
		for (String field : editableFields) {
			assertNotNull(person.getPreference(field));
		}
		
		// Turn off each field.
		for (String field : editableFields) {
			
			Map<String,String> params = new HashMap<String,String>();
			params.put("field", field);
			params.put("value","");
			Response response = POST(URL,params);
			assertContentMatch("\"success\": \"true\"", response);
		}
		
		// Check that all the fields are turned off.
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		person = personRepo.findPersonByEmail("bthornton@gmail.com");
		for (String field : editableFields) {
			assertNull(person.getPreference(field));
		}
		
	}
}
