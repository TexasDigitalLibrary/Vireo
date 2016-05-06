package org.tdl.vireo.controller;

import static edu.tamu.framework.enums.ApiResponseType.ERROR;
import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;

import java.io.IOException;
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
import org.tdl.vireo.model.repo.WorkflowStepRepo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.aspect.annotation.ApiVariable;
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
        Map<String,List<Organization>> map = new HashMap<String,List<Organization>>();        
        map.put("list", organizationRepo.findAll());
        return new ApiResponse(SUCCESS, getAll());
    }

    //TODO: Resolve model issues: lazy initialization error when trying to get an Org Cat from the repo
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
        
        simpMessagingTemplate.convertAndSend("/channel/organization", new ApiResponse(SUCCESS, getAll()));
        
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
    
    @ApiMapping("/workflow-step/{id}")
    @Auth(role="MANAGER")
    @Transactional
    public ApiResponse stepForID(@ApiVariable String id){
        Long wStepID = null;
        try {
            wStepID = Long.valueOf(id);
        } catch (NumberFormatException e) {
            return new ApiResponse(ERROR, "Enable to parse long from string: [" + id + "]");
        }
        
        WorkflowStep potentiallyExistingStep = workflowStepRepo.findOne(wStepID);
        if (potentiallyExistingStep != null) {
            System.out.println("going to return step: " + potentiallyExistingStep.getName());
            simpMessagingTemplate.convertAndSend("/channel/organization/workflow-step", new ApiResponse(SUCCESS, potentiallyExistingStep));
            return new ApiResponse(SUCCESS);
        }
        return new ApiResponse(ERROR, "No wStep for id [" + wStepID.toString() + "]");
    }
}
