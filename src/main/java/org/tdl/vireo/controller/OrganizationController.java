package org.tdl.vireo.controller;

import static edu.tamu.weaver.response.ApiStatus.ERROR;
import static edu.tamu.weaver.response.ApiStatus.SUCCESS;
import static edu.tamu.weaver.validation.model.BusinessValidationType.CREATE;
import static edu.tamu.weaver.validation.model.BusinessValidationType.DELETE;
import static edu.tamu.weaver.validation.model.BusinessValidationType.UPDATE;
import static org.springframework.beans.BeanUtils.copyProperties;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tdl.vireo.exception.ComponentNotPresentOnOrgException;
import org.tdl.vireo.exception.WorkflowStepNonOverrideableException;
import org.tdl.vireo.model.EmailRecipient;
import org.tdl.vireo.model.EmailTemplate;
import org.tdl.vireo.model.EmailWorkflowRule;
import org.tdl.vireo.model.FieldPredicate;
import org.tdl.vireo.model.Organization;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.SubmissionStatus;
import org.tdl.vireo.model.WorkflowStep;
import org.tdl.vireo.model.repo.AbstractEmailRecipientRepo;
import org.tdl.vireo.model.repo.EmailTemplateRepo;
import org.tdl.vireo.model.repo.EmailWorkflowRuleRepo;
import org.tdl.vireo.model.repo.FieldPredicateRepo;
import org.tdl.vireo.model.repo.OrganizationRepo;
import org.tdl.vireo.model.repo.SubmissionStatusRepo;
import org.tdl.vireo.model.repo.WorkflowStepRepo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.tamu.weaver.response.ApiResponse;
import edu.tamu.weaver.validation.aspect.annotation.WeaverValidatedModel;
import edu.tamu.weaver.validation.aspect.annotation.WeaverValidation;

@RestController
@RequestMapping("/organization")
public class OrganizationController {

    @Autowired
    private OrganizationRepo organizationRepo;

    @Autowired
    private AbstractEmailRecipientRepo abstractEmailRecipientRepo;

    @Autowired
    private FieldPredicateRepo fieldPredicateRepo;

    @Autowired
    private EmailTemplateRepo emailTemplateRepo;

    @Autowired
    private EmailWorkflowRuleRepo emailWorkflowRuleRepo;

    @Autowired
    private SubmissionStatusRepo submissionStatusRepo;

    @Autowired
    private WorkflowStepRepo workflowStepRepo;

    @Autowired
    private ObjectMapper objectMapper;

    @Transactional
    @RequestMapping("/all")
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse allOrganizations() {
        return new ApiResponse(SUCCESS, organizationRepo.findAllByOrderByIdAsc());
    }

    @Transactional
    @RequestMapping("/get/{id}")
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse getOrganization(@PathVariable Long id) {
        return new ApiResponse(SUCCESS, organizationRepo.read(id));
    }

    @RequestMapping(value = "/create/{parentOrgID}", method = POST)
    @PreAuthorize("hasRole('MANAGER')")
    @WeaverValidation(business = { @WeaverValidation.Business(value = CREATE) })
    public ApiResponse createOrganization(@PathVariable Long parentOrgID, @WeaverValidatedModel Organization organization) {
        Organization parentOrganization = organizationRepo.read(parentOrgID);
        organization = organizationRepo.create(organization.getName(), parentOrganization, organization.getCategory());
        return new ApiResponse(SUCCESS, organization);
    }

    @RequestMapping(value = "/update", method = POST)
    @PreAuthorize("hasRole('MANAGER')")
    @WeaverValidation(business = { @WeaverValidation.Business(value = UPDATE) })
    public ApiResponse updateOrganization(@WeaverValidatedModel Organization organization) {
        Organization persistedOrg = organizationRepo.read(organization.getId());
        copyProperties(organization, persistedOrg, "originalWorkflowSteps", "aggregateWorkflowSteps", "parentOrganization", "childrenOrganizations", "emailWorkflowRules");
        persistedOrg = organizationRepo.update(persistedOrg);
        return new ApiResponse(SUCCESS, persistedOrg);
    }

    @RequestMapping(value = "/delete", method = POST)
    @PreAuthorize("hasRole('MANAGER')")
    @WeaverValidation(business = { @WeaverValidation.Business(value = DELETE, params = { "originalWorkflowSteps" }, joins = { Submission.class }), @WeaverValidation.Business(value = DELETE, path = { "id" }, restrict = "1") })
    public ApiResponse deleteOrganization(@WeaverValidatedModel Organization organization) {
        organizationRepo.delete(organizationRepo.read(organization.getId()));
        return new ApiResponse(SUCCESS, "Organization " + organization.getName() + " has been deleted!");
    }

