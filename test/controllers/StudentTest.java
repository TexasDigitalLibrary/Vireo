package controllers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tdl.vireo.model.ActionLog;
import org.tdl.vireo.model.AttachmentType;
import org.tdl.vireo.model.Configuration;
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

import play.Play;
import play.db.jpa.JPA;
import play.modules.spring.Spring;
import play.mvc.Http.Response;
import play.mvc.Router;

import static org.tdl.vireo.constant.AppConfig.*;


/**
 * Test all the functions of the Student Controller.
 * 
 * @author <a href="http://www.scottphillips.com/">Scott Phillips</a>
 */
public class StudentTest extends AbstractVireoFunctionalTest {

	// Spring dependencies
	public static SecurityContext context = Spring.getBeanOfType(SecurityContext.class);
	public static PersonRepository personRepo = Spring.getBeanOfType(PersonRepository.class);
	public static SubmissionRepository subRepo = Spring.getBeanOfType(SubmissionRepository.class);
	public static SettingsRepository settingRepo = Spring.getBeanOfType(SettingsRepository.class);
	public static StateManager stateManager = Spring.getBeanOfType(StateManager.class);


	// The original configuration, we will restore to these after the test.
	public String originalSubmissionsOpen = null;
	public String originalAllowMultiple = null;
	
	public Person submitter = null;
	
	public List<Submission> subs = new ArrayList<Submission>();
	
	/**
	 * Setup
	 * 
	 * Grab the original configuration settings, and store their values.
	 */
	@Before
	public void setup() {
		// Turn off authentication for the test thread
		context.turnOffAuthorization();
		
		// Get the configuration setting prior to doing anything so that we can restore them after.
		originalSubmissionsOpen = settingRepo.getConfigValue(SUBMISSIONS_OPEN);
		originalAllowMultiple = settingRepo.getConfigValue(ALLOW_MULTIPLE_SUBMISSIONS);
		
		submitter = personRepo.createPerson("netid", "student@tdl.org", "first", "last", RoleType.STUDENT).save();
		submitter.setPassword("password");
		submitter.save();
		
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
	}

	/**
	 * Cleanup
	 * 
	 * Restore the original configuration settings, and delete the submission.
	 */
	@After
	public void cleanup() {

		// Restore our configuration.
		Configuration submissionsOpen = settingRepo.findConfigurationByName(SUBMISSIONS_OPEN);
		if (originalSubmissionsOpen == null && submissionsOpen != null) {
			submissionsOpen.delete();
		}
		if (originalSubmissionsOpen != null && submissionsOpen == null) {
			settingRepo.createConfiguration(SUBMISSIONS_OPEN,"true").save();
		}
		
		Configuration allowMultiple = settingRepo.findConfigurationByName(ALLOW_MULTIPLE_SUBMISSIONS);
		if (originalAllowMultiple == null && allowMultiple != null) {
			allowMultiple.delete();
		}
		if (originalAllowMultiple != null && allowMultiple == null) {
			settingRepo.createConfiguration(ALLOW_MULTIPLE_SUBMISSIONS,"true").save();
		}
		
		// Delete any left over submission.
		for(Submission sub : subs) {
			subRepo.findSubmission(sub.getId()).delete();
		}
		
		// Delete the submitter.
		if (submitter != null) {
			
			for(Submission sub : subRepo.findSubmission(submitter)) {
				sub.delete();
			}
			
			personRepo.findPerson(submitter.getId()).delete();
		}
		
		context.restoreAuthorization();
		
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
	}

	
	
	
	
	/**
	 * Test that when submissions are open students with no submissions skip
	 * straight starting a submission.
	 */
	@Test
	public void testOpenWithNoSubmissions() {
		
		configure(true,false);
		
		LOGIN("student@tdl.org");
		
		final String LIST_URL = Router.reverse("Student.submissionList").url;
		final String PERSONAL_INFO_URL = Router.reverse("submit.PersonalInfo.personalInfo").url;

		
		Response response = GET(LIST_URL);
		assertEquals(PERSONAL_INFO_URL,response.getHeader("Location"));
		response = GET(PERSONAL_INFO_URL);
		assertContentMatch("<title>Verify Personal Information</title>",response);		
	}

