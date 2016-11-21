package org.tdl.vireo.controller;

import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.tdl.vireo.model.repo.SubmissionStateRepo;

import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.aspect.annotation.Auth;
import edu.tamu.framework.model.ApiResponse;

@Controller
@ApiMapping("/submission-state")
public class SubmissionStateController {
	
    @Autowired
    private SubmissionStateRepo submissionStateRepo;
	
	@ApiMapping("/all")
    @Auth(role = "MANAGER")
    public ApiResponse getAllSubmissionStates() {
        return new ApiResponse(SUCCESS, submissionStateRepo.findAll());
    }

}
