package org.tdl.vireo.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.tdl.vireo.Application;
import org.tdl.vireo.exception.OrganizationDoesNotAcceptSubmissionsException;
import org.tdl.vireo.mock.MockData;
import org.tdl.vireo.model.Action;
import org.tdl.vireo.model.ActionLog;
import org.tdl.vireo.model.EmailRecipient;
import org.tdl.vireo.model.EmailRecipientContact;
import org.tdl.vireo.model.EmailRecipientPlainAddress;
import org.tdl.vireo.model.EmailTemplate;
import org.tdl.vireo.model.EmailWorkflowRuleByAction;
import org.tdl.vireo.model.EmailWorkflowRuleByStatus;
import org.tdl.vireo.model.FieldPredicate;
import org.tdl.vireo.model.FieldValue;
import org.tdl.vireo.model.InputType;
import org.tdl.vireo.model.Organization;
import org.tdl.vireo.model.OrganizationCategory;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.SubmissionState;
import org.tdl.vireo.model.SubmissionStatus;
import org.tdl.vireo.model.User;
import org.tdl.vireo.model.repo.ActionLogRepo;
import org.tdl.vireo.model.repo.EmailTemplateRepo;
import org.tdl.vireo.model.repo.EmailWorkflowRuleByActionRepo;
import org.tdl.vireo.model.repo.EmailWorkflowRuleRepo;
import org.tdl.vireo.model.repo.FieldPredicateRepo;
import org.tdl.vireo.model.repo.InputTypeRepo;
import org.tdl.vireo.model.repo.SubmissionRepo;
import org.tdl.vireo.model.repo.impl.AbstractEmailRecipientRepoImpl;
import org.tdl.vireo.utility.TemplateUtility;

import com.fasterxml.jackson.core.JsonProcessingException;

@ActiveProfiles(value = { "test", "isolated-test" })
@ExtendWith(MockitoExtension.class)
@SpringBootTest(classes = { Application.class })
public class SubmissionEmailServiceTest extends MockData {

    private static final Long TEST_FIELD_PREDICATE_ADVISOR_ID = 1L;
    private static final int TEST_FIELD_PREDICATE_ADVISOR_ID_INTEGER = 1;
    private static final String TEST_FIELD_PREDICATE_ADVISOR_NAME = "Committee Chair";
    private static final String TEST_FIELD_PREDICATE_ADVISOR_VALUE = "dc.contributor.advisor";

    private static final Long TEST_FIELD_PREDICATE_COMMITTEE_MEMBER_ID = 2L;
    private static final String TEST_FIELD_PREDICATE_COMMITTEE_MEMBER_NAME = "Non-Committee Chair";
    private static final String TEST_FIELD_PREDICATE_COMMITTEE_MEMBER_VALUE = "dc.contributor.committeeMember";

    private static final FieldPredicate TEST_FIELD_PREDICATE_ADVISOR = new FieldPredicate();
    static {
        TEST_FIELD_PREDICATE_ADVISOR.setId(1L);
        TEST_FIELD_PREDICATE_ADVISOR.setDocumentTypePredicate(false);
        TEST_FIELD_PREDICATE_ADVISOR.setValue(TEST_FIELD_PREDICATE_ADVISOR_NAME);
    }

    private static final FieldPredicate TEST_FIELD_PREDICATE_COMMITTEE_MEMBER = new FieldPredicate();
    static {
        TEST_FIELD_PREDICATE_COMMITTEE_MEMBER.setId(2L);
        TEST_FIELD_PREDICATE_COMMITTEE_MEMBER.setDocumentTypePredicate(false);
        TEST_FIELD_PREDICATE_COMMITTEE_MEMBER.setValue(TEST_FIELD_PREDICATE_COMMITTEE_MEMBER_NAME);
    }

    private static final EmailRecipient TEST_EMAIL_RECIPIENT_ADVISOR = new EmailRecipientContact(TEST_FIELD_PREDICATE_ADVISOR_NAME, TEST_FIELD_PREDICATE_ADVISOR);
    private static final EmailRecipient TEST_EMAIL_RECIPIENT_PLAIN = new EmailRecipientPlainAddress(TEST_USER_EMAIL);

    private static final Map<String, String> TEST_USER1_SETTINGS1 = new HashMap<>();
    static {
        TEST_USER1_SETTINGS1.put("displayName", "Display Name 1");
        TEST_USER1_SETTINGS1.put("preferedEmail", TEST_USER_EMAIL);
    }