	/**
	 * Test that when submissions are closed, students without any submissions
	 * go to the list page.
	 */
	@Test
	public void testClosedWithNoSubmissions() {
		configure(false,false);
		
		LOGIN("student@tdl.org");
		
		final String LIST_URL = Router.reverse("Student.submissionList").url;

		Response response = GET(LIST_URL);
		assertIsOk(response);
		assertContentMatch("<title>Submission Status</title>",response);

	}

	/**
	 * Test that when submissions are open, and multiple submissions are
	 * allowed, the list page is shown.
	 */
	@Test
	public void testOpenAndMultipleWithSubmissions() {
		configure(true,true);
				
		// Create a submission
		subs.add((Submission) subRepo.createSubmission(submitter).save());
		
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		
		LOGIN("student@tdl.org");
		
		final String LIST_URL = Router.reverse("Student.submissionList").url;
		
		Response response = GET(LIST_URL);
				
		assertIsOk(response);
		assertContentMatch("<title>Submission Status</title>",response);
		
	}

	/**
	 * Test that when submissons are open, and multiple submissions are
	 * disallowed, the view page is shown for that one submission.
	 */
	@Test
	public void testOpenAndNoMultipleWithOneSubmission() {

		configure(true,false);
		
		Submission sub = subRepo.createSubmission(submitter);
		sub.setState(sub.getState().getTransitions(sub).get(0));
		sub.save();
		subs.add(sub);
		
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		
		LOGIN("student@tdl.org");
		
		Map<String,Object> routeArgs = new HashMap<String,Object>();
		routeArgs.put("subId",sub.getId());
		final String LIST_URL = Router.reverse("Student.submissionList").url;
		final String VIEW_URL = Router.reverse("Student.submissionView",routeArgs).url;

		
		Response response = GET(LIST_URL);
		assertEquals(VIEW_URL,response.getHeader("Location"));
		response = GET(VIEW_URL);
		assertContentMatch("<title>View Application</title>",response);	
	}
	
	/**
	 * Test that when submissons are open, and multiple submissions are
	 * disallowed, and the student has an archived submission that they
	 * are able to submit a new submission.
	 */
	@Test
	public void testOpenAndNoMultipleWithOneArchivedSubmission() {

		configure(true,false);
		
		Submission sub = subRepo.createSubmission(submitter);
		for (State state : stateManager.getAllStates()) {
			if (state.isArchived()) {
				sub.setState(state);
				break;
			}
		}
		sub.save();
		
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		
		LOGIN("student@tdl.org");
		
		Map<String,Object> routeArgs = new HashMap<String,Object>();
		routeArgs.put("subId",sub.getId());
		final String LIST_URL = Router.reverse("Student.submissionList").url;
		final String VIEW_URL = Router.reverse("Student.submissionView",routeArgs).url;
		final String NEW_URL = Router.reverse("submit.PersonalInfo.personalInfo").url;

		
		Response response = GET(LIST_URL);
		assertIsOk(response);
		assertContentMatch("<title>Submission Status</title>",response);	
		assertContentMatch(VIEW_URL,response);
		assertContentMatch(NEW_URL,response);

	
		response = GET(NEW_URL);
		assertContentMatch("<title>Verify Personal Information</title>",response);
		
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		
		List<Submission> found = subRepo.findSubmission(submitter);
		assertEquals(2,found.size());
		subs.addAll(found);
		
	}
	
