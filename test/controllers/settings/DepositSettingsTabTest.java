package controllers.settings;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tdl.vireo.model.DepositLocation;
import org.tdl.vireo.model.PersonRepository;
import org.tdl.vireo.model.SettingsRepository;
import org.tdl.vireo.security.SecurityContext;

import play.db.jpa.JPA;
import play.modules.spring.Spring;
import play.mvc.Http.Response;
import play.mvc.Router;
import controllers.AbstractVireoFunctionalTest;
/**
 * Test for the deposit settings tab. 
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public class DepositSettingsTabTest extends AbstractVireoFunctionalTest {

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
	public void testDisplayOfEmailSettingsTab() {
		
		LOGIN();
		
		final String URL = Router.reverse("settings.DepositSettingsTab.depositSettings").url;

		Response response = GET(URL);
		assertIsOk(response);
	}
	
	/**
	 * Test retrieving an updated list of deposit locations.
	 */
	@Test
	public void testUpdateDepositLocationList() {
		LOGIN();
		
		// Get our urls and a list of fields.
		final String URL = Router.reverse("settings.DepositSettingsTab.updateDepositLocationList").url;

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
		final String URL = Router.reverse("settings.DepositSettingsTab.loadDepositLocation").url;

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
		final String SAVE_URL = Router.reverse("settings.DepositSettingsTab.saveDepositLocation").url;
		final String REMOVE_URL = Router.reverse("settings.DepositSettingsTab.removeDepositLocationJSON").url;

		
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
		
		final String REORDER_URL = Router.reverse("settings.DepositSettingsTab.reorderDepositLocationsJSON").url;
		
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
		final String SAVE_URL = Router.reverse("settings.DepositSettingsTab.saveDepositLocation").url;
		final String REMOVE_URL = Router.reverse("settings.DepositSettingsTab.removeDepositLocationJSON").url;

		
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
