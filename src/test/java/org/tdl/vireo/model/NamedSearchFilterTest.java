package org.tdl.vireo.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.UUID;

import javax.validation.constraints.AssertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;
import org.tdl.vireo.Application;
import org.tdl.vireo.annotations.Order;
import org.tdl.vireo.enums.Role;
import org.tdl.vireo.model.repo.ActionLogRepo;
import org.tdl.vireo.model.repo.AttachmentRepo;
import org.tdl.vireo.model.repo.CustomActionDefinitionRepo;
import org.tdl.vireo.model.repo.CustomActionValueRepo;
import org.tdl.vireo.model.repo.EmbargoTypeRepo;
import org.tdl.vireo.model.repo.NamedSearchFilterRepo;
import org.tdl.vireo.model.repo.OrganizationCategoryRepo;
import org.tdl.vireo.model.repo.OrganizationRepo;
import org.tdl.vireo.model.repo.SubmissionRepo;
import org.tdl.vireo.model.repo.SubmissionStateRepo;
import org.tdl.vireo.model.repo.UserRepo;
import org.tdl.vireo.runner.OrderedRunner;

@RunWith(OrderedRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class NamedSearchFilterTest {
	
	private static final String TEST_USER_EMAIL = "admin@tdl.org";
    private static final String TEST_USER_FIRSTNAME = "TDL";
    private static final String TEST_USER_LASTNAME = "Admin";
    private static final Role TEST_USER_ROLE = Role.ADMINISTRATOR;
    
    private static final String TEST_INCLUDED_SUBMITTER_EMAIL = "includedSubmitter@tdl.org";
    private static final String TEST_INCLUDED_SUBMITTER_FIRSTNAME = "Included Submitter First Name";
    private static final String TEST_INCLUDED_SUBMITTER_LASTNAME = "Included Submitter Last Name";
   
    
    
    private static final String TEST_EXCLUDED_SUBMITTER_EMAIL = "excludedSubmitter@tdl.org";
    private static final String TEST_EXCLUDED_SUBMITTER_FIRSTNAME = "Included Submitter First Name";
    private static final String TEST_EXCLUDED_SUBMITTER_LASTNAME = "Included Submitter Last Name";
    private static final Role TEST_SUBMITTER_ROLE = Role.ADMINISTRATOR;  
    
    private static final String TEST_ASSIGNEE_EMAIL = "assignee@tdl.org";
    private static final String TEST_ASSIGNEE_FIRSTNAME = "TDL";
    private static final String TEST_ASSIGNEE_LASTNAME = "Admin";   
    private static final Role TEST_ASSIGNEE_ROLE = Role.USER;
    
    private static final String TEST_EMBARGO_NAME = "Test Embargo Name";
    private static final String TEST_EMBARGO_DESCRIPTION = "Test Embargo Description";
    private static Integer TEST_EMBARGO_DURATION  = new Integer(3);
    
	
    private static final String TEST_NAMED_SEARCH_FILTER_NAME = "Test Filter Name";
    
    private static final String TEST_INCLUDED_SUBMISSION_STATE_NAME = "Test Included Submission State Name";
    private static final String TEST_EXCLUDED_SUBMISSION_STATE_NAME = "Test Excluded Submission State Name";
    private static final String TEST_SUBMISSION_STATE_NAME = "Test Submission State Name";
    
    private static final boolean TEST_SUBMISSION_STATE_ARCHIVED = true;
    private static final boolean TEST_SUBMISSION_STATE_PUBLISHABLE = true;
    private static final boolean TEST_SUBMISSION_STATE_DELETABLE = true;
    private static final boolean TEST_SUBMISSION_STATE_EDITABLE_BY_REVIEWER = true;
    private static final boolean TEST_SUBMISSION_STATE_EDITABLE_BY_STUDENT = true;
    private static final boolean TEST_SUBMISSION_STATE_ACTIVE = true;
    
    private static final String TEST_ATTACHMENT_NAME = "Test Attachment Name";
    private static Calendar TEST_ACTION_LOG_ACTION_DATE = Calendar.getInstance();
    private static UUID TEST_UUID = UUID.randomUUID();
    
    private static final String TEST_ACTION_LOG_ENTRY = "Test ActionLog Entry";
    private static final boolean TEST_ACTION_LOG_FLAG                       = true;  
    
    private static String TEST_CUSTOM_ACTION_DEFINITION_LABEL = "Test Custom Action Definition Label";
	private static Boolean TEST_CUSTOM_ACTION_DEFINITION_VISIBLE_BY_STUDENT = true; 
	
	private static Boolean TEST_CUSTOM_ACTION_VALUE = true;
	
	private static final int TEST_ORGANIZATION_CATEGORY_LEVEL      = 0;
	private static final String TEST_ORGANIZATION_CATEGORY_NAME      = "Test Organization Category";
	
	private static final String TEST_ORGANIZATION_NAME            = "Test Organization Name";
    
    private static EmbargoType embargoType;
    
    private static Organization organization;
    private static OrganizationCategory organizationCategory;
    
    private static User creator;
	private static User assignee;
	private static User includedSubmitter;
	private static User excludedSubmitter;
	
	private static Submission includedSubmission;
	private static Submission excludedSubmission;
	
	private static SubmissionState submissionState;
	private static SubmissionState includedSubmissionState;
	private static SubmissionState excludedSubmissionState;
	
	private static Attachment attachment;
	
	private static ActionLog includedActionLog;
	private static ActionLog excludedActionLog;
	
	private static CustomActionDefinition customActionDefinition;
	private static CustomActionValue customActionValue;
	
	@Autowired
	private NamedSearchFilterRepo namedSearchFilterRepo;
	@Autowired
    private	UserRepo userRepo;
	
	@Autowired
    private EmbargoTypeRepo embargoTypeRepo;
	
	@Autowired
    private SubmissionRepo submissionRepo;
	
	@Autowired
    private SubmissionStateRepo submissionStateRepo;
	
	@Autowired
    private ActionLogRepo actionLogRepo;
	
	@Autowired
    private OrganizationRepo organizationRepo;
	
	@Autowired
    private OrganizationCategoryRepo organizationCategoryRepo;
	
	@Autowired
    private CustomActionDefinitionRepo customActionDefinitionRepo;
	@Autowired
    private CustomActionValueRepo customActionValueRepo;
	
	@Autowired
    private AttachmentRepo attachmentRepo;
	
	
	
	@Before
    public void setUp() {
		assertEquals("SearchFilter Repo is not empty", 0,namedSearchFilterRepo.count());
		
		creator = userRepo.create(TEST_USER_EMAIL, TEST_USER_FIRSTNAME, TEST_USER_LASTNAME, TEST_USER_ROLE);
		
		includedSubmitter = userRepo.create(TEST_INCLUDED_SUBMITTER_EMAIL, TEST_INCLUDED_SUBMITTER_FIRSTNAME, TEST_INCLUDED_SUBMITTER_LASTNAME, TEST_SUBMITTER_ROLE);
		includedSubmissionState = submissionStateRepo.create(TEST_INCLUDED_SUBMISSION_STATE_NAME, TEST_SUBMISSION_STATE_ARCHIVED, TEST_SUBMISSION_STATE_PUBLISHABLE, TEST_SUBMISSION_STATE_DELETABLE, TEST_SUBMISSION_STATE_EDITABLE_BY_REVIEWER, TEST_SUBMISSION_STATE_EDITABLE_BY_STUDENT, TEST_SUBMISSION_STATE_ACTIVE);
		includedSubmission = submissionRepo.create(includedSubmitter, includedSubmissionState);
		
		
		excludedSubmitter = userRepo.create(TEST_EXCLUDED_SUBMITTER_EMAIL, TEST_EXCLUDED_SUBMITTER_FIRSTNAME, TEST_EXCLUDED_SUBMITTER_LASTNAME, TEST_SUBMITTER_ROLE);
		excludedSubmissionState = submissionStateRepo.create(TEST_EXCLUDED_SUBMISSION_STATE_NAME, TEST_SUBMISSION_STATE_ARCHIVED, TEST_SUBMISSION_STATE_PUBLISHABLE, TEST_SUBMISSION_STATE_DELETABLE, TEST_SUBMISSION_STATE_EDITABLE_BY_REVIEWER, TEST_SUBMISSION_STATE_EDITABLE_BY_STUDENT, TEST_SUBMISSION_STATE_ACTIVE);
		excludedSubmission = submissionRepo.create(excludedSubmitter, excludedSubmissionState);
		assertEquals("The submission does not exist!", 2, submissionRepo.count());
		
		attachment = attachmentRepo.create(TEST_ATTACHMENT_NAME,TEST_UUID );
		assertEquals("The attachment repository is not empty!", 1, attachmentRepo.count());
		
		includedActionLog = actionLogRepo.create(includedSubmission, includedSubmissionState, includedSubmitter, TEST_ACTION_LOG_ACTION_DATE, attachment,TEST_ACTION_LOG_ENTRY, TEST_ACTION_LOG_FLAG);      	
		excludedActionLog = actionLogRepo.create(excludedSubmission, excludedSubmissionState, excludedSubmitter, TEST_ACTION_LOG_ACTION_DATE, attachment,TEST_ACTION_LOG_ENTRY, TEST_ACTION_LOG_FLAG);
		assertEquals("The actionLog repository is not empty!", 2, actionLogRepo.count());
		
		submissionState = submissionStateRepo.create(TEST_SUBMISSION_STATE_NAME, TEST_SUBMISSION_STATE_ARCHIVED, TEST_SUBMISSION_STATE_PUBLISHABLE, TEST_SUBMISSION_STATE_DELETABLE, TEST_SUBMISSION_STATE_EDITABLE_BY_REVIEWER, TEST_SUBMISSION_STATE_EDITABLE_BY_STUDENT, TEST_SUBMISSION_STATE_ACTIVE);
		
		embargoType = embargoTypeRepo.create(TEST_EMBARGO_NAME, TEST_EMBARGO_DESCRIPTION, TEST_EMBARGO_DURATION);
		assertEquals("The embargoType repository is not empty!", 1, embargoTypeRepo.count());
		
		assignee = userRepo.create(TEST_ASSIGNEE_EMAIL, TEST_ASSIGNEE_FIRSTNAME, TEST_ASSIGNEE_LASTNAME, TEST_ASSIGNEE_ROLE);
		assertEquals("The assignee is not in the user repo",4,userRepo.count());
		
       	customActionDefinition = customActionDefinitionRepo.create(TEST_CUSTOM_ACTION_DEFINITION_LABEL, TEST_CUSTOM_ACTION_DEFINITION_VISIBLE_BY_STUDENT);
       	customActionValue = customActionValueRepo.create(includedSubmission, customActionDefinition, TEST_CUSTOM_ACTION_VALUE);
       	assertEquals("The customActionValue Repo is empty",1,customActionValueRepo.count());
       	organizationCategory = organizationCategoryRepo.create(TEST_ORGANIZATION_CATEGORY_NAME, TEST_ORGANIZATION_CATEGORY_LEVEL);
       	assertEquals("The category does not exist!", 1, organizationCategoryRepo.count());
       	organization = organizationRepo.create(TEST_ORGANIZATION_NAME, organizationCategory);
		assertEquals("The organization Repo is empty",1,organizationRepo.count());
	}
	
	@Test
    @Order(value = 1)
    @Transactional
    public void testCreate() {
		NamedSearchFilter namedSearchFilter = namedSearchFilterRepo.create(creator, TEST_NAMED_SEARCH_FILTER_NAME);
		assertEquals("SearchFilter was not created in the repo", 1,namedSearchFilterRepo.count());
		
		assertEquals("Saved Filter does not have the correct user", true, namedSearchFilter.getCreator().equals(creator));
		assertEquals("Saved Filter does not have the correct name",TEST_NAMED_SEARCH_FILTER_NAME,namedSearchFilter.getName());
		
		namedSearchFilter.addIncludedSubmission(includedSubmission);			
		namedSearchFilter.addExcludedSubmission(excludedSubmission);		
		namedSearchFilter.addIncludedActionLog(includedActionLog);		
		namedSearchFilter.addExcludedActionLog(excludedActionLog);
		namedSearchFilter.addSubmissionState(submissionState);		
		namedSearchFilter.addEmbargoType(embargoType);		
		namedSearchFilter.addAssignee(assignee);		
		namedSearchFilter.addCustomActionValue(customActionValue);		
		namedSearchFilter.addOrganization(organization);
		
		namedSearchFilter = namedSearchFilterRepo.save(namedSearchFilter);
		
		assertEquals("Filter does not have includedSubmission ",true, namedSearchFilter.getIncludedSubmissions().contains(includedSubmission));
		assertEquals("Filter does not have excludedSubmission ",true, namedSearchFilter.getExcludedSubmissions().contains(excludedSubmission));
		assertEquals("Filter does not have includedActionLog ",true, namedSearchFilter.getIncludedActionLogs().contains(includedActionLog));
		assertEquals("Filter does not have submission state",true,namedSearchFilter.getSubmissionStates().contains(submissionState));
		assertEquals("Filter does not have the embargo ",true,namedSearchFilter.getEmbargoTypes().contains(embargoType));
		assertEquals("Filter does not contain the assignee",true,namedSearchFilter.getAssignees().contains(assignee));
		assertEquals("Filter does not have CustomActionValue ",true, namedSearchFilter.getCustomActionValues().contains(customActionValue));
		assertEquals("Filter does not have includedSubmission ",true, namedSearchFilter.getOrganizations().contains(organization));
		
		
	}
	
	@Test
	@Order(value = 2)
	public void testDuplication() {
		namedSearchFilterRepo.create(creator, TEST_NAMED_SEARCH_FILTER_NAME);
		try{
		namedSearchFilterRepo.create(creator, TEST_NAMED_SEARCH_FILTER_NAME);
		} catch(DataIntegrityViolationException e) {
		/* SUCCESS*/
		}
		assertEquals("SearchFilter was not created in the repo", 1,namedSearchFilterRepo.count());
	}
	
	@Test
    @Order(value = 3)
    public void testFind() {
		// TODO
	}
	
	@Test
    @Order(value = 4)
    public void testDelete() { 
		NamedSearchFilter namedSearchFilter = namedSearchFilterRepo.create(creator, TEST_NAMED_SEARCH_FILTER_NAME);
		assertEquals("SearchFilter was not created in the repo", 1,namedSearchFilterRepo.count());
		namedSearchFilterRepo.delete(namedSearchFilter);
		assertEquals("SearchFilter was not deleted in the repo", 0,namedSearchFilterRepo.count());
	}
	
	@Test
    @Order(value = 5)
    @Transactional
    public void testCascade() {
		NamedSearchFilter namedSearchFilter = namedSearchFilterRepo.create(creator, TEST_NAMED_SEARCH_FILTER_NAME);
		namedSearchFilter.addIncludedSubmission(includedSubmission);			
		namedSearchFilter.addExcludedSubmission(excludedSubmission);		
		namedSearchFilter.addIncludedActionLog(includedActionLog);		
		namedSearchFilter.addExcludedActionLog(excludedActionLog);
		namedSearchFilter.addSubmissionState(submissionState);		
		namedSearchFilter.addEmbargoType(embargoType);		
		namedSearchFilter.addAssignee(assignee);		
		namedSearchFilter.addCustomActionValue(customActionValue);		
		namedSearchFilter.addOrganization(organization);
		
		namedSearchFilter = namedSearchFilterRepo.save(namedSearchFilter);
		
		namedSearchFilterRepo.delete(namedSearchFilter);
		
		namedSearchFilter = namedSearchFilterRepo.findOne(namedSearchFilter.getId());
		assertEquals("namedSearchFilter was not deleted",null, namedSearchFilter);
		assertEquals("The submissions were deleted",2,submissionRepo.count());
		assertEquals("The action logs were deleted",2,actionLogRepo.count());
		assertEquals("The submissionState was deleted",3,submissionStateRepo.count());
		assertEquals("The embargoType was deleted",1,embargoTypeRepo.count());
		assertEquals("The assignee was deleted",4,userRepo.count());
		assertEquals("The organization was deleted",1,organizationRepo.count());
	}
	


	@After
    public void cleanUp() {
		namedSearchFilterRepo.deleteAll();		
		embargoTypeRepo.deleteAll();		
		organizationRepo.deleteAll();
		organizationCategoryRepo.deleteAll();
		actionLogRepo.deleteAll();		
		attachmentRepo.deleteAll();		
		customActionValueRepo.deleteAll();
		
		submissionRepo.deleteAll();submissionStateRepo.deleteAll();
		userRepo.deleteAll();
		customActionDefinitionRepo.deleteAll();
	}
	
}
