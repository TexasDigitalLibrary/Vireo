package controllers.settings;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tdl.vireo.model.College;
import org.tdl.vireo.model.CommitteeMemberRoleType;
import org.tdl.vireo.model.Degree;
import org.tdl.vireo.model.DegreeLevel;
import org.tdl.vireo.model.Department;
import org.tdl.vireo.model.DocumentType;
import org.tdl.vireo.model.EmbargoGuarantor;
import org.tdl.vireo.model.EmbargoType;
import org.tdl.vireo.model.GraduationMonth;
import org.tdl.vireo.model.Language;
import org.tdl.vireo.model.Major;
import org.tdl.vireo.model.PersonRepository;
import org.tdl.vireo.model.Program;
import org.tdl.vireo.model.SettingsRepository;
import org.tdl.vireo.security.SecurityContext;

import play.Logger;
import play.db.jpa.JPA;
import play.modules.spring.Spring;
import play.mvc.Http.Response;
import play.mvc.Router;
import controllers.AbstractVireoFunctionalTest;
/**
 * Test for the configurable settings tab. 
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public class ConfigurableSettingsTabTest extends AbstractVireoFunctionalTest {

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
	public void testDisplayOfConfigurableSettingsTab() {
		
		LOGIN();
		
		final String URL = Router.reverse("settings.ConfigurableSettingsTab.configurableSettings").url;

		Response response = GET(URL);
		assertIsOk(response);
	}
	
	
	@Test
	public void testBulkAdd() {
		LOGIN();
		
		final String REDIRECT_URL = Router.reverse("settings.ConfigurableSettingsTab.configurableSettings").url;
		final String BULK_URL = Router.reverse("settings.ConfigurableSettingsTab.bulkAdd").url;

		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		
		Map<String,String> params = new HashMap<String,String>();
		params.put("modelType", "college");
		params.put("bulkAdd", "one\ntwo\nthree\n");
		Response response = POST(BULK_URL,params);
		
		assertEquals(REDIRECT_URL,response.getHeader("Location"));
		
		List<College> colleges = settingRepo.findAllColleges();
		assertEquals("three",colleges.get(colleges.size()-1).getName());
		assertEquals("two",colleges.get(colleges.size()-2).getName());
		assertEquals("one",colleges.get(colleges.size()-3).getName());

		colleges.get(colleges.size()-1).delete();
		colleges.get(colleges.size()-2).delete();
		colleges.get(colleges.size()-3).delete();
	}
	
	/**
	 * Test adding and editing an Email Template.
	 */
	@Test
	public void testAddingEditingDeletingEmbargoTypes() {
		
		LOGIN();
		
		// Get our urls and a list of fields.
		final String EDIT_URL = Router.reverse("settings.ConfigurableSettingsTab.editEmbargoTypeJSON").url;
		final String REMOVE_URL = Router.reverse("settings.ConfigurableSettingsTab.removeEmbargoTypeJSON").url;
		
		// Add a new template
		Map<String,String> params = new HashMap<String,String>();
		params.put("name","New Embargo Type");
		params.put("description","New Description");
		params.put("months","4");
		params.put("active", "true");
		params.put("guarantor", "DEFAULT");

		Response response = POST(EDIT_URL,params);
		assertContentMatch("\"success\": \"true\"", response);
		
		// Extract the id of the newly created action.
		Pattern ID_PATTERN = Pattern.compile("\"id\": ([0-9]+), ");
		Matcher tokenMatcher = ID_PATTERN.matcher(getContent(response));
		assertTrue(tokenMatcher.find());
		String idString = tokenMatcher.group(1);
		assertNotNull(idString);
		Long id = Long.valueOf(idString);
		
		// Verify the embargo exists in the database.
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		assertNotNull(settingRepo.findEmbargoType(id));
		assertEquals("New Embargo Type",settingRepo.findEmbargoType(id).getName());
		assertEquals("New Description",settingRepo.findEmbargoType(id).getDescription());
		assertEquals(new Integer(4),settingRepo.findEmbargoType(id).getDuration());
		assertEquals(true,settingRepo.findEmbargoType(id).isActive());
		
		// Now edit the embargo
		params.clear();
		params.put("embargoTypeId", String.valueOf(id));
		params.put("name", "Changed Name");
		params.put("description", "Changed Description");
		params.put("guarantor", "DEFAULT");
		//params.put("months", null);
		//params.put("active", false);
		response = POST(EDIT_URL,params);
		assertContentMatch("\"success\": \"true\"", response);

		
		// Verify the action was updated in the database.
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		assertEquals("Changed Name",settingRepo.findEmbargoType(id).getName());
		assertEquals("Changed Description",settingRepo.findEmbargoType(id).getDescription());
		assertEquals(null,settingRepo.findEmbargoType(id).getDuration());
		assertEquals(false,settingRepo.findEmbargoType(id).isActive());
		
		// Now remove the embargo type
		params.clear();
		params.put("embargoTypeId",String.valueOf(id));
		response = POST(REMOVE_URL,params);
		assertContentMatch("\"success\": \"true\"", response);
		
		// Verify the action was deleted in the database;
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		assertNull(settingRepo.findEmbargoType(id));
	}
	
	/**
	 * Test reordering a set of embargo types.
	 */
	@Test
	public void testReorderingEmbargoTypes() {
		LOGIN();
		
		final String REORDER_URL = Router.reverse("settings.ConfigurableSettingsTab.reorderEmbargoTypesJSON").url;
		
		// Create two custom actions:
		EmbargoType embargo1 = settingRepo.createEmbargoType("name1", "description", null, false).save();
		EmbargoType embargo2 = settingRepo.createEmbargoType("name2", "description", null, false).save();
		
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		
		// Reorder the custom actions
		Map<String,String> params = new HashMap<String,String>();
		params.put("embargoTypeIds", "embargoType_"+embargo2.getId()+",embargoType_"+embargo1.getId());
		Response response = POST(REORDER_URL,params);
		assertContentMatch("\"success\": \"true\"", response);
		
		// Verify that the actions were reorderd
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		embargo1 = settingRepo.findEmbargoType(embargo1.getId());
		embargo2 = settingRepo.findEmbargoType(embargo2.getId());
		assertTrue(embargo1.getDisplayOrder() > embargo2.getDisplayOrder());
		
		// Cleanup
		embargo1.delete();
		embargo2.delete();
	}

	/**
	 * Test alphabetizing EmbargoTypes
	 */
	@Test
	public void testAlphabetizingEmbargoTypes() {
		LOGIN();
		
		final String REDIRECT_URL = Router.reverse("settings.ConfigurableSettingsTab.configurableSettings").url;
		final String ALPHA_URL = Router.reverse("settings.ConfigurableSettingsTab.alphabetizeAllEmbargoTypes").url;
		
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		
		Response response = GET(ALPHA_URL);
		assertEquals(REDIRECT_URL,response.getHeader("Location"));
		
		List<EmbargoType> types = settingRepo.findAllEmbargoTypes();
		String previousName = null;
		EmbargoGuarantor previousGuarantor = null;
		for (EmbargoType type : types) {
			String name = String.format("%2d - %s", (type.getDuration() == null ? 999 : type.getDuration()),type.getName());
			if (previousName != null && previousGuarantor != null) {
				// only compare with previous if the guarantors are the same (otherwise the names can be the same)
				if(previousGuarantor == type.getGuarantor()) {
					assertTrue(previousName.compareTo(name) <= 0);
				}
			} 
			previousName = name;
			previousGuarantor = type.getGuarantor();
		}
	}
	
	/**
	 * Test adding, editing, and removing a program.
	 */
	@Test
	public void testAddingEditingRemovingPrograms() {
		
		LOGIN();
		
		// Get our urls and a list of fields.
		final String EDIT_URL = Router.reverse("settings.ConfigurableSettingsTab.addEditProgramJSON").url;
		final String REMOVE_URL = Router.reverse("settings.ConfigurableSettingsTab.removeProgramJSON").url;

		// Add a new custom action
		Map<String,String> params = new HashMap<String,String>();
		params.put("name","New Program");
		Response response = POST(EDIT_URL,params);
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
		assertNotNull(settingRepo.findProgram(id));
		assertEquals("New Program",settingRepo.findProgram(id).getName());
		
		
		// Now edit the custom action
		params.clear();
		params.put("programId",String.valueOf(id));
		params.put("name", "Changed Name");
		params.put("emails", "me@me.com, test@test.com");
		response = POST(EDIT_URL,params);
		
		// Verify the action was updated in the database.
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		Program program = settingRepo.findProgram(id);
		assertEquals("Changed Name", program.getName());
		assertEquals("me@me.com", program.getEmails().get(0));
		assertEquals("test@test.com", program.getEmails().get(1));
		
		// Now remove the custom action
		params.clear();
		params.put("programId",String.valueOf(id));
		response = POST(REMOVE_URL,params);
		assertContentMatch("\"success\": \"true\"", response);
		
		// Verify the action was deleted in the database;
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		assertNull(settingRepo.findProgram(id));
	}
	
	/**
	 * Test reordering a set of programs.
	 */
	@Test
	public void testReorderingProgram() {
		LOGIN();
		
		final String REORDER_URL = Router.reverse("settings.ConfigurableSettingsTab.reorderProgramsJSON").url;
		
		// Create two custom actions:
		Program program1 = settingRepo.createProgram("test one").save();
		Program program2 = settingRepo.createProgram("test two").save();
		
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		
		// Reorder the custom actions
		Map<String,String> params = new HashMap<String,String>();
		params.put("programIds", "program_"+program2.getId()+",program_"+program1.getId());
		Response response = POST(REORDER_URL,params);
		assertContentMatch("\"success\": \"true\"", response);
		
		// Verify that the actions were reorderd
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		program1 = settingRepo.findProgram(program1.getId());
		program2 = settingRepo.findProgram(program2.getId());
		
		assertTrue(program1.getDisplayOrder() > program2.getDisplayOrder());
		
		// Cleanup
		program1.delete();
		program2.delete();
	}
	
	/**
	 * Test alphabetizing porgrams
	 */
	@Test
	public void testAlphabetizingPrograms() {
		LOGIN();
		
		final String REDIRECT_URL = Router.reverse("settings.ConfigurableSettingsTab.configurableSettings").url;
		final String ALPHA_URL = Router.reverse("settings.ConfigurableSettingsTab.alphabetizeAllPrograms").url;
		
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		
		Response response = GET(ALPHA_URL);
		assertEquals(REDIRECT_URL,response.getHeader("Location"));
		
		List<Program> programs = settingRepo.findAllPrograms();
		String previousName = null;
		for (Program program : programs) {
			if (previousName != null) {
				assertTrue(previousName.compareTo(program.getName()) < 0);
			} 
			previousName = program.getName();
		}
	}
	
	/**
	 * Test adding, editing, and removing a college.
	 */
	@Test
	public void testAddingEditingRemovingColleges() {
		
		LOGIN();
		
		// Get our urls and a list of fields.
		final String EDIT_URL = Router.reverse("settings.ConfigurableSettingsTab.addEditCollegeJSON").url;
		final String REMOVE_URL = Router.reverse("settings.ConfigurableSettingsTab.removeCollegeJSON").url;

		// Add a new custom action
		Map<String,String> params = new HashMap<String,String>();
		params.put("name","New College");
		Response response = POST(EDIT_URL,params);
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
		assertNotNull(settingRepo.findCollege(id));
		assertEquals("New College",settingRepo.findCollege(id).getName());
		
		
		// Now edit the custom action
		params.clear();
		params.put("collegeId",String.valueOf(id));
		params.put("name", "Changed Name");
		params.put("emails", "me@me.com, test@test.com");
		response = POST(EDIT_URL,params);
		
		// Verify the action was updated in the database.
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		College college = settingRepo.findCollege(id);
		assertEquals("Changed Name", college.getName());
		assertEquals("me@me.com", college.getEmails().get(0));
		assertEquals("test@test.com", college.getEmails().get(1));
		
		// Now remove the custom action
		params.clear();
		params.put("collegeId",String.valueOf(id));
		response = POST(REMOVE_URL,params);
		assertContentMatch("\"success\": \"true\"", response);
		
		// Verify the action was deleted in the database;
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		assertNull(settingRepo.findCollege(id));
	}
	
	/**
	 * Test reordering a set of colleges.
	 */
	@Test
	public void testReorderingColleges() {
		LOGIN();
		
		final String REORDER_URL = Router.reverse("settings.ConfigurableSettingsTab.reorderCollegesJSON").url;
		
		// Create two custom actions:
		College college1 = settingRepo.createCollege("test one").save();
		College college2 = settingRepo.createCollege("test two").save();
		
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		
		// Reorder the custom actions
		Map<String,String> params = new HashMap<String,String>();
		params.put("collegeIds", "college_"+college2.getId()+",college_"+college1.getId());
		Response response = POST(REORDER_URL,params);
		assertContentMatch("\"success\": \"true\"", response);
		
		// Verify that the actions were reorderd
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		college1 = settingRepo.findCollege(college1.getId());
		college2 = settingRepo.findCollege(college2.getId());
		
		assertTrue(college1.getDisplayOrder() > college2.getDisplayOrder());
		
		// Cleanup
		college1.delete();
		college2.delete();
	}
	
	
	/**
	 * Test alphabetizing Colleges
	 */
	@Test
	public void testAlphabetizingColleges() {
		LOGIN();
		
		final String REDIRECT_URL = Router.reverse("settings.ConfigurableSettingsTab.configurableSettings").url;
		final String ALPHA_URL = Router.reverse("settings.ConfigurableSettingsTab.alphabetizeAllColleges").url;
		
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		
		Response response = GET(ALPHA_URL);
		assertEquals(REDIRECT_URL,response.getHeader("Location"));
		
		List<College> colleges = settingRepo.findAllColleges();
		String previousName = null;
		for (College college : colleges) {
			if (previousName != null) {
				assertTrue(previousName.compareTo(college.getName()) < 0);
			} 
			previousName = college.getName();
		}
	}
	
	/**
	 * Test adding, editing, and removing a department.
	 */
	@Test
	public void testAddingEditingRemovingDepartments() {
		
		LOGIN();
		
		// Get our urls and a list of fields.
		final String EDIT_URL = Router.reverse("settings.ConfigurableSettingsTab.addEditDepartmentJSON").url;
		final String REMOVE_URL = Router.reverse("settings.ConfigurableSettingsTab.removeDepartmentJSON").url;

		// Add a new custom action
		Map<String,String> params = new HashMap<String,String>();
		params.put("name","New Department");
		Response response = POST(EDIT_URL,params);
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
		assertNotNull(settingRepo.findDepartment(id));
		assertEquals("New Department",settingRepo.findDepartment(id).getName());
		
		// Now edit the custom action
		params.clear();
		params.put("departmentId",String.valueOf(id));
		params.put("name", "Changed Name");
		params.put("emails", "me@me.com, test@test.com");
		response = POST(EDIT_URL,params);
		
		// Verify the action was updated in the database.
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		Department department = settingRepo.findDepartment(id);
		assertEquals("Changed Name", department.getName());
		assertEquals("me@me.com", department.getEmails().get(0));
		assertEquals("test@test.com", department.getEmails().get(1));
		
		// Now remove the custom action
		params.clear();
		params.put("departmentId",String.valueOf(id));
		response = POST(REMOVE_URL,params);
		assertContentMatch("\"success\": \"true\"", response);
		
		// Verify the action was deleted in the database;
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		assertNull(settingRepo.findDepartment(id));
	}
	
	/**
	 * Test reordering a set of departments.
	 */
	@Test
	public void testReorderingDepartments() {
		LOGIN();
		
		final String REORDER_URL = Router.reverse("settings.ConfigurableSettingsTab.reorderDepartmentsJSON").url;
		
		// Create two custom actions:
		Department department1 = settingRepo.createDepartment("test one").save();
		Department department2 = settingRepo.createDepartment("test two").save();
		
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		
		// Reorder the custom actions
		Map<String,String> params = new HashMap<String,String>();
		params.put("departmentIds", "department_"+department2.getId()+",department_"+department1.getId());
		Response response = POST(REORDER_URL,params);
		assertContentMatch("\"success\": \"true\"", response);
		
		// Verify that the actions were reorderd
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		department1 = settingRepo.findDepartment(department1.getId());
		department2 = settingRepo.findDepartment(department2.getId());
		
		assertTrue(department1.getDisplayOrder() > department2.getDisplayOrder());
		
		// Cleanup
		department1.delete();
		department2.delete();
	}
	
	/**
	 * Test alphabetizing departments
	 */
	@Test
	public void testAlphabetizingDepartments() {
		LOGIN();
		
		final String REDIRECT_URL = Router.reverse("settings.ConfigurableSettingsTab.configurableSettings").url;
		final String ALPHA_URL = Router.reverse("settings.ConfigurableSettingsTab.alphabetizeAllDepartments").url;
		
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		
		Response response = GET(ALPHA_URL);
		assertEquals(REDIRECT_URL,response.getHeader("Location"));
		
		List<Department> departments = settingRepo.findAllDepartments();
		String previousName = null;
		for (Department department : departments) {
			if (previousName != null) {
				assertTrue(previousName.compareTo(department.getName()) < 0);
			} 
			previousName = department.getName();
		}
	}
	
	/**
	 * Test adding, editing, and removing a major.
	 */
	@Test
	public void testAddingEditingRemovingMajors() {
		
		LOGIN();
		
		// Get our urls and a list of fields.
		final String ADD_URL = Router.reverse("settings.ConfigurableSettingsTab.addMajorJSON").url;
		final String EDIT_URL = Router.reverse("settings.ConfigurableSettingsTab.editMajorJSON").url;
		final String REMOVE_URL = Router.reverse("settings.ConfigurableSettingsTab.removeMajorJSON").url;

		// Add a new custom action
		Map<String,String> params = new HashMap<String,String>();
		params.put("name","New Major");
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
		assertNotNull(settingRepo.findMajor(id));
		assertEquals("New Major",settingRepo.findMajor(id).getName());
		
		
		// Now edit the custom action
		params.clear();
		params.put("majorId","major_"+id);
		params.put("name", "Changed Name");
		response = POST(EDIT_URL,params);
		
		// Verify the action was updated in the database.
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		assertEquals("Changed Name",settingRepo.findMajor(id).getName());
		
		// Now remove the custom action
		params.clear();
		params.put("majorId","major_"+id);
		response = POST(REMOVE_URL,params);
		assertContentMatch("\"success\": \"true\"", response);
		
		// Verify the action was deleted in the database;
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		assertNull(settingRepo.findMajor(id));
	}
	
	/**
	 * Test reordering a set of majors.
	 */
	@Test
	public void testReorderingMajors() {
		LOGIN();
		
		final String REORDER_URL = Router.reverse("settings.ConfigurableSettingsTab.reorderMajorsJSON").url;
		
		// Create two custom actions:
		Major major1 = settingRepo.createMajor("test one").save();
		Major major2 = settingRepo.createMajor("test two").save();
		
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		
		// Reorder the custom actions
		Map<String,String> params = new HashMap<String,String>();
		params.put("majorIds", "major_"+major2.getId()+",major_"+major1.getId());
		Response response = POST(REORDER_URL,params);
		assertContentMatch("\"success\": \"true\"", response);
		
		// Verify that the actions were reorderd
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		major1 = settingRepo.findMajor(major1.getId());
		major2 = settingRepo.findMajor(major2.getId());
		
		assertTrue(major1.getDisplayOrder() > major2.getDisplayOrder());
		
		// Cleanup
		major1.delete();
		major2.delete();
	}
	
	/**
	 * Test alphabetizing majors
	 */
	@Test
	public void testAlphabetizingMajors() {
		LOGIN();
		
		final String REDIRECT_URL = Router.reverse("settings.ConfigurableSettingsTab.configurableSettings").url;
		final String ALPHA_URL = Router.reverse("settings.ConfigurableSettingsTab.alphabetizeAllMajors").url;
		
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		
		Response response = GET(ALPHA_URL);
		assertEquals(REDIRECT_URL,response.getHeader("Location"));
		
		List<Major> majors = settingRepo.findAllMajors();
		String previousName = null;
		for (Major major : majors) {
			if (previousName != null) {
				assertTrue(previousName.compareTo(major.getName()) < 0);
			} 
			previousName = major.getName();
		}
	}
	
	/**
	 * Test adding, editing, and removing a degree.
	 */
	@Test
	public void testAddingEditingRemovingDegrees() {
		
		LOGIN();
		
		// Get our urls and a list of fields.
		final String ADD_URL = Router.reverse("settings.ConfigurableSettingsTab.addDegreeJSON").url;
		final String EDIT_URL = Router.reverse("settings.ConfigurableSettingsTab.editDegreeJSON").url;
		final String REMOVE_URL = Router.reverse("settings.ConfigurableSettingsTab.removeDegreeJSON").url;

		// Add a new custom action
		Map<String,String> params = new HashMap<String,String>();
		params.put("name","New Degree");
		params.put("level", String.valueOf(DegreeLevel.UNDERGRADUATE.getId()));
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
		assertNotNull(settingRepo.findDegree(id));
		assertEquals("New Degree",settingRepo.findDegree(id).getName());
		assertEquals(DegreeLevel.UNDERGRADUATE,settingRepo.findDegree(id).getLevel());

		
		// Now edit the custom action
		params.clear();
		params.put("degreeId","degree_"+id);
		params.put("name", "Changed Name");
		params.put("level", String.valueOf(DegreeLevel.DOCTORAL.getId()));
		response = POST(EDIT_URL,params);
		
		// Verify the action was updated in the database.
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		assertEquals("Changed Name",settingRepo.findDegree(id).getName());
		assertEquals(DegreeLevel.DOCTORAL,settingRepo.findDegree(id).getLevel());

		
		// Now remove the custom action
		params.clear();
		params.put("degreeId","degree_"+id);
		response = POST(REMOVE_URL,params);
		assertContentMatch("\"success\": \"true\"", response);
		
		// Verify the action was deleted in the database;
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		assertNull(settingRepo.findDegree(id));
	}
	
	/**
	 * Test reordering a set of degrees.
	 */
	@Test
	public void testReorderingDegrees() {
		LOGIN();
		
		final String REORDER_URL = Router.reverse("settings.ConfigurableSettingsTab.reorderDegreesJSON").url;
		
		// Create two custom actions:
		Degree degree1 = settingRepo.createDegree("test one",DegreeLevel.DOCTORAL).save();
		Degree degree2 = settingRepo.createDegree("test two",DegreeLevel.MASTERS).save();
		
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		
		// Reorder the custom actions
		Map<String,String> params = new HashMap<String,String>();
		params.put("degreeIds", "degree_"+degree2.getId()+",degree_"+degree1.getId());
		Response response = POST(REORDER_URL,params);
		assertContentMatch("\"success\": \"true\"", response);
		
		// Verify that the actions were reorderd
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		degree1 = settingRepo.findDegree(degree1.getId());
		degree2 = settingRepo.findDegree(degree2.getId());
		
		assertTrue(degree1.getDisplayOrder() > degree2.getDisplayOrder());
		
		// Cleanup
		degree1.delete();
		degree2.delete();
	}
	
	/**
	 * Test alphabetizing degrees
	 */
	@Test
	public void testAlphabetizingDegrees() {
		LOGIN();
		
		final String REDIRECT_URL = Router.reverse("settings.ConfigurableSettingsTab.configurableSettings").url;
		final String ALPHA_URL = Router.reverse("settings.ConfigurableSettingsTab.alphabetizeAllDegrees").url;
		
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		
		Response response = GET(ALPHA_URL);
		assertEquals(REDIRECT_URL,response.getHeader("Location"));
		
		List<Degree> degrees = settingRepo.findAllDegrees();
		String previousName = null;
		for (Degree degree : degrees) {
			String name = (degree.getLevel().getId() - 5) + "-"+degree.getName();
			if (previousName != null) {
				assertTrue(previousName.compareTo(name) < 0);
			} 
			previousName = name;
		}
	}
	
	/**
	 * Test adding, editing, and removing a document type.
	 */
	@Test
	public void testAddingEditingRemovingDocumentTypes() {
		
		LOGIN();
		
		// Get our urls and a list of fields.
		final String ADD_URL = Router.reverse("settings.ConfigurableSettingsTab.addDocumentTypeJSON").url;
		final String EDIT_URL = Router.reverse("settings.ConfigurableSettingsTab.editDocumentTypeJSON").url;
		final String REMOVE_URL = Router.reverse("settings.ConfigurableSettingsTab.removeDocumentTypeJSON").url;

		// Add a new custom action
		Map<String,String> params = new HashMap<String,String>();
		params.put("name","New Document Type");
		params.put("level", String.valueOf(DegreeLevel.UNDERGRADUATE.getId()));
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
		assertNotNull(settingRepo.findDocumentType(id));
		assertEquals("New Document Type",settingRepo.findDocumentType(id).getName());
		assertEquals(DegreeLevel.UNDERGRADUATE,settingRepo.findDocumentType(id).getLevel());

		
		// Now edit the custom action
		params.clear();
		params.put("documentTypeId","documentType_"+id);
		params.put("name", "Changed Name");
		params.put("level", String.valueOf(DegreeLevel.DOCTORAL.getId()));
		response = POST(EDIT_URL,params);
		
		// Verify the action was updated in the database.
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		assertEquals("Changed Name",settingRepo.findDocumentType(id).getName());
		assertEquals(DegreeLevel.DOCTORAL,settingRepo.findDocumentType(id).getLevel());

		
		// Now remove the custom action
		params.clear();
		params.put("documentTypeId","documentType_"+id);
		response = POST(REMOVE_URL,params);
		assertContentMatch("\"success\": \"true\"", response);
		
		// Verify the action was deleted in the database;
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		assertNull(settingRepo.findDocumentType(id));
	}
	
	/**
	 * Test reordering a set of document types.
	 */
	@Test
	public void testReorderingDocumentTypes() {
		LOGIN();
		
		final String REORDER_URL = Router.reverse("settings.ConfigurableSettingsTab.reorderDocumentTypesJSON").url;
		
		// Create two custom actions:
		DocumentType docType1 = settingRepo.createDocumentType("test one",DegreeLevel.DOCTORAL).save();
		DocumentType docType2 = settingRepo.createDocumentType("test two",DegreeLevel.MASTERS).save();
		
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		
		// Reorder the custom actions
		Map<String,String> params = new HashMap<String,String>();
		params.put("documentTypeIds", "documentType_"+docType2.getId()+",documentType_"+docType1.getId());
		Response response = POST(REORDER_URL,params);
		assertContentMatch("\"success\": \"true\"", response);
		
		// Verify that the actions were reorderd
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		docType1 = settingRepo.findDocumentType(docType1.getId());
		docType2 = settingRepo.findDocumentType(docType2.getId());
		
		assertTrue(docType1.getDisplayOrder() > docType2.getDisplayOrder());
		
		// Cleanup
		docType1.delete();
		docType2.delete();
	}
	
	/**
	 * Test alphabetizing document types
	 */
	@Test
	public void testAlphabetizingDocumentTypes() {
		LOGIN();
			
		final String REDIRECT_URL = Router.reverse("settings.ConfigurableSettingsTab.configurableSettings").url;
		final String ALPHA_URL = Router.reverse("settings.ConfigurableSettingsTab.alphabetizeAllDocumentTypes").url;
		
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		
		Response response = GET(ALPHA_URL);
		assertEquals(REDIRECT_URL,response.getHeader("Location"));
		
		List<DocumentType> types = settingRepo.findAllDocumentTypes();
		String previousName = null;
		for (DocumentType type : types) {
			String name = (type.getLevel().getId() - 5) + "-"+type.getName();
			if (previousName != null) {
				assertTrue(previousName.compareTo(name) <= 0);
			} 
			previousName = name;;
		}
	}
	
	/**
	 * Test adding, editing, and removing a committee member role type.
	 */
	@Test
	public void testAddingEditingRemovingCommitteeMemberRoleTypes() {
		
		LOGIN();
		
		// Get our urls and a list of fields.
		final String ADD_URL = Router.reverse("settings.ConfigurableSettingsTab.addCommitteeMemberRoleTypeJSON").url;
		final String EDIT_URL = Router.reverse("settings.ConfigurableSettingsTab.editCommitteeMemberRoleTypeJSON").url;
		final String REMOVE_URL = Router.reverse("settings.ConfigurableSettingsTab.removeCommitteeMemberRoleTypeJSON").url;

		// Add a new custom action
		Map<String,String> params = new HashMap<String,String>();
		params.put("name","New Role Type");
		params.put("level", String.valueOf(DegreeLevel.UNDERGRADUATE.getId()));
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
		assertNotNull(settingRepo.findCommitteeMemberRoleType(id));
		assertEquals("New Role Type",settingRepo.findCommitteeMemberRoleType(id).getName());
		assertEquals(DegreeLevel.UNDERGRADUATE,settingRepo.findCommitteeMemberRoleType(id).getLevel());

		
		// Now edit the custom action
		params.clear();
		params.put("committeeMemberRoleTypeId","committeeMemberRoleType_"+id);
		params.put("name", "Changed Name");
		params.put("level", String.valueOf(DegreeLevel.DOCTORAL.getId()));
		response = POST(EDIT_URL,params);
		
		// Verify the action was updated in the database.
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		assertEquals("Changed Name",settingRepo.findCommitteeMemberRoleType(id).getName());
		assertEquals(DegreeLevel.DOCTORAL,settingRepo.findCommitteeMemberRoleType(id).getLevel());

		
		// Now remove the custom action
		params.clear();
		params.put("committeeMemberRoleTypeId","committeeMemberRoleType_"+id);
		response = POST(REMOVE_URL,params);
		assertContentMatch("\"success\": \"true\"", response);
		
		// Verify the action was deleted in the database;
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		assertNull(settingRepo.findCommitteeMemberRoleType(id));
	}
	
	/**
	 * Test reordering a set of document types.
	 */
	@Test
	public void testReorderingCommitteeMemberRoleTypes() {
		LOGIN();
		
		final String REORDER_URL = Router.reverse("settings.ConfigurableSettingsTab.reorderCommitteeMemberRoleTypesJSON").url;
		
		// Create two custom actions:
		CommitteeMemberRoleType roleType1 = settingRepo.createCommitteeMemberRoleType("test one", DegreeLevel.DOCTORAL).save();
		CommitteeMemberRoleType roleType2 = settingRepo.createCommitteeMemberRoleType("test two", DegreeLevel.MASTERS).save();
		
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		
		// Reorder the custom actions
		Map<String,String> params = new HashMap<String,String>();
		params.put("committeeMemberRoleTypeIds", "committeeMemberRoleType_"+roleType2.getId()+",committeeMemberRoleType_"+roleType1.getId());
		Response response = POST(REORDER_URL,params);
		assertContentMatch("\"success\": \"true\"", response);
		
		// Verify that the actions were reorderd
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		roleType1 = settingRepo.findCommitteeMemberRoleType(roleType1.getId());
		roleType2 = settingRepo.findCommitteeMemberRoleType(roleType2.getId());
		
		assertTrue(roleType1.getDisplayOrder() > roleType2.getDisplayOrder());
		
		// Cleanup
		roleType1.delete();
		roleType2.delete();
	}
	
	/**
	 * Test alphabetizing committee member role types
	 */
	@Test
	public void testAlphabetizingCommitteeMemberRoleTypes() {
		LOGIN();
		
		final String REDIRECT_URL = Router.reverse("settings.ConfigurableSettingsTab.configurableSettings").url;
		final String ALPHA_URL = Router.reverse("settings.ConfigurableSettingsTab.alphabetizeAllCommitteeMemberRoleTypes").url;
		
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		
		Response response = GET(ALPHA_URL);
		assertEquals(REDIRECT_URL,response.getHeader("Location"));
		
		List<CommitteeMemberRoleType> types = settingRepo.findAllCommitteeMemberRoleTypes();
		String previousName = null;
		for (CommitteeMemberRoleType type : types) {
			String name = (type.getLevel().getId() - 5) + "-"+type.getName();
			if (previousName != null) {
				assertTrue(previousName.compareTo(name) <= 0);
			} 
			previousName = name;
		}
	}
	
	/**
	 * Test adding, editing, and removing a graduation month.
	 */
	@Test
	public void testAddingEditingRemovingGraduationMonths() {
		
		LOGIN();
		
		// Get our urls and a list of fields.
		final String ADD_URL = Router.reverse("settings.ConfigurableSettingsTab.addGraduationMonthJSON").url;
		final String EDIT_URL = Router.reverse("settings.ConfigurableSettingsTab.editGraduationMonthJSON").url;
		final String REMOVE_URL = Router.reverse("settings.ConfigurableSettingsTab.removeGraduationMonthJSON").url;

		// Add a new custom action
		Map<String,String> params = new HashMap<String,String>();
		params.put("name","November");
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
		assertNotNull(settingRepo.findGraduationMonth(id));
		assertEquals(10,settingRepo.findGraduationMonth(id).getMonth());
		
		
		// Now edit the custom action
		params.clear();
		params.put("graduationMonthId","graduationMonth_"+id);
		params.put("name", "January");
		response = POST(EDIT_URL,params);
		
		// Verify the action was updated in the database.
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		assertEquals(0,settingRepo.findGraduationMonth(id).getMonth());
		
		// Now remove the custom action
		params.clear();
		params.put("graduationMonthId","graduationMonth_"+id);
		response = POST(REMOVE_URL,params);
		assertContentMatch("\"success\": \"true\"", response);
		
		// Verify the action was deleted in the database;
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		assertNull(settingRepo.findGraduationMonth(id));
	}
	
	/**
	 * Test reordering a set of GraduationMonths.
	 */
	@Test
	public void testReorderingGraduationMonths() {
		LOGIN();
		
		final String REORDER_URL = Router.reverse("settings.ConfigurableSettingsTab.reorderGraduationMonthsJSON").url;
		
		// Create two custom actions:
		GraduationMonth month1 = settingRepo.createGraduationMonth(6).save();
		GraduationMonth month2 = settingRepo.createGraduationMonth(10).save();
		
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		
		// Reorder the custom actions
		Map<String,String> params = new HashMap<String,String>();
		params.put("graduationMonthIds", "graduationMonth_"+month2.getId()+",graduationMonth_"+month1.getId());
		Response response = POST(REORDER_URL,params);
		assertContentMatch("\"success\": \"true\"", response);
		
		// Verify that the actions were reorderd
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		month1 = settingRepo.findGraduationMonth(month1.getId());
		month2 = settingRepo.findGraduationMonth(month2.getId());
		
		assertTrue(month1.getDisplayOrder() > month2.getDisplayOrder());
		
		// Cleanup
		month1.delete();
		month2.delete();
	}
	
	/**
	 * Test alphabetizing Graduation Months
	 */
	@Test
	public void testAlphabetizingGraduationMonths() {
		LOGIN();
		
		final String REDIRECT_URL = Router.reverse("settings.ConfigurableSettingsTab.configurableSettings").url;
		final String ALPHA_URL = Router.reverse("settings.ConfigurableSettingsTab.alphabetizeAllGraduationMonths").url;
		
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		
		Response response = GET(ALPHA_URL);
		assertEquals(REDIRECT_URL,response.getHeader("Location"));
		
		List<GraduationMonth> months = settingRepo.findAllGraduationMonths();
		String previousName = null;
		for (GraduationMonth month : months) {
			String name = String.format("%2d",month.getMonth());
			if (previousName != null) {
				assertTrue(previousName.compareTo(name) < 0);
			} 
			previousName = name;
		}
	}
	
	/**
	 * Test adding, editing, and removing a langauge.
	 */
	@Test
	public void testAddingEditingRemovingLanguage() {
		
		LOGIN();
		
		// Get our urls and a list of fields.
		final String ADD_URL = Router.reverse("settings.ConfigurableSettingsTab.addLanguageJSON").url;
		final String REMOVE_URL = Router.reverse("settings.ConfigurableSettingsTab.removeLanguageJSON").url;

		// Add a new language
		Map<String,String> params = new HashMap<String,String>();
		params.put("name","ru");
		Response response = POST(ADD_URL,params);
		assertContentMatch("\"success\": \"true\"", response);
		
		// Extract the id of the newly created language.
		Pattern ID_PATTERN = Pattern.compile("\"id\": ([0-9]+), ");
		Matcher tokenMatcher = ID_PATTERN.matcher(getContent(response));
		assertTrue(tokenMatcher.find());
		String idString = tokenMatcher.group(1);
		assertNotNull(idString);
		Long id = Long.valueOf(idString);
		
		// Verify the language exists in the database.
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		assertNotNull(settingRepo.findLanguage(id));
		assertEquals("ru",settingRepo.findLanguage(id).getName());
		
		// Now remove the custom action
		params.clear();
		params.put("languageId","language_"+id);
		response = POST(REMOVE_URL,params);
		assertContentMatch("\"success\": \"true\"", response);
		
		// Verify the action was deleted in the database;
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		assertNull(settingRepo.findLanguage(id));
	}
	
	/**
	 * Test reordering a set of programs.
	 */
	@Test
	public void testReorderingLanguage() {
		LOGIN();
		
		final String REORDER_URL = Router.reverse("settings.ConfigurableSettingsTab.reorderLanguagesJSON").url;
		
		// Create two custom actions:
		Language lang1 = settingRepo.createLanguage("hi").save();
		Language lang2 = settingRepo.createLanguage("ir").save();
		
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		
		// Reorder the custom actions
		Map<String,String> params = new HashMap<String,String>();
		params.put("languageIds", "language_"+lang2.getId()+",language_"+lang1.getId());
		Response response = POST(REORDER_URL,params);
		assertContentMatch("\"success\": \"true\"", response);
		
		// Verify that the actions were reorderd
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		lang1 = settingRepo.findLanguage(lang1.getId());
		lang2 = settingRepo.findLanguage(lang2.getId());
		
		assertTrue(lang1.getDisplayOrder() > lang2.getDisplayOrder());
		
		// Cleanup
		lang1.delete();
		lang2.delete();
	}
	
	/**
	 * Test alphabetizing languages
	 */
	@Test
	public void testAlphabetizingLanguages() {
		LOGIN();
		
		final String REDIRECT_URL = Router.reverse("settings.ConfigurableSettingsTab.configurableSettings").url;
		final String ALPHA_URL = Router.reverse("settings.ConfigurableSettingsTab.alphabetizeAllLanguages").url;
		
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		
		Response response = GET(ALPHA_URL);
		assertEquals(REDIRECT_URL,response.getHeader("Location"));
		
		List<Language> languages = settingRepo.findAllLanguages();
		String previousName = null;
		for (Language language : languages) {
			if (previousName != null) {
				assertTrue(previousName.compareTo(language.getLocale().getDisplayName()) < 0);
			} 
			previousName = language.getLocale().getDisplayName();
		}
	}
}
