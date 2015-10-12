package org.tdl.vireo.model;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.tdl.vireo.Application;
import org.tdl.vireo.annotations.Order;
import org.tdl.vireo.enums.Role;
import org.tdl.vireo.model.repo.CustomActionDefinitionRepo;
import org.tdl.vireo.model.repo.CustomActionValueRepo;
import org.tdl.vireo.model.repo.SubmissionRepo;
import org.tdl.vireo.model.repo.SubmissionStateRepo;
import org.tdl.vireo.model.repo.UserRepo;
import org.tdl.vireo.runner.OrderedRunner;

@RunWith(OrderedRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class CustomActionValueTest {
	
	private static final String TEST_USER_EMAIL = "admin@tdl.org";
    private static final String TEST_USER_FIRSTNAME = "TDL";
    private static final String TEST_USER_LASTNAME = "Admin";
    private static final Role TEST_USER_ROLE = Role.ADMINISTRATOR;
    
    private static final String TEST_SUBMISSION_STATE_NAME                  = "Test Submission State";
    private static final boolean TEST_SUBMISSION_STATE_ARCHIVED             = true;
    private static final boolean TEST_SUBMISSION_STATE_PUBLISHABLE          = true;
    private static final boolean TEST_SUBMISSION_STATE_DELETABLE            = true;
    private static final boolean TEST_SUBMISSION_STATE_EDITABLE_BY_REVIEWER = true;
    private static final boolean TEST_SUBMISSION_STATE_EDITABLE_BY_STUDENT  = true;
    private static final boolean TEST_SUBMISSION_STATE_ACTIVE               = true;
    
    private static String TEST_CUSTOM_ACTION_DEFINITION_LABEL = "Test Custom Action Definition Label";
	private static Boolean TEST_CUSTOM_ACTION_DEFINITION_VISIBLE_BY_STUDENT = true; 
	
	private static Submission testSubmission;
	private static User testUser;
	private static SubmissionState testSubmissionState;
	
	private static CustomActionDefinition testCustomActionDefinition;
	private static Boolean TEST_CUSTOM_ACTION_VALUE = true;
	
	@Autowired
	private CustomActionValueRepo customActionValueRepo;
	
	@Autowired
	private SubmissionRepo submissionRepo;
	
	@Autowired
    private	UserRepo userRepo;
	
	@Autowired
    private SubmissionStateRepo submissionStateRepo;
	
	@Autowired
	private CustomActionDefinitionRepo customActionDefinitionRepo;
	

	@Before
    public void setUp() {
		assertEquals("The CustomActionValue repository is not empty!", 0, customActionValueRepo.count());
		
		testUser = userRepo.create(TEST_USER_EMAIL, TEST_USER_FIRSTNAME, TEST_USER_LASTNAME, TEST_USER_ROLE);
       	assertEquals("The user repository is not empty!", 1, userRepo.count());
       	
       	testSubmissionState = submissionStateRepo.create(TEST_SUBMISSION_STATE_NAME, TEST_SUBMISSION_STATE_ARCHIVED, TEST_SUBMISSION_STATE_PUBLISHABLE, TEST_SUBMISSION_STATE_DELETABLE, TEST_SUBMISSION_STATE_EDITABLE_BY_REVIEWER, TEST_SUBMISSION_STATE_EDITABLE_BY_STUDENT, TEST_SUBMISSION_STATE_ACTIVE);
       	assertEquals("The submissionState repository is not empty!", 1, submissionStateRepo.count());
       	
       	testSubmission  = submissionRepo.create(testUser, testSubmissionState);
      	assertEquals("The submission repository is not empty!", 1, submissionRepo.count());
      	
      	testCustomActionDefinition = customActionDefinitionRepo.create(TEST_CUSTOM_ACTION_DEFINITION_LABEL, TEST_CUSTOM_ACTION_DEFINITION_VISIBLE_BY_STUDENT);
      	assertEquals("The customActionDefinition repository is not empty!", 1, customActionDefinitionRepo.count());
	}
	
	@Test
    @Order(value = 1)
    @Transactional
    public void testCreate() { 
		CustomActionValue testCustomActionValue = customActionValueRepo.create(testSubmission, testCustomActionDefinition, TEST_CUSTOM_ACTION_VALUE);
		assertEquals("The custom action value was not created in the repository",1,customActionValueRepo.count());
		assertEquals("Saved custom Action Value does not contain the correct submitter first name", TEST_USER_FIRSTNAME, testCustomActionValue.getSubmission().getSubmitter().getFirstName());
		//TODO - similar tests for custom action definition
	}
	
	@Test
	@Order(value = 2)
	public void testDuplication() {
		customActionValueRepo.create(testSubmission, testCustomActionDefinition, TEST_CUSTOM_ACTION_VALUE);
		customActionValueRepo.create(testSubmission, testCustomActionDefinition, TEST_CUSTOM_ACTION_VALUE);
		assertEquals("The action log entry was not duplicated", 2, customActionValueRepo.count());
	}
	
	@Test
    @Order(value = 3)
    public void testFind() {
		CustomActionValue testCustomActionValue1 = customActionValueRepo.create(testSubmission, testCustomActionDefinition, TEST_CUSTOM_ACTION_VALUE);
		CustomActionValue testCustomActionValue2 = customActionValueRepo.findBySubmissionAndDefinition(testSubmission, testCustomActionDefinition);
		assertEquals("Found custom Action value for the correct submission",true, testCustomActionValue1.equals(testCustomActionValue2));
	}
	
	@Test
    @Order(value = 4)
    public void testDelete() {
		CustomActionValue testCustomActionValue = customActionValueRepo.create(testSubmission, testCustomActionDefinition, TEST_CUSTOM_ACTION_VALUE);
		customActionValueRepo.delete(testCustomActionValue);
		 assertEquals("CustomActionValue was not deleted!", 0, customActionValueRepo.count());
	}
	
	@Test
    @Order(value = 5)
    @Transactional
    public void testCascade() {
		CustomActionValue testCustomActionValue = customActionValueRepo.create(testSubmission, testCustomActionDefinition, TEST_CUSTOM_ACTION_VALUE);
		customActionValueRepo.delete(testCustomActionValue);
		assertEquals("Submission was deleted", 1, submissionRepo.count());
		assertEquals("CustomActionDefinition was deleted", 1, customActionDefinitionRepo.count());
	}
	
	@After
    public void cleanUp() {
		customActionValueRepo.deleteAll();
		submissionRepo.deleteAll();
		userRepo.deleteAll();
		submissionStateRepo.deleteAll();
		customActionDefinitionRepo.deleteAll();
	}

}