    @RequestMapping(value = "/restore-defaults", method = POST)
    @PreAuthorize("hasRole('MANAGER')")
    @WeaverValidation(business = { @WeaverValidation.Business(value = UPDATE) })
    public ApiResponse restoreOrganizationDefaults(@WeaverValidatedModel Organization organization) {
        organization = organizationRepo.restoreDefaults(organizationRepo.read(organization.getId()));
        return new ApiResponse(SUCCESS, "Organization " + organization.getName() + " has been restored to defaults!");
    }

    @Transactional
    @RequestMapping("/{requestingOrgId}/workflow")
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse getWorkflowStepsForOrganization(@PathVariable Long requestingOrgId) {
        Organization org = organizationRepo.read(requestingOrgId);
        return new ApiResponse(SUCCESS, org.getAggregateWorkflowSteps());
    }

    @PreAuthorize("hasRole('MANAGER')")
    @RequestMapping(value = "/{requestingOrgId}/create-workflow-step", method = POST)
    @WeaverValidation(business = { @WeaverValidation.Business(value = CREATE) })
    public ApiResponse createWorkflowStepsForOrganization(@PathVariable Long requestingOrgId, @WeaverValidatedModel WorkflowStep workflowStep) {
        Organization org = organizationRepo.read(requestingOrgId);
        WorkflowStep newWorkflowStep = workflowStepRepo.create(workflowStep.getName(), org);
        return new ApiResponse(SUCCESS, newWorkflowStep);
    }

    @PreAuthorize("hasRole('MANAGER')")
    @RequestMapping(value = "/{requestingOrgId}/update-workflow-step", method = POST)
    @WeaverValidation(business = { @WeaverValidation.Business(value = UPDATE) })
    public ApiResponse updateWorkflowStepsForOrganization(@PathVariable Long requestingOrgId, @WeaverValidatedModel WorkflowStep workflowStep) throws WorkflowStepNonOverrideableException, ComponentNotPresentOnOrgException {
        Organization requestingOrg = organizationRepo.read(requestingOrgId);
        WorkflowStep persistedWorkflowStep = workflowStepRepo.read(workflowStep.getId());
        copyProperties(workflowStep, persistedWorkflowStep, "aggregateFieldProfiles", "aggregateNotes", "originatingOrganization", "originatingWorkflowStep", "originalFieldProfiles", "originalNotes");
        workflowStepRepo.update(persistedWorkflowStep, requestingOrg);
        return new ApiResponse(SUCCESS);
    }

    @RequestMapping(value = "/{requestingOrgId}/delete-workflow-step", method = POST)
    @PreAuthorize("hasRole('MANAGER')")
    @WeaverValidation(business = { @WeaverValidation.Business(value = DELETE) })
    public ApiResponse deleteWorkflowStep(@PathVariable Long requestingOrgId, @WeaverValidatedModel WorkflowStep workflowStep) {
        Organization requestingOrg = organizationRepo.read(requestingOrgId);
        WorkflowStep workflowStepToDelete = workflowStepRepo.findOne(workflowStep.getId());
        workflowStepRepo.removeFromOrganization(requestingOrg, workflowStepToDelete);
        return new ApiResponse(SUCCESS);
    }

    @RequestMapping("/{requestingOrgId}/shift-workflow-step-up/{workflowStepID}")
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse shiftWorkflowStepUp(@PathVariable Long requestingOrgId, @PathVariable Long workflowStepID) {
        Organization requestingOrg = organizationRepo.read(requestingOrgId);
        WorkflowStep workflowStepToShiftUp = workflowStepRepo.findOne(workflowStepID);
        int workflowStepToShiftIndex = requestingOrg.getAggregateWorkflowSteps().indexOf(workflowStepToShiftUp);
        if (workflowStepToShiftIndex - 1 > -1) {
            WorkflowStep workflowStepToShiftDown = requestingOrg.getAggregateWorkflowSteps().get(workflowStepToShiftIndex - 1);
            organizationRepo.reorderWorkflowSteps(requestingOrg, workflowStepToShiftUp, workflowStepToShiftDown);
        }
        return new ApiResponse(SUCCESS);
    }

    @RequestMapping("/{requestingOrgId}/shift-workflow-step-down/{workflowStepID}")
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse shiftWorkflowStepDown(@PathVariable Long requestingOrgId, @PathVariable Long workflowStepID) {
        Organization requestingOrg = organizationRepo.read(requestingOrgId);
        WorkflowStep workflowStepToShiftUp = workflowStepRepo.findOne(workflowStepID);
        int workflowStepToShiftIndex = requestingOrg.getAggregateWorkflowSteps().indexOf(workflowStepToShiftUp);
        if (workflowStepToShiftIndex + 1 < requestingOrg.getAggregateWorkflowSteps().size()) {
            WorkflowStep workflowStepToShiftDown = requestingOrg.getAggregateWorkflowSteps().get(workflowStepToShiftIndex + 1);
            organizationRepo.reorderWorkflowSteps(requestingOrg, workflowStepToShiftUp, workflowStepToShiftDown);
        }
        return new ApiResponse(SUCCESS);
    }

