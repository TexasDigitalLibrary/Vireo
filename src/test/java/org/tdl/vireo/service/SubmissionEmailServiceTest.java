package org.tdl.vireo.service;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
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

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.tdl.vireo.Application;
import org.tdl.vireo.exception.OrganizationDoesNotAcceptSubmissionsException;
import org.tdl.vireo.mock.MockData;
import org.tdl.vireo.model.ActionLog;
import org.tdl.vireo.model.EmailRecipient;
import org.tdl.vireo.model.EmailRecipientContact;
import org.tdl.vireo.model.EmailRecipientPlainAddress;
import org.tdl.vireo.model.EmailTemplate;
import org.tdl.vireo.model.EmailWorkflowRule;
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
import org.tdl.vireo.model.repo.EmailWorkflowRuleRepo;
import org.tdl.vireo.model.repo.FieldPredicateRepo;
import org.tdl.vireo.model.repo.InputTypeRepo;
import org.tdl.vireo.model.repo.SubmissionRepo;
import org.tdl.vireo.model.repo.impl.AbstractEmailRecipientRepoImpl;
import org.tdl.vireo.utility.TemplateUtility;

import com.fasterxml.jackson.core.JsonProcessingException;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
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

    private static final ActionLog TEST_ACTION_LOG1 = new ActionLog(TEST_SUBMISSION_STATUS1, TEST_CALENDAR1, "Test Action Log 1", false);

    private static final EmailWorkflowRule TEST_EMAIL_WORKFLOW_RULE_ADVISOR = new EmailWorkflowRule();
    static {
        TEST_EMAIL_WORKFLOW_RULE_ADVISOR.setId(1L);
        TEST_EMAIL_WORKFLOW_RULE_ADVISOR.setEmailRecipient(TEST_EMAIL_RECIPIENT_ADVISOR);
        TEST_EMAIL_WORKFLOW_RULE_ADVISOR.setSubmissionStatus(TEST_SUBMISSION_STATUS1);
        TEST_EMAIL_WORKFLOW_RULE_ADVISOR.setEmailTemplate(TEST_EMAIL_TEMPLATE1);
        TEST_EMAIL_WORKFLOW_RULE_ADVISOR.isDisabled(false);
    }

    private static final EmailWorkflowRule TEST_EMAIL_WORKFLOW_RULE_PLAIN = new EmailWorkflowRule();
    static {
        TEST_EMAIL_WORKFLOW_RULE_PLAIN.setId(2L);
        TEST_EMAIL_WORKFLOW_RULE_PLAIN.setEmailRecipient(TEST_EMAIL_RECIPIENT_PLAIN);
        TEST_EMAIL_WORKFLOW_RULE_PLAIN.setSubmissionStatus(TEST_SUBMISSION_STATUS2);
        TEST_EMAIL_WORKFLOW_RULE_PLAIN.setEmailTemplate(TEST_EMAIL_TEMPLATE1);
        TEST_EMAIL_WORKFLOW_RULE_PLAIN.isDisabled(false);
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

    private List<EmailWorkflowRule> mockEmailWorkflowRules;

    @MockBean
    protected AbstractEmailRecipientRepoImpl mockAbstractEmailRecipientRepoImpl;

    @MockBean
    protected EmailWorkflowRuleRepo mockEmailWorkflowRuleRepo;

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

    @Before
    public void setUp() throws OrganizationDoesNotAcceptSubmissionsException, MessagingException {
        MockitoAnnotations.initMocks(this);
        mockData = new HashMap<>();
        mockFieldValues = new ArrayList<>();
        mockEmailWorkflowRules = new ArrayList<>();

        TEST_USER.setSettings(TEST_USER1_SETTINGS1);

        mockEmailWorkflowRules.add(TEST_EMAIL_WORKFLOW_RULE_ADVISOR);
        mockEmailWorkflowRules.add(TEST_EMAIL_WORKFLOW_RULE_PLAIN);

        when(mockOrganization.getId()).thenReturn(1L);
        when(mockOrganization.getName()).thenReturn(TEST_ORGANIZATION1_NAME);
        when(mockOrganization.getCategory()).thenReturn(TEST_ORGANIZATION_CATEGORY1);
        when(mockOrganization.getAggregateEmailWorkflowRules()).thenReturn(mockEmailWorkflowRules);

        when(mockSubmission.getOrganization()).thenReturn(mockOrganization);
        when(mockSubmission.getSubmissionStatus()).thenReturn(TEST_SUBMISSION_STATUS1);
        when(mockSubmission.getSubmitter()).thenReturn(TEST_USER);

        when(mockSubmission.getFieldValuesByPredicateValue(any(String.class))).thenReturn(mockFieldValues);
        when(mockSubmission.getFieldValuesByInputType(any(InputType.class))).thenReturn(mockFieldValues);

        when(mockInputTypeRepo.getOne(1L)).thenReturn(TEST_INPUT_TYPE1);
        when(mockInputTypeRepo.findOne(1L)).thenReturn(TEST_INPUT_TYPE1);
        when(mockInputTypeRepo.findByName(any(String.class))).thenReturn(TEST_INPUT_TYPE1);

        when(mockEmailTemplateRepo.getOne(1L)).thenReturn(TEST_EMAIL_TEMPLATE1);
        when(mockEmailTemplateRepo.findOne(1L)).thenReturn(TEST_EMAIL_TEMPLATE1);
        when(mockEmailTemplateRepo.findByName(any(String.class))).thenReturn(TEST_EMAIL_TEMPLATES1);
        when(mockEmailTemplateRepo.findByNameAndSystemRequired(any(String.class), any(Boolean.class))).thenReturn(TEST_EMAIL_TEMPLATE1);

        when(mockActionLogRepo.createPublicLog(any(Submission.class), any(User.class), any(String.class))).thenReturn(TEST_ACTION_LOG1);

        when(mockFieldPredicateRepo.getOne(TEST_FIELD_PREDICATE_ADVISOR_ID)).thenReturn(TEST_FIELD_PREDICATE_ADVISOR);
        when(mockFieldPredicateRepo.findOne(TEST_FIELD_PREDICATE_ADVISOR_ID)).thenReturn(TEST_FIELD_PREDICATE_ADVISOR);
        when(mockFieldPredicateRepo.findByValue(TEST_FIELD_PREDICATE_ADVISOR_VALUE)).thenReturn(TEST_FIELD_PREDICATE_ADVISOR);

        when(mockFieldPredicateRepo.getOne(TEST_FIELD_PREDICATE_COMMITTEE_MEMBER_ID)).thenReturn(TEST_FIELD_PREDICATE_COMMITTEE_MEMBER);
        when(mockFieldPredicateRepo.findOne(TEST_FIELD_PREDICATE_COMMITTEE_MEMBER_ID)).thenReturn(TEST_FIELD_PREDICATE_COMMITTEE_MEMBER);
        when(mockFieldPredicateRepo.findByValue(TEST_FIELD_PREDICATE_COMMITTEE_MEMBER_VALUE)).thenReturn(TEST_FIELD_PREDICATE_COMMITTEE_MEMBER);

        when(mockAbstractEmailRecipientRepoImpl.createAdvisorRecipient()).thenReturn(TEST_EMAIL_RECIPIENT_ADVISOR);

        List<EmailWorkflowRule> emailWorkflowRuleAdvisors = new ArrayList<EmailWorkflowRule>();
        emailWorkflowRuleAdvisors.add(TEST_EMAIL_WORKFLOW_RULE_ADVISOR);
        when(mockEmailWorkflowRuleRepo.findByEmailRecipientAndIsDisabled(TEST_EMAIL_RECIPIENT_ADVISOR, false)).thenReturn(emailWorkflowRuleAdvisors);

        doNothing().when(mockEmailSender).send(any(MimeMessage.class));
        doNothing().when(mockEmailSender).sendEmail(any(String[].class), any(String[].class), any(String[].class), any(String.class), any(String.class));
    }

    @Test
    public void testSendAdvisorEmails() throws MessagingException {
        submissionEmailService.sendAdvisorEmails(TEST_USER, mockSubmission);
        verify(mockEmailSender, never()).sendEmail(any(String[].class), any(String.class), any(String.class));
        reset(mockEmailSender);

        mockFieldValues.add(TEST_FIELD_VALUE3);
        submissionEmailService.sendAdvisorEmails(TEST_USER, mockSubmission);
        verify(mockEmailSender, times(1)).sendEmail(any(String[].class), any(String.class), any(String.class));
        reset(mockEmailSender);

        TEST_USER.setSettings(TEST_USER1_SETTINGS2);

        submissionEmailService.sendAdvisorEmails(TEST_USER, mockSubmission);
        verify(mockEmailSender, times(1)).sendEmail(any(String[].class), any(String.class), any(String.class));
        reset(mockEmailSender);

        TEST_USER.setSettings(TEST_USER1_SETTINGS3);

        submissionEmailService.sendAdvisorEmails(TEST_USER, mockSubmission);
        verify(mockEmailSender, times(1)).sendEmail(any(String[].class), any(String.class), any(String.class));
        reset(mockEmailSender);
    }

    @Test
    public void testSendAdvisorEmailsWithoutRules() throws MessagingException {
        mockEmailWorkflowRules.clear();

        submissionEmailService.sendAdvisorEmails(TEST_USER, mockSubmission);
        verify(mockEmailSender, never()).sendEmail(any(String[].class), any(String.class), any(String.class));
        reset(mockEmailSender);
    }

    @Test
    public void testSendAdvisorEmailsThrowMessagingException() throws MessagingException {
        doThrow(MessagingException.class).when(mockEmailSender).sendEmail(any(String[].class), any(String.class), any(String.class));

        mockFieldValues.add(TEST_FIELD_VALUE3);

        submissionEmailService.sendAdvisorEmails(TEST_USER, mockSubmission);
        verify(mockEmailSender, times(1)).sendEmail(any(String[].class), any(String.class), any(String.class));
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

        submissionEmailService.sendAutomatedEmails(TEST_USER, mockSubmission, mockData);
        verify(mockEmailSender, times(1)).sendEmail(any(String[].class), any(String[].class), any(String[].class), any(String.class), any(String.class));
        reset(mockEmailSender);
    }

    @Test
    public void testSendAutomatedEmailsThrowMessagingException() throws MessagingException, JsonProcessingException, IOException {
        doThrow(MessagingException.class).when(mockEmailSender).sendEmail(any(String[].class), any(String[].class), any(String[].class), any(String.class), any(String.class));

        List<Map<String, Object>> emails = new ArrayList<Map<String, Object>>();
        emails.add(TEST_EMAIL_RECIPIENT_MAP1);

        mockData.put("commentVisibility", "public");
        mockData.put("message", "Mock Message.");
        mockData.put("recipientEmails", emails);
        mockData.put("sendEmailToRecipient", true);
        TEST_USER.setSettings(TEST_USER1_SETTINGS4);

        submissionEmailService.sendAutomatedEmails(TEST_USER, mockSubmission, mockData);
        verify(mockEmailSender, times(1)).sendEmail(any(String[].class), any(String[].class), any(String[].class), any(String.class), any(String.class));
        reset(mockEmailSender);
    }

    @Test
    public void testSendWorkflowEmails() throws MessagingException {
        doCallRealMethod().when(mockEmailSender).sendEmail(any(String.class), any(String.class), any(String.class));

        submissionEmailService.sendWorkflowEmails(TEST_USER, mockSubmission);
        verify(mockEmailSender, times(1)).sendEmail(any(String[].class), any(String[].class), any(String[].class), any(String.class), any(String.class));
        reset(mockEmailSender);

        TEST_USER.setSettings(TEST_USER1_SETTINGS2);

        submissionEmailService.sendWorkflowEmails(TEST_USER, mockSubmission);
        verify(mockEmailSender, times(1)).sendEmail(any(String.class), any(String[].class), any(String.class), any(String.class));
        reset(mockEmailSender);

        TEST_USER.setSettings(TEST_USER1_SETTINGS3);

        submissionEmailService.sendWorkflowEmails(TEST_USER, mockSubmission);
        verify(mockEmailSender, times(1)).sendEmail(any(String.class), any(String[].class), any(String.class), any(String.class));
        reset(mockEmailSender);

        mockEmailWorkflowRules.clear();

        submissionEmailService.sendWorkflowEmails(TEST_USER, mockSubmission);
        verify(mockEmailSender, never()).sendEmail(any(String.class), any(String[].class), any(String.class), any(String.class));
        reset(mockEmailSender);
    }

    @Test
    public void testSendWorkflowEmailsThrowMessagingException() throws MessagingException, JsonProcessingException, IOException {
        doThrow(MessagingException.class).when(mockEmailSender).sendEmail(any(String.class), any(String.class), any(String.class));

        submissionEmailService.sendWorkflowEmails(TEST_USER, mockSubmission);
        verify(mockEmailSender, times(1)).sendEmail(any(String.class), any(String.class), any(String.class));
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

        submissionEmailService.sendAutomatedEmails(TEST_USER, mockSubmission, mockData);
        verify(mockEmailSender, never()).sendEmail(any(String[].class), any(String[].class), any(String[].class), any(String.class), any(String.class));
        reset(mockEmailSender);

        mockData.put("commentVisibility", "public");
        mockData.put("message", "Mock Message.");
        mockData.put("recipientEmails", emails);
        mockData.put("ccRecipientEmails", ccEmails);

        submissionEmailService.sendAutomatedEmails(TEST_USER, mockSubmission, mockData);
        verify(mockEmailSender, never()).sendEmail(any(String[].class), any(String[].class), any(String[].class), any(String.class), any(String.class));
        reset(mockEmailSender);

        mockData.put("sendEmailToRecipient", true);
        mockData.put("sendEmailToCCRecipient", cc);

        submissionEmailService.sendAutomatedEmails(TEST_USER, mockSubmission, mockData);
        verify(mockEmailSender, times(1)).sendEmail(any(String[].class), any(String[].class), any(String[].class), any(String.class), any(String.class));
        reset(mockEmailSender);

        mockData.put("commentVisibility", "private");
        mockData.put("sendEmailToRecipient", false);
        mockData.put("sendEmailToCCRecipient", false);

        submissionEmailService.sendAutomatedEmails(TEST_USER, mockSubmission, mockData);
        verify(mockEmailSender, never()).sendEmail(any(String[].class), any(String[].class), any(String[].class), any(String.class), any(String.class));
        reset(mockEmailSender);

        mockData.put("commentVisibility", "public");
        mockData.put("sendEmailToRecipient", true);
        mockData.put("sendEmailToCCRecipient", cc);
        TEST_USER.setSettings(TEST_USER1_SETTINGS2);

        submissionEmailService.sendAutomatedEmails(TEST_USER, mockSubmission, mockData);
        verify(mockEmailSender, times(1)).sendEmail(any(String[].class), any(String[].class), any(String[].class), any(String.class), any(String.class));
        reset(mockEmailSender);

        TEST_USER.setSettings(TEST_USER1_SETTINGS3);

        submissionEmailService.sendAutomatedEmails(TEST_USER, mockSubmission, mockData);
        verify(mockEmailSender, times(1)).sendEmail(any(String[].class), any(String[].class), any(String[].class), any(String.class), any(String.class));
        reset(mockEmailSender);
    }

}