    private static final Map<String, String> TEST_USER1_SETTINGS2 = new HashMap<>();
    static {
        TEST_USER1_SETTINGS2.put("displayName", "Display Name 1");
        TEST_USER1_SETTINGS2.put("ccEmail", "true");
    }

    private static final Map<String, String> TEST_USER1_SETTINGS3 = new HashMap<>();
    static {
        TEST_USER1_SETTINGS3.put("displayName", "Display Name 1");
        TEST_USER1_SETTINGS3.put("ccEmail", "true");
        TEST_USER1_SETTINGS3.put("preferedEmail", TEST_USER_EMAIL);
    }

    private static final Map<String, String> TEST_USER1_SETTINGS4 = new HashMap<>();
    static {
        TEST_USER1_SETTINGS4.put("displayName", "Display Name 1");
        TEST_USER1_SETTINGS4.put("ccEmail", "false");
    }

    private static final EmailTemplate TEST_EMAIL_TEMPLATE1 = new EmailTemplate();
    static {
        TEST_EMAIL_TEMPLATE1.setId(1L);
        TEST_EMAIL_TEMPLATE1.setName(TEST_EMAIL_TEMPLATE_NAME);
        TEST_EMAIL_TEMPLATE1.setMessage(TEST_EMAIL_TEMPLATE_MESSAGE);
        TEST_EMAIL_TEMPLATE1.setSubject(TEST_EMAIL_TEMPLATE_SUBJECT);
    }

    private static final List<EmailTemplate> TEST_EMAIL_TEMPLATES1 = new ArrayList<EmailTemplate>();
    static {
        TEST_EMAIL_TEMPLATES1.add(TEST_EMAIL_TEMPLATE1);
    }

    private static final String TEST_ORGANIZATION1_NAME = "Test Organization 1";
    private static final OrganizationCategory TEST_ORGANIZATION_CATEGORY1 = new OrganizationCategory("Test Organization Category 1");

    private static final InputType TEST_INPUT_TYPE1 = new InputType("Test Input Type 1");

    private static final SubmissionStatus TEST_SUBMISSION_STATUS1 = new SubmissionStatus("Test Submission Status 1", false, false, false, false, false, true, SubmissionState.IN_PROGRESS);
    private static final SubmissionStatus TEST_SUBMISSION_STATUS2 = new SubmissionStatus("Test Submission Status 2", true, false, false, false, false, false, SubmissionState.CANCELED);

    private static final Calendar TEST_CALENDAR1 = new Calendar.Builder().build();

    private static final ActionLog TEST_ACTION_LOG1 = new ActionLog(Action.UNDETERMINED, TEST_SUBMISSION_STATUS1, TEST_CALENDAR1, "Test Action Log 1", false);

    private static final EmailWorkflowRuleByStatus TEST_EMAIL_WORKFLOW_RULE_ADVISOR = new EmailWorkflowRuleByStatus();
    static {
        TEST_EMAIL_WORKFLOW_RULE_ADVISOR.setId(1L);
        TEST_EMAIL_WORKFLOW_RULE_ADVISOR.setEmailRecipient(TEST_EMAIL_RECIPIENT_ADVISOR);
        TEST_EMAIL_WORKFLOW_RULE_ADVISOR.setSubmissionStatus(TEST_SUBMISSION_STATUS1);
        TEST_EMAIL_WORKFLOW_RULE_ADVISOR.setEmailTemplate(TEST_EMAIL_TEMPLATE1);
        TEST_EMAIL_WORKFLOW_RULE_ADVISOR.isDisabled(false);
    }

    private static final EmailWorkflowRuleByStatus TEST_EMAIL_WORKFLOW_RULE_PLAIN = new EmailWorkflowRuleByStatus();
    static {
        TEST_EMAIL_WORKFLOW_RULE_PLAIN.setId(2L);
        TEST_EMAIL_WORKFLOW_RULE_PLAIN.setEmailRecipient(TEST_EMAIL_RECIPIENT_PLAIN);
        TEST_EMAIL_WORKFLOW_RULE_PLAIN.setSubmissionStatus(TEST_SUBMISSION_STATUS2);
        TEST_EMAIL_WORKFLOW_RULE_PLAIN.setEmailTemplate(TEST_EMAIL_TEMPLATE1);
        TEST_EMAIL_WORKFLOW_RULE_PLAIN.isDisabled(false);
    }

