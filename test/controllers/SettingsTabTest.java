package controllers;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.tdl.vireo.constant.AppConfig;
import org.tdl.vireo.constant.AppPref;
import org.tdl.vireo.model.NameFormat;
import org.tdl.vireo.model.Person;
import org.tdl.vireo.model.PersonRepository;
import org.tdl.vireo.model.Preference;
import org.tdl.vireo.security.SecurityContext;

import play.db.jpa.JPA;
import play.modules.spring.Spring;
import play.mvc.Http.Response;
import play.mvc.Router;

/**
 * Test the functions common to all the settings tabs. This basically means the
 * profile box that is on the left of every page.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public class SettingsTabTest extends AbstractVireoFunctionalTest {

	public static SecurityContext context = Spring.getBeanOfType(SecurityContext.class);
	public static PersonRepository personRepo = Spring.getBeanOfType(PersonRepository.class);
	
	/**
	 * Test that a user can change their display name.
	 */
	@Test
	public void testUpdateProfileDisplayName() {
		context.turnOffAuthorization();
		Person person = personRepo.findPersonByEmail("bthornton@gmail.com");
		String originalDisplayName = person.getDisplayName();
		JPA.em().detach(person);
		
		// Login as an administrator
		LOGIN();
		
		final String UPDATE_URL = Router.reverse("SettingsTab.updateProfileJSON").url;

		
		Map<String,String> params = new HashMap<String,String>();
		params.put("field", "displayName");
		params.put("value", "changed");
		
		Response response = POST(UPDATE_URL,params);
		assertContentMatch("\"success\": \"true\",", response);
		
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		
		person = personRepo.findPersonByEmail("bthornton@gmail.com");
		assertEquals("changed",person.getDisplayName());
		person.setDisplayName(originalDisplayName);
		person.save();
		
		context.restoreAuthorization();

	}
	
	/**
	 * Test that if a users sets their display name to blank, that it reverts to their full name.
	 */
	@Test
	public void testUpdateProfileDisplayNameToBlank() {
		context.turnOffAuthorization();

		Person person = personRepo.findPersonByEmail("bthornton@gmail.com");
		String originalDisplayName = person.getDisplayName();

		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		
		// Login as an administrator
		LOGIN();
		
		final String UPDATE_URL = Router.reverse("SettingsTab.updateProfileJSON").url;

		
		Map<String,String> params = new HashMap<String,String>();
		params.put("field", "displayName");
		params.put("", "changed");
		
		Response response = POST(UPDATE_URL,params);
		assertContentMatch("\"success\": \"true\",", response);
		
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		
		person = personRepo.findPersonByEmail("bthornton@gmail.com");
		assertEquals(person.getFormattedName(NameFormat.FIRST_LAST),person.getDisplayName());
		person.setDisplayName(originalDisplayName);
		person.save();
		
		context.restoreAuthorization();
	}
	
	/**
	 * Test that a user can udpate their current Email address.
	 */
	@Test
	public void testUpdateProfileCurrentEmailAddress() {
		context.turnOffAuthorization();

		Person person = personRepo.findPersonByEmail("bthornton@gmail.com");
		String originalCurrentEmailAddress = person.getCurrentEmailAddress();
		
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		
		// Login as an administrator
		LOGIN();
		
		final String UPDATE_URL = Router.reverse("SettingsTab.updateProfileJSON").url;

		
		Map<String,String> params = new HashMap<String,String>();
		params.put("field", "currentEmailAddress");
		params.put("value", "changed@email.com");
		
		Response response = POST(UPDATE_URL,params);
		assertContentMatch("\"success\": \"true\",", response);
		
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		
		person = personRepo.findPersonByEmail("bthornton@gmail.com");
		assertEquals("changed@email.com",person.getCurrentEmailAddress());
		person.setCurrentEmailAddress(originalCurrentEmailAddress);
		person.save();
		
		context.restoreAuthorization();
	}
	
	/**
	 * Test that a user can update their "ccEmail" preference flag.
	 */
	@Test
	public void testUpdateProfileCCEmail() {
		context.turnOffAuthorization();

		Person person = personRepo.findPersonByEmail("bthornton@gmail.com");
		Preference originalCCEmail = person.getPreference(AppPref.CC_EMAILS);
		
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		
		// Login as an administrator
		LOGIN();
		
		final String UPDATE_URL = Router.reverse("SettingsTab.updateProfileJSON").url;

		
		Map<String,String> params = new HashMap<String,String>();
		params.put("field", "ccEmail");
		params.put("value", "checked");
		
		Response response = POST(UPDATE_URL,params);
		assertContentMatch("\"success\": \"true\",", response);
		
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		
		person = personRepo.findPersonByEmail("bthornton@gmail.com");
		assertNotNull(person.getPreference(AppPref.CC_EMAILS));
		person.getPreference(AppPref.CC_EMAILS).delete();
		person.save();
		
		context.restoreAuthorization();
	}
	
}
