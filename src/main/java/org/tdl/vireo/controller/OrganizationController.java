package org.tdl.vireo.controller;

import static edu.tamu.framework.enums.ApiResponseType.ERROR;
import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;
import static edu.tamu.framework.enums.BusinessValidationType.CREATE;
import static edu.tamu.framework.enums.BusinessValidationType.DELETE;
import static edu.tamu.framework.enums.BusinessValidationType.EXISTS;
import static edu.tamu.framework.enums.BusinessValidationType.NONEXISTS;
import static edu.tamu.framework.enums.BusinessValidationType.UPDATE;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.tdl.vireo.model.Organization;
import org.tdl.vireo.model.WorkflowStep;
import org.tdl.vireo.model.repo.OrganizationRepo;
import org.tdl.vireo.model.repo.WorkflowStepRepo;
import org.tdl.vireo.model.repo.impl.WorkflowStepNonOverrideableException;

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
    private WorkflowStepRepo workflowStepRepo;

    @Autowired 
    private SimpMessagingTemplate simpMessagingTemplate;

    @Transactional
    @ApiMapping("/all")
    @Auth(role="STUDENT")    
    public ApiResponse allOrganizations() {        
        return new ApiResponse(SUCCESS, organizationRepo.findAll());
    }
    
    @Transactional
    @ApiMapping("/get/{id}")
    @Auth(role="STUDENT")    
    public ApiResponse getOrganization(@ApiVariable Long id) {        
        Organization org = organizationRepo.findOne(id);
        return new ApiResponse(SUCCESS, org);
    }

    @Transactional
    @ApiMapping("/create/{parentOrgID}")
    @Auth(role="MANAGER")
    @ApiValidation(business = { @ApiValidation.Business(value = CREATE), @ApiValidation.Business(value = EXISTS) })
    public ApiResponse createOrganization(@ApiVariable Long parentOrgID, @ApiValidatedModel Organization organization) {
        Organization parentOrganization = organizationRepo.findOne(parentOrgID);
        
        organizationRepo.create(organization.getName(), parentOrganization, organization.getCategory());
        
        simpMessagingTemplate.convertAndSend("/channel/organizations", new ApiResponse(SUCCESS, organizationRepo.findAll()));
        
        return new ApiResponse(SUCCESS);        
    }
     
    @ApiMapping("/update")
    @Auth(role="MANAGER")
    @ApiValidation(business = { @ApiValidation.Business(value = UPDATE), @ApiValidation.Business(value = NONEXISTS) })
    public ApiResponse updateOrganization(@ApiValidatedModel Organization organization) {
        Organization updatedOrganization = organizationRepo.findOne(organization.getId());
        
        updatedOrganization.setName(organization.getName());
        
        updatedOrganization.setCategory(organization.getCategory());
        
        organization = organizationRepo.save(updatedOrganization);

        simpMessagingTemplate.convertAndSend("/channel/organizations", new ApiResponse(SUCCESS,  organizationRepo.findAll()));
        
        return new ApiResponse(SUCCESS, organization);        
    }
    
    @ApiMapping("/delete/{organizationId}")
    @Auth(role="MANAGER")
    @ApiValidation(business = { @ApiValidation.Business(value = DELETE, params = {"originalWorkflowStep"}) })
    public ApiResponse deleteOrganization(@ApiVariable Long organizationId) {
    	organizationRepo.delete(organizationRepo.findOne(organizationId));
    	return new ApiResponse(SUCCESS);
    }
    
    @Transactional
    @ApiMapping("/{requestingOrgID}/workflow")
    @Auth(role="STUDENT")   
    public ApiResponse getWorkflowStepsForOrganization(@ApiVariable Long requestingOrgID) {
        Organization org = organizationRepo.findOne(requestingOrgID);
        return new ApiResponse(SUCCESS, org.getAggregateWorkflowSteps());
    }
    
    @Transactional //TODO remove
    @ApiMapping("/{requestingOrgID}/create-workflow-step")
    @Auth(role="MANAGER")
    @ApiValidation(business = { @ApiValidation.Business(value = CREATE), @ApiValidation.Business(value = EXISTS) })
    public ApiResponse createWorkflowStepsForOrganization(@ApiVariable Long requestingOrgID, @ApiValidatedModel WorkflowStep workflowStep) { 
        Organization org = organizationRepo.findOne(requestingOrgID);
        WorkflowStep newWorkflowStep = workflowStepRepo.create(workflowStep.getName(), org);
        simpMessagingTemplate.convertAndSend("/channel/organization", new ApiResponse(SUCCESS, organizationRepo.findOne(requestingOrgID)));
        return new ApiResponse(SUCCESS, newWorkflowStep);
    }
    
    @ApiMapping("/{requestingOrgID}/update-workflow-step")
    @Auth(role="MANAGER")    
    @ApiValidation(business = { @ApiValidation.Business(value = UPDATE), @ApiValidation.Business(value = NONEXISTS) })
    public ApiResponse updateWorkflowStepsForOrganization(@ApiVariable Long requestingOrgID, @ApiValidatedModel WorkflowStep workflowStep) {
        Organization requestingOrg = organizationRepo.findOne(requestingOrgID);
                
        try {
            workflowStepRepo.update(workflowStep, requestingOrg);
            simpMessagingTemplate.convertAndSend("/channel/organization", new ApiResponse(SUCCESS, organizationRepo.findOne(requestingOrgID)));
            return new ApiResponse(SUCCESS);
        } catch (WorkflowStepNonOverrideableException e) {
            e.printStackTrace();
            return new ApiResponse(ERROR, "Unable to update workflow step!");
        }
        
    }
    
    @ApiMapping("/{requestingOrgID}/delete-workflow-step")
    @Auth(role="MANAGER")
    @ApiValidation(business = { @ApiValidation.Business(value = DELETE), @ApiValidation.Business(value = NONEXISTS) })
    public ApiResponse deleteWorkflowStep(@ApiVariable Long requestingOrgID, @ApiValidatedModel WorkflowStep workflowStep) {
        Organization requestingOrg = organizationRepo.findOne(requestingOrgID);
        WorkflowStep workflowStepToDelete = workflowStepRepo.findOne(workflowStep.getId());
        
        workflowStepRepo.removeFromOrganization(requestingOrg, workflowStepToDelete);
        
        simpMessagingTemplate.convertAndSend("/channel/organization", new ApiResponse(SUCCESS, organizationRepo.findOne(requestingOrgID)));
        
        return new ApiResponse(SUCCESS);
    }
    
    @ApiMapping("/{requestingOrgID}/shift-workflow-step-up/{workflowStepID}")
    @Auth(role="MANAGER")
    public ApiResponse shiftWorkflowStepUp(@ApiVariable Long requestingOrgID, @ApiVariable Long workflowStepID) {
        Organization requestingOrg = organizationRepo.findOne(requestingOrgID);
        WorkflowStep workflowStepToShiftUp = workflowStepRepo.findOne(workflowStepID);
        
        int workflowStepToShiftIndex = requestingOrg.getAggregateWorkflowSteps().indexOf(workflowStepToShiftUp);
        
        if(workflowStepToShiftIndex-1 > -1) {
            WorkflowStep workflowStepToShiftDown = requestingOrg.getAggregateWorkflowSteps().get(workflowStepToShiftIndex-1);
            
            organizationRepo.reorderWorkflowSteps(requestingOrg, workflowStepToShiftUp, workflowStepToShiftDown);
            
            simpMessagingTemplate.convertAndSend("/channel/organization", new ApiResponse(SUCCESS, organizationRepo.findOne(requestingOrgID)));
        }
        
        return new ApiResponse(SUCCESS);
    }
    
    @ApiMapping("/{requestingOrgID}/shift-workflow-step-down/{workflowStepID}")
    @Auth(role="MANAGER")
    public ApiResponse shiftWorkflowStepDown(@ApiVariable Long requestingOrgID, @ApiVariable Long workflowStepID) {
        Organization requestingOrg = organizationRepo.findOne(requestingOrgID);
        WorkflowStep workflowStepToShiftUp = workflowStepRepo.findOne(workflowStepID);
        
        int workflowStepToShiftIndex = requestingOrg.getAggregateWorkflowSteps().indexOf(workflowStepToShiftUp);
        
        if(workflowStepToShiftIndex+1 < requestingOrg.getAggregateWorkflowSteps().size()) {
            WorkflowStep workflowStepToShiftDown = requestingOrg.getAggregateWorkflowSteps().get(workflowStepToShiftIndex+1);
            
            organizationRepo.reorderWorkflowSteps(requestingOrg, workflowStepToShiftUp, workflowStepToShiftDown);
            
            simpMessagingTemplate.convertAndSend("/channel/organization", new ApiResponse(SUCCESS, organizationRepo.findOne(requestingOrgID)));
        }
        
        return new ApiResponse(SUCCESS);
    }
    
}