    private static final EmailWorkflowRuleByAction TEST_EMAIL_WORKFLOW_RULE_BY_ACTION = new EmailWorkflowRuleByAction();
    static {
        TEST_EMAIL_WORKFLOW_RULE_BY_ACTION.setId(3L);
        TEST_EMAIL_WORKFLOW_RULE_BY_ACTION.setEmailRecipient(TEST_EMAIL_RECIPIENT_PLAIN);
        TEST_EMAIL_WORKFLOW_RULE_BY_ACTION.setAction(Action.STUDENT_MESSAGE);
        TEST_EMAIL_WORKFLOW_RULE_BY_ACTION.setEmailTemplate(TEST_EMAIL_TEMPLATE1);
        TEST_EMAIL_WORKFLOW_RULE_BY_ACTION.isDisabled(false);
    }

    private static final Map<String, Object> TEST_EMAIL_RECIPIENT_MAP1 = new HashMap<String, Object>();
    static {
        TEST_EMAIL_RECIPIENT_MAP1.put("type", "PLAIN_ADDRESS");
        TEST_EMAIL_RECIPIENT_MAP1.put("name", TEST_USER_EMAIL);
        TEST_EMAIL_RECIPIENT_MAP1.put("data", TEST_USER_EMAIL);
    }

    private static final Map<String, Object> TEST_EMAIL_RECIPIENT_MAP2 = new HashMap<String, Object>();
    static {
        TEST_EMAIL_RECIPIENT_MAP2.put("type", "ASSIGNEE");
        TEST_EMAIL_RECIPIENT_MAP2.put("name", "Assignee");
        TEST_EMAIL_RECIPIENT_MAP2.put("data", "Assignee");
    }

    private static final Map<String, Object> TEST_EMAIL_RECIPIENT_MAP3 = new HashMap<String, Object>();
    static {
        TEST_EMAIL_RECIPIENT_MAP3.put("type", "CONTACT");
        TEST_EMAIL_RECIPIENT_MAP3.put("name", "Contact");
        TEST_EMAIL_RECIPIENT_MAP3.put("data", TEST_FIELD_PREDICATE_ADVISOR_ID_INTEGER);
    }

    private static final Map<String, Object> TEST_EMAIL_RECIPIENT_MAP4 = new HashMap<String, Object>();
    static {
        TEST_EMAIL_RECIPIENT_MAP4.put("type", "ORGANIZATION");
        TEST_EMAIL_RECIPIENT_MAP4.put("name", "Organization");
        TEST_EMAIL_RECIPIENT_MAP4.put("data", null);
    }

    private static final Map<String, Object> TEST_EMAIL_RECIPIENT_MAP5 = new HashMap<String, Object>();
    static {
        TEST_EMAIL_RECIPIENT_MAP5.put("type", "SUBMITTER");
        TEST_EMAIL_RECIPIENT_MAP5.put("name", "Submitter");
        TEST_EMAIL_RECIPIENT_MAP5.put("data", "Submitter");
    }

    private static final Map<String, Object> TEST_EMAIL_RECIPIENT_MAP6 = new HashMap<String, Object>();
    static {
        TEST_EMAIL_RECIPIENT_MAP6.put("type", "ADVISOR");
        TEST_EMAIL_RECIPIENT_MAP6.put("name", "Advisor");
        TEST_EMAIL_RECIPIENT_MAP6.put("data", "Advisor");
    }

    private static final FieldPredicate TEST_FIELD_PREDICATE4 = new FieldPredicate();
    static {
        TEST_FIELD_PREDICATE4.setId(4L);
        TEST_FIELD_PREDICATE4.setDocumentTypePredicate(true);
        TEST_FIELD_PREDICATE4.setValue("dc.contributor.advisor");
    }

    private static final List<String> TEST_CONTACTS_LIST1 = new ArrayList<>();
    static {
        TEST_CONTACTS_LIST1.add(TEST_USER_EMAIL);
    }

    private static final List<String> TEST_CONTACTS_LIST2 = new ArrayList<>();
    static {
        TEST_CONTACTS_LIST2.add(TEST_USER_EMAIL);
        TEST_CONTACTS_LIST2.add(TEST_EMAIL);
    }

    private static final List<String> TEST_CONTACTS_LIST3 = new ArrayList<>();
    static {
        TEST_CONTACTS_LIST3.add(TEST_USER_EMAIL);
    }

