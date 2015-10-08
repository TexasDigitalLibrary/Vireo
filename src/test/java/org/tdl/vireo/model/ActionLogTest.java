package org.tdl.vireo.model;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;

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
import org.tdl.vireo.model.repo.ActionLogRepo;
import org.tdl.vireo.model.repo.SubmissionRepo;
import org.tdl.vireo.model.repo.SubmissionStateRepo;
import org.tdl.vireo.model.repo.UserRepo;
import org.tdl.vireo.runner.OrderedRunner;

@RunWith(OrderedRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class ActionLogTest {
	
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
    
    private static final String TEST_SUBMISSION_STATE                  = "Test Submitted State";
    
    private static final String TEST_ACTION_LOG_ENTRY = "Test ActionLog Entry";
    private static final boolean TEST_ACTION_LOG_FLAG                       = true;
    
    private Calendar TEST_ACTION_LOG_ACTION_DATE;
	@Autowired
    private ActionLogRepo actionLogRepo;
	
	@Autowired
    private SubmissionRepo submissionRepo;
	
	@Autowired
    private SubmissionStateRepo submissionStateRepo;
	
	@Autowired
    private	UserRepo userRepo;
	
	@Before
    public void setUp() {
        assertEquals("The address repository is not empty!", 0, actionLogRepo.count());        
    }
	
	@Test
    @Order(value = 1)
    @Transactional
    public void testCreate() {
		User testUser = userRepo.create(TEST_USER_EMAIL, TEST_USER_FIRSTNAME, TEST_USER_LASTNAME, TEST_USER_ROLE);
		SubmissionState testSubmissionState = submissionStateRepo.create(TEST_SUBMISSION_STATE_NAME, TEST_SUBMISSION_STATE_ARCHIVED, TEST_SUBMISSION_STATE_PUBLISHABLE, TEST_SUBMISSION_STATE_DELETABLE, TEST_SUBMISSION_STATE_EDITABLE_BY_REVIEWER, TEST_SUBMISSION_STATE_EDITABLE_BY_STUDENT, TEST_SUBMISSION_STATE_ACTIVE);
		Submission testsubmission  = submissionRepo.create(testUser, testSubmissionState);
		ActionLog testActionLog = actionLogRepo.create(testsubmission, testSubmissionState, testUser, TEST_ACTION_LOG_ACTION_DATE, TEST_ACTION_LOG_ENTRY, TEST_ACTION_LOG_FLAG);
		
		assertEquals("The test Action log was not saved",1, actionLogRepo.count());
		assertEquals("Saved action log does not have the correct submitter email",TEST_USER_EMAIL, testActionLog.getUser().getEmail());
		assertEquals("Saved action log does not have the correct submitter first name",TEST_USER_FIRSTNAME, testActionLog.getUser().getFirstName());
		assertEquals("Saved action log does not have the correct submitter last name",TEST_USER_LASTNAME, testActionLog.getUser().getLastName());
		assertEquals("Saved action log does not have the correct submitter role",TEST_USER_ROLE, testActionLog.getUser().getRole());
		
		assertEquals("Saved action log does not have the correct submission state name",TEST_SUBMISSION_STATE_NAME, testActionLog.getSubmissionState().getName());
		assertEquals("Saved action log does not have the correct submission archived state ",TEST_SUBMISSION_STATE_ARCHIVED, testActionLog.getSubmissionState().getArchived());
		assertEquals("Saved action log does not have the correct submission publishable state ",TEST_SUBMISSION_STATE_PUBLISHABLE, testActionLog.getSubmissionState().getPublishable());
		assertEquals("Saved action log does not have the correct submission deletable state ",TEST_SUBMISSION_STATE_DELETABLE, testActionLog.getSubmissionState().getDeletable());
		assertEquals("Saved action log does not have the correct submission editable by reviewer",TEST_SUBMISSION_STATE_EDITABLE_BY_REVIEWER, testActionLog.getSubmissionState().getEditableByReviewer());
		assertEquals("Saved action log does not have the correct submission editable by  student state ",TEST_SUBMISSION_STATE_EDITABLE_BY_STUDENT, testActionLog.getSubmissionState().getEditableByStudent());
		assertEquals("Saved action log does not have the correct submission  active state ",TEST_SUBMISSION_STATE_ACTIVE, testActionLog.getSubmissionState().getActive());
		
		assertEquals("Saved action log does not have the correct log entry ",TEST_ACTION_LOG_ENTRY, testActionLog.getEntry());
		assertEquals("Saved action log does not have the correct flag ",TEST_ACTION_LOG_FLAG, testActionLog.isPrivateFlag());		
	}
	
	@Test
	@Order(value = 2)
	public void testDuplication() {
		User testUser = userRepo.create(TEST_USER_EMAIL, TEST_USER_FIRSTNAME, TEST_USER_LASTNAME, TEST_USER_ROLE);
		SubmissionState testSubmissionState = submissionStateRepo.create(TEST_SUBMISSION_STATE_NAME, TEST_SUBMISSION_STATE_ARCHIVED, TEST_SUBMISSION_STATE_PUBLISHABLE, TEST_SUBMISSION_STATE_DELETABLE, TEST_SUBMISSION_STATE_EDITABLE_BY_REVIEWER, TEST_SUBMISSION_STATE_EDITABLE_BY_STUDENT, TEST_SUBMISSION_STATE_ACTIVE);
		Submission testsubmission  = submissionRepo.create(testUser, testSubmissionState);
		actionLogRepo.create(testsubmission, testSubmissionState, testUser, TEST_ACTION_LOG_ACTION_DATE, TEST_ACTION_LOG_ENTRY, TEST_ACTION_LOG_FLAG);
		actionLogRepo.create(testsubmission, testSubmissionState, testUser, TEST_ACTION_LOG_ACTION_DATE, TEST_ACTION_LOG_ENTRY, TEST_ACTION_LOG_FLAG);
		
		assertEquals("The action log entry was not duplicated", 2, actionLogRepo.count());		
		}
	
	@Test
    @Order(value = 3)
    public void testFind() { 
		User testUser = userRepo.create(TEST_USER_EMAIL, TEST_USER_FIRSTNAME, TEST_USER_LASTNAME, TEST_USER_ROLE);
		ActionLog testActionLog = actionLogRepo.findByUserAndSubmissionState(testUser,TEST_SUBMISSION_STATE);
		assertEquals("Saved action log does not have the correct submitter email",TEST_USER_EMAIL, testActionLog.getUser().getEmail());
		assertEquals("Saved action log does not have the correct submitter first name",TEST_USER_FIRSTNAME, testActionLog.getUser().getFirstName());
		assertEquals("Saved action log does not have the correct submitter last name",TEST_USER_LASTNAME, testActionLog.getUser().getLastName());
		assertEquals("Saved action log does not have the correct submitter role",TEST_USER_ROLE, testActionLog.getUser().getRole());
	}
	
	@Test
    @Order(value = 4)
    public void testDelete() { 
		User testUser = userRepo.create(TEST_USER_EMAIL, TEST_USER_FIRSTNAME, TEST_USER_LASTNAME, TEST_USER_ROLE);
		ActionLog testActionLog = actionLogRepo.findByUserAndSubmissionState(testUser,TEST_SUBMISSION_STATE);
		actionLogRepo.delete(testActionLog);
		assertEquals("The test action log was not deleted", 0, actionLogRepo.count());
	}
	
	@Test
    @Order(value = 5)
    @Transactional
    public void testCascade() {
		
		User testUser = userRepo.create(TEST_USER_EMAIL, TEST_USER_FIRSTNAME, TEST_USER_LASTNAME, TEST_USER_ROLE);
		SubmissionState testSubmissionState = submissionStateRepo.create(TEST_SUBMISSION_STATE_NAME, TEST_SUBMISSION_STATE_ARCHIVED, TEST_SUBMISSION_STATE_PUBLISHABLE, TEST_SUBMISSION_STATE_DELETABLE, TEST_SUBMISSION_STATE_EDITABLE_BY_REVIEWER, TEST_SUBMISSION_STATE_EDITABLE_BY_STUDENT, TEST_SUBMISSION_STATE_ACTIVE);
		Submission testsubmission  = submissionRepo.create(testUser, testSubmissionState);
		ActionLog testActionLog = actionLogRepo.create(testsubmission, testSubmissionState, testUser, TEST_ACTION_LOG_ACTION_DATE, TEST_ACTION_LOG_ENTRY, TEST_ACTION_LOG_FLAG);
		//test detach submission
		actionLogRepo.delete(testActionLog);
		assertEquals("The testActionLog is not deleted from the repo", 0, actionLogRepo.count());
		assertEquals("Submission is not deleted", 1, submissionRepo.count());
		assertEquals("Submission State is not deleted", 1, submissionStateRepo.count());
		assertEquals("User is not deleted", 1, userRepo.count());
	}
	
	@After
    public void cleanUp() {
		actionLogRepo.deleteAll();
		submissionRepo.deleteAll();
		submissionStateRepo.deleteAll();
		userRepo.deleteAll();
	}
	

	}


