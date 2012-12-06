package controllers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.tdl.vireo.model.Attachment;
import org.tdl.vireo.model.AttachmentType;
import org.tdl.vireo.model.CommitteeMember;
import org.tdl.vireo.model.CustomActionDefinition;
import org.tdl.vireo.model.EmailTemplate;
import org.tdl.vireo.model.EmbargoType;
import org.tdl.vireo.model.Person;
import org.tdl.vireo.model.PersonRepository;
import org.tdl.vireo.model.RoleType;
import org.tdl.vireo.model.SettingsRepository;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.SubmissionRepository;
import org.tdl.vireo.model.jpa.JpaAttachmentImpl;
import org.tdl.vireo.security.SecurityContext;
import org.tdl.vireo.state.State;
import org.tdl.vireo.state.StateManager;

import play.Logger;
import play.Play;
import play.db.jpa.JPA;
import play.modules.spring.Spring;
import play.mvc.Http.Response;
import play.mvc.Router;
import play.mvc.Scope.Session;

/**
 * Test the methods of the view tab.
 * 
 * @author Micah Cooper
 *
 */
public class ViewTabTest extends AbstractVireoFunctionalTest {

	public static SecurityContext context = Spring.getBeanOfType(SecurityContext.class);
	public static PersonRepository personRepo = Spring.getBeanOfType(PersonRepository.class);
	public static SubmissionRepository subRepo = Spring.getBeanOfType(SubmissionRepository.class);
	public static SettingsRepository settingRepo = Spring.getBeanOfType(SettingsRepository.class);
	public static StateManager stateManager = Spring.getBeanOfType(StateManager.class);
	
	/**
	 * Simple test to make sure we can view a blank document in the viewTab
	 * without generating errors.
	 */
	@Test
	public void testViewingABlankSubmission() {
		context.turnOffAuthorization();
		
		Person person = personRepo.findPersonByEmail("bthornton@gmail.com");		
		Submission submission = subRepo.createSubmission(person).save();
		Long id = submission.getId();
		
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		
		LOGIN();
		
		final String VIEW_URL = Router.reverse("ViewTab.view").url;

		Response response = GET(VIEW_URL+"?subId="+id);
		assertIsOk(response);
		
		submission = subRepo.findSubmission(id);
		submission.delete();
		
		context.restoreAuthorization();
	}
	
	/**
	 * Test that an admin can change an item on a submission.
	 */
	@Test
	public void testUpdateJSON() {
		context.turnOffAuthorization();
		
		Person person = personRepo.findPersonByEmail("bthornton@gmail.com");		
		Submission submission = subRepo.createSubmission(person);
		submission.setDocumentTitle("My Document Title");
		submission.setDocumentAbstract("My Document Abstract");
		submission.save();
		Long id = submission.getId();
		
		assertEquals("My Document Title", submission.getDocumentTitle());
		assertEquals("My Document Abstract", submission.getDocumentAbstract());
		
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		
		LOGIN();
		
		final String UPDATE_URL = Router.reverse("ViewTab.updateJSON").url;
		
		Map<String,String> params = new HashMap<String,String>();
		params.put("subId", id.toString());
		params.put("field", "title");
		params.put("value", "This is a new title");
		
		Response response = POST(UPDATE_URL,params);
		assertIsOk(response);
		assertContentMatch("\"success\": true,",response);
		
		submission = subRepo.findSubmission(id);
		
		assertFalse("My Document Title".equals(submission.getDocumentTitle()));
		assertEquals("This is a new title", submission.getDocumentTitle());
		assertEquals("My Document Abstract", submission.getDocumentAbstract());
		
		submission.delete();
		
		context.restoreAuthorization();
	}
	