    private static final FieldValue TEST_FIELD_VALUE1 = new FieldValue();
    static {
        TEST_FIELD_VALUE1.setId(1L);
        TEST_FIELD_VALUE1.setContacts(TEST_CONTACTS_LIST1);
        TEST_FIELD_VALUE1.setDefinition("Mock Field Value Definition 1");
        TEST_FIELD_VALUE1.setFieldPredicate(TEST_FIELD_PREDICATE_ADVISOR);
        TEST_FIELD_VALUE1.setIdentifier("1");
        TEST_FIELD_VALUE1.setValue("Mock Field Value Value 1");
    }

    private static final FieldValue TEST_FIELD_VALUE2 = new FieldValue();
    static {
        TEST_FIELD_VALUE2.setId(2L);
        TEST_FIELD_VALUE2.setContacts(TEST_CONTACTS_LIST2);
        TEST_FIELD_VALUE2.setDefinition("Mock Field Value Definition 2");
        TEST_FIELD_VALUE2.setFieldPredicate(TEST_FIELD_PREDICATE_COMMITTEE_MEMBER);
        TEST_FIELD_VALUE2.setIdentifier("2");
        TEST_FIELD_VALUE2.setValue("Mock Field Value Value 2");
    }

    private static final FieldValue TEST_FIELD_VALUE3 = new FieldValue();
    static {
        TEST_FIELD_VALUE3.setId(3L);
        TEST_FIELD_VALUE3.setContacts(TEST_CONTACTS_LIST2);
        TEST_FIELD_VALUE3.setDefinition("Mock Field Value Definition 3");
        TEST_FIELD_VALUE3.setFieldPredicate(TEST_FIELD_PREDICATE4);
        TEST_FIELD_VALUE3.setIdentifier("3");
        TEST_FIELD_VALUE3.setValue("email@mailinator.com");
    }

    private Map<String, Object> mockData;

    private List<FieldValue> mockFieldValues;

    private List<EmailWorkflowRuleByStatus> mockEmailWorkflowRules;

    @MockBean
    protected AbstractEmailRecipientRepoImpl mockAbstractEmailRecipientRepoImpl;

    @MockBean
    protected EmailWorkflowRuleRepo mockEmailWorkflowRuleRepo;

    @MockBean
    protected EmailWorkflowRuleByActionRepo mockEmailWorkflowRuleByActionRepo;

    @MockBean
    protected ActionLogRepo mockActionLogRepo;

    @MockBean
    protected SubmissionRepo mockSubmissionRepo;

    @MockBean
    protected EmailTemplateRepo mockEmailTemplateRepo;

    @MockBean
    private InputTypeRepo mockInputTypeRepo;

    @MockBean
    private FieldPredicateRepo mockFieldPredicateRepo;

    @MockBean
    private VireoEmailSender mockEmailSender;

    @Mock
    private Organization mockOrganization;

    @Mock
    private Submission mockSubmission;

    @Mock
    private TemplateUtility mockTemplateUtility;

    @InjectMocks
    private SubmissionEmailService submissionEmailService;

