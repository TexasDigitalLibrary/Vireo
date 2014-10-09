package controllers.settings;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tdl.vireo.email.RecipientType;
import org.tdl.vireo.model.ConditionType;
import org.tdl.vireo.model.EmailTemplate;
import org.tdl.vireo.model.EmailWorkflowRule;
import org.tdl.vireo.model.PersonRepository;
import org.tdl.vireo.model.SettingsRepository;
import org.tdl.vireo.security.SecurityContext;

import play.db.jpa.JPA;
import play.modules.spring.Spring;
import play.mvc.Http.Response;
import play.mvc.Router;
import controllers.AbstractVireoFunctionalTest;
/**
 * Test for the email settings tab. 
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public class EmailSettingsTabTest extends AbstractVireoFunctionalTest {

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
		
		final String URL = Router.reverse("settings.EmailSettingsTab.emailSettings").url;

		Response response = GET(URL);
		assertIsOk(response);
	}
	
	/**
	 * Test adding, editing, and removing Email Workflow Rules
	 */
	@Test
	public void testAddingEditingRemovingEmailWorkflowRules() {
		LOGIN();
		
		final String ADDEDIT_URL = Router.reverse("settings.EmailSettingsTab.addEditEmailWorkflowRuleJSON").url;
		final String REMOVE_URL = Router.reverse("settings.EmailSettingsTab.removeEmailWorkflowRuleJSON").url;

		Map<String,String> params = new HashMap<String,String>();
		
		// create an email workflow rule
		params.put("stateString", "Submitted");
		Response response = POST(ADDEDIT_URL, params);
		assertIsOk(response);
		assertContentMatch("\"success\": true", response);
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
		EmailWorkflowRule emailRule = settingRepo.findEmailWorkflowRule(id);
		assertEquals("Submitted", emailRule.getAssociatedState().getBeanName());
		
		// edit an email workflow rule
		Long collegeId = settingRepo.findCollegeByName("College of Agriculture and Life Sciences").getId(); // College of Agriculture and Life Sciences [FROM TEST DATA]
		Long templateId = settingRepo.findEmailTemplateByName("SYSTEM Initial Submission").getId(); // SYSTEM Initial Submission [FROM TEST DATA]
		params.clear();
		params.put("id", String.valueOf(id));
		params.put("stateString", "Submitted");
		params.put("conditionCategory", ConditionType.College.name());
		params.put("conditionIDString", String.valueOf(collegeId));
		params.put("recipientString", RecipientType.College.name());
		params.put("templateString", String.valueOf(templateId));
		response = POST(ADDEDIT_URL, params);
		assertIsOk(response);
		assertContentMatch("\"success\": true", response);
		// Verify the action exists in the database.
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		emailRule = settingRepo.findEmailWorkflowRule(id);
		assertEquals("Submitted", emailRule.getAssociatedState().getBeanName());
		assertEquals(ConditionType.College, emailRule.getCondition().getConditionType());
		assertEquals(collegeId, emailRule.getCondition().getConditionId());
		assertEquals(RecipientType.College, emailRule.getRecipientType());
		assertEquals(templateId, emailRule.getEmailTemplate().getId());
		
		// remove an email workflow rule
		params.clear();
		params.put("ruleID", String.valueOf(id));
		response = POST(REMOVE_URL, params);
		assertIsOk(response);
		assertContentMatch("\"success\": true", response);
		// Verify the action exists in the database.
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		assertNull(settingRepo.findEmailWorkflowRule(id));
	}
	
	/**
	 * Test adding, editing, and removing an Email Template.
	 */
	@Test
	public void testAddingRetrievingEditingRemovingTemplates() {
		
		LOGIN();
		
		// Get our urls and a list of fields.
		final String ADD_URL = Router.reverse("settings.EmailSettingsTab.addEmailTemplateJSON").url;
		final String RETRIEVE_URL = Router.reverse("settings.EmailSettingsTab.retrieveEmailTemplateJSON").url;
		final String EDIT_URL = Router.reverse("settings.EmailSettingsTab.editEmailTemplateJSON").url;
		final String REMOVE_URL = Router.reverse("settings.EmailSettingsTab.removeEmailTemplateJSON").url;

		// Add a new template
		Map<String,String> params = new HashMap<String,String>();
		params.put("name","New Template");
		params.put("subject","New Subject");
		params.put("message","New Message");

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
		assertNotNull(settingRepo.findEmailTemplate(id));
		assertEquals("New Template",settingRepo.findEmailTemplate(id).getName());
		assertEquals("New Subject",settingRepo.findEmailTemplate(id).getSubject());
		assertEquals("New Message",settingRepo.findEmailTemplate(id).getMessage());
		
		// Now retrieve the template
		params.clear();
		params.put("emailTemplateId", "emailTemplate_"+id);
		response = POST(RETRIEVE_URL,params);
		assertContentMatch("New Template", response);
		assertContentMatch("New Subject", response);
		assertContentMatch("New Message", response);

		
		// Now edit the custom action
		params.clear();
		params.put("emailTemplateId","emailTemplate_"+id);
		params.put("name", "Changed Name");
		params.put("subject", "Changed Subject");
		params.put("message", "Changed Message");
		response = POST(EDIT_URL,params);
		assertContentMatch("\"success\": \"true\"", response);

		
		// Verify the action was updated in the database.
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		assertEquals("Changed Name",settingRepo.findEmailTemplate(id).getName());
		assertEquals("Changed Subject",settingRepo.findEmailTemplate(id).getSubject());
		assertEquals("Changed Message",settingRepo.findEmailTemplate(id).getMessage());
		
		// Now remove the custom action
		params.clear();
		params.put("emailTemplateId","emailTemplate_"+id);
		response = POST(REMOVE_URL,params);
		assertContentMatch("\"success\": \"true\"", response);
		
		// Verify the action was deleted in the database;
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		assertNull(settingRepo.findEmailTemplate(id));
	}
	
	/**
	 * Test reordering a set of email templates.
	 */
	@Test
	public void testReorderingTemplates() {
		LOGIN();
		
		final String REORDER_URL = Router.reverse("settings.EmailSettingsTab.reorderEmailTemplatesJSON").url;
		
		// Create two custom actions:
		EmailTemplate template1 = settingRepo.createEmailTemplate("name1", "subject", "message").save();
		EmailTemplate template2 = settingRepo.createEmailTemplate("name2", "subject", "message").save();
		
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		
		// Reorder the custom actions
		Map<String,String> params = new HashMap<String,String>();
		params.put("emailTemplateIds", "emailTemplate_"+template2.getId()+",emailTemplate_"+template1.getId());
		Response response = POST(REORDER_URL,params);
		assertContentMatch("\"success\": \"true\"", response);
		
		// Verify that the actions were reorderd
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		template1 = settingRepo.findEmailTemplate(template1.getId());
		template2 = settingRepo.findEmailTemplate(template2.getId());
		assertTrue(template1.getDisplayOrder() > template2.getDisplayOrder());
		
		// Cleanup
		template1.delete();
		template2.delete();
	}
}
