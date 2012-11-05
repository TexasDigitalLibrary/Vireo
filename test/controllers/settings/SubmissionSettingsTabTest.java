package controllers.settings;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tdl.vireo.constant.AppConfig;
import org.tdl.vireo.model.Configuration;
import org.tdl.vireo.model.PersonRepository;
import org.tdl.vireo.model.SettingsRepository;
import org.tdl.vireo.security.SecurityContext;

import play.db.jpa.JPA;
import play.modules.spring.Spring;
import play.mvc.Http.Response;
import play.mvc.Router;
import controllers.AbstractVireoFunctionalTest;

/**
 * Test for the submission settings tab. 
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public class SubmissionSettingsTabTest extends AbstractVireoFunctionalTest {

	public static SecurityContext context = Spring.getBeanOfType(SecurityContext.class);
	public static SettingsRepository settingRepo = Spring.getBeanOfType(SettingsRepository.class);
	
	@Before
	public void setup() {
		context.turnOffAuthorization();
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
	}
	
	@After
	public void cleanup() {
		context.restoreAuthorization();
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
	}
	
	/**
	 * Just test that the page is displayed without error.
	 */
	@Test
	public void testDisplayOfApplicationSettingsTab() {
		
		LOGIN();
		
		final String URL = Router.reverse("settings.SubmissionSettingsTab.submissionSettings").url;

		Response response = GET(URL);
		assertIsOk(response);
		assertContentMatch("Verify Personal Information",response);
		assertContentMatch("License",response);
		assertContentMatch("Document Information",response);
		assertContentMatch("Upload Files",response);
	}
	
	/**
	 * Test setting and unsetting all the global configurations
	 */
	@Test
	public void testUpdatingSettingsConfigurations() {
		LOGIN();
		final String URL = Router.reverse("settings.SubmissionSettingsTab.updateSubmissionSettingsJSON").url;

		// Save current state
		Map<String,String> originalState = new HashMap<String,String>();
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		for (String field : SubmissionSettingsTab.allSettings) {
			Configuration config = settingRepo.findConfigurationByName(field);
			if (config == null) {
				originalState.put(field, null);
			} else {
				originalState.put(field, config.getValue());
			}
		}
		
		
		// Set each field to something
		for (String field : SubmissionSettingsTab.allSettings) {
			
			Map<String,String> params = new HashMap<String,String>();
			params.put("field", field);
			params.put("value","something");
			Response response = POST(URL,params);
			assertContentMatch("\"success\": \"true\"", response);
		}
		
		// Check that all the fields are set.
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		for (String field : SubmissionSettingsTab.allSettings) {
			assertEquals("something",settingRepo.findConfigurationByName(field).getValue());
		}
		
		// Set each field to blank
		for (String field : SubmissionSettingsTab.allSettings) {
			
			Map<String,String> params = new HashMap<String,String>();
			params.put("field", field);
			params.put("value","");
			Response response = POST(URL,params);
			assertContentMatch("\"success\": \"true\"", response);
		}
		
		// Check that all the fields are blank.
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		for (String field : SubmissionSettingsTab.allSettings) {
			assertEquals("",settingRepo.findConfigurationByName(field).getValue());
		}

		// Restore to original state
		for (String field : originalState.keySet()) {
			Configuration config = settingRepo.findConfigurationByName(field);
			config.setValue(originalState.get(field));
			config.save();
		}
	}
	
	/**
	 * Test updating sticky note values.
	 */
	@Test
	public void testUpdateStickySettingsJSON() {
		LOGIN();
		final String URL = Router.reverse("settings.SubmissionSettingsTab.updateStickySettingsJSON").url;

		
		// Save current state
		String originalState = null;
		Configuration config = settingRepo.findConfigurationByName(AppConfig.SUBMIT_PERSONAL_INFO_STICKIES);
		if (config != null)
			originalState = config.getValue();
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		
		

		Map<String,String> params = new HashMap<String,String>();
		params.put("field", AppConfig.SUBMIT_PERSONAL_INFO_STICKIES);
		params.put("value","something");
		Response response = POST(URL,params);
		assertContentMatch("\"success\": \"true\"", response);
		
		// Check that all the fields are set.
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		assertTrue(settingRepo.findConfigurationByName(AppConfig.SUBMIT_PERSONAL_INFO_STICKIES).getValue().contains("something"));
		
		if (originalState == null) {
			settingRepo.findConfigurationByName(AppConfig.SUBMIT_PERSONAL_INFO_STICKIES).delete();
		} else {
			config = settingRepo.findConfigurationByName(AppConfig.SUBMIT_PERSONAL_INFO_STICKIES);
			config.setValue(originalState);
			config.save();
		}
	}
	
	/**
	 * Test reseting a set of fields to their default values by removing their
	 * configuration objects
	 */
	@Test
	public void testResetSettings() {
		
		
		String originalState = null;
		Configuration config = settingRepo.findConfigurationByName(AppConfig.SUBMIT_PERSONAL_INFO_STICKIES);
		if (config != null)
			originalState = config.getValue();
		else
			settingRepo.createConfiguration(AppConfig.SUBMIT_PERSONAL_INFO_STICKIES, "something");
		
		
		
		
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		
		LOGIN();
		final String URL = Router.reverse("settings.SubmissionSettingsTab.resetSettings").url;

		Response response = GET(URL+"?group=personal-info-stickies");
		assertNotNull(response.getHeader("Location"));
		
		
		// Check that the field was cleared.
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		assertNull(settingRepo.findConfigurationByName(AppConfig.SUBMIT_PERSONAL_INFO_STICKIES));
		
		// Restore to original state
		if (originalState != null) {
			config = settingRepo.findConfigurationByName(AppConfig.SUBMIT_PERSONAL_INFO_STICKIES);
			config.setValue(originalState);
			config.save();
		}
		
	}
	
	
}
