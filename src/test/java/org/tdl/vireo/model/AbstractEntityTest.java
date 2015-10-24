package org.tdl.vireo.model;

import java.util.Calendar;
import java.util.UUID;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.tdl.vireo.annotations.Order;
import org.tdl.vireo.config.constant.ConfigurationName;
import org.tdl.vireo.enums.InputType;
import org.tdl.vireo.enums.Role;
import org.tdl.vireo.model.repo.ActionLogRepo;
import org.tdl.vireo.model.repo.AddressRepo;
import org.tdl.vireo.model.repo.AttachmentRepo;
import org.tdl.vireo.model.repo.ConfigurationRepo;
import org.tdl.vireo.model.repo.ContactInfoRepo;
import org.tdl.vireo.model.repo.ControlledVocabularyRepo;
import org.tdl.vireo.model.repo.CustomActionDefinitionRepo;
import org.tdl.vireo.model.repo.CustomActionValueRepo;
import org.tdl.vireo.model.repo.DepositLocationRepo;
import org.tdl.vireo.model.repo.EmailTemplateRepo;
import org.tdl.vireo.model.repo.EmailWorkflowRuleRepo;
import org.tdl.vireo.model.repo.EmbargoTypeRepo;
import org.tdl.vireo.model.repo.FieldGlossRepo;
import org.tdl.vireo.model.repo.FieldPredicateRepo;
import org.tdl.vireo.model.repo.FieldProfileRepo;
import org.tdl.vireo.model.repo.FieldValueRepo;
import org.tdl.vireo.model.repo.LanguageRepo;
import org.tdl.vireo.model.repo.OrganizationCategoryRepo;
import org.tdl.vireo.model.repo.OrganizationRepo;
import org.tdl.vireo.model.repo.SubmissionRepo;
import org.tdl.vireo.model.repo.SubmissionStateRepo;
import org.tdl.vireo.model.repo.UserRepo;
import org.tdl.vireo.model.repo.WorkflowRepo;
import org.tdl.vireo.model.repo.WorkflowStepRepo;

public abstract class AbstractEntityTest {

	protected static final Boolean TEST_SUBMISSION_STATE_ARCHIVED = true;
	protected static final Boolean TEST_SUBMISSION_STATE_PUBLISHABLE = true;
	protected static final Boolean TEST_SUBMISSION_STATE_DELETABLE = true;
	protected static final Boolean TEST_SUBMISSION_STATE_EDITABLE_BY_REVIEWER = true;
	protected static final Boolean TEST_SUBMISSION_STATE_EDITABLE_BY_STUDENT = true;
	protected static final Boolean TEST_SUBMISSION_STATE_ACTIVE = true;
	protected static final Boolean TEST_ACTION_LOG_FLAG = true;
	protected static Boolean TEST_CUSTOM_ACTION_DEFINITION_VISIBLE_BY_STUDENT = true;
	protected static Boolean TEST_CUSTOM_ACTION_VALUE = true;
	protected static final boolean TEST_FIELD_PROFILE_REPEATABLE = true;
	protected static final boolean TEST_FIELD_PROFILE_REQUIRED = true;

	protected static final String TEST_USER_EMAIL = "admin@tdl.org";
	protected static final String TEST_USER_FIRSTNAME = "TDL";
	protected static final String TEST_USER_LASTNAME = "Admin";

	protected static final String TEST_SUBMISSION_STATE_NAME = "Test Submission State";
	protected static final String TEST_ATTACHMENT_NAME = "Test Attachment Name";

	protected static final String TEST_ACTION_LOG_ENTRY = "Test ActionLog Entry";

	// AddressTest
	protected static final String TEST_ADDRESS1 = "101. E. 21st St.";
	protected static final String TEST_ADDRESS2 = "PCL 1.333";
	protected static final String TEST_CITY = "Austin";
	protected static final String TEST_STATE = "Texas";
	protected static final String TEST_POSTAL_CODE = "78759";
	protected static final String TEST_COUNTRY = "USA";

