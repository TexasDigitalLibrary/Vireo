package org.tdl.vireo.controller;

import static edu.tamu.framework.enums.ApiResponseType.ERROR;
import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.tdl.vireo.model.Organization;
import org.tdl.vireo.model.OrganizationCategory;
import org.tdl.vireo.model.WorkflowStep;
import org.tdl.vireo.model.repo.OrganizationCategoryRepo;
import org.tdl.vireo.model.repo.OrganizationRepo;
import org.tdl.vireo.model.repo.WorkflowStepRepo;
import org.tdl.vireo.model.repo.impl.WorkflowStepNonOverrideableException;

import com.fasterxml.jackson.databind.JsonNode;

import edu.tamu.framework.aspect.annotation.ApiData;
import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.aspect.annotation.ApiModel;
import edu.tamu.framework.aspect.annotation.ApiValidatedModel;
import edu.tamu.framework.aspect.annotation.ApiVariable;
import edu.tamu.framework.aspect.annotation.Auth;
import edu.tamu.framework.model.ApiResponse;

@Controller
@ApiMapping("/organization")
public class OrganizationController {
    
    @Autowired
    private OrganizationRepo organizationRepo;
    
    @Autowired
    private OrganizationCategoryRepo organizationCategoryRepo;
    
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
    @ApiMapping("/create")
    @Auth(role="MANAGER")
    public ApiResponse createOrganization(@ApiData JsonNode dataNode) {
                
        OrganizationCategory newOrganizationCategory = organizationCategoryRepo.findOne(dataNode.get("category").get("id").asLong());
        Organization newOrganizationParent = organizationRepo.findOne(dataNode.get("parentOrganizationId").asLong());
        
        organizationRepo.create(dataNode.get("name").asText(), newOrganizationParent, newOrganizationCategory);
        
        simpMessagingTemplate.convertAndSend("/channel/organizations", new ApiResponse(SUCCESS, organizationRepo.findAll()));
        
        return new ApiResponse(SUCCESS);        
    }
     
    @ApiMapping("/update")
    @Auth(role="MANAGER")
    public ApiResponse updateOrganization(@ApiValidatedModel Organization organization) {

        Organization updatedOrganization = organizationRepo.findOne(organization.getId());
        
        updatedOrganization.setName(organization.getName());
        
        updatedOrganization.setCategory(organization.getCategory());
        
        
        organization = organizationRepo.save(updatedOrganization);

        simpMessagingTemplate.convertAndSend("/channel/organizations", new ApiResponse(SUCCESS,  organizationRepo.findAll()));
        
        return new ApiResponse(SUCCESS, organization);
        
    }
    
    @Transactional
    @ApiMapping("/{requestingOrgID}/workflow")
    @Auth(role="STUDENT")   
    public ApiResponse getWorkflowStepsForOrganization(@ApiVariable Long requestingOrgID) {
        Organization org = organizationRepo.findOne(requestingOrgID);
        return new ApiResponse(SUCCESS, org.getAggregateWorkflowSteps());
    }
    
    @Transactional
    @ApiMapping("/{requestingOrgID}/create-workflow-step/{name}")
    @Auth(role="MANAGER")    
    public ApiResponse createWorkflowStepsForOrganization(@ApiVariable Long requestingOrgID, @ApiVariable String name) { 
        Organization org = organizationRepo.findOne(requestingOrgID);
        WorkflowStep newWorkflowStep = workflowStepRepo.create(name, org);
        simpMessagingTemplate.convertAndSend("/channel/organization", new ApiResponse(SUCCESS, organizationRepo.findOne(requestingOrgID)));
        return new ApiResponse(SUCCESS, newWorkflowStep);
    }
    
    @ApiMapping("/{requestingOrgID}/update-workflow-step")
    @Auth(role="MANAGER")    
    public ApiResponse updateWorkflowStepsForOrganization(@ApiVariable Long requestingOrgID, @ApiModel WorkflowStep workflowStepToUpdate) {
        Organization requestingOrg = organizationRepo.findOne(requestingOrgID);
                
        try {
            workflowStepRepo.update(workflowStepToUpdate, requestingOrg);
            simpMessagingTemplate.convertAndSend("/channel/organization", new ApiResponse(SUCCESS, organizationRepo.findOne(requestingOrgID)));
            return new ApiResponse(SUCCESS);
        } catch (WorkflowStepNonOverrideableException e) {
            e.printStackTrace();
            return new ApiResponse(ERROR, "Unable to update workflow step!");
        }
        
    }
    
    @ApiMapping("/{requestingOrgID}/delete-workflow-step/{workflowStepID}")
    @Auth(role="MANAGER")
    public ApiResponse deleteWorkflowStep(@ApiVariable Long requestingOrgID, @ApiVariable Long workflowStepID) {
        Organization requestingOrg = organizationRepo.findOne(requestingOrgID);
        WorkflowStep workflowStepToDelete = workflowStepRepo.findOne(workflowStepID);
        
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
