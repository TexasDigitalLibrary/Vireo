package org.tdl.vireo.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import edu.tamu.weaver.response.ApiResponse;
import edu.tamu.weaver.response.ApiStatus;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.ActiveProfiles;
import org.tdl.vireo.exception.ComponentNotPresentOnOrgException;
import org.tdl.vireo.exception.SystemEmailRuleNotDeleteableException;
import org.tdl.vireo.exception.WorkflowStepNonOverrideableException;
import org.tdl.vireo.model.EmailRecipient;
import org.tdl.vireo.model.EmailRecipientAssignee;
import org.tdl.vireo.model.EmailRecipientContact;
import org.tdl.vireo.model.EmailRecipientOrganization;
import org.tdl.vireo.model.EmailRecipientSubmitter;
import org.tdl.vireo.model.EmailTemplate;
import org.tdl.vireo.model.EmailWorkflowRuleByStatus;
import org.tdl.vireo.model.FieldPredicate;
import org.tdl.vireo.model.Organization;
import org.tdl.vireo.model.OrganizationCategory;
import org.tdl.vireo.model.SubmissionState;
import org.tdl.vireo.model.SubmissionStatus;
import org.tdl.vireo.model.WorkflowStep;
import org.tdl.vireo.model.repo.AbstractEmailRecipientRepo;
import org.tdl.vireo.model.repo.EmailTemplateRepo;
import org.tdl.vireo.model.repo.EmailWorkflowRuleRepo;
import org.tdl.vireo.model.repo.FieldPredicateRepo;
import org.tdl.vireo.model.repo.OrganizationRepo;
import org.tdl.vireo.model.repo.SubmissionRepo;
import org.tdl.vireo.model.repo.SubmissionStatusRepo;
import org.tdl.vireo.model.repo.WorkflowStepRepo;

@ActiveProfiles(value = { "test", "isolated-test" })
public class OrganizationControllerTest extends AbstractControllerTest {

    @Mock
    private AbstractEmailRecipientRepo abstractEmailRecipientRepo;

    @Mock
    private EmailTemplateRepo emailTemplateRepo;

    @Mock
    private EmailWorkflowRuleRepo emailWorkflowRuleRepo;

    @Mock
    private FieldPredicateRepo fieldPredicateRepo;

    @Mock
    private OrganizationRepo organizationRepo;

    @Mock
    private SubmissionRepo submissionRepo;

    @Mock
    private SubmissionStatusRepo submissionStatusRepo;

    @Mock
    private WorkflowStepRepo workflowStepRepo;

    @InjectMocks
    private OrganizationController organizationController;
    private EmailTemplate emailTemplate1;
    private EmailTemplate emailTemplate2;

    private EmailWorkflowRuleByStatus emailWorkflowRule1;

    private Organization organization1;
    private Organization organization2;

    private OrganizationCategory organizationCategory1;
    private OrganizationCategory organizationCategory2;

    private SubmissionStatus submissionStatus1;
    private SubmissionStatus submissionStatus2;

    private WorkflowStep workflowStep1;
    private WorkflowStep workflowStep2;

    private List<Organization> organizations;
    private List<WorkflowStep> workflowSteps;
    private List<EmailWorkflowRuleByStatus> emailWorkflowRules;