	/**
	 * Test that if a student has an archived submission they can see the list page to start another submission.
	 */
	@Test
	public void testOpenAndMultipleArchivedSubmission() {
		
		configure(true,false);
		
		Submission sub = subRepo.createSubmission(submitter);
		sub.setState(sub.getState().getTransitions(sub).get(0));
		for (State state : stateManager.getAllStates()) {
			if (state.isArchived()) {
				sub.setState(state);
				break;
			}
		}
		sub.save();
		subs.add(sub);
		
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		
		LOGIN("student@tdl.org");
		
		final String LIST_URL = Router.reverse("Student.submissionList").url;
		
		Response response = GET(LIST_URL);
		assertIsOk(response);
		assertContentMatch("<title>Submission Status</title>",response);
		assertContentMatch(">Start a new submission</a>",response);
		
	}

	/**
	 * Test that when submissions are closed, and multiple submissions are
	 * allowed, the list page is shown.
	 */
	@Test
	public void testClosedAndMultipleWithOneSubmission() {

		configure(false,true);
		
		// Create a submission
		Submission sub = subRepo.createSubmission(submitter);
		sub.setState(sub.getState().getTransitions(sub).get(0));
		sub.save();
		subs.add(sub);
		
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		
		LOGIN("student@tdl.org");
		
		final String LIST_URL = Router.reverse("Student.submissionList").url;
		
		Response response = GET(LIST_URL);
		assertIsOk(response);
		assertContentMatch("<title>Submission Status</title>",response);
	}

	/**
	 * Test that when submissions are closed, and multiple submissions are
	 * disallowed, and the user has one submission that the view page is shown.
	 */
	@Test
	public void testClosedAndNoMultipleWithOneSubmission() {
		
		configure(false,false);
		
		Submission sub = subRepo.createSubmission(submitter);
		sub.setState(sub.getState().getTransitions(sub).get(0));
		sub.save();
		subs.add(sub);
		
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		
		LOGIN("student@tdl.org");
		
		Map<String,Object> routeArgs = new HashMap<String,Object>();
		routeArgs.put("subId",sub.getId());
		final String LIST_URL = Router.reverse("Student.submissionList").url;
		final String VIEW_URL = Router.reverse("Student.submissionView",routeArgs).url;

		
		Response response = GET(LIST_URL);
		assertEquals(VIEW_URL,response.getHeader("Location"));
		response = GET(VIEW_URL);
		assertContentMatch("<title>View Application</title>",response);
	}

	/**
	 * Test deleting submission with other submissions in list.
	 */
	@Test
	public void testDeletingSubmissionWithOtherSubmissions() {
		
		configure(true,true);
		
		Submission sub1 = subRepo.createSubmission(submitter).save();
		Submission sub2 = subRepo.createSubmission(submitter).save();
		subs.add(sub2);
		
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		
		LOGIN("student@tdl.org");
		
		Map<String,Object> routeArgs = new HashMap<String,Object>();
		routeArgs.put("subId",sub1.getId());
		final String LIST_URL = Router.reverse("Student.submissionList").url;
		final String DELETE_URL = Router.reverse("Student.submissionDelete",routeArgs).url;

		Response response = GET(LIST_URL);
		assertIsOk(response);
		assertContentMatch("<title>Submission Status</title>",response);
		assertContentMatch(DELETE_URL,response);
		
		response = GET(DELETE_URL);
		assertEquals(LIST_URL,response.getHeader("Location"));
		
		// confirm the submission was deleted
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		
		assertNull(subRepo.findSubmission(sub1.getId()));
		
	}

	/**
	 * Test deleting submission with no other submissions.
	 */
	@Test
	public void testDeletingSubmissionWithNoOtherSubmissions() {

		configure(true,true);
		
		Submission sub = subRepo.createSubmission(submitter).save();
		
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		
		LOGIN("student@tdl.org");
		
		Map<String,Object> routeArgs = new HashMap<String,Object>();
		routeArgs.put("subId",sub.getId());
		final String INDEX_URL = Router.reverse("Application.index").url;
		final String LIST_URL = Router.reverse("Student.submissionList").url;
		final String DELETE_URL = Router.reverse("Student.submissionDelete",routeArgs).url;

		Response response = GET(LIST_URL);
		assertIsOk(response);
		assertContentMatch("<title>Submission Status</title>",response);
		assertContentMatch(DELETE_URL,response);
		
		response = GET(DELETE_URL);
		assertEquals(INDEX_URL,response.getHeader("Location"));
		
		// confirm the submission was deleted
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		
		assertNull(subRepo.findSubmission(sub.getId()));
	}