	// ConfigurationTest
	protected static final String TEST_VIREO_CONFIG_INSTALL_DIR_KEY = ConfigurationName.APPLICATION_INSTALL_DIRECTORY;
	protected static final String TEST_VIREO_INSTALL_DIR = "./";
	protected static final String TEST_VIREO_INSTALL_DIR_CHANGED = TEST_VIREO_INSTALL_DIR + "changed/";

	// ControlledVocabulary Test
	protected static final String TEST_LANGUAGE = "English";
	protected static final String TEST_CONTROLLED_VOCABULARY_NAME = "Test Vocabulary";
	protected static final String TEST_CONTROLLED_VOCABULARY_VALUE = "Test Vocabulary Value";
	protected static final String TEST_DETACHABLE_CONTROLLED_VOCABULARY_VALUE = "Test Detachable Vocabulary Value";

	// DepositLocation test
	protected static final String TEST_DEPOSIT_LOCATION_NAME = "Test Deposit Name";

	// CustomActionDefinition Test
	protected static String TEST_CUSTOM_ACTION_DEFINITION_LABEL = "Test Custom Action Definition Label";

	// ContactInfo Test
	protected static final String TEST_PHONE = "512-495-4418";
	protected static final String TEST_EMAIL = "admin@tdl.org";
	// EmailTemplateTest
	protected static final String TEST_EMAIL_TEMPLATE_NAME = "Test Email Template Name";
	protected static final String TEST_EMAIL_TEMPLATE_MESSAGE = "Test Email Template Message";
	protected static final String TEST_EMAIL_TEMPLATE_SUBJECT = "Test Email Template Subject";

	// WorkFlow test
	protected static final String TEST_WORKFLOW_NAME = "Test Workflow";
	protected static final String TEST_FIELD_GLOSS_VALUE = "Test Field Gloss";

	// Organization Category test

	protected static final String TEST_CATEGORY_NAME = "Test Category";
	protected static final int TEST_CATEGORY_LEVEL = 0;

	protected static final String TEST_ORGANIZATION_NAME = "Test Organization";
	// Field Predicate Test
	protected static final String TEST_FIELD_PREDICATE_VALUE = "test.predicate";
	protected static final String TEST_DETACHABLE_FIELD_GLOSS_VALUE = "Test Detachable Gloss";
	protected static final String TEST_DETACHABLE_CONTROLLED_VOCABULARY_NAME = "Test Detachable Controlled Vocaublary";

	// Submission State Test
	protected static final String TEST_PARENT_SUBMISSION_STATE_NAME = "Test Parent Submission State";
	protected static final boolean TEST_PARENT_SUBMISSION_STATE_ARCHIVED = true;
	protected static final boolean TEST_PARENT_SUBMISSION_STATE_PUBLISHABLE = true;
	protected static final boolean TEST_PARENT_SUBMISSION_STATE_DELETABLE = true;
	protected static final boolean TEST_PARENT_SUBMISSION_STATE_EDITABLE_BY_REVIEWER = true;
	protected static final boolean TEST_PARENT_SUBMISSION_STATE_EDITABLE_BY_STUDENT = true;
	protected static final boolean TEST_PARENT_SUBMISSION_STATE_ACTIVE = true;
	
	protected static final boolean TEST_PARENT_WORKFLOW_INHERITABILITY = true;
	protected static final boolean TEST_CHILD_WORKFLOW_INHERITABILITY = true;
	protected static final boolean TEST_GRAND_CHILD_WORKFLOW_INHERITABILITY = true;
	protected static final boolean TEST_DETACHABLE_PARENT_WORKFLOW_INHERITABILITY = true;
	protected static final boolean TEST_DETACHABLE_CHILD_WORKFLOW_INHERITABILITY = true;