    @BeforeEach
    public void setup() {
        emailTemplate1 = new EmailTemplate("name1", "subject1", "message1");
        emailTemplate2 = new EmailTemplate("name2", "subject2", "message2");
        emailWorkflowRule1 = new EmailWorkflowRuleByStatus();
        organizationCategory1 = new OrganizationCategory("1");
        organizationCategory2 = new OrganizationCategory("2");
        organization1 = new Organization("Organization 1", organizationCategory1);
        organization2 = new Organization("Organization 2", organizationCategory2);
        submissionStatus1 = new SubmissionStatus("name1", false, false, false, false, false, true, SubmissionState.IN_PROGRESS);
        submissionStatus2 = new SubmissionStatus("name1", false, true, true, true, true, true, SubmissionState.APPROVED);
        workflowStep1 = new WorkflowStep("WorkflowStep 1");
        workflowStep2 = new WorkflowStep("WorkflowStep 2");

        emailTemplate1.setId(1L);
        emailTemplate2.setId(2L);
        emailWorkflowRule1.setId(1L);
        organizationCategory1.setId(1L);
        organizationCategory2.setId(2L);
        organization1.setId(1L);
        organization2.setId(2L);
        submissionStatus1.setId(1L);
        submissionStatus2.setId(2L);
        workflowStep1.setId(1L);
        workflowStep2.setId(2L);

        organizations = new ArrayList<>(Arrays.asList(organization1));
        workflowSteps = new ArrayList<>(Arrays.asList(workflowStep1));
        emailWorkflowRules = new ArrayList<>(Arrays.asList(emailWorkflowRule1));
    }

    @Test
    public void testAllOrganizations() {
        when(organizationRepo.findAllByOrderByIdAsc()).thenReturn(organizations);

        ApiResponse response = organizationController.allOrganizations();
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        List<?> got = (ArrayList<?>) response.getPayload().get("ArrayList<Organization>");
        assertEquals(organizations.size(), got.size());
    }

    @Test
    public void testAllSpecificOrganizationsSpecificTree() {
        when(organizationRepo.findViewAllByOrderByIdAsc(Mockito.<Class<Organization>>any())).thenReturn(organizations);

        ApiResponse response = organizationController.getSpecificAllOrganizations("tree");
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        List<?> got = (ArrayList<?>) response.getPayload().get("ArrayList<Organization>");
        assertEquals(organizations.size(), got.size());
    }

    @Test
    public void testAllSpecificOrganizationsSpecificShallow() {
        when(organizationRepo.findViewAllByOrderByIdAsc(Mockito.<Class<Organization>>any())).thenReturn(organizations);

        ApiResponse response = organizationController.getSpecificAllOrganizations("shallow");
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        List<?> got = (ArrayList<?>) response.getPayload().get("ArrayList<Organization>");
        assertEquals(organizations.size(), got.size());
    }

    @Test
    public void testAllSpecificOrganizationsInvalidSpecific() {
        ApiResponse response = organizationController.getSpecificAllOrganizations("unknown and not valid");
        assertEquals(ApiStatus.INVALID, response.getMeta().getStatus());
    }

    @Test
    public void testGetOrganization() {
        when(organizationRepo.read(any(Long.class))).thenReturn(organization1);

        ApiResponse response = organizationController.getOrganization(organization1.getId());
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        Organization got = (Organization) response.getPayload().get("Organization");
        assertEquals(organization1, got);
    }

    @Test
    public void testCreateOrganization() {
        when(organizationRepo.read(any(Long.class))).thenReturn(organization1);
        when(organizationRepo.create(any(String.class), any(Organization.class), any(OrganizationCategory.class))).thenReturn(organization2);

        ApiResponse response = organizationController.createOrganization(0L, organization1);
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        Organization got = (Organization) response.getPayload().get("Organization");
        assertEquals(organization2, got);
    }

    @Test
    public void testUpdateOrganization() {
        when(organizationRepo.read(any(Long.class))).thenReturn(organization2);
        when(organizationRepo.update(any(Organization.class))).thenReturn(organization2);

        ApiResponse response = organizationController.updateOrganization(organization1);
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        Organization got = (Organization) response.getPayload().get("Organization");
        assertEquals(organization2, got);
    }

    @Test
    public void testDeleteOrganization() {
        when(organizationRepo.read(any(Long.class))).thenReturn(organization1);
        doNothing().when(organizationRepo).delete(any(Organization.class));

        ApiResponse response = organizationController.deleteOrganization(organization1);
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        verify(organizationRepo).delete(any(Organization.class));
    }