	/**
	 * Test viewing a non-editable submission
	 */
	@Test
	public void testViewingSubmission() {
		
		configure(true,true);
		
		Submission sub = subRepo.createSubmission(submitter).save();
		subs.add(sub);
		
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		
		LOGIN("student@tdl.org");
		
		Map<String,Object> routeArgs = new HashMap<String,Object>();
		routeArgs.put("subId",sub.getId());
		final String VIEW_URL = Router.reverse("Student.submissionView",routeArgs).url;

		Response response = GET(VIEW_URL);
		assertIsOk(response);
		assertContentMatch("<title>View Application</title>",response);
		assertFalse(getContent(response).contains("Upload additional supplementary files"));
	}

	/**
	 * Test leaving an comment
	 */
	@Test
	public void testLeavingAComment() {

		configure(true,true);
		
		Submission sub = subRepo.createSubmission(submitter);
		sub.setState(sub.getState().getTransitions(sub).get(0));
		sub.save();
		subs.add(sub);
		
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		
		LOGIN("student@tdl.org");
		
		Map<String,Object> routeArgs = new HashMap<String,Object>();
		routeArgs.put("subId",sub.getId());
		final String VIEW_URL = Router.reverse("Student.submissionView",routeArgs).url;

		Response response = GET(VIEW_URL);
		assertIsOk(response);
		assertContentMatch("<title>View Application</title>",response);
		assertFalse(getContent(response).contains("Upload additional supplementary files"));
		
		// Post a message
		Map<String,String> params = new HashMap<String,String>();
		params.put("studentMessage","This is an action log");
		params.put("submit_addMessage","Add Message");
		response = POST(VIEW_URL,params);
		
		assertIsOk(response);
		assertContentMatch("<title>View Application</title>",response);
		assertContentMatch("This is an action log",response);
		
		// Verify the submission
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		
		sub = subRepo.findSubmission(sub.getId());
		List<ActionLog> logs = subRepo.findActionLog(sub);
		assertEquals(3,logs.size());
	}

	/**
	 * Test replacing the primary document
	 */
	@Test
	public void testManagingPrimaryDocument() throws IOException {

		Submission sub = subRepo.createSubmission(submitter);
		for(State state : stateManager.getAllStates()) {
			if (state.isEditableByStudent())
				sub.setState(state);
		}
		sub.save();
		subs.add(sub);
		
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		
		LOGIN("student@tdl.org");
		
		Map<String,Object> routeArgs = new HashMap<String,Object>();
		routeArgs.put("subId",sub.getId());
		final String VIEW_URL = Router.reverse("Student.submissionView",routeArgs).url;

		Response response = GET(VIEW_URL);
		assertIsOk(response);
		assertContentMatch("<title>View Application</title>",response);
		assertTrue(getContent(response).contains("Upload additional files"));
		
		File testPDF = getResourceFile("SamplePrimaryDocument.pdf");
		
		// Post a manuscript
		Map<String,File> fileParams = new HashMap<String,File>();
		Map<String,String> params = new HashMap<String,String>();
		fileParams.put("primaryDocument",testPDF);
		params.put("uploadPrimary","Upload");
		response = POST(VIEW_URL,params,fileParams);
		
		assertIsOk(response);
		assertContentMatch("<title>View Application</title>",response);
		assertContentMatch("PRIMARY-DOCUMENT.pdf",response);
		
		// Delete a manuscript
		params.clear();
		params.put("replacePrimary","Replace Manuscript");
		response = POST(VIEW_URL,params);
		
		assertIsOk(response);
		assertContentMatch("<title>View Application</title>",response);
		assertFalse(getContent(response).contains("PRIMARY-DOCUMENT.pdf</a>"));
		
		sub = subRepo.findSubmission(sub.getId());
		assertEquals("PRIMARY-DOCUMENT-archived-on-"+JpaAttachmentImpl.dateFormat.format(new Date())+".pdf",sub.getAttachmentsByType(AttachmentType.ARCHIVED).get(0).getName());
	}

