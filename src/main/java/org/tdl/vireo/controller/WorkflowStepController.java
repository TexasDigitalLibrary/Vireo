package org.tdl.vireo.controller;

import static edu.tamu.framework.enums.ApiResponseType.ERROR;
import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.tdl.vireo.model.FieldProfile;
import org.tdl.vireo.model.WorkflowStep;
import org.tdl.vireo.model.repo.FieldProfileRepo;
import org.tdl.vireo.model.repo.OrganizationRepo;
import org.tdl.vireo.model.repo.WorkflowStepRepo;
import org.tdl.vireo.model.repo.impl.WorkflowStepNonOverrideableException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.aspect.annotation.ApiVariable;
import edu.tamu.framework.aspect.annotation.Auth;
import edu.tamu.framework.aspect.annotation.Data;
import edu.tamu.framework.model.ApiResponse;

@Controller
@ApiMapping("/workflow-step")
public class WorkflowStepController {
    
    @Autowired
    private OrganizationRepo organizationRepo;
    
    @Autowired
    private WorkflowStepRepo workflowStepRepo;

    @Autowired
    private FieldProfileRepo fieldProfileRepo;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired 
    private SimpMessagingTemplate simpMessagingTemplate;
    
    @ApiMapping("/all")
    @Auth(role="MANAGER")
    public ApiResponse getAll() {
        Map<String,List<WorkflowStep>> map = new HashMap<String,List<WorkflowStep>>();        
        map.put("list", workflowStepRepo.findAll());
        return new ApiResponse(SUCCESS, map);
    }
    
    @ApiMapping("/get/{id}")
    @Transactional
    public ApiResponse getStepById(@ApiVariable String id){

        Long wStepID = null;
        try {
            wStepID = Long.valueOf(id);
        } catch (NumberFormatException e) {
            return new ApiResponse(ERROR, "Enable to parse long from string: [" + id + "]");
        }

        WorkflowStep potentiallyExistingStep = workflowStepRepo.findOne(wStepID);
        if (potentiallyExistingStep != null) {
            return new ApiResponse(SUCCESS, potentiallyExistingStep);
        }

        return new ApiResponse(ERROR, "No wStep for id [" + wStepID.toString() + "]");
    }
    
    @ApiMapping("/{workflowStepId}/reorder-field-profiles/{src}/{dest}")
    @Auth(role="MANAGER")
    public ApiResponse reorderFieldProfiles(@ApiVariable String workflowStepId, @ApiVariable String src, @ApiVariable String dest, @Data String data) throws NumberFormatException, WorkflowStepNonOverrideableException {
        
        JsonNode dataNode = null;
        try {
            dataNode = objectMapper.readTree(data);
        } catch (IOException e) {
            return new ApiResponse(ERROR, "Unable to parse data json ["+e.getMessage()+"]");
        }
        
        Long reqOrgId = Long.parseLong(dataNode.get("requestingOrgId").toString());
        
        System.out.println("\nReorder field profile");
        System.out.println("workflow step id: " + workflowStepId);
        System.out.println("src: " + src);
        System.out.println("dest: " + dest);
        System.out.println("req org id: " + reqOrgId + "\n");
        
        
        WorkflowStep workflowStep = workflowStepRepo.findOne(Long.parseLong(workflowStepId));
        
        workflowStepRepo.reorderFieldProfiles(organizationRepo.findOne(reqOrgId), workflowStep, Integer.parseInt(src), Integer.parseInt(dest));     
        
        simpMessagingTemplate.convertAndSend("/channel/organization", new ApiResponse(SUCCESS, organizationRepo.findOne(reqOrgId)));
        
        return new ApiResponse(SUCCESS);
    }
    
    @ApiMapping("/{workflowStepId}/remove-field-profile/{fieldProfileId}")
    @Auth(role="MANAGER")
    public ApiResponse removeFieldProfile(@ApiVariable String workflowStepId, @ApiVariable String fieldProfileId, @Data String data) throws NumberFormatException, WorkflowStepNonOverrideableException {
        
    	JsonNode dataNode = null;
        try {
            dataNode = objectMapper.readTree(data);
        } catch (IOException e) {
            return new ApiResponse(ERROR, "Unable to parse data json ["+e.getMessage()+"]");
        }
        
        Long reqOrgId = Long.parseLong(dataNode.get("requestingOrgId").toString());
        
        System.out.println("\nRemove field profile");
        System.out.println("workflow step id: " + workflowStepId);
        System.out.println("field profile id: " + fieldProfileId);
        System.out.println("req org id: " + reqOrgId + "\n");
        
        WorkflowStep workflowStep = workflowStepRepo.findOne(Long.parseLong(workflowStepId));
        FieldProfile fieldProfile = fieldProfileRepo.findOne(Long.parseLong(fieldProfileId));
        
        fieldProfileRepo.disinheritFromWorkflowStep(organizationRepo.findOne(reqOrgId), workflowStep, fieldProfile);     
        
        simpMessagingTemplate.convertAndSend("/channel/organization", new ApiResponse(SUCCESS, organizationRepo.findOne(reqOrgId)));
        
        return new ApiResponse(SUCCESS);
    }
    

}