	protected static final String TEST_TRANSITION1_SUBMISSION_STATE_NAME = "Test Transition1 Submission State";
	protected static final String TEST_TRANSITION2_SUBMISSION_STATE_NAME = "Test Transition2 Submission State";
	protected static final boolean TEST_TRANSITION_SUBMISSION_STATE_ARCHIVED = true;
	protected static final boolean TEST_TRANSITION_SUBMISSION_STATE_PUBLISHABLE = true;
	protected static final boolean TEST_TRANSITION_SUBMISSION_STATE_DELETABLE = true;
	protected static final boolean TEST_TRANSITION_SUBMISSION_STATE_EDITABLE_BY_REVIEWER = true;
	protected static final boolean TEST_TRANSITION_SUBMISSION_STATE_EDITABLE_BY_STUDENT = true;
	protected static final boolean TEST_TRANSITION_SUBMISSION_STATE_ACTIVE = true;
	protected static final boolean TEST_WORKFLOW_INHERITABILITY = true;
	protected static final Boolean TEST_DETACHABLE_FIELD_PROFILE_REPEATABLE = false;
	protected static final Boolean TEST_DETACHABLE_FIELD_PROFILE_REQUIRED = false;

	// Submission Test
	protected static final String TEST_SUBMISSION_SUBMITTER_EMAIL = "admin@tdl.org";
	protected static final String TEST_SUBMISSION_SUBMITTER_FIRSTNAME = "TDL";
	protected static final String TEST_SUBMISSION_SUBMITTER_LASTNAME = "Admin";
	protected static final Role TEST_SUBMISSION_SUBMITTER_ROLE = Role.ADMINISTRATOR;

	protected static final String TEST_SUBMISSION_REVIEWER1_EMAIL = "reviewer1@tdl.org";
	protected static final String TEST_SUBMISSION_REVIEWER1_FIRSTNAME = "Ronald";
	protected static final String TEST_SUBMISSION_REVIEWER1_LASTNAME = "Reviewer";
	protected static final Role TEST_SUBMISSION_REVIEWER1_ROLE = Role.ADMINISTRATOR;

	protected static final String TEST_SUBMISSION_REVIEWER2_EMAIL = "reviewer2@tdl.org";
	protected static final String TEST_SUBMISSION_REVIEWER2_FIRSTNAME = "Roger";
	protected static final String TEST_SUBMISSION_REVIEWER2_LASTNAME = "Reviewer";
	protected static final Role TEST_SUBMISSION_REVIEWER2_ROLE = Role.ADMINISTRATOR;

	protected static final String TEST_DETACHABLE_FIELD_PREDICATE_VALUE = "dc.detachable";

	protected static final String TEST_DETACHABLE_ORGANIZATION_NAME = "Test Detachable Organization";

	protected static final String TEST_WORKFLOW_STEP_NAME = "Test Parent Workflow Step";
	protected static final String TEST_DETACHABLE_WORKFLOW_STEP_NAME = "Test Detachable Workflow Step";

	protected static final String TEST_SUBMISSION_STATE_ACTION_LOG_ENTRY = "Test ActionLog Entry";
	protected static final boolean TEST_SUBMISSION_STATE_ACTION_LOG_FLAG = true;
	protected static final Calendar TEST_SUBMISSION_STATE_ACTION_LOG_ACTION_DATE = Calendar.getInstance();

	protected static final String TEST_EMBARGO_TYPE_NAME = "Test Embargo Type Name";
	protected static final String TEST_EMBARGO_TYPE_DESCRIPTION = "Test Embargo Type Description";
	protected static final int TEST_EMBARGO_TYPE_DURATION = 0;

	// User Test

	protected static final String TEST_SHIBBOLETH_AFFILIATION = "shib_affiliation";

	protected static final String TEST_PREFERENCE_KEY = "key";
	protected static final String TEST_PREFERENCE_VALUE = "value";

