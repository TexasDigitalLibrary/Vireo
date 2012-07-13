package controllers.settings;
import static org.tdl.vireo.model.Configuration.ALLOW_MULTIPLE_SUBMISSIONS;
import static org.tdl.vireo.model.Configuration.CURRENT_SEMESTER;
import static org.tdl.vireo.model.Configuration.REQUEST_COLLEGE;
import static org.tdl.vireo.model.Configuration.REQUEST_UMI;
import static org.tdl.vireo.model.Configuration.SUBMISSIONS_OPEN;
import static org.tdl.vireo.model.Configuration.SUBMISSION_INSTRUCTIONS;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tdl.vireo.model.Configuration;
import org.tdl.vireo.model.CustomActionDefinition;
import org.tdl.vireo.model.Person;
import org.tdl.vireo.model.PersonRepository;
import org.tdl.vireo.model.SettingsRepository;
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
public class ApplicationSettingsTabTest extends AbstractVireoFunctionalTest {

	public static SecurityContext context = Spring.getBeanOfType(SecurityContext.class);
	public static PersonRepository personRepo = Spring.getBeanOfType(PersonRepository.class);
	public static SettingsRepository settingRepo = Spring.getBeanOfType(SettingsRepository.class);
	
	@Before
	public void setup() {
		context.turnOffAuthorization();
	}
	
	@After
	public void cleanup() {
		context.restoreAuthorization();
	}
	
	/**
	 * Just test that the page is displayed without error.
	 */
	@Test
	public void testDisplayOfApplicationSettingsTab() {
		
		LOGIN();
		
		final String URL = Router.reverse("settings.ApplicationSettingsTab.applicationSettings").url;

		Response response = GET(URL);
		assertIsOk(response);
	}
	
	/**
	 * Test setting and unsetting all the global configurations
	 */
	@Test
	public void testToggelingConfigurations() {
		LOGIN();
		
		// Get our urls and a list of fields.
		final String URL = Router.reverse("settings.ApplicationSettingsTab.updateApplicationSettingsJSON").url;

		List<String> booleanFields = new ArrayList<String>();
		booleanFields.add(SUBMISSIONS_OPEN);
		booleanFields.add(ALLOW_MULTIPLE_SUBMISSIONS);
		booleanFields.add(REQUEST_COLLEGE);
		booleanFields.add(REQUEST_UMI);
		
		
		// Get the current list of 
		List<String> originalState = new ArrayList<String>();
		JPA.em().clear();
		for (String field : booleanFields) {
			if (settingRepo.findConfigurationByName(field) != null)
				originalState.add(field);
		}
		
		
		// Set each field.
		for (String field : booleanFields) {
			
			Map<String,String> params = new HashMap<String,String>();
			params.put("field", field);
			params.put("value","checked");
			Response response = POST(URL,params);
			assertContentMatch("\"success\": \"true\"", response);
		}
		
		// Check that all the fields are set.
		JPA.em().clear();
		for (String field : booleanFields) {
			assertNotNull(settingRepo.findConfigurationByName(field));
		}
		
		// Turn off each field.
		for (String field : booleanFields) {
			
			Map<String,String> params = new HashMap<String,String>();
			params.put("field", field);
			params.put("value","");
			Response response = POST(URL,params);
			assertContentMatch("\"success\": \"true\"", response);
		}
		
		// Check that all the fields are turned off.
		JPA.em().clear();
		for (String field : booleanFields) {
			assertNull(settingRepo.findConfigurationByName(field));
		}
		
		// Restore to original state
		for (String field : originalState) {
			settingRepo.createConfiguration(field, "true");
		}
	}
	
	/**
	 * Test changing the current semester
	 */
	@Test
	public void testCurrentSemester() {
		
		LOGIN();
		
		// Get our urls and a list of fields.
		final String URL = Router.reverse("settings.ApplicationSettingsTab.updateApplicationSettingsJSON").url;

		
		Configuration originalValue = settingRepo.findConfigurationByName(CURRENT_SEMESTER);
		
		// change the current semester
		Map<String,String> params = new HashMap<String,String>();
		params.put("field", CURRENT_SEMESTER);
		params.put("value","changed \"by test\"");
		Response response = POST(URL,params);
		assertContentMatch("\"success\": \"true\"", response);
	
		
		// Check that all the fields are set.
		assertNotNull(settingRepo.findConfigurationByName(CURRENT_SEMESTER));
		assertEquals("changed \"by test\"",settingRepo.findConfigurationByName(CURRENT_SEMESTER).getValue());
		
		JPA.em().clear();
		if (originalValue == null) {
			settingRepo.findConfigurationByName(CURRENT_SEMESTER).delete();
		} else {
			Configuration value = settingRepo.findConfigurationByName(CURRENT_SEMESTER);
			value.setValue(originalValue.getValue());
			value.save();
			
		}
	}
	