	/**
	 * Test that an admin can add a committee member.
	 */
	@Test
	public void testAddCommitteeMemberJSON() {
		context.turnOffAuthorization();
		
		Person person = personRepo.findPersonByEmail("bthornton@gmail.com");
		Submission submission = subRepo.createSubmission(person);
		submission.save();
		Long id = submission.getId();
		
		assertEquals(0, submission.getCommitteeMembers().size());
		
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		
		LOGIN();	
		
		final String UPDATE_URL = Router.reverse("ViewTab.addCommitteeMemberJSON").url;
		
		Map<String,String> params = new HashMap<String,String>();
		params.put("subId", id.toString());
		params.put("firstName", "John");
		params.put("lastName", "Doe");
		params.put("middleName", "T");
		params.put("roles", "Committee Member");
		
		
		Response response = POST(UPDATE_URL,params);
		assertIsOk(response);
		assertContentMatch("\"success\": true,",response);
		
		submission = subRepo.findSubmission(id);
		
		CommitteeMember member = submission.getCommitteeMembers().get(0);
		
		assertEquals("John", member.getFirstName());
		assertEquals("Doe", member.getLastName());
		assertEquals("T", member.getMiddleName());
		assertEquals(1, member.getRoles().size());
		assertEquals("Committee Member", member.getRoles().get(0));
		
		member.delete();
		submission.delete();		
		
		context.restoreAuthorization();
		
	}
	
	/**
	 * Test that an admin can update a committee member
	 */
	@Test
	public void testUpdateCommitteeMemberJSON() {
		context.turnOffAuthorization();
		
		Person person = personRepo.findPersonByEmail("bthornton@gmail.com");
		Submission submission = subRepo.createSubmission(person).save();
		CommitteeMember member = submission.addCommitteeMember("John", "Doe", "T").save();
		member.addRole("Committee Member");
		submission.save();
		
		Long subId = submission.getId();
		Long id = member.getId();
		
		assertEquals(1, submission.getCommitteeMembers().size());
		assertEquals("John", member.getFirstName());
		assertEquals("Doe", member.getLastName());
		assertEquals("T", member.getMiddleName());
		assertEquals("Committee Member", member.getRoles().get(0));
		
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		
		LOGIN();	
		
		final String UPDATE_URL = Router.reverse("ViewTab.updateCommitteeMemberJSON").url;
		
		Map<String,String> params = new HashMap<String,String>();
		params.put("id", id.toString());
		params.put("firstName", "Jill");
		params.put("lastName", "Duck");
		params.put("middleName", "M");
		params.put("roles", "Committee Chair");
		
		Response response = POST(UPDATE_URL,params);
		assertIsOk(response);
		assertContentMatch("\"success\": true,",response);
		
		submission = subRepo.findSubmission(subId);		
		member = subRepo.findCommitteeMember(id);
		
		assertEquals("Jill", member.getFirstName());
		assertEquals("Duck", member.getLastName());
		assertEquals("M", member.getMiddleName());
		assertEquals("Committee Chair", member.getRoles().get(0));
		
		member.delete();
		submission.delete();		
		
		context.restoreAuthorization();
	}
	
	/**
	 * Test that an admin can delete a committee member
	 */
	@Test
	public void testRemoveCommitteeMemberJSON() {
		context.turnOffAuthorization();
		
		Person person = personRepo.findPersonByEmail("bthornton@gmail.com");
		Submission submission = subRepo.createSubmission(person);
		submission.addCommitteeMember("John", "Doe", "T");
		submission.save();
		
		CommitteeMember member = submission.getCommitteeMembers().get(0);
		Long subId = submission.getId();
		Long id = member.getId();
		
		assertEquals(1, submission.getCommitteeMembers().size());
		assertEquals("John", member.getFirstName());
		assertEquals("Doe", member.getLastName());
		assertEquals("T", member.getMiddleName());
		
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		
		LOGIN();	
		
		final String UPDATE_URL = Router.reverse("ViewTab.removeCommitteeMemberJSON").url;
		
		Map<String,String> params = new HashMap<String,String>();
		params.put("id", id.toString());
		
		Response response = POST(UPDATE_URL,params);
		assertIsOk(response);
		assertContentMatch("\"success\": true,",response);
		
		submission = subRepo.findSubmission(subId);		
		member = subRepo.findCommitteeMember(id);
		
		assertNull(member);
		
		submission.delete();		
		
		context.restoreAuthorization();
	}
	
