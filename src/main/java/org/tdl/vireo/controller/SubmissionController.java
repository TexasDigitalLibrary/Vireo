package org.tdl.vireo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.repo.SubmissionRepo;

import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.aspect.annotation.Auth;
import edu.tamu.framework.model.ApiResponse;

import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@ApiMapping("/submission")
public class SubmissionController {

    @Autowired
    private SubmissionRepo submissionRepo;
    
    @ApiMapping("/all")
    @Auth(role = "MANAGER")
    public ApiResponse getAll() {
        
        Map<String, List<Submission>> allSubmissions = new HashMap<String, List<Submission>>();
        
        allSubmissions.put("list", submissionRepo.findAll());
        
        return new ApiResponse(SUCCESS, allSubmissions);
        
    }
    
}