	/**
	 * Test changing the submission instructions
	 */
	@Test
	public void testSubmissionInstructions() {
		
		LOGIN();
		
		// Get our urls and a list of fields.
		final String URL = Router.reverse("settings.ApplicationSettingsTab.updateApplicationSettingsJSON").url;

		
		Configuration originalValue = settingRepo.findConfigurationByName(SUBMISSION_INSTRUCTIONS);
		
		// change the current semester
		Map<String,String> params = new HashMap<String,String>();
		params.put("field", SUBMISSION_INSTRUCTIONS);
		params.put("value","changed \"by test\"");
		Response response = POST(URL,params);
		assertContentMatch("\"success\": \"true\"", response);
	
		
		// Check that all the fields are set.
		assertNotNull(settingRepo.findConfigurationByName(SUBMISSION_INSTRUCTIONS));
		assertEquals("changed \"by test\"",settingRepo.findConfigurationByName(SUBMISSION_INSTRUCTIONS).getValue());
		
		JPA.em().clear();
		if (originalValue == null) {
			settingRepo.findConfigurationByName(SUBMISSION_INSTRUCTIONS).delete();
		} else {
			Configuration value = settingRepo.findConfigurationByName(SUBMISSION_INSTRUCTIONS);
			value.setValue(originalValue.getValue());
			value.save();
			
		}
	}
	
	
	/**
	 * Test adding and removing custom actions
	 */
	@Test
	public void testAddingEditingAndRemovingACustomAction() {
		
		LOGIN();
		
		// Get our urls and a list of fields.
		final String ADD_URL = Router.reverse("settings.ApplicationSettingsTab.addCustomActionJSON").url;
		final String EDIT_URL = Router.reverse("settings.ApplicationSettingsTab.editCustomActionJSON").url;
		final String REMOVE_URL = Router.reverse("settings.ApplicationSettingsTab.removeCustomActionJSON").url;

		// Add a new custom action
		Map<String,String> params = new HashMap<String,String>();
		params.put("name","New \"Custom\" action");
		Response response = POST(ADD_URL,params);
		assertContentMatch("\"success\": \"true\"", response);
		
		// Extract the id of the newly created action.
		Pattern ID_PATTERN = Pattern.compile("\"id\": ([0-9]+), ");
		Matcher tokenMatcher = ID_PATTERN.matcher(getContent(response));
		assertTrue(tokenMatcher.find());
		String idString = tokenMatcher.group(1);
		assertNotNull(idString);
		Long id = Long.valueOf(idString);
		
		// Verify the action exists in the database.
		JPA.em().clear();
		assertNotNull(settingRepo.findCustomActionDefinition(id));
		
		
		// Now edit the custom action
		params.clear();
		params.put("actionId","action_"+id);
		params.put("name", "Changed Label");
		response = POST(EDIT_URL,params);
		
		// Verify the action was updated in the database.
		JPA.em().clear();
		assertEquals("Changed Label",settingRepo.findCustomActionDefinition(id).getLabel());
		
		// Now remove the custom action
		params.clear();
		params.put("actionId","action_"+id);
		response = POST(REMOVE_URL,params);
		assertContentMatch("\"success\": \"true\"", response);
		
		// Verify the action was deleted in the database;
		JPA.em().clear();
		assertNull(settingRepo.findCustomActionDefinition(id));
	}
	
	/**
	 * Test reordering a set of custom actions
	 */
	@Test
	public void testReorderingCustomActions() {
		LOGIN();
		
		final String REORDER_URL = Router.reverse("settings.ApplicationSettingsTab.reorderCustomActionsJSON").url;
		
		
		// Create two custom actions:
		CustomActionDefinition action1 = settingRepo.createCustomActionDefinition("test one").save();
		CustomActionDefinition action2 = settingRepo.createCustomActionDefinition("test two").save();
		
		// Reorder the custom actions
		Map<String,String> params = new HashMap<String,String>();
		params.put("actionIds", "action_"+action2.getId()+",action_"+action1.getId());
		Response response = POST(REORDER_URL,params);
		assertContentMatch("\"success\": \"true\"", response);
		
		// Verify that the actions were reorderd
		JPA.em().clear();
		action1 = settingRepo.findCustomActionDefinition(action1.getId());
		action2 = settingRepo.findCustomActionDefinition(action2.getId());
		
		assertTrue(action1.getDisplayOrder() > action2.getDisplayOrder());
		
		// Cleanup
		action1.delete();
		action2.delete();
	}
}