	/**
	 * Test that the action log table gets refreshed properly.
	 */
	@Test
	public void testRefreshActionLogTable() {
		context.turnOffAuthorization();		
		
		Person person = personRepo.findPersonByEmail("bthornton@gmail.com");		
		Submission submission = subRepo.createSubmission(person);
		submission.setDocumentTitle("My Document Title");
		State state = stateManager.getState("InReview");
		submission.setState(state);
		submission.save();
		Long id = submission.getId();
		
		assertEquals("My Document Title", submission.getDocumentTitle());
				
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		
		LOGIN();
		
		String UPDATE_URL = Router.reverse("ViewTab.updateJSON").url;
		
		Map<String,String> params = new HashMap<String,String>();
		params.put("subId", id.toString());
		params.put("field", "title");
		params.put("value", "This is a new title");
		
		Response response = POST(UPDATE_URL,params);
		assertIsOk(response);
		assertContentMatch("\"success\": true,",response);
		
		submission = subRepo.findSubmission(id);
		
		UPDATE_URL = Router.reverse("ViewTab.refreshActionLogTable").url;
		
		params.clear();
		params.put("id", id.toString());
		
		response = POST(UPDATE_URL,params);
		assertIsOk(response);
		assertContentMatch("Document title changed to",response);				
		
		submission.delete();
		
		context.restoreAuthorization();
	}
	
	/**
	 * Test that the left column gets refreshed properly.
	 */
	@Test
	public void testRefreshLeftColumn() {
		context.turnOffAuthorization();
				
		Person person = personRepo.findPersonByEmail("bthornton@gmail.com");		
		Submission submission = subRepo.createSubmission(person);
		submission.setDocumentTitle("My Document Title");
		State state = stateManager.getState("InReview");
		submission.setState(state);
		EmbargoType embargo = settingRepo.findAllEmbargoTypes().get(0);
		submission.setEmbargoType(embargo);
		submission.save();
		Long id = submission.getId();
		
		assertEquals("My Document Title", submission.getDocumentTitle());
				
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		
		LOGIN();
		
		String UPDATE_URL = Router.reverse("ViewTab.updateJSON").url;
		
		Map<String,String> params = new HashMap<String,String>();
		params.put("subId", id.toString());
		params.put("field", "title");
		params.put("value", "This is a new title");
		
		Response response = POST(UPDATE_URL,params);
		assertIsOk(response);
		assertContentMatch("\"success\": true,",response);
		
		submission = subRepo.findSubmission(id);
		
		UPDATE_URL = Router.reverse("ViewTab.refreshLeftColumn").url;
		
		params.clear();
		params.put("id", id.toString());
		
		response = POST(UPDATE_URL,params);
		assertIsOk(response);
		assertContentMatch("Document title changed to",response);				
		
		submission.delete();
		
		context.restoreAuthorization();		
	}
	
	/**
	 * Test that an admin can change the submission status
	 */
	@Test
	public void testChangeSubmissionStatus() {
		context.turnOffAuthorization();
				
		Person person = personRepo.findPersonByEmail("bthornton@gmail.com");		
		Submission submission = subRepo.createSubmission(person);
		State state = stateManager.getState("InReview");
		submission.setState(state);
		submission.save();
		Long id = submission.getId();
		
		assertEquals(submission.getState().getBeanName(), "InReview");
		
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		
		LOGIN();
		
		String UPDATE_URL = Router.reverse("ViewTab.changeSubmissionStatus").url;
		
		Map<String,String> params = new HashMap<String,String>();
		params.put("id", id.toString());
		params.put("submission-status", "NeedsCorrection");
		params.put("depositLocationId", "1");
		params.put("special_value", "");
		
		Response response = POST(UPDATE_URL,params);
		assertStatus(302, response);
		
		submission = subRepo.findSubmission(id);	
		
		assertEquals(submission.getState().getBeanName(), "NeedsCorrection");
		
		submission.delete();
		
		context.restoreAuthorization();	
	}
	
