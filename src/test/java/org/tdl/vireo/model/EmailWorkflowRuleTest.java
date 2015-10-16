package org.tdl.vireo.model;

import static org.junit.Assert.*;

import java.util.Set;
import java.util.TreeSet;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.tdl.vireo.Application;
import org.tdl.vireo.enums.RecipientType;
import org.tdl.vireo.model.repo.EmailTemplateRepo;
import org.tdl.vireo.model.repo.EmailWorkflowRuleRepo;
import org.tdl.vireo.model.repo.OrganizationCategoryRepo;
import org.tdl.vireo.model.repo.OrganizationRepo;
import org.tdl.vireo.model.repo.SubmissionStateRepo;
import org.tdl.vireo.runner.OrderedRunner;

@RunWith(OrderedRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class EmailWorkflowRuleTest {
	
	//TODO:  these occur in the ActionLogTest also - move them to a common place.
    private static final String TEST_SUBMISSION_STATE_NAME                  = "Test Submission State";
    private static final boolean TEST_SUBMISSION_STATE_ARCHIVED             = true;
    private static final boolean TEST_SUBMISSION_STATE_PUBLISHABLE          = true;
    private static final boolean TEST_SUBMISSION_STATE_DELETABLE            = true;
    private static final boolean TEST_SUBMISSION_STATE_EDITABLE_BY_REVIEWER = true;
    private static final boolean TEST_SUBMISSION_STATE_EDITABLE_BY_STUDENT  = true;
    private static final boolean TEST_SUBMISSION_STATE_ACTIVE               = true;

	@Autowired
	private EmailWorkflowRuleRepo emailWorkflowRuleRepo;
	
	@Autowired
    private SubmissionStateRepo submissionStateRepo;
	
	@Autowired
	private OrganizationRepo organizationRepo;
	
	@Autowired
	private OrganizationCategoryRepo organizationCategoryRepo;
	
	@Autowired
	private EmailTemplateRepo emailTemplateRepo;
	
	private static SubmissionState submissionState;
	private static Organization hogwarts;
	private static Organization hufflepuff;
	private static OrganizationCategory school;
	private static OrganizationCategory house;
	private static Set<Organization> organizations = new TreeSet<Organization>();
	private static EmailTemplate marvelousSubmissionMessage;
	private static EmailTemplate preposterousSubmissionMessage;
	
	
	
	
	@Before
	public void setUp() {
		//make sure repositories are clean
		assertTrue("The repository was not empty!", emailWorkflowRuleRepo.count() == 0);
		
		
		//set up some entities
		submissionState = submissionStateRepo.create(TEST_SUBMISSION_STATE_NAME, TEST_SUBMISSION_STATE_ARCHIVED, TEST_SUBMISSION_STATE_PUBLISHABLE, TEST_SUBMISSION_STATE_DELETABLE, TEST_SUBMISSION_STATE_EDITABLE_BY_REVIEWER, TEST_SUBMISSION_STATE_EDITABLE_BY_STUDENT, TEST_SUBMISSION_STATE_ACTIVE);
		school = organizationCategoryRepo.create("school", 0);
		house = organizationCategoryRepo.create("house", 1);
		hogwarts = organizationRepo.create("Hogwart's School of Witchcraft and Wizardry", school);
		hufflepuff = organizationRepo.create("Hufflepuff", house);
		hogwarts.addChildOrganization(hufflepuff);
		organizations.add(hogwarts);
		organizations.add(hufflepuff);
		
		marvelousSubmissionMessage = emailTemplateRepo.create("Important Notification", "A Marvelous Submission", "Be it known to ye that this submission is marvelous.");
		preposterousSubmissionMessage = emailTemplateRepo.create("Odd Information", "Preposterous Claims Herein", "Be it known to ye that preposterous claims be made in yon submission.");
		
	}
	
	@Test
	public void testCreate() {
		EmailWorkflowRule notifyEverybodyOfImportantDoings = emailWorkflowRuleRepo.create(submissionState, organizations, RecipientType.DEPARTMENT, marvelousSubmissionMessage);
		EmailWorkflowRule confuseEverybodyWithOddInformation = emailWorkflowRuleRepo.create(submissionState, organizations, RecipientType.DEPARTMENT, preposterousSubmissionMessage);
		
		//make sure they have been created and persisted
		assertEquals("We had different organization sets for the two rules but intended the same!", notifyEverybodyOfImportantDoings.getOrganizations(), confuseEverybodyWithOddInformation.getOrganizations());
		assertTrue("We didn't have the right submissionState on our rule!", notifyEverybodyOfImportantDoings.getSubmissionState().equals(submissionState));
		assertTrue("We didn't have the right submissionState on our rule!", confuseEverybodyWithOddInformation.getSubmissionState().equals(submissionState));
		assertTrue("We didn't have the right recipient type on our rule!", notifyEverybodyOfImportantDoings.getRecipientType().equals(RecipientType.DEPARTMENT));
		assertTrue("We didn't have the right recipient type on our rule!", confuseEverybodyWithOddInformation.getRecipientType().equals(RecipientType.DEPARTMENT));
		assertTrue("We didn't have the right template on our rule!", notifyEverybodyOfImportantDoings.getEmailTemplate().equals(marvelousSubmissionMessage));
		assertTrue("We didn't have the right template on our rule!", confuseEverybodyWithOddInformation.getEmailTemplate().equals(preposterousSubmissionMessage));
		assertTrue("We didn't have enough email workflow rules in the repo!", emailWorkflowRuleRepo.count() == 2);
	}
	
	@Test
	public void testDuplication() {
		EmailWorkflowRule sillyNotification = emailWorkflowRuleRepo.create(submissionState, organizations, RecipientType.DEPARTMENT, marvelousSubmissionMessage);
		EmailWorkflowRule sillierNotification = emailWorkflowRuleRepo.create(submissionState, organizations, RecipientType.DEPARTMENT, marvelousSubmissionMessage);
		assertEquals("Duplicated!", sillyNotification, sillierNotification);
		assertEquals("Duplicated!", sillyNotification.getId(), sillierNotification.getId());
		assertTrue("Duplicated!", emailWorkflowRuleRepo.count() == 1);
	}
	
	@Test
	public void testFind() {
		emailWorkflowRuleRepo.create(submissionState, organizations, RecipientType.DEPARTMENT, marvelousSubmissionMessage);
		
		EmailWorkflowRule foundRule = emailWorkflowRuleRepo.findBySubmissionStateAndOrganizationsAndRecipientTypeAndEmailTemplate(
				submissionState, organizations, RecipientType.DEPARTMENT, marvelousSubmissionMessage);
		
		assertTrue("Didn't find what we thought we had created!", foundRule.getSubmissionState().equals(submissionState));
		assertTrue("Didn't find what we thought we had created!", foundRule.getOrganizations().equals(organizations));
		assertTrue("Didn't find what we thought we had created!", foundRule.getRecipientType().equals(RecipientType.DEPARTMENT));
		assertTrue("Didn't find what we thought we had created!", foundRule.getEmailTemplate().equals(marvelousSubmissionMessage));		
	}
	
	@Test
	public void testDelete() {
		EmailWorkflowRule ruleToDelete = emailWorkflowRuleRepo.create(submissionState, organizations, RecipientType.DEPARTMENT, marvelousSubmissionMessage);
		
		assertEquals("Didn't create the rule!", 1, emailWorkflowRuleRepo.count() );
		
		emailWorkflowRuleRepo.delete(ruleToDelete);
		
		assertEquals("Didn't delete the rule!", 0, emailWorkflowRuleRepo.count() );
		
		
	}
	
	@Test
	public void testCascade() {
		
	}
	
	
	@After
	public void cleanup() {
		emailWorkflowRuleRepo.deleteAll();
		submissionStateRepo.deleteAll();
		organizationRepo.deleteAll();
		organizationCategoryRepo.deleteAll();
		emailTemplateRepo.deleteAll();		
	}

}
