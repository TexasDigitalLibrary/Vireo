package org.tdl.vireo.model;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.tdl.vireo.Application;
import org.tdl.vireo.config.constant.ConfigurationName;
import org.tdl.vireo.enums.AppRole;
import org.tdl.vireo.enums.EmbargoGuarantor;
import org.tdl.vireo.model.packager.Packager;
import org.tdl.vireo.model.repo.AbstractEmailRecipientRepo;
import org.tdl.vireo.model.repo.AbstractPackagerRepo;
import org.tdl.vireo.model.repo.ActionLogRepo;
import org.tdl.vireo.model.repo.AddressRepo;
import org.tdl.vireo.model.repo.ConfigurationRepo;
import org.tdl.vireo.model.repo.ContactInfoRepo;
import org.tdl.vireo.model.repo.ControlledVocabularyRepo;
import org.tdl.vireo.model.repo.CustomActionDefinitionRepo;
import org.tdl.vireo.model.repo.CustomActionValueRepo;
import org.tdl.vireo.model.repo.DepositLocationRepo;
import org.tdl.vireo.model.repo.DocumentTypeRepo;
import org.tdl.vireo.model.repo.EmailTemplateRepo;
import org.tdl.vireo.model.repo.EmailWorkflowRuleRepo;
import org.tdl.vireo.model.repo.EmbargoRepo;
import org.tdl.vireo.model.repo.EntityCVWhitelistRepo;
import org.tdl.vireo.model.repo.FieldGlossRepo;
import org.tdl.vireo.model.repo.FieldPredicateRepo;
import org.tdl.vireo.model.repo.FieldProfileRepo;
import org.tdl.vireo.model.repo.FieldValueRepo;
import org.tdl.vireo.model.repo.InputTypeRepo;
import org.tdl.vireo.model.repo.LanguageRepo;
import org.tdl.vireo.model.repo.NamedSearchFilterGroupRepo;
import org.tdl.vireo.model.repo.NoteRepo;
import org.tdl.vireo.model.repo.OrganizationCategoryRepo;
import org.tdl.vireo.model.repo.OrganizationRepo;
import org.tdl.vireo.model.repo.SubmissionFieldProfileRepo;
import org.tdl.vireo.model.repo.SubmissionRepo;
import org.tdl.vireo.model.repo.SubmissionStateRepo;
import org.tdl.vireo.model.repo.SubmissionWorkflowStepRepo;
import org.tdl.vireo.model.repo.UserRepo;
import org.tdl.vireo.model.repo.VocabularyWordRepo;
import org.tdl.vireo.model.repo.WorkflowStepRepo;
import org.tdl.vireo.service.EntityControlledVocabularyService;

import edu.tamu.framework.model.Credentials;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { Application.class })
public abstract class AbstractEntityTest {

    protected static final boolean TEST_SUBMISSION_STATE_ARCHIVED = true;
    protected static final boolean TEST_SUBMISSION_STATE_PUBLISHABLE = true;
    protected static final boolean TEST_SUBMISSION_STATE_DELETABLE = true;
    protected static final boolean TEST_SUBMISSION_STATE_EDITABLE_BY_REVIEWER = true;
    protected static final boolean TEST_SUBMISSION_STATE_EDITABLE_BY_STUDENT = true;
    protected static final boolean TEST_SUBMISSION_STATE_ACTIVE = true;
    protected static final boolean TEST_ACTION_LOG_FLAG = true;
    protected static final boolean TEST_CUSTOM_ACTION_DEFINITION_VISIBLE_BY_STUDENT = true;
    protected static final boolean TEST_CUSTOM_ACTION_VALUE = true;
    protected static final boolean TEST_FIELD_PROFILE_REPEATABLE = true;
    protected static final boolean TEST_FIELD_PROFILE_ENABLED = true;
    protected static final boolean TEST_FIELD_PROFILE_OPTIONAL = false;
    protected static final boolean TEST_FIELD_PROFILE_OVERRIDEABLE = true;
    protected static final boolean TEST_FIELD_PROFILE_FLAGGED = false;
    protected static final boolean TEST_FIELD_PROFILE_LOGGED = false;
    protected static final String TEST_FIELD_PROFILE_USAGE = "Test Field Profile Usage";

    protected static final String TEST_USER_EMAIL = "admin@tdl.org";
    protected static final String TEST_USER_FIRSTNAME = "TDL";
    protected static final String TEST_USER_LASTNAME = "Admin";