    @BeforeEach
    public void setUp() throws OrganizationDoesNotAcceptSubmissionsException, MessagingException {
        mockData = new HashMap<>();
        mockFieldValues = new ArrayList<>();
        mockEmailWorkflowRules = new ArrayList<>();

        TEST_USER.setSettings(TEST_USER1_SETTINGS1);

        mockEmailWorkflowRules.add(TEST_EMAIL_WORKFLOW_RULE_ADVISOR);
        mockEmailWorkflowRules.add(TEST_EMAIL_WORKFLOW_RULE_PLAIN);

        lenient().when(mockOrganization.getId()).thenReturn(1L);
        lenient().when(mockOrganization.getName()).thenReturn(TEST_ORGANIZATION1_NAME);
        lenient().when(mockOrganization.getCategory()).thenReturn(TEST_ORGANIZATION_CATEGORY1);
        lenient().when(mockOrganization.getAggregateEmailWorkflowRules()).thenReturn(mockEmailWorkflowRules);

        lenient().when(mockSubmission.getOrganization()).thenReturn(mockOrganization);
        lenient().when(mockSubmission.getSubmissionStatus()).thenReturn(TEST_SUBMISSION_STATUS1);
        lenient().when(mockSubmission.getSubmitter()).thenReturn(TEST_USER);

        lenient().when(mockSubmission.getFieldValuesByPredicateValue(any(String.class))).thenReturn(mockFieldValues);
        lenient().when(mockSubmission.getFieldValuesByInputType(any(InputType.class))).thenReturn(mockFieldValues);

        lenient().when(mockInputTypeRepo.getById(1L)).thenReturn(TEST_INPUT_TYPE1);
        lenient().when(mockInputTypeRepo.findByName(any(String.class))).thenReturn(TEST_INPUT_TYPE1);

        lenient().when(mockEmailTemplateRepo.findById(1L)).thenReturn(Optional.of(TEST_EMAIL_TEMPLATE1));
        lenient().when(mockEmailTemplateRepo.findByName(any(String.class))).thenReturn(TEST_EMAIL_TEMPLATES1);
        lenient().when(mockEmailTemplateRepo.findByNameAndSystemRequired(any(String.class), any(Boolean.class))).thenReturn(TEST_EMAIL_TEMPLATE1);

        lenient().when(mockActionLogRepo.createPublicLog(any(Action.class), any(Submission.class), any(User.class), any(String.class))).thenReturn(TEST_ACTION_LOG1);

        lenient().when(mockFieldPredicateRepo.getById(TEST_FIELD_PREDICATE_ADVISOR_ID)).thenReturn(TEST_FIELD_PREDICATE_ADVISOR);
        lenient().when(mockFieldPredicateRepo.findByValue(TEST_FIELD_PREDICATE_ADVISOR_VALUE)).thenReturn(TEST_FIELD_PREDICATE_ADVISOR);

        lenient().when(mockFieldPredicateRepo.getById(TEST_FIELD_PREDICATE_COMMITTEE_MEMBER_ID)).thenReturn(TEST_FIELD_PREDICATE_COMMITTEE_MEMBER);
        lenient().when(mockFieldPredicateRepo.findByValue(TEST_FIELD_PREDICATE_COMMITTEE_MEMBER_VALUE)).thenReturn(TEST_FIELD_PREDICATE_COMMITTEE_MEMBER);

        lenient().when(mockSubmissionRepo.findById(mockSubmission.getId())).thenReturn(Optional.of(mockSubmission));

        lenient().when(mockAbstractEmailRecipientRepoImpl.createAdvisorRecipient()).thenReturn(TEST_EMAIL_RECIPIENT_ADVISOR);

        List<EmailWorkflowRuleByStatus> emailWorkflowRuleAdvisors = new ArrayList<EmailWorkflowRuleByStatus>();
        emailWorkflowRuleAdvisors.add(TEST_EMAIL_WORKFLOW_RULE_ADVISOR);
        lenient().when(mockEmailWorkflowRuleRepo.findByEmailRecipientAndIsDisabled(TEST_EMAIL_RECIPIENT_ADVISOR, false)).thenReturn(emailWorkflowRuleAdvisors);

        doNothing().when(mockEmailSender).send(any(MimeMessage.class));
        doNothing().when(mockEmailSender).sendEmail(any(String[].class), any(String[].class), any(String[].class), any(String.class), any(String.class));
    }

    @Test
    public void testSendAdvisorEmails() throws MessagingException {
        submissionEmailService.sendAdvisorEmails(TEST_USER, mockSubmission.getId());
        verify(mockEmailSender, never()).sendEmail(any(String[].class), any(String.class), any(String.class));
        reset(mockEmailSender);

        mockFieldValues.add(TEST_FIELD_VALUE3);
        submissionEmailService.sendAdvisorEmails(TEST_USER, mockSubmission.getId());
        verify(mockEmailSender, times(1)).sendEmail(any(String[].class), isNull(), isNull());
        reset(mockEmailSender);

        TEST_USER.setSettings(TEST_USER1_SETTINGS2);

        submissionEmailService.sendAdvisorEmails(TEST_USER, mockSubmission.getId());
        verify(mockEmailSender, times(1)).sendEmail(any(String[].class), isNull(), isNull());
        reset(mockEmailSender);

        TEST_USER.setSettings(TEST_USER1_SETTINGS3);

        submissionEmailService.sendAdvisorEmails(TEST_USER, mockSubmission.getId());
        verify(mockEmailSender, times(1)).sendEmail(any(String[].class), isNull(), isNull());
        reset(mockEmailSender);
    }

    @Test
    public void testSendAdvisorEmailsWithoutRules() throws MessagingException {
        mockEmailWorkflowRules.clear();

        submissionEmailService.sendAdvisorEmails(TEST_USER, mockSubmission.getId());
        verify(mockEmailSender, never()).sendEmail(any(String[].class), any(String.class), any(String.class));
        reset(mockEmailSender);
    }

    @Test
    public void testSendAdvisorEmailsThrowMessagingException() throws MessagingException {
        doThrow(MessagingException.class).when(mockEmailSender).sendEmail(any(String[].class), any(String.class), any(String.class));

        mockFieldValues.add(TEST_FIELD_VALUE3);

        submissionEmailService.sendAdvisorEmails(TEST_USER, mockSubmission.getId());
        verify(mockEmailSender, times(1)).sendEmail(any(String[].class), isNull(), isNull());
        reset(mockEmailSender);
    }