	protected static final String TEST_CURRENT_ADDRESS1 = "101. E. 21st St.";
	protected static final String TEST_CURRENT_ADDRESS2 = "PCL 1.333";
	protected static final String TEST_CURRENT_CITY = "Austin";
	protected static final String TEST_CURRENT_STATE = "Texas";
	protected static final String TEST_CURRENT_POSTAL_CODE = "78759";
	protected static final String TEST_CURRENT_COUNTRY = "USA";
	protected static final String TEST_CURRENT_PHONE = "512-495-4418";
	protected static final String TEST_CURRENT_EMAIL = "admin@tdl.org";

	protected static final String TEST_PERMANENT_ADDRESS1 = "101. E. 21st St. <p>";
	protected static final String TEST_PERMANENT_ADDRESS2 = "PCL 1.333 <p>";
	protected static final String TEST_PERMANENT_CITY = "Austin <p>";
	protected static final String TEST_PERMANENT_STATE = "Texas <p>";
	protected static final String TEST_PERMANENT_POSTAL_CODE = "78759 <p>";
	protected static final String TEST_PERMANENT_COUNTRY = "USA <p>";
	protected static final String TEST_PERMANENT_PHONE = "512-495-4418 <p>";
	protected static final String TEST_PERMANENT_EMAIL = "admin@tdl.org <p>";

	// Organization Test

	protected static final int TEST_PARENT_CATEGORY_LEVEL = 0;
	protected static final int TEST_CHILD_CATEGORY_LEVEL = 1;
	protected static final int TEST_GRAND_CHILD_CATEGORY_LEVEL = 2;

	protected static final String TEST_PARENT_CATEGORY_NAME = "Test Parent Category";
	protected static final String TEST_CHILD_CATEGORY_NAME = "Test Child Category";
	protected static final String TEST_GRAND_CHILD_CATEGORY_NAME = "Test Grand Child Category";

	protected static final String TEST_PARENT_ORGANIZATION_NAME = "Test Parent Organization";
	protected static final String TEST_CHILD_ORGANIZATION_NAME = "Test Child Organization";
	protected static final String TEST_GRAND_CHILD_ORGANIZATION_NAME = "Test Grand Child Organization";
	protected static final String TEST_DETACHABLE_PARENT_ORGANIZATION_NAME = "Test Detachable Parent Organization";
	protected static final String TEST_DETACHABLE_CHILD_ORGANIZATION_NAME = "Test Detachable Child Organization";

	protected static final String TEST_PARENT_WORKFLOW_NAME = "Test Parent Workflow";
	protected static final String TEST_CHILD_WORKFLOW_NAME = "Test Child Workflow";
	protected static final String TEST_GRAND_CHILD_WORKFLOW_NAME = "Test Grand Child Workflow";
	protected static final String TEST_DETACHABLE_PARENT_WORKFLOW_NAME = "Test Detachable Parent Workflow";
	protected static final String TEST_DETACHABLE_CHILD_WORKFLOW_NAME = "Test Detachable Child Workflow";

	protected static final String TEST_PARENT_EMAIL = "Test Parent Email";
	protected static final String TEST_CHILD_EMAIL = "Test Child Email";
	protected static final String TEST_GRAND_CHILD_EMAIL = "Test Grand Child Email";
	protected static final String TEST_DETACHABLE_PARENT_EMAIL = "Test Detachable Parent Email";
	protected static final String TEST_DETACHABLE_CHILD_EMAIL = "Test Detachable Child Email";

	protected static final String TEST_CHILD_SUBMISSION_STATE_NAME = "Test Child Submission State";
	protected static final String TEST_DETACHABLE_PARENT_SUBMISSION_STATE_NAME = "Test Detachable Parent Submission State";
	protected static final String TEST_DETACHABLE_CHILD_SUBMISSION_STATE_NAME = "Test Detachable Child Submission State";
	protected static final String TEST_FIELD_VALUE = "Test Field Value";

	protected static final InputType TEST_FIELD_PROFILE_INPUT_TYPE = InputType.INPUT_TEXT;
	protected static final InputType TEST_DETACHABLE_FIELD_PROFILE_INPUT_TYPE = InputType.INPUT_TEXT;

