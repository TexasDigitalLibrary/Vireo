package controllers.settings;
import static org.tdl.vireo.constant.AppConfig.*;

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
import org.tdl.vireo.model.DepositLocation;
import org.tdl.vireo.model.NameFormat;
import org.tdl.vireo.model.Person;
import org.tdl.vireo.model.PersonRepository;
import org.tdl.vireo.model.RoleType;
import org.tdl.vireo.model.SettingsRepository;
import org.tdl.vireo.security.SecurityContext;

import play.db.jpa.JPA;
import play.modules.spring.Spring;
import play.mvc.Http.Response;
import play.mvc.Router;
import controllers.AbstractVireoFunctionalTest;
/**
 * Test for the application settings tab. 
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
//		booleanFields.add(PROQUEST_INDEXING);
		
		
		// Get the current list of 
		List<String> originalState = new ArrayList<String>();
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
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
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
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
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		for (String field : booleanFields) {
			assertNull(settingRepo.findConfigurationByName(field));
		}
		
		// Restore to original state
		for (String field : originalState) {
			settingRepo.createConfiguration(field, "true").save();
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
		params.put("value","May 2012");
		Response response = POST(URL,params);
		assertContentMatch("\"success\": \"true\"", response);
		JPA.em().clear();

		
		// Check that all the fields are set.
		assertNotNull(settingRepo.findConfigurationByName(CURRENT_SEMESTER));
		assertEquals("May 2012",settingRepo.findConfigurationByName(CURRENT_SEMESTER).getValue());
		
		if (originalValue == null) {
			settingRepo.findConfigurationByName(CURRENT_SEMESTER).delete();
		} else {
			Configuration value = settingRepo.findConfigurationByName(CURRENT_SEMESTER);
			value.setValue(originalValue.getValue());
			value.save();
			
		}
	}
	
	/**
	 * Test changing the granting instutition
	 */
	@Test
	public void testGrantor() {
		
		LOGIN();
		
		// Get our urls and a list of fields.
		final String URL = Router.reverse("settings.ApplicationSettingsTab.updateApplicationSettingsJSON").url;

		
		Configuration originalValue = settingRepo.findConfigurationByName(GRANTOR);
		
		// change the grantor
		Map<String,String> params = new HashMap<String,String>();
		params.put("field", GRANTOR);
		params.put("value","My Institution");
		Response response = POST(URL,params);
		assertContentMatch("\"success\": \"true\"", response);
		JPA.em().clear();
		
		// Check that all the fields are set.
		assertNotNull(settingRepo.findConfigurationByName(GRANTOR));
		assertEquals("My Institution",settingRepo.findConfigurationByName(GRANTOR).getValue());
		
		if (originalValue == null) {
			settingRepo.findConfigurationByName(GRANTOR).delete();
		} else {
			Configuration value = settingRepo.findConfigurationByName(GRANTOR);
			value.setValue(originalValue.getValue());
			value.save();
		}
	}
	
	/**
	 * Test changing the proquest institution code
	 */
	@Test
	public void testProquestInstitutionCode() {
		
		LOGIN();
		
		// Get our urls and a list of fields.
		final String URL = Router.reverse("settings.ApplicationSettingsTab.updateApplicationSettingsJSON").url;

		
		Configuration originalValue = settingRepo.findConfigurationByName(PROQUEST_INSTITUTION_CODE);
		
		// change the institution code
		Map<String,String> params = new HashMap<String,String>();
		params.put("field", PROQUEST_INSTITUTION_CODE);
		params.put("value","TAMU01");
		Response response = POST(URL,params);
		assertContentMatch("\"success\": \"true\"", response);
		JPA.em().clear();
		
		// Check that all the fields are set.
		assertNotNull(settingRepo.findConfigurationByName(PROQUEST_INSTITUTION_CODE));
		assertEquals("TAMU01",settingRepo.findConfigurationByName(PROQUEST_INSTITUTION_CODE).getValue());
		
		if (originalValue == null) {
			settingRepo.findConfigurationByName(PROQUEST_INSTITUTION_CODE).delete();
		} else {
			Configuration value = settingRepo.findConfigurationByName(PROQUEST_INSTITUTION_CODE);
			value.setValue(originalValue.getValue());
			value.save();
		}
	}
	
	/**
	 * Test changing the submission license
	 */
	@Test
	public void testSubmissionLicense() {
		
		LOGIN();
		
		// Get our urls and a list of fields.
		final String URL = Router.reverse("settings.ApplicationSettingsTab.updateApplicationSettingsJSON").url;

		
		Configuration originalValue = settingRepo.findConfigurationByName(SUBMIT_LICENSE_TEXT);
		
		// change the current semester
		Map<String,String> params = new HashMap<String,String>();
		params.put("field", SUBMIT_LICENSE_TEXT);
		params.put("value","changed \"by test\"");
		Response response = POST(URL,params);
		assertContentMatch("\"success\": \"true\"", response);
	
		
		// Check that all the fields are set.
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		assertNotNull(settingRepo.findConfigurationByName(SUBMIT_LICENSE_TEXT));
		assertEquals("changed \"by test\"",settingRepo.findConfigurationByName(SUBMIT_LICENSE_TEXT).getValue());
		
		JPA.em().clear();
		if (originalValue == null) {
			settingRepo.findConfigurationByName(SUBMIT_LICENSE_TEXT).delete();
		} else {
			Configuration value = settingRepo.findConfigurationByName(SUBMIT_LICENSE_TEXT);
			value.setValue(originalValue.getValue());
			value.save();
			
		}
	}
	
	
	/**
	 * Test adding and removing custom actions
	 * @throws InterruptedException 
	 */
	@Test
	public void testAddingEditingAndRemovingACustomAction() throws InterruptedException {
		
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
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		assertNotNull(settingRepo.findCustomActionDefinition(id));
		
		
		// Now edit the custom action
		params.clear();
		params.put("actionId","action_"+id);
		params.put("name", "Changed Label");
		response = POST(EDIT_URL,params);
		
		// Verify the action was updated in the database.
		Thread.yield();
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		assertEquals("Changed Label",settingRepo.findCustomActionDefinition(id).getLabel());
		
		// Now remove the custom action
		params.clear();
		params.put("actionId","action_"+id);
		response = POST(REMOVE_URL,params);
		assertContentMatch("\"success\": \"true\"", response);
		
		// Verify the action was deleted in the database;
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
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
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		
		
		// Reorder the custom actions
		Map<String,String> params = new HashMap<String,String>();
		params.put("actionIds", "action_"+action2.getId()+",action_"+action1.getId());
		Response response = POST(REORDER_URL,params);
		assertContentMatch("\"success\": \"true\"", response);
		
		
		// Verify that the actions were reorderd
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		action1 = settingRepo.findCustomActionDefinition(action1.getId());
		action2 = settingRepo.findCustomActionDefinition(action2.getId());
		
		assertTrue(action1.getDisplayOrder() > action2.getDisplayOrder());
		
		// Cleanup
		action1.delete();
		action2.delete();
	}
	
	/**
	 * Test searching for members. This tests both the blank query, pagination,
	 * and a query for "Billy"
	 */
	@Test
	public void testSearchMembers() {
		LOGIN();
		
		final String SEARCH_URL = Router.reverse("settings.ApplicationSettingsTab.searchMembers").url;
		
		// Do an empty search
		Response response = POST(SEARCH_URL);
		
		assertContentMatch("Search",response);
		List<Person> results = personRepo.searchPersons(null, 0, ApplicationSettingsTab.SEARCH_MEMBERS_RESULTS_PER_PAGE);
		for (Person result : results) {
			assertContentMatch("personId_"+result.getId(),response);
			assertContentMatch(result.getFormattedName(NameFormat.FIRST_LAST),response);
		}
		
		// Paginate to the next page
		Map<String,String> params = new HashMap<String,String>();
		params.put("query", "");
		params.put("offset", "2");
		response = POST(SEARCH_URL,params);
		
		assertContentMatch("Search",response);
		results = personRepo.searchPersons("", 2, ApplicationSettingsTab.SEARCH_MEMBERS_RESULTS_PER_PAGE);
		for (Person result : results) {
			assertContentMatch("personId_"+result.getId(),response);
			assertContentMatch(result.getFormattedName(NameFormat.FIRST_LAST),response);
		}
		
		// Do a specific search for "Billy", there should be two users from the
		// TestData loader that this matches.
		params.clear();
		params.put("query", "Billy");
		params.put("offset", "0");
		response = POST(SEARCH_URL,params);
		
		assertContentMatch("Search",response);
		results = personRepo.searchPersons("Billy", 0, ApplicationSettingsTab.SEARCH_MEMBERS_RESULTS_PER_PAGE);
		for (Person result : results) {
			assertContentMatch("personId_"+result.getId(),response);
			assertContentMatch(result.getFormattedName(NameFormat.FIRST_LAST),response);
		}
		
	}
	
	/**
	 * Test updated a person's role. 
	 */
	@Test
	public void testUpdatePersonRole() {
		LOGIN();
		
		final String UPDATE_URL = Router.reverse("settings.ApplicationSettingsTab.updatePersonRole").url;
		
		// Create a person
		Person person = personRepo.createPerson("netid", "email@email.com", "firstName", "lastName", RoleType.NONE).save();
		
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		
		// Upgrade person to a reviewer
		Map<String,String> params = new HashMap<String,String>();
		params.put("personId", "personId_"+person.getId());
		params.put("role", String.valueOf(RoleType.REVIEWER.getId()));
		Response response = POST(UPDATE_URL,params);
		
		assertContentMatch("Add Member",response);
		assertContentMatch("personId_"+person.getId(),response);
		assertContentMatch(person.getFormattedName(NameFormat.FIRST_LAST),response);
		
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		personRepo.findPerson(person.getId()).delete();
		
		
	}
	
	
	
	
	
	/**
	 * Test retrieving an updated list of deposit locations.
	 */
	@Test
	public void testUpdateDepositLocationList() {
		LOGIN();
		
		// Get our urls and a list of fields.
		final String URL = Router.reverse("settings.ApplicationSettingsTab.updateDepositLocationList").url;

		Response response = POST(URL);
		
		List<DepositLocation> locations = settingRepo.findAllDepositLocations();
		for (DepositLocation location : locations) {
			assertContentMatch(location.getName(),response);
		}	
	}
	
	/**
	 * Test loading a deposit location dialog box.
	 */
	@Test
	public void testLoadDepositLocation() {
		LOGIN();
		
		// Get our urls and a list of fields.
		final String URL = Router.reverse("settings.ApplicationSettingsTab.loadDepositLocation").url;

		DepositLocation location = settingRepo.findAllDepositLocations().get(0);
		
		Map<String,String> params = new HashMap<String,String>();
		params.put("depositLocationId", "depositLocation_"+location.getId());
		Response response = POST(URL,params);
		
		assertContentMatch(location.getName(),response);
		assertContentMatch("value=\""+location.getPackager().getBeanName()+"\" selected=\"true\"",response);
		assertContentMatch(location.getRepository(),response);
		assertContentMatch(location.getCollection(),response);
		assertContentMatch(location.getUsername(),response);
	}
	
	/**
	 * Test saving a deposit location
	 */
	@Test
	public void testSaveAndDeleteDepositLocation() {
		
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		LOGIN();
		
		// Get our urls and a list of fields.
		final String SAVE_URL = Router.reverse("settings.ApplicationSettingsTab.saveDepositLocation").url;
		final String REMOVE_URL = Router.reverse("settings.ApplicationSettingsTab.removeDepositLocationJSON").url;

		
		Map<String,String> params = new HashMap<String,String>();
		params.put("depositLocationId", "");
		params.put("name","New Deposit Location");
		params.put("packager","DSpaceMETS");
		params.put("depositor","Sword1Deposit");
		params.put("repository","http://localhost:8082/servicedocument");
		params.put("username","testUser");
		params.put("password","testPassword");
		params.put("onBehalfOf","");
		params.put("collection","http://localhost:8082/deposit/a");
		Response response = POST(SAVE_URL,params);
				
		assertContentMatch("New Deposit Location",response);
		assertContentMatch("packager",response);
		assertContentMatch("DSpaceMETS",response);
		assertContentMatch("Sword1Deposit",response);
		assertContentMatch("servicedocument",response);
		assertContentMatch("testUser",response);

		// Extract the id of the newly created location.
		Pattern ID_PATTERN = Pattern.compile("id=\"depositLocation-id\" value=\"depositLocation_([0-9]+)\"");
		Matcher tokenMatcher = ID_PATTERN.matcher(getContent(response));
		assertTrue(tokenMatcher.find());
		String idString = tokenMatcher.group(1);
		assertNotNull(idString);
		Long id = Long.valueOf(idString);
		
		// Check if the location exists
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		DepositLocation location = settingRepo.findDepositLocation(id);
		
		assertNotNull(location);
		assertEquals("New Deposit Location",location.getName());
		
		
		// Try and delete 
		params.clear();
		params.put("depositLocationId", "depositLocation_"+location.getId());
		response = POST(REMOVE_URL,params);
		assertContentMatch("\"success\": \"true\"", response);
		
		// Check that it was deleted in the database.
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		assertNull(settingRepo.findDepositLocation(id));
	}
	
	/**
	 * Test reordering a set of deposit locations
	 */
	@Test
	public void testReorderingTemplates() {
		LOGIN();
		
		final String REORDER_URL = Router.reverse("settings.ApplicationSettingsTab.reorderDepositLocationsJSON").url;
		
		// Create two custom actions:
		DepositLocation location1 = settingRepo.createDepositLocation("one").save();
		DepositLocation location2 = settingRepo.createDepositLocation("two").save();
		
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		
		// Reorder the custom actions
		Map<String,String> params = new HashMap<String,String>();
		params.put("depositLocationIds", "depositLocation_"+location2.getId()+",depositLocation_"+location1.getId());
		Response response = POST(REORDER_URL,params);
		assertContentMatch("\"success\": \"true\"", response);
		
		// Verify that the actions were reorderd
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		location1 = settingRepo.findDepositLocation(location1.getId());
		location2 = settingRepo.findDepositLocation(location2.getId());
		assertTrue(location1.getDisplayOrder() > location2.getDisplayOrder());
		
		// Cleanup
		location1.delete();
		location2.delete();
	}
	
	/**
	 * Test submiting a test item to a test sword server... you know test the
	 * test. But really it's broken several times now and no one noticed, so I'm
	 * adding a test so we know when it breaks.
	 */
	@Test
	public void testDepositingSubmission() {
		LOGIN();
		
		// Get our urls and a list of fields.
		final String SAVE_URL = Router.reverse("settings.ApplicationSettingsTab.saveDepositLocation").url;
		final String REMOVE_URL = Router.reverse("settings.ApplicationSettingsTab.removeDepositLocationJSON").url;

		
		Map<String,String> params = new HashMap<String,String>();
		params.put("depositLocationId", "");
		params.put("name","New Deposit Location");
		params.put("packager","DSpaceMETS");
		params.put("depositor","Sword1Deposit");
		params.put("repository","http://localhost:8082/servicedocument");
		params.put("username","testUser");
		params.put("password","testPassword");
		params.put("onBehalfOf","");
		params.put("collection","http://localhost:8082/deposit/a");
		params.put("action","depositLocation-test-submit");
		Response response = POST(SAVE_URL,params);
				
		assertContentMatch("New Deposit Location",response);
		assertContentMatch("packager",response);
		assertContentMatch("DSpaceMETS",response);
		assertContentMatch("Sword1Deposit",response);
		assertContentMatch("servicedocument",response);
		assertContentMatch("testUser",response);
		assertContentMatch("Test item successfully deposited: ",response);

		// Extract the id of the newly created location.
		Pattern ID_PATTERN = Pattern.compile("id=\"depositLocation-id\" value=\"depositLocation_([0-9]+)\"");
		Matcher tokenMatcher = ID_PATTERN.matcher(getContent(response));
		assertTrue(tokenMatcher.find());
		String idString = tokenMatcher.group(1);
		assertNotNull(idString);
		Long id = Long.valueOf(idString);
		
		// Check if the location exists
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		DepositLocation location = settingRepo.findDepositLocation(id);
		
		assertNotNull(location);
		assertEquals("New Deposit Location",location.getName());
		
		
		// Try and delete 
		params.clear();
		params.put("depositLocationId", "depositLocation_"+location.getId());
		response = POST(REMOVE_URL,params);
		assertContentMatch("\"success\": \"true\"", response);
		
		// Check that it was deleted in the database.
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		assertNull(settingRepo.findDepositLocation(id));
	}
	
	
}