    protected static final String TEST_SUBMISSION_STATE_NAME = "Test Submission State";
    protected static final String TEST_DOCUMENT_NAME = "Test Document Name";

    protected static final String TEST_ACTION_LOG_ENTRY = "Test ActionLog Entry";

    // AddressTest
    protected static final String TEST_ADDRESS1 = "101. E. 21st St.";
    protected static final String TEST_ADDRESS2 = "PCL 1.333";
    protected static final String TEST_CITY = "Austin";
    protected static final String TEST_STATE = "Texas";
    protected static final String TEST_POSTAL_CODE = "78759";
    protected static final String TEST_COUNTRY = "USA";

    // LanguageTest
    protected static final String TEST_LANGUAGE_NAME = "Test Language";

    // ConfigurationTest
    protected static final String TEST_VIREO_CONFIG_SUBMISSIONS_OPEN_KEY = ConfigurationName.SUBMISSIONS_OPEN;
    protected static final String TEST_VIREO_INSTALL_DIR = "./";
    protected static final String TEST_VIREO_INSTALL_DIR_CHANGED = TEST_VIREO_INSTALL_DIR + "changed/";

    // ControlledVocabulary Test
    protected static final String TEST_LANGUAGE = "English";
    protected static final String TEST_CONTROLLED_VOCABULARY_NAME = "Test Vocabulary";
    protected static final String TEST_CONTROLLED_VOCABULARY_WORD = "Test Vocabulary Word";
    protected static final String TEST_CONTROLLED_VOCABULARY_DEFINITION = "This is an awesome word!";
    protected static final String TEST_CONTROLLED_VOCABULARY_IDENTIFIER = "http://linked.to.data";
    protected static final String TEST_SEVERABLE_CONTROLLED_VOCABULARY_WORD = "Test Severable Vocabulary Word";
    protected static final String TEST_SEVERABLE_CONTROLLED_VOCABULARY_DEFINITION = "Test Severable Vocabulary Definition";
    protected static final String TEST_SEVERABLE_CONTROLLED_VOCABULARY_IDENTIFIER = "Test Severable Vocabulary Identifier";

    // must be the name of the property on the entity
    protected static final String TEST_CONTROLLED_VOCABULARY_EMBARGO_GUARANTOR = "guarantor";
    protected static final String TEST_CONTROLLED_VOCABULARY_EMBARGO = "Embargo";

    // DepositLocation test
    protected static final String TEST_DEPOSIT_LOCATION_NAME = "Test Deposit Name";
    protected static final String TEST_DEPOSIT_REPOSITORY = "Test Deposit Repository";
    protected static final String TEST_DEPOSIT_COLLECTION = "Test Deposit Collection";
    protected static final String TEST_DEPOSIT_USERNAME = "Test Deposit Username";
    protected static final String TEST_DEPOSIT_PASSWORD = "Test Deposit Password";
    protected static final String TEST_DEPOSIT_ONBEHALFOF = "Test Deposit OnBehalfOf";
    protected static final String TEST_DEPOSIT_DEPOSITOR = "Test Deposit Depositor";

    // DocumentType test
    protected static final String TEST_DOCUMENT_TYPE_NAME = "Test Document Type";

    // CustomActionDefinition Test
    protected static final String TEST_CUSTOM_ACTION_DEFINITION_LABEL = "Test Custom Action Definition Label";

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
    protected static final String TEST_ORGANIZATION_NAME = "Test Organization";

    // Field Predicate Test
    protected static final String TEST_FIELD_PREDICATE_VALUE = "test.predicate";
    protected static final String TEST_SEVERABLE_FIELD_GLOSS_VALUE = "Test Severable Gloss";
    protected static final String TEST_SEVERABLE_CONTROLLED_VOCABULARY_NAME = "Test Severable Controlled Vocaublary";

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
    protected static final boolean TEST_SEVERABLE_PARENT_WORKFLOW_INHERITABILITY = true;
    protected static final boolean TEST_SEVERABLE_CHILD_WORKFLOW_INHERITABILITY = true;