    @Test
    public void testSendAutomatedEmails1() throws JsonProcessingException, IOException, MessagingException {
        doTestSendAutomatedEmails(false);
    }

    @Test
    public void testSendAutomatedEmails2() throws JsonProcessingException, IOException, MessagingException {
        doTestSendAutomatedEmails(true);
    }

    @Test
    public void testSendAutomatedEmails3() throws JsonProcessingException, IOException, MessagingException {
        List<Map<String, Object>> emails = new ArrayList<Map<String, Object>>();
        emails.add(TEST_EMAIL_RECIPIENT_MAP1);

        mockData.put("commentVisibility", "public");
        mockData.put("message", "Mock Message.");
        mockData.put("recipientEmails", emails);
        mockData.put("sendEmailToRecipient", true);
        TEST_USER.setSettings(TEST_USER1_SETTINGS4);

        submissionEmailService.sendAutomatedEmails(TEST_USER, mockSubmission.getId(), mockData);
        verify(mockEmailSender, times(1)).sendEmail(any(String[].class), any(String[].class), any(String[].class), isNull(), isNull());
        reset(mockEmailSender);
    }

    @Test
    public void testSendAutomatedEmailsWithCc() throws JsonProcessingException, IOException, MessagingException {
        List<Map<String, Object>> emails = new ArrayList<Map<String, Object>>();
        emails.add(TEST_EMAIL_RECIPIENT_MAP1);

        mockData.put("commentVisibility", "public");
        mockData.put("message", "Mock Message.");
        mockData.put("recipientEmails", emails);
        mockData.put("sendEmailToRecipient", true);
        mockData.put("sendEmailToCCRecipient", true);
        mockData.put("ccRecipientEmails", emails);
        TEST_USER.setSettings(TEST_USER1_SETTINGS4);

        submissionEmailService.sendAutomatedEmails(TEST_USER, mockSubmission.getId(), mockData);
        verify(mockEmailSender, times(1)).sendEmail(any(String[].class), any(String[].class), any(String[].class), isNull(), isNull());
        reset(mockEmailSender);
    }

    @Test
    public void testSendAutomatedEmailsThrowMessagingException() throws MessagingException, JsonProcessingException, IOException {
        doThrow(MessagingException.class).when(mockEmailSender).sendEmail(any(String[].class), any(String[].class), any(String[].class), isNull(), isNull());

        List<Map<String, Object>> emails = new ArrayList<Map<String, Object>>();
        emails.add(TEST_EMAIL_RECIPIENT_MAP1);

        mockData.put("commentVisibility", "public");
        mockData.put("message", "Mock Message.");
        mockData.put("recipientEmails", emails);
        mockData.put("sendEmailToRecipient", true);
        TEST_USER.setSettings(TEST_USER1_SETTINGS4);

        submissionEmailService.sendAutomatedEmails(TEST_USER, mockSubmission.getId(), mockData);
        verify(mockEmailSender, times(1)).sendEmail(any(String[].class), any(String[].class), any(String[].class), isNull(), isNull());
        reset(mockEmailSender);
    }

    @Test
    public void testSendWorkflowEmails() throws MessagingException {
        doCallRealMethod().when(mockEmailSender).sendEmail(any(String.class), any(String.class), any(String.class));

        submissionEmailService.sendWorkflowEmails(TEST_USER, mockSubmission.getId());
        verify(mockEmailSender, times(1)).sendEmail(any(String.class), isNull(), isNull());
        reset(mockEmailSender);

        TEST_USER.setSettings(TEST_USER1_SETTINGS2);

        submissionEmailService.sendWorkflowEmails(TEST_USER, mockSubmission.getId());
        verify(mockEmailSender, times(1)).sendEmail(any(String.class), any(String[].class), isNull(), isNull());
        reset(mockEmailSender);

        TEST_USER.setSettings(TEST_USER1_SETTINGS3);

        submissionEmailService.sendWorkflowEmails(TEST_USER, mockSubmission.getId());
        verify(mockEmailSender, times(1)).sendEmail(any(String.class), any(String[].class), isNull(), isNull());
        reset(mockEmailSender);

        mockEmailWorkflowRules.clear();

        submissionEmailService.sendWorkflowEmails(TEST_USER, mockSubmission.getId());
        verify(mockEmailSender, never()).sendEmail(any(String.class), any(String[].class), any(String.class), any(String.class));
        reset(mockEmailSender);
    }