	/**
	 * Test that an admin can change the submission assignee
	 */
	@Test
	public void testChangeAssignedTo() {
		context.turnOffAuthorization();
				
		Person person = personRepo.findPersonByEmail("bthornton@gmail.com");
		Person newPerson = personRepo.createPerson("jdoe", "jdoe@gmail.com", "John", "Doe", RoleType.REVIEWER);
		newPerson.save();
		Submission submission = subRepo.createSubmission(person);
		submission.setAssignee(person);
		submission.save();
		
		Long id = submission.getId();
		Long personId = newPerson.getId();
		
		assertEquals(submission.getAssignee().getCurrentEmailAddress(), "bthornton@gmail.com");
		
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		
		LOGIN();
		
		String UPDATE_URL = Router.reverse("ViewTab.changeAssignedTo").url;
		
		Map<String,String> params = new HashMap<String,String>();
		params.put("id", id.toString());
		params.put("assignee", personId.toString());
		params.put("special_value", "");
		
		Response response = POST(UPDATE_URL,params);
		assertStatus(302, response);
		
		submission = subRepo.findSubmission(id);	
		newPerson = personRepo.findPerson(personId);
		
		assertEquals(submission.getAssignee().getCurrentEmailAddress(), "jdoe@gmail.com");
		
		submission.delete();
		newPerson.delete();
		
		context.restoreAuthorization();	
	}
	
	/**
	 * Test that an admin can change the submission date.
	 */
	@Test
	public void testChangeSubmissionDate() {
		context.turnOffAuthorization();
		
		Person person = personRepo.findPersonByEmail("bthornton@gmail.com");
		Submission submission = subRepo.createSubmission(person);
		submission.setAssignee(person);
		submission.save();
		
		Long id = submission.getId();
		
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		
		LOGIN();
		
		String UPDATE_URL = Router.reverse("ViewTab.changeSubmissionDate").url;
		
		Map<String,String> params = new HashMap<String,String>();
		params.put("id", id.toString());
		params.put("submission-date", "05/30/1999");
		
		Response response = POST(UPDATE_URL,params);
		assertStatus(302, response);
		
		submission = subRepo.findSubmission(id);
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
		
		String theDate = dateFormat.format(submission.getSubmissionDate());
		
		assertEquals(theDate, "05/30/1999");
		
		submission.delete();
		
		context.restoreAuthorization();	
	}
	
	/**
	 * Test that an admin can add an action log comment
	 */
	@Test
	public void testAddActionLogComment() {
		context.turnOffAuthorization();
				
		Person person = personRepo.findPersonByEmail("bthornton@gmail.com");
		Submission submission = subRepo.createSubmission(person);
		submission.setAssignee(person);
		submission.save();
		
		Long id = submission.getId();
		int numActionLogs = subRepo.findActionLog(submission).size();
		
		assertEquals(submission.getAssignee().getCurrentEmailAddress(), "bthornton@gmail.com");
		
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		
		LOGIN();
		
		String UPDATE_URL = Router.reverse("ViewTab.view").url;
		
		Map<String,String> params = new HashMap<String,String>();
		params.put("subId", id.toString());
		params.put("subject", "The subject");
		params.put("comment", "This is the comment.");
		params.put("visibility", "public");
		params.put("addActionLogComment", "true");
		
		Response response = POST(UPDATE_URL,params);
		assertIsOk(response);
		
		submission = subRepo.findSubmission(id);
		assertTrue(subRepo.findActionLog(submission).size()>numActionLogs);
		assertEquals("The subject: This is the comment.", subRepo.findActionLog(submission).get(0).getEntry());
		
		submission.delete();
		
		context.restoreAuthorization();		
	}
	