    protected static final String TEST_TRANSITION1_SUBMISSION_STATE_NAME = "Test Transition1 Submission State";
    protected static final String TEST_TRANSITION2_SUBMISSION_STATE_NAME = "Test Transition2 Submission State";
    protected static final boolean TEST_TRANSITION_SUBMISSION_STATE_ARCHIVED = true;
    protected static final boolean TEST_TRANSITION_SUBMISSION_STATE_PUBLISHABLE = true;
    protected static final boolean TEST_TRANSITION_SUBMISSION_STATE_DELETABLE = true;
    protected static final boolean TEST_TRANSITION_SUBMISSION_STATE_EDITABLE_BY_REVIEWER = true;
    protected static final boolean TEST_TRANSITION_SUBMISSION_STATE_EDITABLE_BY_STUDENT = true;
    protected static final boolean TEST_TRANSITION_SUBMISSION_STATE_ACTIVE = true;
    protected static final boolean TEST_WORKFLOW_INHERITABILITY = true;
    protected static final String TEST_SEVERABLE_FIELD_PROFILE_USAGE = "Test Severable Field Profile Usage";
    protected static final boolean TEST_SEVERABLE_FIELD_PROFILE_REPEATABLE = false;
    protected static final boolean TEST_SEVERABLE_FIELD_PROFILE_ENABLED = false;
    protected static final boolean TEST_SEVERABLE_FIELD_PROFILE_OPTIONAL = false;

    // Submission Test
    protected static final String TEST_SUBMISSION_SUBMITTER_EMAIL = "admin@tdl.org";
    protected static final String TEST_SUBMISSION_SUBMITTER_FIRSTNAME = "TDL";
    protected static final String TEST_SUBMISSION_SUBMITTER_LASTNAME = "Admin";
    protected static final AppRole TEST_SUBMISSION_SUBMITTER_ROLE = AppRole.ADMINISTRATOR;

    protected static final String TEST_SUBMISSION_REVIEWER1_EMAIL = "reviewer1@tdl.org";
    protected static final String TEST_SUBMISSION_REVIEWER1_FIRSTNAME = "Ronald";
    protected static final String TEST_SUBMISSION_REVIEWER1_LASTNAME = "Reviewer";
    protected static final AppRole TEST_SUBMISSION_REVIEWER1_ROLE = AppRole.ADMINISTRATOR;

    protected static final String TEST_SUBMISSION_REVIEWER2_EMAIL = "reviewer2@tdl.org";
    protected static final String TEST_SUBMISSION_REVIEWER2_FIRSTNAME = "Roger";
    protected static final String TEST_SUBMISSION_REVIEWER2_LASTNAME = "Reviewer";
    protected static final AppRole TEST_SUBMISSION_REVIEWER2_ROLE = AppRole.ADMINISTRATOR;

    protected static final String TEST_SEVERABLE_FIELD_PREDICATE_VALUE = "dc.detachable";

    protected static final String TEST_SEVERABLE_ORGANIZATION_NAME = "Test Severable Organization";

    protected static final String TEST_WORKFLOW_STEP_NAME = "Test Workflow Step";
    protected static final String TEST_PARENT_WORKFLOW_STEP_NAME = "Test Parent Workflow Step";
    protected static final String TEST_SEVERABLE_WORKFLOW_STEP_NAME = "Test Severable Workflow Step";

    protected static final String TEST_SUBMISSION_STATE_ACTION_LOG_ENTRY = "Test ActionLog Entry";
    protected static final boolean TEST_SUBMISSION_STATE_ACTION_LOG_FLAG = true;
    protected static final Calendar TEST_SUBMISSION_STATE_ACTION_LOG_ACTION_DATE = Calendar.getInstance();

    protected static final String TEST_EMBARGO_TYPE_NAME = "Test Embargo Type Name";
    protected static final String TEST_EMBARGO_TYPE_DESCRIPTION = "Test Embargo Type Description";
    protected static final int TEST_EMBARGO_TYPE_DURATION = 0;
    protected static final EmbargoGuarantor TEST_EMBARGO_TYPE_GUARANTOR = EmbargoGuarantor.DEFAULT;
    protected static final boolean TEST_EMBARGO_IS_ACTIVE = true;

    // User Test
    protected static final String TEST_SHIBBOLETH_AFFILIATION = "shib_affiliation";

    protected static final String TEST_SETTING_KEY = "key";
    protected static final String TEST_SETTING_VALUE = "value";

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
    protected static final String TEST_PARENT_CATEGORY_NAME = "Test Parent Category";
    protected static final String TEST_CHILD_CATEGORY_NAME = "Test Child Category";
    protected static final String TEST_GRAND_CHILD_CATEGORY_NAME = "Test Grand Child Category";

    protected static final String TEST_PARENT_ORGANIZATION_NAME = "Test Parent Organization";
    protected static final String TEST_CHILD_ORGANIZATION_NAME = "Test Child Organization";
    protected static final String TEST_GRAND_CHILD_ORGANIZATION_NAME = "Test Grand Child Organization";
    protected static final String TEST_SEVERABLE_PARENT_ORGANIZATION_NAME = "Test Severable Parent Organization";
    protected static final String TEST_SEVERABLE_CHILD_ORGANIZATION_NAME = "Test Severable Child Organization";

