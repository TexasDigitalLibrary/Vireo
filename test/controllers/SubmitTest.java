package controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.Ignore;

import play.Play;
import play.Logger;
import play.mvc.Http.Response;
import play.mvc.Router;
import play.test.FunctionalTest;
import play.modules.spring.Spring;
import play.db.jpa.JPA;

import org.tdl.vireo.security.SecurityContext;
import org.tdl.vireo.model.SubmissionRepository;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.PersonRepository;
import org.tdl.vireo.model.Person;
import org.tdl.vireo.model.RoleType;
import org.tdl.vireo.model.CommitteeMember;


/**
 * Test all the actions in the Submit controller.
 * 
 * @author Dan Galewsky</a>
 */

public class SubmitTest extends AbstractVireoFunctionalTest {

	// Spring dependencies
	public static SecurityContext context = Spring.getBeanOfType(SecurityContext.class);
	public static PersonRepository personRepo = Spring.getBeanOfType(PersonRepository.class);
	public static SubmissionRepository subRepo = Spring.getBeanOfType(SubmissionRepository.class);
	
	// Make sure that we can get the VerifyInfo page
	
	 @Test
	 public void testGetVerifyInfoPage() {

		 LOGIN();
		 
		 final String VERIFY_URL = Router
				 .reverse("Submit.verifyPersonalInformation").url;
		 Response response = GET(VERIFY_URL);
		 assertStatus(200, response);
	 }
	 
	 // Test posting VerifyPersonalInformation to the license page
	 
	 @Test
	 @Ignore // Scott didn't finish this test.
	 public void testPostVerifyInfo(){
		 
		 LOGIN();
		 
		 final String DO_VERIFY_URL = Router
				 .reverse("Submit.doVerifyPersonalInformation").url;
		 
		 Map<String,String> verifyArgs = new HashMap<String,String>();
		 
		 verifyArgs.put("middleName","TestStudentFirstName");
		 verifyArgs.put("yearOfBirth","1996");
		 verifyArgs.put("department","science");
		 verifyArgs.put("degree","bs");
		 verifyArgs.put("major","computer science");
		 verifyArgs.put("permPhone","555-1212");
		 verifyArgs.put("permAddress","2222 Fake Street");
		 verifyArgs.put("permEmail","noreply@noreply.org");
		 verifyArgs.put("currentPhone","555-1212");
		 verifyArgs.put("currentAddress","2222 Fake Street");

			
		 Response response = POST(DO_VERIFY_URL, verifyArgs);
		 
		 assertStatus(200, response);
	 }
         
         @Ignore
	 @Test
	 public void testDocumentInfo() {

	 	LOGIN();

	 	context.turnOffAuthorization();

                Person person = personRepo.findPersonByEmail("bthornton@gmail.com");
                
	 	Submission s = subRepo.createSubmission(person);
	 	s.save();

		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();

	 	long subId = s.getId();
	 	
	 	// Auth on
	 	context.restoreAuthorization();

	 	Map<String,Object> docArgs = new HashMap<String,Object>();

	 	docArgs.put("subId", Long.toString(subId));
		docArgs.put("title", "Test Title");
		docArgs.put("committeeFirstName", "First");
		docArgs.put("committeeMiddleInitial", "Middle");
		docArgs.put("committeeLastName", "Last");
		docArgs.put("chairFlag", "checked");
		docArgs.put("chairEmail", "fake@email.com");
		docArgs.put("embargo", "1");
                docArgs.put("submit_next", "");

		// Nulling person here because I was getting an exception when attempting to delete it:
		// A java.lang.RuntimeException has been caught, java.lang.IllegalArgumentException: Removing a detached instance org.tdl.vireo.model.jpa.JpaPersonImpl#7
		person = null;
		s = null;

		// Verify that POST succeeds and we land on the Upload step
		final String DOC_URL = Router.reverse("Submit.docInfo", docArgs).url;
                
		Response response = POST(DOC_URL);
                Logger.info(response.current.get().toString());
		// FIXME: Still getting 302 on this
                assertIsOk(response);
		assertContentMatch("Upload Your Files",response);

		// Check that values are in submission object
		s = subRepo.findSubmission(subId);
		assertNotNull(s);
		assertEquals("Test Title", s.getDocumentTitle());
		assertEquals("fake@email.com", s.getCommitteeContactEmail());

		List<CommitteeMember> committeeMembers = s.getCommitteeMembers();
		assertEquals(1, committeeMembers.size());
		// TODO: Iterate through committee members and verify individual fields
		// TODO: Add tests to verify appropriate embargo state once code is written

		context.turnOffAuthorization();

		// Clean up the submission created on the mock user
		s.delete();

		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		
		context.restoreAuthorization();
		
		JPA.em().getTransaction().commit();
		JPA.em().clear();
	 }
}