	protected static final Role TEST_USER_ROLE = Role.ADMINISTRATOR;
	protected static Calendar TEST_ACTION_LOG_ACTION_DATE = Calendar.getInstance();
	protected static UUID TEST_UUID = UUID.randomUUID();

	@Autowired
	protected ActionLogRepo actionLogRepo;

	@Autowired
	protected SubmissionRepo submissionRepo;

	@Autowired
	protected SubmissionStateRepo submissionStateRepo;

	@Autowired
	protected UserRepo userRepo;

	@Autowired
	protected AttachmentRepo attachmentRepo;

	@Autowired
	protected AddressRepo addressRepo;

	@Autowired
	protected CustomActionDefinitionRepo customActionDefinitionRepo;

	@Autowired
	protected CustomActionValueRepo customActionValueRepo;

	@Autowired
	protected DepositLocationRepo depositLocationRepo;

	@Autowired
	protected EmailTemplateRepo emailTemplateRepo;

	@Autowired
	protected EmailWorkflowRuleRepo emailWorkflowRuleRepo;

	@Autowired
	protected ConfigurationRepo configurationRepo;

	@Autowired
	protected ContactInfoRepo contactInfoRepo;

	@Autowired
	protected ControlledVocabularyRepo controlledVocabularyRepo;

	@Autowired
	protected LanguageRepo languageRepo;

	@Autowired
	protected FieldGlossRepo fieldGlossRepo;

	@Autowired
	protected FieldPredicateRepo fieldPredicateRepo;

	@Autowired
	protected WorkflowStepRepo workflowStepRepo;

	@Autowired
	protected EmbargoTypeRepo embargoTypeRepo;

	protected FieldPredicate fieldPredicate;

	@Autowired
	protected FieldProfileRepo fieldProfileRepo;
	
	@Autowired
	protected FieldValueRepo fieldValueRepo;

	@Autowired
	protected OrganizationCategoryRepo organizationCategoryRepo;

	@Autowired
	protected OrganizationRepo organizationRepo;

	@Autowired
	protected WorkflowRepo workflowRepo;

	// WorkFlow Step test
	// protected static final String TEST_WORKFLOW_STEP_NAME = "Test Workflow
	// Step";

	// protected static final String TEST_FIELD_PREDICATE_VALUE =
	// "test.predicate";
	// protected static final Boolean TEST_FIELD_PROFILE_REPEATABLE = true;
	// protected static final Boolean TEST_FIELD_PROFILE_REQUIRED = true;
	// protected static final InputType TEST_FIELD_PROFILE_INPUT_TYPE =
	// InputType.INPUT_TEXT;

	// protected static final String TEST_DETACHABLE_FIELD_PREDICATE_VALUE =
	// "test.detachable.predicate";

	protected static Attachment attachment;
	protected static Address testAddress;
	protected static CustomActionDefinition testCustomActionDefinition;	
	protected static EmailTemplate emailTemplate;
	protected static EmailWorkflowRule emailWorkflowRule;
	protected static EmbargoType embargoType;	
	protected static FieldValue fieldValue;
	protected static Organization organization;	
	protected static Language language;
	protected static OrganizationCategory parentCategory;	
	protected static SubmissionState submissionState;
	protected static Submission testSubmission;
	protected static User testUser;
	protected static User submitter;
	protected static User graduateOfficeEmployee1;
	protected static User graduateOfficeEmployee2;
	protected static WorkflowStep workflowStep;

	@Test
	@Order(value = 1)
	@Transactional
	public abstract void testCreate();

	@Test
	@Order(value = 2)
	public abstract void testDuplication();

	@Test
	@Order(value = 3)
	public abstract void testFind();

	@Test
	@Order(value = 4)
	public abstract void testDelete();

	@Test
	@Order(value = 5)
	@Transactional
	public abstract void testCascade();

}
