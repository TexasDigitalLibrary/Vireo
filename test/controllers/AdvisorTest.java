package controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tdl.vireo.model.ActionLog;
import org.tdl.vireo.model.Person;
import org.tdl.vireo.model.PersonRepository;
import org.tdl.vireo.model.RoleType;
import org.tdl.vireo.model.SettingsRepository;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.SubmissionRepository;
import org.tdl.vireo.security.SecurityContext;
import org.tdl.vireo.state.StateManager;

import play.db.jpa.JPA;
import play.modules.spring.Spring;
import play.mvc.Http.Response;
import play.mvc.Router;

/**
 * Test all the functions of the advisor Controller.
 * 
 * @author <a href="http://www.scottphillips.com/">Scott Phillips</a>
 */
public class AdvisorTest extends AbstractVireoFunctionalTest {

	// Spring dependencies
	public static SecurityContext context = Spring.getBeanOfType(SecurityContext.class);
	public static PersonRepository personRepo = Spring.getBeanOfType(PersonRepository.class);
	public static SubmissionRepository subRepo = Spring.getBeanOfType(SubmissionRepository.class);
	public static SettingsRepository settingRepo = Spring.getBeanOfType(SettingsRepository.class);
	public static StateManager stateManager = Spring.getBeanOfType(StateManager.class);


	// Dynamic test data
	public Person student;
	public Person advisor;
	public Submission sub;
	public String token;
	
	/**
	 * Setup
	 * 
	 * create a submission to test with.
	 */
	@Before
	public void setup() {
		// Turn off authentication for the test thread
		context.turnOffAuthorization();
		
		// Create people
		student = personRepo.createPerson("student", "student@tdl.org", "first", "last", RoleType.STUDENT).save();
		student.setPassword("password");
		student.save();
		advisor = personRepo.createPerson("advisor", "advisor@tdl.org", "first", "last", RoleType.NONE).save();
		advisor.setPassword("password");
		advisor.addAffiliation("faculty");
		advisor.addAffiliation("affiliate");
		advisor.save();
		
		// Create a submission
		sub = subRepo.createSubmission(student).save();
		sub.setState(sub.getState().getTransitions(sub).get(0));
		sub.setCommitteeEmailHash("ABC"+sub.getId());
		sub.save();
		
		// Save the token
		token = sub.getCommitteeEmailHash();
		
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
	}

	/**
	 * Cleanup
	 * 
	 * Delete the submission and student.
	 */
	@After
	public void cleanup() {

		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		
		if (sub != null)
			subRepo.findSubmission(sub.getId()).delete();
		
		if (student != null)
			personRepo.findPerson(student.getId()).delete();
		
		if (advisor != null)
			personRepo.findPerson(advisor.getId()).delete();
	
		context.restoreAuthorization();
	}


	/**
	 * We assume that Vireo is configured to reject all non-faculty
	 * affiliations.
	 */
	@Test
	public void testNonAdvisorAccess() {
		
		LOGIN("student@tdl.org");
		
		Map<String,Object> routeArgs = new HashMap<String,Object>();
		routeArgs.put("token", token);
		final String ADVISOR_URL = Router.reverse("Advisor.review",routeArgs).url;
		
		Response response = GET(ADVISOR_URL);
		assertIsOk(response);
	}
	
	
	/** 
	 * Test that an advisor can just leave a comment without supplying anything else.
	 */
	@Test
	public void testLeavingComment() {
		
		LOGIN("advisor@tdl.org");
		
		Map<String,Object> routeArgs = new HashMap<String,Object>();
		routeArgs.put("token", token);
		final String ADVISOR_URL = Router.reverse("Advisor.review",routeArgs).url;
		final String ADVISOR_URL_JSON = Router.reverse("Advisor.reviewJSON",routeArgs).url;
		
		Response response = GET(ADVISOR_URL);
		assertIsOk(response);
		assertContentMatch("<title>Review Application</title>",response);
		
		
		Map<String,String> params = new HashMap<String,String>();
		params.put("advisorMessage","Hi this is my super cool message.");
		params.put("submit_advisor","Submit");
		
		response = POST(ADVISOR_URL_JSON,params);
		assertIsOk(response);
		assertContentMatch(" \"success\": true, \"inputReceived\": true",response);
		
		
		// verify the action log.
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		
		sub = subRepo.findSubmission(sub.getId());
		List<ActionLog> logs = subRepo.findActionLog(sub);
		
		boolean foundLog = false;
		for (ActionLog log : logs) 
			if (log.getEntry().contains("Hi this is my super cool message."))
				foundLog = true;
		
		assertTrue(foundLog);
	}
	
