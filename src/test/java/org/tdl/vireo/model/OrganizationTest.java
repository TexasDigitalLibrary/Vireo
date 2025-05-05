package org.tdl.vireo.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.Arguments;
import org.mockito.InjectMocks;
import org.springframework.test.util.ReflectionTestUtils;

public class OrganizationTest extends AbstractModelCustomMethodTest<Organization> {

    @InjectMocks
    private Organization organization;

    @Test
    public void testAddEmail() {
        List<String> emails = new ArrayList<>();
        String email1 = "email1";
        String email2 = "email2";

        emails.add(email1);

        ReflectionTestUtils.setField(organization, "emails", emails);

        organization.addEmail(email2);

        assertTrue(emails.contains(email2), "E-mail 2 is not found.");
    }

    @Test
    public void testRemoveEmail() {
        List<String> emails = new ArrayList<>();
        String email1 = "email1";
        String email2 = "email2";

        emails.add(email1);
        emails.add(email2);

        ReflectionTestUtils.setField(organization, "emails", emails);

        organization.removeEmail(email2);

        assertFalse(emails.contains(email2), "E-mail 2 is found.");
    }

    @Test
    public void testReplaceOriginalWorkflowStep() {
        List<WorkflowStep> originalWorkflowSteps = new ArrayList<>();
        List<WorkflowStep> aggregateWorkflowSteps = new ArrayList<>();
        WorkflowStep workflowStep1 = new WorkflowStep();
        WorkflowStep workflowStep2 = new WorkflowStep();
        WorkflowStep workflowStep3 = new WorkflowStep();

        workflowStep1.setId(1L);
        workflowStep2.setId(2L);
        workflowStep3.setId(3L);
        originalWorkflowSteps.add(workflowStep1);
        originalWorkflowSteps.add(workflowStep2);
        aggregateWorkflowSteps.add(workflowStep1);
        aggregateWorkflowSteps.add(workflowStep2);

        ReflectionTestUtils.setField(organization, "originalWorkflowSteps", originalWorkflowSteps);
        ReflectionTestUtils.setField(organization, "aggregateWorkflowSteps", aggregateWorkflowSteps);

        organization.replaceOriginalWorkflowStep(workflowStep1, workflowStep3);

        assertTrue(originalWorkflowSteps.contains(workflowStep3), "Workflow Step 3 is not found in Original Workflow Steps array.");
        assertFalse(originalWorkflowSteps.contains(workflowStep1), "Workflow Step 1 is found in Original Workflow Steps array.");

        assertTrue(aggregateWorkflowSteps.contains(workflowStep3), "Workflow Step 3 is not found in Aggregate Workflow Steps array.");
        assertFalse(aggregateWorkflowSteps.contains(workflowStep1), "Workflow Step 1 is found in Aggregate Workflow Steps array.");
    }

    @Test
    public void testGetAggregateEmailWorkflowRules() {
        List<EmailWorkflowRuleByStatus> emailWorkflowRules = new ArrayList<>();
        List<EmailWorkflowRuleByStatus> parentEmailWorkflowRules = new ArrayList<>();
        EmailWorkflowRuleByStatus emailWorkflowRule1 = new EmailWorkflowRuleByStatus();
        EmailWorkflowRuleByStatus emailWorkflowRule2 = new EmailWorkflowRuleByStatus();
        EmailWorkflowRuleByStatus emailWorkflowRule3 = new EmailWorkflowRuleByStatus();
        EmailRecipient emailRecipient1 = new EmailRecipientPlainAddress("email@Recipient1.nowhere");
        EmailRecipient emailRecipient2 = new EmailRecipientPlainAddress("email@Recipient2.nowhere");
        EmailRecipient emailRecipient3 = new EmailRecipientPlainAddress("email@Recipient3.nowhere");
        EmailTemplate emailTemplate1 = new EmailTemplate();
        EmailTemplate emailTemplate2 = new EmailTemplate();
        EmailTemplate emailTemplate3 = new EmailTemplate();
        Organization parentOrganization = new Organization();

        parentOrganization.setId(1L);
        emailTemplate1.setId(1L);
        emailTemplate2.setId(2L);
        emailTemplate3.setId(3L);
        emailTemplate1.setName("name1");
        emailTemplate2.setName("name2");
        emailTemplate3.setName("name3");
        emailWorkflowRule1.setId(1L);
        emailWorkflowRule2.setId(2L);
        emailWorkflowRule3.setId(3L);
        emailWorkflowRule1.setEmailRecipient(emailRecipient1);
        emailWorkflowRule2.setEmailRecipient(emailRecipient2);
        emailWorkflowRule3.setEmailRecipient(emailRecipient3);
        emailWorkflowRule1.setEmailTemplate(emailTemplate1);
        emailWorkflowRule2.setEmailTemplate(emailTemplate2);
        emailWorkflowRule3.setEmailTemplate(emailTemplate3);
        emailWorkflowRules.add(emailWorkflowRule1);
        emailWorkflowRules.add(emailWorkflowRule2);
        parentEmailWorkflowRules.add(emailWorkflowRule2);
        parentEmailWorkflowRules.add(emailWorkflowRule3);
        parentOrganization.setEmailWorkflowRules(parentEmailWorkflowRules);

        ReflectionTestUtils.setField(organization, "id", 2L);
        ReflectionTestUtils.setField(organization, "emailWorkflowRules", emailWorkflowRules);
        ReflectionTestUtils.setField(organization, "parentOrganization", parentOrganization);

        List<EmailWorkflowRuleByStatus> got = organization.getAggregateEmailWorkflowRules();

        assertTrue(got.contains(emailWorkflowRule1), "E-mail Workflow Rule 1 is not found in Aggregate E-mail Workflow Rules array.");
        assertTrue(got.contains(emailWorkflowRule2), "E-mail Workflow Rule 2 is not found in Aggregate E-mail Workflow Rules array.");
        assertTrue(got.contains(emailWorkflowRule3), "E-mail Workflow Rule 3 is not found in Aggregate E-mail Workflow Rules array.");
    }