    protected static final String TEST_PARENT_WORKFLOW_NAME = "Test Parent Workflow";
    protected static final String TEST_CHILD_WORKFLOW_NAME = "Test Child Workflow";
    protected static final String TEST_GRAND_CHILD_WORKFLOW_NAME = "Test Grand Child Workflow";
    protected static final String TEST_SEVERABLE_PARENT_WORKFLOW_NAME = "Test Severable Parent Workflow";
    protected static final String TEST_SEVERABLE_CHILD_WORKFLOW_NAME = "Test Severable Child Workflow";

    protected static final String TEST_PARENT_EMAIL = "Test Parent Email";
    protected static final String TEST_CHILD_EMAIL = "Test Child Email";
    protected static final String TEST_GRAND_CHILD_EMAIL = "Test Grand Child Email";
    protected static final String TEST_SEVERABLE_PARENT_EMAIL = "Test Severable Parent Email";
    protected static final String TEST_SEVERABLE_CHILD_EMAIL = "Test Severable Child Email";

    protected static final String TEST_CHILD_SUBMISSION_STATE_NAME = "Test Child Submission State";
    protected static final String TEST_SEVERABLE_PARENT_SUBMISSION_STATE_NAME = "Test Severable Parent Submission State";
    protected static final String TEST_SEVERABLE_CHILD_SUBMISSION_STATE_NAME = "Test Severable Child Submission State";
    protected static final String TEST_FIELD_VALUE = "Test Field Value";

    protected static final String TEST_INCLUDED_SUBMITTER_EMAIL = "includedSubmitter@tdl.org";
    protected static final String TEST_INCLUDED_SUBMITTER_FIRSTNAME = "Included Submitter First Name";
    protected static final String TEST_INCLUDED_SUBMITTER_LASTNAME = "Included Submitter Last Name";

    protected static final String TEST_EXCLUDED_SUBMITTER_EMAIL = "excludedSubmitter@tdl.org";
    protected static final String TEST_EXCLUDED_SUBMITTER_FIRSTNAME = "Included Submitter First Name";
    protected static final String TEST_EXCLUDED_SUBMITTER_LASTNAME = "Included Submitter Last Name";
    protected static final AppRole TEST_SUBMITTER_ROLE = AppRole.ADMINISTRATOR;

    protected static final String TEST_ASSIGNEE_EMAIL = "assignee@tdl.org";
    protected static final String TEST_ASSIGNEE_FIRSTNAME = "TDL";
    protected static final String TEST_ASSIGNEE_LASTNAME = "Admin";
    protected static final AppRole TEST_ASSIGNEE_ROLE = AppRole.STUDENT;

    protected static final String TEST_EMBARGO_NAME = "Test Embargo Name";
    protected static final String TEST_EMBARGO_NAME_2 = "Test Embargo 2 Name";
    protected static final String TEST_EMBARGO_DESCRIPTION = "Test Embargo Description";
    protected static final Integer TEST_EMBARGO_DURATION = 0;

    protected static final String TEST_NAMED_SEARCH_FILTER_NAME = "Test Filter Name";

    protected static final String TEST_INCLUDED_SUBMISSION_STATE_NAME = "Test Included Submission State Name";
    protected static final String TEST_EXCLUDED_SUBMISSION_STATE_NAME = "Test Excluded Submission State Name";

    protected static final String TEST_FIELD_PROFILE_INPUT_TEXT_NAME = "INPUT_TEXT";

    protected static final String TEST_FIELD_PROFILE_INPUT_FILE_NAME = "INPUT_FILE";

    protected static final String TEST_ORGANIZATION_CATEGORY_NAME = "Test Organization Category";

    protected static final String TEST_NOTE_NAME = "Test Note Name";
    protected static final String TEST_NOTE_TEXT = "Test Note Text";

    protected static final String TEST_SEVERABLE_NOTE_NAME = "Test Severable Note Name";
    protected static final String TEST_SEVERABLE_NOTE_TEXT = "Test Severable Note Text";

    protected static final AppRole TEST_USER_ROLE = AppRole.STUDENT;

    protected static final Calendar TEST_ACTION_LOG_ACTION_DATE = Calendar.getInstance();
    protected static final UUID TEST_UUID = UUID.randomUUID();

    @Autowired
    protected EntityControlledVocabularyService entityControlledVocabularyService;