	/**
	 * Test that an advisor can reject approval.
	 */
	@Test
	public void testRejecting() {
		
		LOGIN("advisor@tdl.org");
		
		Map<String,Object> routeArgs = new HashMap<String,Object>();
		routeArgs.put("token", token);
		final String ADVISOR_URL = Router.reverse("Advisor.review",routeArgs).url;
		final String ADVISOR_URL_JSON = Router.reverse("Advisor.reviewJSON",routeArgs).url;
		
		Response response = GET(ADVISOR_URL);
		assertIsOk(response);
		assertContentMatch("<title>Review Application</title>",response);
		
		
		Map<String,String> params = new HashMap<String,String>();
		params.put("embargoApproval","reject");
		params.put("committeeApproval","reject");
		params.put("advisorMessage","Hi this is my super cool rejection message.");
		params.put("submit_advisor","Submit");
		
		response = POST(ADVISOR_URL_JSON,params);
		assertIsOk(response);
		assertContentMatch(" \"success\": true, \"inputReceived\": true",response);

		
		// verify the submission.
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		
		sub = subRepo.findSubmission(sub.getId());
		advisor = personRepo.findPerson(advisor.getId());

		
		assertNull(sub.getCommitteeApprovalDate());
		assertNull(sub.getCommitteeEmbargoApprovalDate());
		
		List<ActionLog> logs = subRepo.findActionLog(sub);
		
		boolean foundLog = false;
		for (ActionLog log : logs) {
			if (log.getEntry().contains("Hi this is my super cool rejection message.")) {
				foundLog = true;
				
				// Check that the advisor is recorded.
				assertEquals(advisor,log.getPerson());
			}
		}
		
		assertTrue(foundLog);
		
	}
	
	/**
	 * Test that an advisor can approve of the application & embargo
	 */
	@Test
	public void testAccepting() {
		LOGIN("advisor@tdl.org");
		
		Map<String,Object> routeArgs = new HashMap<String,Object>();
		routeArgs.put("token", token);
		final String ADVISOR_URL = Router.reverse("Advisor.review",routeArgs).url;
		final String ADVISOR_URL_JSON = Router.reverse("Advisor.reviewJSON",routeArgs).url;
		
		Response response = GET(ADVISOR_URL);
		assertIsOk(response);
		assertContentMatch("<title>Review Application</title>",response);
		
		
		Map<String,String> params = new HashMap<String,String>();
		params.put("embargoApproval","approve");
		params.put("committeeApproval","approve");
		params.put("advisorMessage","Hi this is my super cool approval message.");
		params.put("submit_advisor","Submit");
		
		response = POST(ADVISOR_URL_JSON,params);
		assertIsOk(response);
		assertContentMatch(" \"success\": true, \"inputReceived\": true",response);
		
		// verify the submission.
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		
		sub = subRepo.findSubmission(sub.getId());
		advisor = personRepo.findPerson(advisor.getId());
		
		assertNotNull(sub.getCommitteeApprovalDate());
		assertNotNull(sub.getCommitteeEmbargoApprovalDate());
		
		List<ActionLog> logs = subRepo.findActionLog(sub);
		
		boolean foundLog = false;
		for (ActionLog log : logs) {
			if (log.getEntry().contains("Hi this is my super cool approval message.")) {
				foundLog = true;
				
				// Check that the advisor is recorded as the doing the action.
				assertEquals(advisor,log.getPerson());
			}
		}
		
		assertTrue(foundLog);
	}
}