    @Test
    public void testSendWorkflowEmailsThrowMessagingException() throws MessagingException, JsonProcessingException, IOException {
        doThrow(MessagingException.class).when(mockEmailSender).sendEmail(any(String.class), any(String.class), any(String.class));

        submissionEmailService.sendWorkflowEmails(TEST_USER, mockSubmission.getId());
        verify(mockEmailSender, times(1)).sendEmail(any(String.class), isNull(), isNull());
        reset(mockEmailSender);
    }

    @ParameterizedTest
    @EnumSource(value = Action.class, names = {
        "STUDENT_MESSAGE",
        "ADVISOR_MESSAGE",
        "ADVISOR_APPROVE_SUBMISSION",
        "ADVISOR_CLEAR_APPROVE_SUBMISSION",
        "ADVISOR_APPROVE_EMBARGO",
        "ADVISOR_CLEAR_APPROVE_EMBARGO"
    })
    public void testSendActionEmailsProcessedActions(Action action) throws MessagingException {
        // Setup
        Submission mockSubmission = mock(Submission.class);
        when(mockSubmission.getOrganization()).thenReturn(mockOrganization); // Ensure organization is associated
        List<EmailWorkflowRuleByAction> mockActionRules = new ArrayList<>();
        EmailWorkflowRuleByAction mockWorkflowRule = new EmailWorkflowRuleByAction();
        mockWorkflowRule.setId(1L);
        mockWorkflowRule.setEmailTemplate(TEST_EMAIL_TEMPLATE1);
        mockWorkflowRule.setAction(action);
        mockWorkflowRule.setEmailRecipient(TEST_EMAIL_RECIPIENT_PLAIN);
        mockWorkflowRule.isDisabled(false);
        mockActionRules.add(mockWorkflowRule);

        // Setup ActionLog with the provided action
        ActionLog mockActionLog = new ActionLog();
        mockActionLog.setAction(action);

        // Mock the organization to return our workflow rules by action
        when(mockOrganization.getAggregateEmailWorkflowRulesByAction()).thenReturn(mockActionRules);

        // Mock the template utility
        when(mockTemplateUtility.compileString(any(), eq(mockSubmission))).thenReturn("Mock Subject");
        when(mockTemplateUtility.compileTemplate(any(), eq(mockSubmission))).thenReturn("Mock Content");

        // Call the service method
        submissionEmailService.sendActionEmails(mockSubmission, mockActionLog);

        // Verify the email was sent (indicating processSubmissionActionLog was called)
        verify(mockEmailSender, times(1)).sendEmail(eq(TEST_USER_EMAIL), eq("Mock Subject"), eq("Mock Content"));
        reset(mockEmailSender);
    }

    @ParameterizedTest
    @EnumSource(value = Action.class, names = { "UNDETERMINED" })
    public void testSendActionEmailsUnprocessedActions(Action action) throws MessagingException {
        // Setup
        Submission mockSubmission = mock(Submission.class);
        // Setup ActionLog with the provided action
        ActionLog mockActionLog = new ActionLog();
        mockActionLog.setAction(action);

        // Call the service method
        submissionEmailService.sendActionEmails(mockSubmission, mockActionLog);

        // Verify the email was NOT sent (indicating processSubmissionActionLog was NOT called)
        verify(mockEmailSender, never()).sendEmail(any(String.class), any(String.class), any(String.class));
        reset(mockEmailSender);
    }

