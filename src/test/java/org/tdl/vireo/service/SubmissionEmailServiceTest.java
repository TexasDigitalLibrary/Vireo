package org.tdl.vireo.service;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.tdl.vireo.Application;
import org.tdl.vireo.exception.OrganizationDoesNotAcceptSubmissionsExcception;
import org.tdl.vireo.mock.MockData;
import org.tdl.vireo.model.ActionLog;
import org.tdl.vireo.model.EmailRecipient;
import org.tdl.vireo.model.EmailRecipientPlainAddress;
import org.tdl.vireo.model.EmailTemplate;
import org.tdl.vireo.model.EmailWorkflowRule;
import org.tdl.vireo.model.InputType;
import org.tdl.vireo.model.Organization;
import org.tdl.vireo.model.OrganizationCategory;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.SubmissionState;
import org.tdl.vireo.model.SubmissionStatus;
import org.tdl.vireo.model.User;
import org.tdl.vireo.model.repo.ActionLogRepo;
import org.tdl.vireo.model.repo.EmailTemplateRepo;
import org.tdl.vireo.model.repo.InputTypeRepo;
import org.tdl.vireo.model.repo.SubmissionRepo;

import com.fasterxml.jackson.core.JsonProcessingException;

import edu.tamu.weaver.email.service.EmailSender;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { Application.class })
public class SubmissionEmailServiceTest extends MockData {
    private static final EmailRecipient TEST_EMAIL_RECIPIENT1 = new EmailRecipientPlainAddress(TEST_USER_EMAIL);
    private static final EmailRecipient TEST_EMAIL_RECIPIENT2 = new EmailRecipientPlainAddress(TEST_USER_EMAIL);

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

    private static final EmailWorkflowRule TEST_EMAIL_WORKFLOW_RULE1 = new EmailWorkflowRule();
    static {
        TEST_EMAIL_WORKFLOW_RULE1.setId(1L);
        TEST_EMAIL_WORKFLOW_RULE1.setEmailRecipient(TEST_EMAIL_RECIPIENT1);
        TEST_EMAIL_WORKFLOW_RULE1.setSubmissionStatus(TEST_SUBMISSION_STATUS1);
        TEST_EMAIL_WORKFLOW_RULE1.setEmailTemplate(TEST_EMAIL_TEMPLATE1);
    }

    private static final EmailWorkflowRule TEST_EMAIL_WORKFLOW_RULE2 = new EmailWorkflowRule();
    static {
        TEST_EMAIL_WORKFLOW_RULE2.setId(2L);
        TEST_EMAIL_WORKFLOW_RULE2.setEmailRecipient(TEST_EMAIL_RECIPIENT2);
        TEST_EMAIL_WORKFLOW_RULE2.setSubmissionStatus(TEST_SUBMISSION_STATUS2);
        TEST_EMAIL_WORKFLOW_RULE2.setEmailTemplate(TEST_EMAIL_TEMPLATE1);
    }

    private Map<String, Object> mockData;

    @MockBean
    protected ActionLogRepo mockActionLogRepo;

    @MockBean
    protected SubmissionRepo mockSubmissionRepo;

    @MockBean
    protected EmailTemplateRepo mockEmailTemplateRepo;

    @MockBean
    private InputTypeRepo mockInputTypeRepo;

    @Mock
    private EmailSender mockEmailSender;

    @Mock
    private Organization mockOrganization;

    @Mock
    private Submission mockSubmission;

    @Autowired
    private SubmissionEmailService submissionEmailService;

    @Before
    public void setup() throws OrganizationDoesNotAcceptSubmissionsExcception {
        mockData = new HashMap<>();

        List<EmailWorkflowRule> emailWorkflowRules = new ArrayList<>();
        emailWorkflowRules.add(TEST_EMAIL_WORKFLOW_RULE1);
        emailWorkflowRules.add(TEST_EMAIL_WORKFLOW_RULE2);

        when(mockOrganization.getId()).thenReturn(1L);
        when(mockOrganization.getName()).thenReturn(TEST_ORGANIZATION1_NAME);
        when(mockOrganization.getCategory()).thenReturn(TEST_ORGANIZATION_CATEGORY1);
        when(mockOrganization.getAggregateEmailWorkflowRules()).thenReturn(emailWorkflowRules);

        when(mockSubmission.getOrganization()).thenReturn(mockOrganization);
        when(mockSubmission.getSubmissionStatus()).thenReturn(TEST_SUBMISSION_STATUS1);
        when(mockSubmission.getSubmitter()).thenReturn(TEST_USER);

        when(mockInputTypeRepo.getOne(1L)).thenReturn(TEST_INPUT_TYPE1);
        when(mockInputTypeRepo.findOne(1L)).thenReturn(TEST_INPUT_TYPE1);
        when(mockInputTypeRepo.findByName(any(String.class))).thenReturn(TEST_INPUT_TYPE1);

        when(mockEmailTemplateRepo.getOne(1L)).thenReturn(TEST_EMAIL_TEMPLATE1);
        when(mockEmailTemplateRepo.findOne(1L)).thenReturn(TEST_EMAIL_TEMPLATE1);
        when(mockEmailTemplateRepo.findByName(any(String.class))).thenReturn(TEST_EMAIL_TEMPLATES1);
        when(mockEmailTemplateRepo.findByNameAndSystemRequired(any(String.class), any(Boolean.class))).thenReturn(TEST_EMAIL_TEMPLATE1);

        when(mockActionLogRepo.createPublicLog(any(Submission.class), any(User.class), any(String.class))).thenReturn(TEST_ACTION_LOG1);
    }

    @Test
    public void testSendAdvisorEmails() {
        submissionEmailService.sendAdvisorEmails(TEST_USER, mockSubmission);
    }

    @Test
    public void testSendAutomatedEmails() throws JsonProcessingException, IOException {
        submissionEmailService.sendAutomatedEmails(TEST_USER, mockSubmission, mockData);
    }

    @Test
    public void testSendWorkflowEmails() {
        submissionEmailService.sendWorkflowEmails(TEST_USER, mockSubmission);
    }

    @After
    public void cleanup() {

    }

}