    @Test
    public void testDeleteOrganizationById() {
        when(organizationRepo.findById(any(Long.class))).thenReturn(Optional.of(organization1));
        doNothing().when(organizationRepo).delete(any(Organization.class));

        ApiResponse response = organizationController.deleteOrganizationById(organization1.getId());
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        verify(organizationRepo).delete(any(Organization.class));
    }

    @Test
    public void testDeleteOrganizationByIdWithUnknownOrganization() {
        when(organizationRepo.findById(any(Long.class))).thenReturn(Optional.empty());

        ApiResponse response = organizationController.deleteOrganizationById(organization1.getId());
        assertEquals(ApiStatus.ERROR, response.getMeta().getStatus());

        verify(organizationRepo, never()).delete(any(Organization.class));
    }

    @Test
    public void testRestoreOrganizationDefaults() {
        when(organizationRepo.read(any(Long.class))).thenReturn(organization1);
        when(organizationRepo.restoreDefaults(any(Organization.class))).thenReturn(organization2);

        ApiResponse response = organizationController.restoreOrganizationDefaults(organization1);
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());
    }

    @Test
    public void testGetWorkflowStepsForOrganization() {
        organization1.setAggregateWorkflowSteps(workflowSteps);

        when(organizationRepo.read(any(Long.class))).thenReturn(organization1);

        ApiResponse response = organizationController.getWorkflowStepsForOrganization(organization1.getId());
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());
    }

    @Test
    public void testCreateWorkflowStepsForOrganization() {
        when(organizationRepo.read(any(Long.class))).thenReturn(organization1);
        when(workflowStepRepo.create(anyString(), any(Organization.class))).thenReturn(workflowStep2);

        ApiResponse response = organizationController.createWorkflowStepsForOrganization(organization1.getId(), workflowStep1);
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        WorkflowStep got = (WorkflowStep) response.getPayload().get("WorkflowStep");
        assertEquals(workflowStep2, got);
    }

    @Test
    public void testUpdateWorkflowStepsForOrganization() throws WorkflowStepNonOverrideableException, ComponentNotPresentOnOrgException {
        when(organizationRepo.read(any(Long.class))).thenReturn(organization1);
        when(workflowStepRepo.update(any(WorkflowStep.class), any(Organization.class))).thenReturn(workflowStep2);

        ApiResponse response = organizationController.updateWorkflowStepsForOrganization(organization1.getId(), workflowStep1);
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());
    }

    @Test
    public void testDeleteWorkflowStep() {
        when(organizationRepo.read(any(Long.class))).thenReturn(organization1);
        when(workflowStepRepo.findById(any(Long.class))).thenReturn(Optional.of(workflowStep2));
        doNothing().when(workflowStepRepo).removeFromOrganization(any(Organization.class), any(WorkflowStep.class));

        ApiResponse response = organizationController.deleteWorkflowStep(organization1.getId(), workflowStep1);
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());
    }

    @Test
    public void testShiftWorkflowStepUp() {
        organization1.setAggregateWorkflowSteps(workflowSteps);
        workflowSteps.clear();
        workflowSteps.add(workflowStep1);
        workflowSteps.add(workflowStep2);

        when(organizationRepo.read(any(Long.class))).thenReturn(organization1);
        when(workflowStepRepo.findById(any(Long.class))).thenReturn(Optional.of(workflowStep2));
        when(organizationRepo.reorderWorkflowSteps(any(Organization.class), any(WorkflowStep.class), any(WorkflowStep.class))).thenReturn(organization1);

        ApiResponse response = organizationController.shiftWorkflowStepUp(organization1.getId(), workflowStep1.getId());
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());
    }

    @Test
    public void testShiftWorkflowStepUpWhenWorkflowIsNotFound() {
        organization1.setAggregateWorkflowSteps(workflowSteps);
        workflowSteps.clear();
        workflowSteps.add(workflowStep1);

        when(organizationRepo.read(any(Long.class))).thenReturn(organization1);
        when(workflowStepRepo.findById(any(Long.class))).thenReturn(Optional.of(workflowStep2));

        ApiResponse response = organizationController.shiftWorkflowStepUp(organization1.getId(), workflowStep1.getId());
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());
    }

    @Test
    public void testShiftWorkflowStepUpWhenWorkflowIsAtTop() {
        organization1.setAggregateWorkflowSteps(workflowSteps);
        workflowSteps.clear();
        workflowSteps.add(workflowStep2);
        workflowSteps.add(workflowStep1);

        when(organizationRepo.read(any(Long.class))).thenReturn(organization1);
        when(workflowStepRepo.findById(any(Long.class))).thenReturn(Optional.of(workflowStep2));

        ApiResponse response = organizationController.shiftWorkflowStepUp(organization1.getId(), workflowStep1.getId());
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());
    }

    @Test
    public void testShiftWorkflowStepDown() {
        organization1.setAggregateWorkflowSteps(workflowSteps);
        workflowSteps.clear();
        workflowSteps.add(workflowStep2);
        workflowSteps.add(workflowStep1);

        when(organizationRepo.read(any(Long.class))).thenReturn(organization1);
        when(workflowStepRepo.findById(any(Long.class))).thenReturn(Optional.of(workflowStep2));
        when(organizationRepo.reorderWorkflowSteps(any(Organization.class), any(WorkflowStep.class), any(WorkflowStep.class))).thenReturn(organization1);

        ApiResponse response = organizationController.shiftWorkflowStepDown(organization1.getId(), workflowStep1.getId());
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());
    }

    @Test
    public void testShiftWorkflowStepDownWhenWorkflowIsNotFound() {
        organization1.setAggregateWorkflowSteps(workflowSteps);
        workflowSteps.clear();
        workflowSteps.add(workflowStep1);

        when(organizationRepo.read(any(Long.class))).thenReturn(organization1);
        when(workflowStepRepo.findById(any(Long.class))).thenReturn(Optional.of(workflowStep2));

        ApiResponse response = organizationController.shiftWorkflowStepDown(organization1.getId(), workflowStep1.getId());
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());
    }

    @Test
    public void testShiftWorkflowStepDownWhenWorkflowIsAtBOttom() {
        organization1.setAggregateWorkflowSteps(workflowSteps);
        workflowSteps.clear();
        workflowSteps.add(workflowStep1);
        workflowSteps.add(workflowStep2);

        when(organizationRepo.read(any(Long.class))).thenReturn(organization1);
        when(workflowStepRepo.findById(any(Long.class))).thenReturn(Optional.of(workflowStep2));

        ApiResponse response = organizationController.shiftWorkflowStepDown(organization1.getId(), workflowStep1.getId());
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());
    }

    @ParameterizedTest
    @MethodSource("provideEmailRecipientData")
    public void testAddEmailWorkflowRule(String type, EmailRecipient emailRecipient) {
        Map<String, Object> data = setupEmailWorkflowData(type, emailRecipient);

        when(organizationRepo.read(any(Long.class))).thenReturn(organization1);
        when(submissionStatusRepo.findById(any(Long.class))).thenReturn(Optional.of(submissionStatus1));

        if (emailRecipient != null) {
            when(emailWorkflowRuleRepo.create(any(SubmissionStatus.class), any(EmailRecipient.class), any(EmailTemplate.class))).thenReturn(emailWorkflowRule1);
            when(organizationRepo.update(any(Organization.class))).thenReturn(organization1);
        }

        ApiResponse response = organizationController.addEmailWorkflowRule(organization1.getId(), data);

        if (emailRecipient == null) {
            assertEquals(ApiStatus.ERROR, response.getMeta().getStatus());
        } else {
            assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());
        }
    }

    @ParameterizedTest
    @MethodSource("provideEmailRecipientData")
    public void testUpdateEmailWorkflowRule(String type, EmailRecipient emailRecipient) {
        Map<String, Object> data = setupEmailWorkflowData(type, emailRecipient);

        when(emailWorkflowRuleRepo.findById(any(Long.class))).thenReturn(Optional.of(emailWorkflowRule1));

        if (emailRecipient != null) {
            when(emailWorkflowRuleRepo.save(any(EmailWorkflowRuleByStatus.class))).thenReturn(emailWorkflowRule1);
            doNothing().when(organizationRepo).broadcast(any(Long.class));
        }

        ApiResponse response = organizationController.editEmailWorkflowRule(organization1.getId(), emailWorkflowRule1.getId(), data);

        if (emailRecipient == null) {
            assertEquals(ApiStatus.ERROR, response.getMeta().getStatus());
        } else {
            assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());
        }
    }

    @Test
    public void testRemoveEmailWorkflowRule() throws SystemEmailRuleNotDeleteableException {
        emailWorkflowRule1.isSystem(false);
        organization1.setEmailWorkflowRules(emailWorkflowRules);

        when(organizationRepo.read(any(Long.class))).thenReturn(organization1);
        when(emailWorkflowRuleRepo.findById(any(Long.class))).thenReturn(Optional.of(emailWorkflowRule1));
        doNothing().when(emailWorkflowRuleRepo).delete(any(EmailWorkflowRuleByStatus.class));
        when(organizationRepo.update(any(Organization.class))).thenReturn(organization1);

        ApiResponse response = organizationController.removeEmailWorkflowRule(organization1.getId(), emailWorkflowRule1.getId());

        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());
    }

    @Test
    public void testRemoveEmailWorkflowRuleWhenIsSystem() {
        emailWorkflowRule1.isSystem(true);

        when(organizationRepo.read(any(Long.class))).thenReturn(organization1);
        when(emailWorkflowRuleRepo.findById(any(Long.class))).thenReturn(Optional.of(emailWorkflowRule1));

        Assertions.assertThrows(SystemEmailRuleNotDeleteableException.class, () -> {
            organizationController.removeEmailWorkflowRule(organization1.getId(), emailWorkflowRule1.getId());
        });
    }

    @Test
    public void testChangeEmailWorkflowRuleActivationWhenIsDisabled() {
        boolean isDisabled = true;

        emailWorkflowRule1.isDisabled(isDisabled);
        organization1.setEmailWorkflowRules(emailWorkflowRules);

        when(emailWorkflowRuleRepo.findById(any(Long.class))).thenReturn(Optional.of(emailWorkflowRule1));
        when(emailWorkflowRuleRepo.save(any(EmailWorkflowRuleByStatus.class))).thenReturn(emailWorkflowRule1);
        doNothing().when(organizationRepo).broadcast(any(Long.class));

        ApiResponse response = organizationController.changeEmailWorkflowRuleActivation(organization1.getId(), emailWorkflowRule1.getId());

        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());
        assertEquals(!isDisabled, emailWorkflowRule1.isDisabled());
    }

    @Test
    public void testChangeEmailWorkflowRuleActivationWhenNotIsDisabled() {
        boolean isDisabled = false;

        emailWorkflowRule1.isDisabled(isDisabled);
        organization1.setEmailWorkflowRules(emailWorkflowRules);

        when(emailWorkflowRuleRepo.findById(any(Long.class))).thenReturn(Optional.of(emailWorkflowRule1));
        when(emailWorkflowRuleRepo.save(any(EmailWorkflowRuleByStatus.class))).thenReturn(emailWorkflowRule1);
        doNothing().when(organizationRepo).broadcast(any(Long.class));

        ApiResponse response = organizationController.changeEmailWorkflowRuleActivation(organization1.getId(), emailWorkflowRule1.getId());

        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());
        assertEquals(!isDisabled, emailWorkflowRule1.isDisabled());
    }

    @Test
    public void testCountSubmissions() {
        Long count = 1L;

        when(submissionRepo.countByOrganizationId(any(Long.class))).thenReturn(count);

        ApiResponse response = organizationController.countSubmissions(organization1.getId());
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        Long got = (Long) response.getPayload().get("Long");
        assertEquals(1L, got);
    }

    private Map<String, Object> setupEmailWorkflowData(String type, EmailRecipient emailRecipient) {
        Map<String, Object> recipient = new HashMap<>();
        recipient.put("type", type);
        recipient.put("name", "name");
        recipient.put("data", "1");

        Map<String, Object> data = new HashMap<>();
        data.put("submissionStatusId", Integer.valueOf(1)); // Warning: the code does this: "Long.valueOf((Integer)...", which is not very safe.
        data.put("recipient", recipient);
        data.put("templateId", Integer.valueOf(1)); // Warning: the code does this: "Long.valueOf((Integer)...", which is not very safe.

        when(emailTemplateRepo.findById(any(Long.class))).thenReturn(Optional.of(emailTemplate1));

        lenient().when(abstractEmailRecipientRepo.createSubmitterRecipient()).thenReturn(emailRecipient);
        lenient().when(abstractEmailRecipientRepo.createAssigneeRecipient()).thenReturn(emailRecipient);
        lenient().when(abstractEmailRecipientRepo.createContactRecipient(anyString(), any(FieldPredicate.class))).thenReturn(emailRecipient);
        lenient().when(abstractEmailRecipientRepo.createOrganizationRecipient(any(Organization.class))).thenReturn(emailRecipient);

        if (emailRecipient instanceof EmailRecipientContact) {
            FieldPredicate fieldPredicate = ((EmailRecipientContact) emailRecipient).getFieldPredicate();

            lenient().when(fieldPredicateRepo.findByValue(any(String.class))).thenReturn(fieldPredicate);
            lenient().when(fieldPredicateRepo.findById(any(Long.class))).thenReturn(Optional.of(fieldPredicate));
        }

        if (emailRecipient instanceof EmailRecipientOrganization) {
            when(organizationRepo.read(any(Long.class))).thenReturn(organization1);
        }

        return data;
    }

    private static Stream<Arguments> provideEmailRecipientData() {
        FieldPredicate fieldPredicate1 = new FieldPredicate("value1", false);
        OrganizationCategory organizationCategory1 = new OrganizationCategory("1");
        Organization organization1 = new Organization("Organization 1", organizationCategory1);

        EmailRecipientAssignee emailRecipientAssignee = new EmailRecipientAssignee();
        EmailRecipientContact emailRecipientContact = new EmailRecipientContact("contact", fieldPredicate1);
        EmailRecipientContact emailRecipientAdvisor = new EmailRecipientContact("advisor", fieldPredicate1);
        EmailRecipientOrganization emailRecipientOrganization = new EmailRecipientOrganization();
        EmailRecipientSubmitter emailRecipientSubmitter = new EmailRecipientSubmitter();

        fieldPredicate1.setId(1L);
        emailRecipientAssignee.setId(1L);
        emailRecipientContact.setId(2L);
        emailRecipientAdvisor.setId(3L);
        emailRecipientOrganization.setId(4L);
        emailRecipientSubmitter.setId(5L);
        organizationCategory1.setId(1L);
        organization1.setId(1L);

        return Stream.of(
            Arguments.of("SUBMITTER", emailRecipientSubmitter),
            Arguments.of("ASSIGNEE", emailRecipientAssignee),
            Arguments.of("ADVISOR", emailRecipientAdvisor),
            Arguments.of("ORGANIZATION", emailRecipientOrganization),
            Arguments.of("CONTACT", emailRecipientContact),
            Arguments.of("DOES NOT EXIST", null)
        );
    }
}
