package org.tdl.vireo.controller;

import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;
import static edu.tamu.framework.enums.ApiResponseType.ERROR;
import static edu.tamu.framework.enums.BusinessValidationType.CREATE;
import static edu.tamu.framework.enums.BusinessValidationType.DELETE;
import static edu.tamu.framework.enums.BusinessValidationType.EXISTS;
import static edu.tamu.framework.enums.BusinessValidationType.NONEXISTS;
import static edu.tamu.framework.enums.BusinessValidationType.UPDATE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.tdl.vireo.model.EmailRecipient;
import org.tdl.vireo.model.EmailTemplate;
import org.tdl.vireo.model.EmailWorkflowRule;
import org.tdl.vireo.model.FieldPredicate;
import org.tdl.vireo.model.Organization;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.SubmissionState;
import org.tdl.vireo.model.WorkflowStep;
import org.tdl.vireo.model.repo.AbstractEmailRecipientRepo;
import org.tdl.vireo.model.repo.EmailTemplateRepo;
import org.tdl.vireo.model.repo.EmailWorkflowRuleRepo;
import org.tdl.vireo.model.repo.FieldPredicateRepo;
import org.tdl.vireo.model.repo.OrganizationRepo;
import org.tdl.vireo.model.repo.SubmissionStateRepo;
import org.tdl.vireo.model.repo.WorkflowStepRepo;
import org.tdl.vireo.model.repo.impl.ComponentNotPresentOnOrgException;
import org.tdl.vireo.model.repo.impl.WorkflowStepNonOverrideableException;

import com.fasterxml.jackson.databind.JsonNode;

import edu.tamu.framework.aspect.annotation.ApiData;
import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.aspect.annotation.ApiValidatedModel;
import edu.tamu.framework.aspect.annotation.ApiValidation;
import edu.tamu.framework.aspect.annotation.ApiVariable;
import edu.tamu.framework.aspect.annotation.Auth;
import edu.tamu.framework.model.ApiResponse;