	/**
	 * Test that an email template returns successful with a subject and message
	 */
	@Test
	public void testRetrieveTemplateJSON() {
		context.turnOffAuthorization();
				
		EmailTemplate template = settingRepo.createEmailTemplate("newTemplate", "New Template Subject", "New Template Message");
		template.save();
		Long id = template.getId();
		
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		
		LOGIN();
		
		String UPDATE_URL = Router.reverse("ViewTab.retrieveTemplateJSON").url;
		
		Map<String,String> params = new HashMap<String,String>();
		params.put("id", id.toString());
		
		Response response = POST(UPDATE_URL,params);
		assertIsOk(response);
		assertContentMatch("\"success\": true,",response);
		
		template = settingRepo.findEmailTemplate(id);
		template.delete();
		
		context.restoreAuthorization();		
	}
	
	/**
	 * Test that an admin can change the custom action values.
	 */
	@Test
	public void testUpdateCustomActionsJSON() {
		context.turnOffAuthorization();
				
		Person person = personRepo.findPersonByEmail("bthornton@gmail.com");
		Submission submission = subRepo.createSubmission(person);
		submission.setAssignee(person);
		submission.save();
		
		Long id = submission.getId();
		
		assertEquals(submission.getAssignee().getCurrentEmailAddress(), "bthornton@gmail.com");
		
		CustomActionDefinition actionDef = settingRepo.createCustomActionDefinition("Passed Classes").save();
		
		Long actionId = actionDef.getId();
		
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		
		LOGIN();
		
		String UPDATE_URL = Router.reverse("ViewTab.updateCustomActionsJSON").url;
		
		Map<String,String> params = new HashMap<String,String>();
		params.put("id", id.toString());
		params.put("action", actionId.toString());
		params.put("value", "true");
		
		Response response = POST(UPDATE_URL,params);
		assertIsOk(response);
		
		submission = subRepo.findSubmission(id);
		actionDef = settingRepo.findCustomActionDefinition(actionId);
		
		assertTrue(submission.getCustomAction(actionDef).getValue());
		
		submission.delete();
		actionDef.delete();
		
		context.restoreAuthorization();
	}
	
	/**
	 * Test uploading an "additonal" file
	 */
	@Test
	public void testUploadAdditonal() {
		context.turnOffAuthorization();
			
		Person person = personRepo.findPersonByEmail("bthornton@gmail.com");
		Submission submission = subRepo.createSubmission(person);
		submission.setAssignee(person);
		submission.save();
		
		Long id = submission.getId();
		
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		
		LOGIN();
		
		String UPDATE_URL = Router.reverse("ViewTab.view").url;
		
		Map<String,String> params = new HashMap<String,String>();
		params.put("subId", id.toString());
		params.put("uploadType", "additional");
		params.put("attachmentType", "SUPPLEMENTAL");
		params.put("addFile", "true");
		
		Map<String,File> files = new HashMap<String,File>();
		File file = null;
		
		try {
			file = getResourceFile("SampleSupplementalDocument.doc");
			files.put("additionalAttachment", file);
		} catch (IOException ioe) {
			fail("Test upload file not found.");
		}
		
		Response response = POST(UPDATE_URL,params,files);
		assertIsOk(response);
		
		submission = subRepo.findSubmission(id);
		
		assertNotNull(submission.getSupplementalDocuments().get(0));
		Attachment attachment = submission.getSupplementalDocuments().get(0);
		
		attachment.delete();		
		submission.delete();
		
		context.restoreAuthorization();
	}
	