    @Test
    public void testSendActionEmailsThrowMessagingException() throws MessagingException {
        // Setup
        Submission mockSubmission = mock(Submission.class);
        when(mockSubmission.getOrganization()).thenReturn(mockOrganization); // Ensure organization is associated
        doThrow(MessagingException.class).when(mockEmailSender).sendEmail(any(String.class), any(String.class), any(String.class));

        // Setup action rules
        List<EmailWorkflowRuleByAction> mockActionRules = new ArrayList<>();
        EmailWorkflowRuleByAction mockWorkflowRule = new EmailWorkflowRuleByAction();
        mockWorkflowRule.setId(1L);
        mockWorkflowRule.setEmailTemplate(TEST_EMAIL_TEMPLATE1);
        mockWorkflowRule.setAction(Action.STUDENT_MESSAGE);
        mockWorkflowRule.setEmailRecipient(TEST_EMAIL_RECIPIENT_PLAIN);
        mockWorkflowRule.isDisabled(false);
        mockActionRules.add(mockWorkflowRule);

        // Setup ActionLog
        ActionLog mockActionLog = new ActionLog();
        mockActionLog.setAction(Action.STUDENT_MESSAGE);

        // Mock the organization to return our workflow rules by action
        when(mockOrganization.getAggregateEmailWorkflowRulesByAction()).thenReturn(mockActionRules);

        // Mock the template utility
        when(mockTemplateUtility.compileString(any(), eq(mockSubmission))).thenReturn("Mock Subject");
        when(mockTemplateUtility.compileTemplate(any(), eq(mockSubmission))).thenReturn("Mock Content");

        // Call the service method - should handle the exception internally
        submissionEmailService.sendActionEmails(mockSubmission, mockActionLog);

        // Verify email sender was called but exception was handled
        verify(mockEmailSender, times(1)).sendEmail(eq(TEST_USER_EMAIL), eq("Mock Subject"), eq("Mock Content"));
        reset(mockEmailSender);
    }

    private void doTestSendAutomatedEmails(boolean cc) throws JsonProcessingException, IOException, MessagingException {
        List<Map<String, Object>> emails = new ArrayList<Map<String, Object>>();
        emails.add(TEST_EMAIL_RECIPIENT_MAP1);

        List<Map<String, Object>> ccEmails = new ArrayList<Map<String, Object>>();
        // TEST_EMAIL_RECIPIENT_MAP1 is added twice to test that it gets processed only once.
        ccEmails.add(TEST_EMAIL_RECIPIENT_MAP1);
        ccEmails.add(TEST_EMAIL_RECIPIENT_MAP1);
        ccEmails.add(TEST_EMAIL_RECIPIENT_MAP2);
        ccEmails.add(TEST_EMAIL_RECIPIENT_MAP3);
        ccEmails.add(TEST_EMAIL_RECIPIENT_MAP4);
        ccEmails.add(TEST_EMAIL_RECIPIENT_MAP5);
        ccEmails.add(TEST_EMAIL_RECIPIENT_MAP6);

        submissionEmailService.sendAutomatedEmails(TEST_USER, mockSubmission.getId(), mockData);
        verify(mockEmailSender, never()).sendEmail(any(String[].class), any(String[].class), any(String[].class), any(String.class), any(String.class));
        reset(mockEmailSender);

        mockData.put("commentVisibility", "public");
        mockData.put("message", "Mock Message.");
        mockData.put("recipientEmails", emails);
        mockData.put("ccRecipientEmails", ccEmails);

        submissionEmailService.sendAutomatedEmails(TEST_USER, mockSubmission.getId(), mockData);
        verify(mockEmailSender, never()).sendEmail(any(String[].class), any(String[].class), any(String[].class), any(String.class), any(String.class));
        reset(mockEmailSender);

        mockData.put("sendEmailToRecipient", true);
        mockData.put("sendEmailToCCRecipient", cc);

        submissionEmailService.sendAutomatedEmails(TEST_USER, mockSubmission.getId(), mockData);
        verify(mockEmailSender, times(1)).sendEmail(any(String[].class), any(String[].class), any(String[].class), isNull(), isNull());
        reset(mockEmailSender);

        mockData.put("commentVisibility", "private");
        mockData.put("sendEmailToRecipient", false);
        mockData.put("sendEmailToCCRecipient", false);

        submissionEmailService.sendAutomatedEmails(TEST_USER, mockSubmission.getId(), mockData);
        verify(mockEmailSender, never()).sendEmail(any(String[].class), any(String[].class), any(String[].class), any(String.class), any(String.class));
        reset(mockEmailSender);

        mockData.put("commentVisibility", "public");
        mockData.put("sendEmailToRecipient", true);
        mockData.put("sendEmailToCCRecipient", cc);
        TEST_USER.setSettings(TEST_USER1_SETTINGS2);

        submissionEmailService.sendAutomatedEmails(TEST_USER, mockSubmission.getId(), mockData);
        verify(mockEmailSender, times(1)).sendEmail(any(String[].class), any(String[].class), any(String[].class), isNull(), isNull());
        reset(mockEmailSender);

        TEST_USER.setSettings(TEST_USER1_SETTINGS3);

        submissionEmailService.sendAutomatedEmails(TEST_USER, mockSubmission.getId(), mockData);
        verify(mockEmailSender, times(1)).sendEmail(any(String[].class), any(String[].class), any(String[].class), isNull(), isNull());
        reset(mockEmailSender);
    }

}