@Controller
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
    private SubmissionStateRepo submissionStateRepo;

    @Autowired
    private WorkflowStepRepo workflowStepRepo;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Transactional
    @ApiMapping("/all")
    @Auth(role = "STUDENT")
    public ApiResponse allOrganizations() {
        return new ApiResponse(SUCCESS, organizationRepo.findAll());
    }

    @Transactional
    @ApiMapping("/get/{id}")
    @Auth(role = "STUDENT")
    public ApiResponse getOrganization(@ApiVariable Long id) {
        Organization org = organizationRepo.findOne(id);
        return new ApiResponse(SUCCESS, org);
    }

    @ApiMapping("/create/{parentOrgID}")
    @Auth(role = "MANAGER")
    @ApiValidation(business = { @ApiValidation.Business(value = CREATE), @ApiValidation.Business(value = EXISTS) })
    public ApiResponse createOrganization(@ApiVariable Long parentOrgID, @ApiValidatedModel Organization organization) {
        Organization parentOrganization = organizationRepo.findOne(parentOrgID);
        organizationRepo.create(organization.getName(), parentOrganization, organization.getCategory());
        simpMessagingTemplate.convertAndSend("/channel/organizations", new ApiResponse(SUCCESS, organizationRepo.findAll()));
        return new ApiResponse(SUCCESS);
    }

    @ApiMapping(value = "/update", method = POST)
    @Auth(role = "MANAGER")
    @ApiValidation(business = { @ApiValidation.Business(value = UPDATE), @ApiValidation.Business(value = NONEXISTS) })
    public ApiResponse updateOrganization(@ApiValidatedModel Organization organization) {
        organization = organizationRepo.save(organization);
        simpMessagingTemplate.convertAndSend("/channel/organizations", new ApiResponse(SUCCESS, organizationRepo.findAll()));
        return new ApiResponse(SUCCESS, organization);
    }

    @ApiMapping(value = "/delete", method = POST)
    @Auth(role = "MANAGER")
    @ApiValidation(business = { @ApiValidation.Business(value = DELETE, params = { "originalWorkflowSteps" }, joins = { Submission.class }) })
    public ApiResponse deleteOrganization(@ApiValidatedModel Organization organization) {
        organizationRepo.delete(organization);
        simpMessagingTemplate.convertAndSend("/channel/organizations", new ApiResponse(SUCCESS, organizationRepo.findAll()));
        return new ApiResponse(SUCCESS, "Organization " + organization.getName() + " has been deleted!");
    }
    
    @Transactional
    @ApiMapping("/{requestingOrgID}/add-email-workflow-rule")
    @Auth(role = "MANAGER")
    public ApiResponse addEmailWorkflowRule(@ApiVariable Long requestingOrgID, @ApiData JsonNode dataNode) {
        Organization org = organizationRepo.findOne(requestingOrgID);
        SubmissionState submissionState = submissionStateRepo.findOne(dataNode.get("submissionStateId").asLong());
        JsonNode recipientNode = dataNode.get("recipient");
        EmailTemplate emailTemplate = emailTemplateRepo.findOne(dataNode.get("templateId").asLong());
        
        EmailRecipient emailRecipient;
                
        switch(recipientNode.get("type").asText()) {
        
        	case "SUBMITTER": {
        		emailRecipient = abstractEmailRecipientRepo.createSubmitterRecipient();
        		break;
        	}
        	case "ASSIGNEE": {
        		emailRecipient = abstractEmailRecipientRepo.createAssigneeRecipient();
        		break;
        	}
        	case "ORGANIZATION": {
        		Organization recipientOrganization = organizationRepo.findOne(recipientNode.get("data").asLong());
        		emailRecipient = abstractEmailRecipientRepo.createOrganizationRecipient(recipientOrganization);
        		break;
        	}
        	case "CONTACT": {
        		FieldPredicate recipientPredicate = fieldPredicateRepo.findOne(recipientNode.get("data").asLong());
        		emailRecipient = abstractEmailRecipientRepo.createContactRecipient(recipientNode.get("label").asText(), recipientPredicate);
        		break;
        	}
        	default: {
        		 return new ApiResponse(ERROR, "Could not create recipient.");
        	}
        
        }
        
        EmailWorkflowRule newEmailWorkflowRule = emailWorkflowRuleRepo.create(submissionState, emailRecipient, emailTemplate);
        
        org.addEmailWorkflowRule(newEmailWorkflowRule);
        
        simpMessagingTemplate.convertAndSend("/channel/organization", new ApiResponse(SUCCESS, organizationRepo.findOne(requestingOrgID)));
        
        return new ApiResponse(SUCCESS);
    }
    
    @Transactional
    @ApiMapping("/{requestingOrgID}/remove-email-workflow-rule/{emailWorkflowRuleId}")
    @Auth(role = "MANAGER")
    public ApiResponse removeEmailWorkflowRule(@ApiVariable Long requestingOrgID, @ApiVariable Long emailWorkflowRuleId) {
    	
    	Organization org = organizationRepo.findOne(requestingOrgID);
    	EmailWorkflowRule rule = emailWorkflowRuleRepo.findOne(emailWorkflowRuleId);
    	
    	org.removeEmailWorkflowRule(rule);
    	
        simpMessagingTemplate.convertAndSend("/channel/organization", new ApiResponse(SUCCESS, organizationRepo.findOne(requestingOrgID)));
        return new ApiResponse(SUCCESS);
    }
    
    @Transactional
    @ApiMapping("/{requestingOrgID}/change-email-workflow-rule-activation/{emailWorkflowRuleId}")
    @Auth(role = "MANAGER")
    public ApiResponse changeEmailWorkflowRuleActivation(@ApiVariable Long requestingOrgID, @ApiVariable Long emailWorkflowRuleId) {
    	
    	EmailWorkflowRule rule = emailWorkflowRuleRepo.findOne(emailWorkflowRuleId);
    	
    	rule.isDisabled(!rule.isDisabled());
    	
    	emailWorkflowRuleRepo.save(rule);
    	
        simpMessagingTemplate.convertAndSend("/channel/organization", new ApiResponse(SUCCESS, organizationRepo.findOne(requestingOrgID)));
        return new ApiResponse(SUCCESS);
    }

    @Transactional
    @ApiMapping("/{requestingOrgID}/workflow")
    @Auth(role = "STUDENT")
    public ApiResponse getWorkflowStepsForOrganization(@ApiVariable Long requestingOrgID) {
        Organization org = organizationRepo.findOne(requestingOrgID);
        return new ApiResponse(SUCCESS, org.getAggregateWorkflowSteps());
    }

    @ApiMapping("/{requestingOrgID}/create-workflow-step")
    @Auth(role = "MANAGER")
    @ApiValidation(business = { @ApiValidation.Business(value = CREATE), @ApiValidation.Business(value = EXISTS) })
    public ApiResponse createWorkflowStepsForOrganization(@ApiVariable Long requestingOrgID, @ApiValidatedModel WorkflowStep workflowStep) {
        Organization org = organizationRepo.findOne(requestingOrgID);
        WorkflowStep newWorkflowStep = workflowStepRepo.create(workflowStep.getName(), org);
        simpMessagingTemplate.convertAndSend("/channel/organization", new ApiResponse(SUCCESS, organizationRepo.findOne(requestingOrgID)));
        return new ApiResponse(SUCCESS, newWorkflowStep);
    }

    @ApiMapping("/{requestingOrgID}/update-workflow-step")
    @Auth(role = "MANAGER")
    @ApiValidation(business = { @ApiValidation.Business(value = UPDATE), @ApiValidation.Business(value = NONEXISTS) })
    public ApiResponse updateWorkflowStepsForOrganization(@ApiVariable Long requestingOrgID, @ApiValidatedModel WorkflowStep workflowStep) throws WorkflowStepNonOverrideableException, ComponentNotPresentOnOrgException {
        Organization requestingOrg = organizationRepo.findOne(requestingOrgID);

        workflowStepRepo.update(workflowStep, requestingOrg);
        simpMessagingTemplate.convertAndSend("/channel/organization", new ApiResponse(SUCCESS, organizationRepo.findOne(requestingOrgID)));

        return new ApiResponse(SUCCESS);
    }

    @ApiMapping("/{requestingOrgID}/delete-workflow-step")
    @Auth(role = "MANAGER")
    @ApiValidation(business = { @ApiValidation.Business(value = DELETE), @ApiValidation.Business(value = NONEXISTS) })
    public ApiResponse deleteWorkflowStep(@ApiVariable Long requestingOrgID, @ApiValidatedModel WorkflowStep workflowStep) {
        Organization requestingOrg = organizationRepo.findOne(requestingOrgID);
        WorkflowStep workflowStepToDelete = workflowStepRepo.findOne(workflowStep.getId());

        workflowStepRepo.removeFromOrganization(requestingOrg, workflowStepToDelete);

        simpMessagingTemplate.convertAndSend("/channel/organization", new ApiResponse(SUCCESS, organizationRepo.findOne(requestingOrgID)));

        return new ApiResponse(SUCCESS);
    }

    @ApiMapping("/{requestingOrgID}/shift-workflow-step-up/{workflowStepID}")
    @Auth(role = "MANAGER")
    public ApiResponse shiftWorkflowStepUp(@ApiVariable Long requestingOrgID, @ApiVariable Long workflowStepID) {
        Organization requestingOrg = organizationRepo.findOne(requestingOrgID);
        WorkflowStep workflowStepToShiftUp = workflowStepRepo.findOne(workflowStepID);

        int workflowStepToShiftIndex = requestingOrg.getAggregateWorkflowSteps().indexOf(workflowStepToShiftUp);

        if (workflowStepToShiftIndex - 1 > -1) {
            WorkflowStep workflowStepToShiftDown = requestingOrg.getAggregateWorkflowSteps().get(workflowStepToShiftIndex - 1);

            organizationRepo.reorderWorkflowSteps(requestingOrg, workflowStepToShiftUp, workflowStepToShiftDown);

            simpMessagingTemplate.convertAndSend("/channel/organization", new ApiResponse(SUCCESS, organizationRepo.findOne(requestingOrgID)));
        }

        return new ApiResponse(SUCCESS);
    }

    @ApiMapping("/{requestingOrgID}/shift-workflow-step-down/{workflowStepID}")
    @Auth(role = "MANAGER")
    public ApiResponse shiftWorkflowStepDown(@ApiVariable Long requestingOrgID, @ApiVariable Long workflowStepID) {
        Organization requestingOrg = organizationRepo.findOne(requestingOrgID);
        WorkflowStep workflowStepToShiftUp = workflowStepRepo.findOne(workflowStepID);

        int workflowStepToShiftIndex = requestingOrg.getAggregateWorkflowSteps().indexOf(workflowStepToShiftUp);

        if (workflowStepToShiftIndex + 1 < requestingOrg.getAggregateWorkflowSteps().size()) {
            WorkflowStep workflowStepToShiftDown = requestingOrg.getAggregateWorkflowSteps().get(workflowStepToShiftIndex + 1);

            organizationRepo.reorderWorkflowSteps(requestingOrg, workflowStepToShiftUp, workflowStepToShiftDown);

            simpMessagingTemplate.convertAndSend("/channel/organization", new ApiResponse(SUCCESS, organizationRepo.findOne(requestingOrgID)));
        }

        return new ApiResponse(SUCCESS);
    }

}