	/**
	 * Test uploading a "primary" file
	 * @throws IOException 
	 */
	@Test
	public void testUploadPrimary() throws IOException {
		context.turnOffAuthorization();
				
		Person person = personRepo.findPersonByEmail("bthornton@gmail.com");
		Submission submission = subRepo.createSubmission(person);
		submission.setAssignee(person);
		submission.save();
		File file = getResourceFile("SamplePrimaryDocument.pdf");

		
		Long id = submission.getId();
		
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		
		LOGIN();
		
		String UPDATE_URL = Router.reverse("ViewTab.view").url;
		
		// Update a primary document
		Map<String,String> params = new HashMap<String,String>();
		params.put("subId", id.toString());
		params.put("uploadType", "primary");
		params.put("addFile", "true");
		
		Map<String,File> files = new HashMap<String,File>();
		files.put("primaryAttachment", file);
		
		Response response = POST(UPDATE_URL,params,files);
		assertIsOk(response);
		
		// Re-upload a primary document
		params = new HashMap<String,String>();
		params.put("subId", id.toString());
		params.put("uploadType", "primary");
		params.put("addFile", "true");
		
		files = new HashMap<String,File>();
		files.put("primaryAttachment", file);
		
		response = POST(UPDATE_URL,params,files);
		assertIsOk(response);
		
		
		file.delete();
		// Verify the files were uploaded and archived
		submission = subRepo.findSubmission(id);
		assertEquals("PRIMARY-DOCUMENT.pdf",submission.getPrimaryDocument().getName());
		assertEquals("PRIMARY-DOCUMENT-archived-on-"+JpaAttachmentImpl.dateFormat.format(new Date())+".pdf",submission.getAttachmentsByType(AttachmentType.ARCHIVED).get(0).getName());
		
		submission.delete();
		
		context.restoreAuthorization();
	}
	
	/**
	 * Test editing a file from the view tab.
	 */
	@Test
	public void testEditFile() {
		context.turnOffAuthorization();
		
		Person person = personRepo.findPersonByEmail("bthornton@gmail.com");
		Submission submission = subRepo.createSubmission(person);
		submission.setAssignee(person);
		submission.save();
		
		Long id = submission.getId();
		
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		
		LOGIN();
		
		String UPDATE_URL = Router.reverse("ViewTab.view").url;
		
		Map<String,String> params = new HashMap<String,String>();
		params.put("subId", id.toString());
		params.put("uploadType", "additional");
		params.put("attachmentType", "FEEDBACK");
		params.put("addFile", "true");
		
		Map<String,File> files = new HashMap<String,File>();
		File file = null;
		
		try {
			file = getResourceFile("SamplePrimaryDocument.pdf");
			files.put("additionalAttachment", file);
		} catch (IOException ioe) {
			fail("Test upload file not found.");
		}
		
		Response response = POST(UPDATE_URL,params,files);
		assertIsOk(response);
		
		submission = subRepo.findSubmission(id);
		Long fileId = submission.getAttachments().get(0).getId();
		String oldFileName = submission.findAttachmentById(fileId).getName();
		AttachmentType oldAttachmentType = submission.findAttachmentById(fileId).getType();
		
		assertEquals(AttachmentType.FEEDBACK,oldAttachmentType);
		
		JPA.em().clear();
		
		params.clear();
		
		params.put("subId", id.toString());
		params.put("editFile", "true");
		params.put("attachmentId", fileId.toString());
		params.put("fileName", "newName");
		params.put("attachmentType", "SOURCE");
		
		response = POST(UPDATE_URL,params);
		assertIsOk(response);
		
		submission = subRepo.findSubmission(id);
		String newFileName = submission.findAttachmentById(fileId).getName();
		AttachmentType newAttachmentType = submission.findAttachmentById(fileId).getType();
		
		assertFalse(oldFileName.equals(newFileName));
		assertFalse(AttachmentType.FEEDBACK==newAttachmentType);
		assertTrue("newName".equals(newFileName));
		assertTrue(AttachmentType.SOURCE==newAttachmentType);
		
		Session.current().clear();
		file.delete();
		submission.delete();
		
		context.restoreAuthorization();
	}
	
