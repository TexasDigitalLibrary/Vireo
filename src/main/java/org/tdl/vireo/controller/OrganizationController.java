package org.tdl.vireo.controller;

import static edu.tamu.framework.enums.ApiResponseType.ERROR;
import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.tdl.vireo.model.Organization;
import org.tdl.vireo.model.OrganizationCategory;
import org.tdl.vireo.model.WorkflowStep;
import org.tdl.vireo.model.repo.OrganizationCategoryRepo;
import org.tdl.vireo.model.repo.OrganizationRepo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.aspect.annotation.Auth;
import edu.tamu.framework.aspect.annotation.Data;
import edu.tamu.framework.model.ApiResponse;

@Controller
@ApiMapping("/organization")
public class OrganizationController {
    
    private Logger logger = LoggerFactory.getLogger(this.getClass()); 
    
    @Autowired
    private OrganizationRepo organizationRepo;

    @Autowired
    private OrganizationCategoryRepo organizationCategoryRepo;

    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired 
    private SimpMessagingTemplate simpMessagingTemplate;
    
    private Map<String, List<Organization>> getAll() {
        Map<String, List<Organization>> map = new HashMap<String, List<Organization>>();
        map.put("list", organizationRepo.findAll());
        return map;
    }
    
    private List<Organization> orderedOrgs(){
        List<Organization> sortedOrgs = organizationRepo.findAll();
        for (Organization org : sortedOrgs) {
            System.out.println("Org name " + org.getName());
            List<WorkflowStep> sortedWorkFlowSteps = new ArrayList<WorkflowStep>(org.getWorkflowSteps().size());
            for (WorkflowStep wStep : org.getWorkflowSteps()) {
//                System.out.println("ws step " + wStep.getName()+" with id " + wStep.getId());
//                System.out.println(org.getWorkflowStepOrder());
//                System.out.println("");
//                System.out.println("inserting at index " + org.getWorkflowStepOrder().indexOf(wStep.getId()));
                
                int insertIdx = org.getWorkflowStepOrder().indexOf(wStep.getId());
//                System.out.println("insert idx is " + insertIdx + "steps.size is " + org.getWorkflowSteps().size());
                if (insertIdx >= sortedWorkFlowSteps.size()) {
                    sortedWorkFlowSteps.add(wStep);
                }else{
                    sortedWorkFlowSteps.add(insertIdx, wStep);
                }
                
                
//                List<FieldProfile> sortedFieldProfiles = new ArrayList<FieldProfile>();
//                for (FieldProfile fp : wStep.getFieldProfiles()) {
//                    sortedFieldProfiles.add(wStep., element);
//                }
            }
            org.setWorkflowSteps(sortedWorkFlowSteps);
        }
        return sortedOrgs;
    }
    
    @ApiMapping("/all")
    @Auth(role="ROLE_MANAGER")
    @Transactional
    public ApiResponse allOrganizations() {
        System.out.println("in update");
        Map<String,List<Organization>> map = new HashMap<String,List<Organization>>();
        map.put("list", orderedOrgs());
        System.out.println("about to return");
        return new ApiResponse(SUCCESS, getAll());
    }

    //TODO: Resolve model issues: lazy initialization error when trying to get an Org Cat from the repo
    @ApiMapping("/create")
    @Auth(role="ROLE_MANAGER")
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
        
        simpMessagingTemplate.convertAndSend("/channel/organization", new ApiResponse(SUCCESS, getAll()));
        
        return new ApiResponse(SUCCESS);
        
    }
    
    @ApiMapping("/update")
    @Auth(role="ROLE_MANAGER")
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
}