    @Test
    public void testGetAggregateEmailWorkflowRulesWithoutParentOrganization() {
        List<EmailWorkflowRuleByStatus> emailWorkflowRules = new ArrayList<>();
        EmailWorkflowRuleByStatus emailWorkflowRule1 = new EmailWorkflowRuleByStatus();
        EmailWorkflowRuleByStatus emailWorkflowRule2 = new EmailWorkflowRuleByStatus();

        emailWorkflowRule1.setId(1L);
        emailWorkflowRule2.setId(2L);
        emailWorkflowRules.add(emailWorkflowRule1);
        emailWorkflowRules.add(emailWorkflowRule2);

        ReflectionTestUtils.setField(organization, "id", 2L);
        ReflectionTestUtils.setField(organization, "emailWorkflowRules", emailWorkflowRules);

        List<EmailWorkflowRuleByStatus> got = organization.getAggregateEmailWorkflowRules();

        assertTrue(got.contains(emailWorkflowRule1), "E-mail Workflow Rule 1 is not found in Aggregate E-mail Workflow Rules array.");
        assertTrue(got.contains(emailWorkflowRule2), "E-mail Workflow Rule 2 is not found in Aggregate E-mail Workflow Rules array.");
    }

    @Test
    public void testGetAncestorOrganizations() {
        Organization parentOrganization = new Organization();

        parentOrganization.setId(1L);

        ReflectionTestUtils.setField(organization, "id", 2L);
        ReflectionTestUtils.setField(organization, "parentOrganization", parentOrganization);

        assertEquals(1, organization.getAncestorOrganizations().size(), "Ancestor Organizations array has wrong length.");
    }

    @Test
    public void testGetAncestorOrganizationsParentOrganization() {
        assertTrue(organization.getAncestorOrganizations().isEmpty(), "Ancestor Organizations array is not empty.");
    }

    @Test
    public void testGetAncestorOrganizationsParentOrganizationIsSelf() {
        ReflectionTestUtils.setField(organization, "parentOrganization", organization);

        assertTrue(organization.getAncestorOrganizations().isEmpty(), "Ancestor Organizations array is not empty.");
    }

    @Override
    protected Organization getInstance() {
        return organization;
    }

    protected static Stream<Arguments> provideGetterParameters() {
        return getParameterStream();
    }

    protected static Stream<Arguments> provideSetterParameters() {
        return getParameterStream();
    }

    protected static Stream<Arguments> provideGetterMethodParameters() {
        return Stream.of(
            Arguments.of("getAcceptsSubmissions", "acceptsSubmissions", true),
            Arguments.of("getAcceptsSubmissions", "acceptsSubmissions", false)
        );
    }

    protected static Stream<Arguments> provideSetterMethodParameters() {
        return Stream.of(
            Arguments.of("setAcceptsSubmissions", "acceptsSubmissions", true),
            Arguments.of("setAcceptsSubmissions", "acceptsSubmissions", false)
        );
    }

    private static Stream<Arguments> getParameterStream() {
        List<SubmissionWorkflowStep> originalWorkflowSteps = new ArrayList<>();
        List<SubmissionWorkflowStep> aggregateWorkflowSteps = new ArrayList<>();
        List<String> emails = new ArrayList<>();
        List<EmailWorkflowRuleByStatus> emailWorkflowRules = new ArrayList<>();
        Set<Organization> childrenOrganizations = new HashSet<>();

        return Stream.of(
            Arguments.of("name", "value"),
            Arguments.of("category", new OrganizationCategory()),
            Arguments.of("originalWorkflowSteps", originalWorkflowSteps),
            Arguments.of("aggregateWorkflowSteps", aggregateWorkflowSteps),
            Arguments.of("parentOrganization", new Organization()),
            Arguments.of("childrenOrganizations", childrenOrganizations),
            Arguments.of("emails", emails),
            Arguments.of("emailWorkflowRules", emailWorkflowRules)
        );
    }

}