	/**
	 * Test the ability to delete a file.
	 */
	@Test
	public void testDeleteFile() {
		context.turnOffAuthorization();
		
		Person person = personRepo.findPersonByEmail("bthornton@gmail.com");
		Submission submission = subRepo.createSubmission(person);
		submission.setAssignee(person);
		submission.save();
		
		Long id = submission.getId();
		
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		
		LOGIN();
		
		String UPDATE_URL = Router.reverse("ViewTab.view").url;
		
		Map<String,String> params = new HashMap<String,String>();
		params.put("subId", id.toString());
		params.put("uploadType", "additional");
		params.put("attachmentType", "FEEDBACK");
		params.put("addFile", "true");
		
		Map<String,File> files = new HashMap<String,File>();
		File file = null;
		
		try {
			file = getResourceFile("SamplePrimaryDocument.pdf");
			files.put("additionalAttachment", file);
		} catch (IOException ioe) {
			fail("Test upload file not found.");
		}
		
		Response response = POST(UPDATE_URL,params,files);
		assertIsOk(response);
		
		submission = subRepo.findSubmission(id);
		Long fileId = submission.getAttachments().get(0).getId();
		
		JPA.em().clear();
		
		params.clear();
		
		params.put("subId", id.toString());
		params.put("deleteFile", "true");
		params.put("attachmentId", fileId.toString());
		
		response = POST(UPDATE_URL,params);
		assertIsOk(response);
		
		submission = subRepo.findSubmission(id);
		assertEquals(null,submission.findAttachmentById(fileId));
		
		Session.current().clear();
		file.delete();
		submission.delete();
		
		context.restoreAuthorization();
	}
	
	/**
	 * Test the viewFile page
	 */
	@Test
	public void testViewFile() {
		context.turnOffAuthorization();
				
		Person person = personRepo.findPersonByEmail("bthornton@gmail.com");
		Submission submission = subRepo.createSubmission(person);
		submission.setAssignee(person);
		submission.save();
		
		Long id = submission.getId();
		
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		
		LOGIN();
		
		String UPDATE_URL = Router.reverse("ViewTab.view").url;
		
		Map<String,String> params = new HashMap<String,String>();
		params.put("subId", id.toString());
		params.put("uploadType", "additional");
		params.put("attachmentType", "FEEDBACK");
		params.put("addFile", "true");
		
		Map<String,File> files = new HashMap<String,File>();
		File file = null;
		
		try {
			file = getResourceFile("SamplePrimaryDocument.pdf");
			files.put("additionalAttachment", file);
		} catch (IOException ioe) {
			fail("Test upload file not found.");
		}
		
		Response response = POST(UPDATE_URL,params,files);
		assertIsOk(response);
		
		submission = subRepo.findSubmission(id);
		Long fileId = submission.getAttachments().get(0).getId();
		String fileName = submission.getAttachments().get(0).getName();
		
		Map<String,Object> routeArgs = new HashMap<String,Object>();
		routeArgs.put("id", fileId.toString());
		routeArgs.put("name", fileName);
		
		UPDATE_URL = Router.reverse("ViewTab.viewFile",routeArgs).url;		
		
		Session.current().put("submission", id.toString());
					
		response = GET(UPDATE_URL);
		
		assertIsOk(response);
		
		Session.current().clear();
		file.delete();
		submission.delete();
		
		context.restoreAuthorization();
	}
	
	/**
	 * Test parsing a graduation date
	 */
	@Test
	public void testParseGraduation() {
		List<String> parsedGrad = ViewTab.parseGraduation("May 2012");
		
		assertEquals("May",parsedGrad.get(0));
		assertEquals("2012",parsedGrad.get(1));
	}
	
	/**
	 * Test converting a month String to an integer
	 */
	@Test
	public void testMonthNameToInt() {
		int month = ViewTab.monthNameToInt("May");
		
		assertEquals(4,month);
	}
	
	/**
     * Extract the file from the jar and place it in a temporary location for the test to operate from.
     *
     * @param filePath The path, relative to the classpath, of the file to reference.
     * @return A Java File object reference.
     * @throws IOException
     */
    protected static File getResourceFile(String filePath) throws IOException {

        File file = File.createTempFile("ingest-import-test", ".pdf");

        // While we're packaged by play we have to ask Play for the inputstream instead of the classloader.
        //InputStream is = DSpaceCSVIngestServiceImplTests.class
        //		.getResourceAsStream(filePath);
        InputStream is = Play.classloader.getResourceAsStream(filePath);
        OutputStream os = new FileOutputStream(file);

        // Copy the file out of the jar into a temporary space.
        byte[] buffer = new byte[1024];
        int len;
        while ((len = is.read(buffer)) > 0) {
            os.write(buffer, 0, len);
        }
        is.close();
        os.close();

        return file;
    }
}