    @Autowired
    protected InputTypeRepo inputTypeRepo;

    @Autowired
    protected ActionLogRepo actionLogRepo;

    @Autowired
    protected SubmissionRepo submissionRepo;

    @Autowired
    protected SubmissionStateRepo submissionStateRepo;

    @Autowired
    protected UserRepo userRepo;

    @Autowired
    protected AddressRepo addressRepo;

    @Autowired
    protected CustomActionDefinitionRepo customActionDefinitionRepo;

    @Autowired
    protected CustomActionValueRepo customActionValueRepo;

    @Autowired
    protected DepositLocationRepo depositLocationRepo;

    @Autowired
    protected DocumentTypeRepo documentTypeRepo;

    @Autowired
    protected AbstractEmailRecipientRepo emailRecipientRepo;

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
    protected VocabularyWordRepo vocabularyWordRepo;

    @Autowired
    protected LanguageRepo languageRepo;

    @Autowired
    protected NoteRepo noteRepo;

    @Autowired
    protected FieldGlossRepo fieldGlossRepo;

    @Autowired
    protected FieldPredicateRepo fieldPredicateRepo;

    @Autowired
    protected WorkflowStepRepo workflowStepRepo;

    @Autowired
    protected SubmissionWorkflowStepRepo submissionWorkflowStepRepo;

    @Autowired
    protected EmbargoRepo embargoRepo;

    protected FieldPredicate fieldPredicate;

    @Autowired
    protected FieldProfileRepo fieldProfileRepo;

    @Autowired
    protected SubmissionFieldProfileRepo submissionFieldProfileRepo;

    @Autowired
    protected FieldValueRepo fieldValueRepo;

    @Autowired
    protected OrganizationCategoryRepo organizationCategoryRepo;

    @Autowired
    protected OrganizationRepo organizationRepo;

    @Autowired
    protected NamedSearchFilterGroupRepo namedSearchFilterRepo;

    @Autowired
    protected EntityCVWhitelistRepo entityCVWhitelistRepo;

    @Autowired
    protected AbstractPackagerRepo abstractPackagerRepo;

    protected InputType inputType;

    protected ActionLog includedActionLog;
    protected ActionLog excludedActionLog;

    protected Address testAddress;

    protected CustomActionDefinition testCustomActionDefinition;
    protected CustomActionDefinition customActionDefinition;
    protected CustomActionValue customActionValue;

    protected EmailTemplate emailTemplate;
    protected EmailWorkflowRule emailWorkflowRule;
    protected EmailRecipient emailRecipient;
    protected Embargo embargoType;
    protected FieldValue fieldValue;
    protected Organization organization;
    protected Language language;
    protected OrganizationCategory parentCategory;
    protected OrganizationCategory childCategory;
    protected OrganizationCategory grandChildCategory;
    protected OrganizationCategory organizationCategory;

    protected SubmissionState submissionState;
    protected SubmissionState includedSubmissionState;
    protected SubmissionState excludedSubmissionState;

    protected Submission testSubmission;
    protected Submission includedSubmission;
    protected Submission excludedSubmission;

    protected User testUser;
    protected User submitter;
    protected User graduateOfficeEmployee1;
    protected User graduateOfficeEmployee2;
    protected User creator;
    protected User assignee;
    protected User includedSubmitter;
    protected User excludedSubmitter;

    protected WorkflowStep workflowStep;

    protected SubmissionWorkflowStep submissionWorkflowStep;

    protected VocabularyWord vocabularyWord;

    protected EntityCVWhitelist entityCVWhitelist;

    protected ControlledVocabulary controlledVocabulary;

    protected FieldProfile fieldProfile;

    protected Packager packager;

    private Credentials credentials;

    @Test
    public abstract void testCreate();

    @Test
    public abstract void testDuplication();

    @Test
    public abstract void testDelete();

    @Test
    public abstract void testCascade();

    protected Credentials getCredentials() {
        if (credentials == null) {
            Map<String, String> token = new HashMap<String, String>();
            token.put("lastName", TEST_USER_LASTNAME);
            token.put("firstName", TEST_USER_FIRSTNAME);
            token.put("netid", "netid");
            token.put("uin", "uin");
            token.put("exp", "expires");
            token.put("email", TEST_USER_EMAIL);
            token.put("role", TEST_USER_ROLE.toString());
            token.put("affiliation", TEST_SHIBBOLETH_AFFILIATION);

            credentials = new Credentials(token);
        }
        return credentials;
    }

}
