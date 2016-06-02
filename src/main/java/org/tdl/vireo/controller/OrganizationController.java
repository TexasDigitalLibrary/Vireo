package org.tdl.vireo.controller;

import static edu.tamu.framework.enums.ApiResponseType.ERROR;
import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.aspect.annotation.ApiModel;
import edu.tamu.framework.aspect.annotation.ApiVariable;
import edu.tamu.framework.aspect.annotation.Auth;
import edu.tamu.framework.aspect.annotation.Data;
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
    private ObjectMapper objectMapper;
    
    @Autowired 
    private SimpMessagingTemplate simpMessagingTemplate;
    
    private Map<String, List<Organization>> getAll() {
        Map<String, List<Organization>> map = new HashMap<String, List<Organization>>();
        map.put("list", organizationRepo.findAll());
        return map;
    }
        
    @ApiMapping("/all")
    @Auth(role="MANAGER")
    @Transactional
    public ApiResponse allOrganizations() {        
        return new ApiResponse(SUCCESS, getAll());
    }
    
    @ApiMapping("/get/{id}")
    @Auth(role="MANAGER")
    @Transactional
    public ApiResponse getOrganization(@ApiVariable String id) {        
        Organization org = organizationRepo.findOne(Long.parseLong(id));
        return new ApiResponse(SUCCESS, org);
    }

    @ApiMapping("/create")
    @Auth(role="MANAGER")
    @Transactional
    public ApiResponse createOrganization(@Data String data) {
        
        JsonNode dataNode = null;
        try {
            dataNode = objectMapper.readTree(data);
        } catch (IOException e) {
            return new ApiResponse(ERROR, "Unable to parse data json ["+e.getMessage()+"]");
        }
        
        OrganizationCategory newOrganizationCategory = organizationCategoryRepo.findOne(dataNode.get("category").get("id").asLong());
        Organization newOrganizationParent = organizationRepo.findOne(dataNode.get("parentOrganizationId").asLong());
        
        organizationRepo.create(dataNode.get("name").asText(), newOrganizationParent, newOrganizationCategory);
        
        simpMessagingTemplate.convertAndSend("/channel/organizations", new ApiResponse(SUCCESS, getAll()));
        
        return new ApiResponse(SUCCESS);
        
    }
    
    @ApiMapping("/update")
    @Auth(role="MANAGER")
    @Transactional
    public ApiResponse updateOrganization(@Data String data) {
        
        JsonNode dataNode = null;
        try {
            dataNode = objectMapper.readTree(data);
        } catch (IOException e) {
            return new ApiResponse(ERROR, "Unable to parse data json ["+e.getMessage()+"]");
        }
        
        JsonNode organizationNode = dataNode.get("organization");
        Organization organization = organizationRepo.findOne(organizationNode.get("id").asLong());

        organization.setName(dataNode.get("organization").get("name").asText());
        OrganizationCategory organizationCategory = organizationCategoryRepo.findOne(organizationNode.get("category").asLong());
        
        organization.setCategory(organizationCategory);
        organizationRepo.save(organization);

        simpMessagingTemplate.convertAndSend("/channel/organization", new ApiResponse(SUCCESS, getAll()));
        
        return new ApiResponse(SUCCESS);
        
    }
    
    @ApiMapping("/{id}/worflow")
    @Auth(role="MANAGER")
    @Transactional
    public ApiResponse getWorkflowStepsForOrganization(@ApiVariable String id) {
        Organization org = organizationRepo.findOne(Long.parseLong(id));
        return new ApiResponse(SUCCESS, org.getAggregateWorkflowSteps());
    }
    
    @ApiMapping("/{id}/create-workflow-step")
    @Auth(role="MANAGER")
    @Transactional
    public ApiResponse createWorkflowStepsForOrganization(@ApiVariable String id, @ApiModel WorkflowStep newWorkflowStep) {                
        Organization org = organizationRepo.findOne(Long.parseLong(id));
        newWorkflowStep = workflowStepRepo.create(newWorkflowStep.getName(), org);
        simpMessagingTemplate.convertAndSend("/channel/organization", new ApiResponse(SUCCESS, org));
        return new ApiResponse(SUCCESS, newWorkflowStep);
    }
    
    @ApiMapping("/{id}/update-workflow-step")
    @Auth(role="MANAGER")
    @Transactional
    public ApiResponse updateWorkflowStepsForOrganization(@ApiVariable String id, @ApiModel WorkflowStep workflowStepToUpdate) {
        Organization requestingOrg = organizationRepo.findOne(Long.parseLong(id));
                
        try {
            workflowStepRepo.update(workflowStepToUpdate, requestingOrg);
            simpMessagingTemplate.convertAndSend("/channel/organization", new ApiResponse(SUCCESS, organizationRepo.findOne(Long.parseLong(id))));
            return new ApiResponse(SUCCESS);
        } catch (WorkflowStepNonOverrideableException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return new ApiResponse(ERROR, "Unable to update workflow step!");
        }
        
    }
    
    @ApiMapping("/{requestingOrgID}/delete-workflow-step/{workflowStepID}")
    @Auth(role="MANAGER")
    public ApiResponse deleteWorkflowStep(@ApiVariable String requestingOrgID, @ApiVariable String workflowStepID) {
        Organization requestingOrg = organizationRepo.findOne(Long.parseLong(requestingOrgID));
        
        System.out.println(requestingOrgID);
        System.out.println(workflowStepID);
        
        return null;
    }
    
}