	/**
	 * Test completing corrections.
	 */
	@Test
	public void testCompletingCorrections() throws IOException {

		Submission sub = subRepo.createSubmission(submitter);
		for(State state : stateManager.getAllStates()) {
			if (state.isEditableByStudent())
				sub.setState(state);
		}
		sub.save();
		subs.add(sub);
		State needsCorrection = sub.getState();
		
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		
		LOGIN("student@tdl.org");
		
		Map<String,Object> routeArgs = new HashMap<String,Object>();
		routeArgs.put("subId",sub.getId());
		final String VIEW_URL = Router.reverse("Student.submissionView",routeArgs).url;

		Response response = GET(VIEW_URL);
		assertIsOk(response);
		assertContentMatch("<title>View Application</title>",response);
		assertContentMatch("class=\"btn btn-primary disabled\" name=\"submit_corrections\" value=\"Complete Corrections\"",response);
		
		Map<String,String> params = new HashMap<String,String>();
		params.put("submit_corrections","Confirm Corrections");
		response = POST(VIEW_URL,params);
		
		assertIsOk(response);
		assertContentMatch("<title>View Application</title>",response);
		assertContentMatch("class=\"btn btn-primary disabled\" name=\"submit_corrections\" value=\"Complete Corrections\"",response);

		File primaryFile = getResourceFile("SamplePrimaryDocument.pdf");
		
		Map<String,File> fileParams = new HashMap<String,File>();
		fileParams.put("primaryDocument", primaryFile);
		
		response = POST(VIEW_URL,params,fileParams);
		assertNotNull(response.getHeader("Location"));

		response = GET(response.getHeader("Location"));
		assertContentMatch("Corrections Submitted",response);
		
		
		// Verify the submission.
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		
		sub = subRepo.findSubmission(sub.getId());
		assertFalse(sub.getState() == needsCorrection);
		assertNotNull(sub.getPrimaryDocument());
	}
	 

	/**
	 * Internal helper method to setup configuration for a test.
	 * 
	 * @param submissionsOpen
	 *            Should submissions be open or closed.
	 * @param allowMultiple
	 *            Should multiple submissions be allowed.
	 */
	private void configure(boolean submissionsOpen, boolean allowMultiple) {
		
		Configuration submissionsOpenConfig = settingRepo.findConfigurationByName(SUBMISSIONS_OPEN);
		if (!submissionsOpen && submissionsOpenConfig != null) {
			submissionsOpenConfig.delete();
		}
		if (submissionsOpen && submissionsOpenConfig == null) {
			settingRepo.createConfiguration(SUBMISSIONS_OPEN,"true").save();
		}
		
		Configuration allowMultipleConfig = settingRepo.findConfigurationByName(ALLOW_MULTIPLE_SUBMISSIONS);
		if (!allowMultiple && allowMultipleConfig != null) {
			allowMultipleConfig.delete();
		}
		if (allowMultiple && allowMultipleConfig == null) {
			settingRepo.createConfiguration(ALLOW_MULTIPLE_SUBMISSIONS,"true").save();
		}
		
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
	}
	
	/**
	 * Internal Helper Method
	 * 
	 * Extract the file from the jar and place it in a temporary location for
	 * the test to operate from. The caller needs to remember to delete the file
	 * after it's use.
	 * 
	 * @param filePath
	 *            The path, relative to the classpath, of the file to reference.
	 * @return A Java File object reference.
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
