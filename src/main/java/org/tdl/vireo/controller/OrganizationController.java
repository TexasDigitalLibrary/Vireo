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
import org.springframework.transaction.annotation.Transactional;
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

import edu.tamu.framework.aspect.annotation.ApiData;
import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.aspect.annotation.ApiVariable;
import edu.tamu.framework.aspect.annotation.Auth;
import edu.tamu.weaver.response.ApiResponse;
import edu.tamu.weaver.validation.aspect.annotation.WeaverValidatedModel;
import edu.tamu.weaver.validation.aspect.annotation.WeaverValidation;

@RestController
@ApiMapping("/organization")
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
    @ApiMapping("/all")
    @Auth(role = "STUDENT")
    public ApiResponse allOrganizations() {
        return new ApiResponse(SUCCESS, organizationRepo.findAllByOrderByIdAsc());
    }

    @Transactional
    @ApiMapping("/get/{id}")
    @Auth(role = "STUDENT")
    public ApiResponse getOrganization(@ApiVariable Long id) {
        Organization org = organizationRepo.read(id);
        return new ApiResponse(SUCCESS, org);
    }

    @ApiMapping(value = "/create/{parentOrgID}", method = POST)
    @Auth(role = "MANAGER")
    @WeaverValidation(business = { @WeaverValidation.Business(value = CREATE) })
    public ApiResponse createOrganization(@ApiVariable Long parentOrgID, @WeaverValidatedModel Organization organization) {
        Organization parentOrganization = organizationRepo.read(parentOrgID);
        organization = organizationRepo.create(organization.getName(), parentOrganization, organization.getCategory());
        return new ApiResponse(SUCCESS);
    }

    @ApiMapping(value = "/update", method = POST)
    @Auth(role = "MANAGER")
    @WeaverValidation(business = { @WeaverValidation.Business(value = UPDATE) })
    public ApiResponse updateOrganization(@WeaverValidatedModel Organization organization) {
        Organization persistedOrg = organizationRepo.read(organization.getId());
        copyProperties(organization, persistedOrg, "originalWorkflowSteps", "aggregateWorkflowSteps", "parentOrganization", "childrenOrganizations", "emailWorkflowRules");
        persistedOrg = organizationRepo.update(persistedOrg);
        return new ApiResponse(SUCCESS, persistedOrg);
    }

    @ApiMapping(value = "/delete", method = POST)
    @Auth(role = "MANAGER")
    @WeaverValidation(business = { @WeaverValidation.Business(value = DELETE, params = { "originalWorkflowSteps" }, joins = { Submission.class }), @WeaverValidation.Business(value = DELETE, path = { "id" }, restrict = "1") })
    public ApiResponse deleteOrganization(@WeaverValidatedModel Organization organization) {
        organizationRepo.delete(organization);
        return new ApiResponse(SUCCESS, "Organization " + organization.getName() + " has been deleted!");
    }

    @ApiMapping(value = "/restore-defaults", method = POST)
    @Auth(role = "MANAGER")
    @WeaverValidation(business = { @WeaverValidation.Business(value = UPDATE) })
    public ApiResponse restoreOrganizationDefaults(@WeaverValidatedModel Organization organization) {
        organization = organizationRepo.restoreDefaults(organization);
        return new ApiResponse(SUCCESS, "Organization " + organization.getName() + " has been restored to defaults!");
    }

    @Transactional
    @Auth(role = "MANAGER")
    @ApiMapping(value = "/{requestingOrgID}/add-email-workflow-rule", method = POST)
    public ApiResponse addEmailWorkflowRule(@ApiVariable Long requestingOrgID, @ApiData Map<String, Object> data) {

        ApiResponse response = new ApiResponse(SUCCESS);

        Organization org = organizationRepo.read(requestingOrgID);
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
    @ApiMapping("/{requestingOrgID}/edit-email-workflow-rule/{emailWorkflowRuleId}")
    @Auth(role = "MANAGER")
    public ApiResponse editEmailWorkflowRule(@ApiVariable Long requestingOrgID, @ApiVariable Long emailWorkflowRuleId, @ApiData Map<String, Object> data) {

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
            organizationRepo.broadcast(requestingOrgID);
        }

        return response;
    }

    @Transactional
    @ApiMapping("/{requestingOrgID}/remove-email-workflow-rule/{emailWorkflowRuleId}")
    @Auth(role = "MANAGER")
    public ApiResponse removeEmailWorkflowRule(@ApiVariable Long requestingOrgID, @ApiVariable Long emailWorkflowRuleId) {

        Organization org = organizationRepo.read(requestingOrgID);
        EmailWorkflowRule rule = emailWorkflowRuleRepo.findOne(emailWorkflowRuleId);

        org.removeEmailWorkflowRule(rule);

        organizationRepo.update(org);
        
        return new ApiResponse(SUCCESS);
    }

    @Transactional
    @ApiMapping("/{requestingOrgID}/change-email-workflow-rule-activation/{emailWorkflowRuleId}")
    @Auth(role = "MANAGER")
    public ApiResponse changeEmailWorkflowRuleActivation(@ApiVariable Long requestingOrgID, @ApiVariable Long emailWorkflowRuleId) {

        EmailWorkflowRule rule = emailWorkflowRuleRepo.findOne(emailWorkflowRuleId);

        rule.isDisabled(!rule.isDisabled());

        emailWorkflowRuleRepo.save(rule);

        organizationRepo.broadcast(requestingOrgID);
        
        return new ApiResponse(SUCCESS);
    }

    @Transactional
    @ApiMapping("/{requestingOrgID}/workflow")
    @Auth(role = "STUDENT")
    public ApiResponse getWorkflowStepsForOrganization(@ApiVariable Long requestingOrgID) {
        Organization org = organizationRepo.read(requestingOrgID);
        return new ApiResponse(SUCCESS, org.getAggregateWorkflowSteps());
    }

    @Auth(role = "MANAGER")
    @ApiMapping(value = "/{requestingOrgID}/create-workflow-step", method = POST)
    @WeaverValidation(business = { @WeaverValidation.Business(value = CREATE) })
    public ApiResponse createWorkflowStepsForOrganization(@ApiVariable Long requestingOrgID, @WeaverValidatedModel WorkflowStep workflowStep) {
        Organization org = organizationRepo.read(requestingOrgID);
        WorkflowStep newWorkflowStep = workflowStepRepo.create(workflowStep.getName(), org);
        organizationRepo.broadcast(requestingOrgID);
        return new ApiResponse(SUCCESS, newWorkflowStep);
    }

    @Auth(role = "MANAGER")
    @ApiMapping(value = "/{requestingOrgID}/update-workflow-step", method = POST)
    @WeaverValidation(business = { @WeaverValidation.Business(value = UPDATE) })
    public ApiResponse updateWorkflowStepsForOrganization(@ApiVariable Long requestingOrgID, @WeaverValidatedModel WorkflowStep workflowStep) throws WorkflowStepNonOverrideableException, ComponentNotPresentOnOrgException {
        Organization requestingOrg = organizationRepo.read(requestingOrgID);

        workflowStepRepo.update(workflowStep, requestingOrg);
        organizationRepo.broadcast(requestingOrgID);
        
        return new ApiResponse(SUCCESS);
    }

    @ApiMapping(value = "/{requestingOrgID}/delete-workflow-step", method = POST)
    @Auth(role = "MANAGER")
    @WeaverValidation(business = { @WeaverValidation.Business(value = DELETE) })
    public ApiResponse deleteWorkflowStep(@ApiVariable Long requestingOrgID, @WeaverValidatedModel WorkflowStep workflowStep) {
        Organization requestingOrg = organizationRepo.read(requestingOrgID);
        WorkflowStep workflowStepToDelete = workflowStepRepo.findOne(workflowStep.getId());

        workflowStepRepo.removeFromOrganization(requestingOrg, workflowStepToDelete);

        organizationRepo.broadcast(requestingOrgID);

        return new ApiResponse(SUCCESS);
    }

    @ApiMapping("/{requestingOrgID}/shift-workflow-step-up/{workflowStepID}")
    @Auth(role = "MANAGER")
    public ApiResponse shiftWorkflowStepUp(@ApiVariable Long requestingOrgID, @ApiVariable Long workflowStepID) {
        Organization requestingOrg = organizationRepo.read(requestingOrgID);
        WorkflowStep workflowStepToShiftUp = workflowStepRepo.findOne(workflowStepID);

        int workflowStepToShiftIndex = requestingOrg.getAggregateWorkflowSteps().indexOf(workflowStepToShiftUp);

        if (workflowStepToShiftIndex - 1 > -1) {
            WorkflowStep workflowStepToShiftDown = requestingOrg.getAggregateWorkflowSteps().get(workflowStepToShiftIndex - 1);

            organizationRepo.reorderWorkflowSteps(requestingOrg, workflowStepToShiftUp, workflowStepToShiftDown);

            organizationRepo.broadcast(requestingOrgID);
        }

        return new ApiResponse(SUCCESS);
    }

    @ApiMapping("/{requestingOrgID}/shift-workflow-step-down/{workflowStepID}")
    @Auth(role = "MANAGER")
    public ApiResponse shiftWorkflowStepDown(@ApiVariable Long requestingOrgID, @ApiVariable Long workflowStepID) {
        Organization requestingOrg = organizationRepo.read(requestingOrgID);
        WorkflowStep workflowStepToShiftUp = workflowStepRepo.findOne(workflowStepID);

        int workflowStepToShiftIndex = requestingOrg.getAggregateWorkflowSteps().indexOf(workflowStepToShiftUp);

        if (workflowStepToShiftIndex + 1 < requestingOrg.getAggregateWorkflowSteps().size()) {
            WorkflowStep workflowStepToShiftDown = requestingOrg.getAggregateWorkflowSteps().get(workflowStepToShiftIndex + 1);

            organizationRepo.reorderWorkflowSteps(requestingOrg, workflowStepToShiftUp, workflowStepToShiftDown);

            organizationRepo.broadcast(requestingOrgID);
        }

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