    @Transactional
    @PreAuthorize("hasRole('MANAGER')")
    @RequestMapping(value = "/{requestingOrgId}/add-email-workflow-rule", method = POST)
    public ApiResponse addEmailWorkflowRule(@PathVariable Long requestingOrgId, @RequestBody Map<String, Object> data) {

        ApiResponse response = new ApiResponse(SUCCESS);

        Organization org = organizationRepo.read(requestingOrgId);
        SubmissionStatus submissionStatus = submissionStatusRepo.findOne(Long.valueOf((Integer) data.get("submissionStatusId")));
        JsonNode recipientNode = objectMapper.convertValue(data, JsonNode.class).get("recipient");
        EmailTemplate emailTemplate = emailTemplateRepo.findOne(Long.valueOf((Integer) data.get("templateId")));

        EmailRecipient emailRecipient = buildRecipient(recipientNode);

        if (emailRecipient == null) {
            response = new ApiResponse(ERROR, "Could not create recipient.");
        } else {
            EmailWorkflowRule newEmailWorkflowRule = emailWorkflowRuleRepo.create(submissionStatus, emailRecipient, emailTemplate);
            org.addEmailWorkflowRule(newEmailWorkflowRule);
            organizationRepo.update(org);
        }

        return response;
    }

    @Transactional
    @RequestMapping("/{requestingOrgId}/edit-email-workflow-rule/{emailWorkflowRuleId}")
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse editEmailWorkflowRule(@PathVariable Long requestingOrgId, @PathVariable Long emailWorkflowRuleId, @RequestBody Map<String, Object> data) {

        ApiResponse response = new ApiResponse(SUCCESS);

        JsonNode recipientNode = objectMapper.convertValue(data, JsonNode.class).get("recipient");
        EmailTemplate emailTemplate = emailTemplateRepo.findOne(Long.valueOf((Integer) data.get("templateId")));

        EmailWorkflowRule emailWorkflowRuleToUpdate = emailWorkflowRuleRepo.findOne(emailWorkflowRuleId);

        EmailRecipient emailRecipient = buildRecipient(recipientNode);

        if (emailRecipient == null) {
            response = new ApiResponse(ERROR, "Could not create recipient.");
        } else {

            emailWorkflowRuleToUpdate.setEmailTemplate(emailTemplate);
            emailWorkflowRuleToUpdate.setEmailRecipient(emailRecipient);

            // TODO emailWorkflowRuleRepo.update(emailWorkflowRuleToUpdate);
            emailWorkflowRuleRepo.save(emailWorkflowRuleToUpdate);

            // TODO: Is this needed?
            organizationRepo.broadcast(requestingOrgId);
        }

        return response;
    }

    @Transactional
    @RequestMapping("/{requestingOrgId}/remove-email-workflow-rule/{emailWorkflowRuleId}")
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse removeEmailWorkflowRule(@PathVariable Long requestingOrgId, @PathVariable Long emailWorkflowRuleId) {

        Organization org = organizationRepo.read(requestingOrgId);
        EmailWorkflowRule rule = emailWorkflowRuleRepo.findOne(emailWorkflowRuleId);

        org.removeEmailWorkflowRule(rule);
        emailWorkflowRuleRepo.delete(rule);

        organizationRepo.update(org);

        return new ApiResponse(SUCCESS);
    }

    @Transactional
    @RequestMapping("/{requestingOrgId}/change-email-workflow-rule-activation/{emailWorkflowRuleId}")
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse changeEmailWorkflowRuleActivation(@PathVariable Long requestingOrgId, @PathVariable Long emailWorkflowRuleId) {

        EmailWorkflowRule rule = emailWorkflowRuleRepo.findOne(emailWorkflowRuleId);

        rule.isDisabled(!rule.isDisabled());

        emailWorkflowRuleRepo.save(rule);

        organizationRepo.broadcast(requestingOrgId);

        return new ApiResponse(SUCCESS);
    }

    private EmailRecipient buildRecipient(JsonNode recipientNode) {

        EmailRecipient emailRecipient;

        switch (recipientNode.get("type").asText()) {
        case "SUBMITTER":
            emailRecipient = abstractEmailRecipientRepo.createSubmitterRecipient();
            break;
        case "ASSIGNEE":
            emailRecipient = abstractEmailRecipientRepo.createAssigneeRecipient();
            break;
        case "ORGANIZATION":
            Organization recipientOrganization = organizationRepo.read(recipientNode.get("data").asLong());
            emailRecipient = abstractEmailRecipientRepo.createOrganizationRecipient(recipientOrganization);
            break;
        case "CONTACT":
            FieldPredicate recipientPredicate = fieldPredicateRepo.findOne(recipientNode.get("data").asLong());
            emailRecipient = abstractEmailRecipientRepo.createContactRecipient(recipientNode.get("name").asText(), recipientPredicate);
            break;
        default:
            emailRecipient = null;
        }

        return emailRecipient;
    }

}
