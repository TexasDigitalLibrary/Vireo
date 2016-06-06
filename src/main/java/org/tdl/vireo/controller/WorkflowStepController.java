package org.tdl.vireo.controller;

import static edu.tamu.framework.enums.ApiResponseType.ERROR;
import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.tdl.vireo.model.WorkflowStep;
import org.tdl.vireo.model.repo.WorkflowStepRepo;

import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.aspect.annotation.ApiVariable;
import edu.tamu.framework.aspect.annotation.Auth;
import edu.tamu.framework.model.ApiResponse;

@Controller
@ApiMapping("/workflow-step")
public class WorkflowStepController {
    
    @Autowired
    private WorkflowStepRepo workflowStepRepo;
    
    
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

    @ApiMapping("/delete/{organizationID}/{workflowStepID}")
    public ApiResponse deleteStepById(String organizationID, String workflowStepID) {
        System.out.println("got orgID " + " and wsID: " + workflowStepID);
        return null;
    }
}
