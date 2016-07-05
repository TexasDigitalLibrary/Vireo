package org.tdl.vireo.controller;

import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.repo.SubmissionRepo;

import com.fasterxml.jackson.databind.JsonNode;

import edu.tamu.framework.aspect.annotation.ApiCredentials;
import edu.tamu.framework.aspect.annotation.ApiData;
import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.aspect.annotation.ApiVariable;
import edu.tamu.framework.aspect.annotation.Auth;
import edu.tamu.framework.model.ApiResponse;
import edu.tamu.framework.model.Credentials;

@Controller
@ApiMapping("/submission")
public class SubmissionController {

    @Autowired
    private SubmissionRepo submissionRepo;

    @ApiMapping("/all")
    @Auth(role = "STUDENT")
    public ApiResponse getAll() {
        return new ApiResponse(SUCCESS, submissionRepo.findAll());
    }
    
    @ApiMapping("/get-one/{submissionId}")
    @Auth(role = "STUDENT")
    @Transactional
    public ApiResponse getOne(@ApiVariable Long submissionId) {
        return new ApiResponse(SUCCESS, submissionRepo.findOne(submissionId));
    }
    
    @ApiMapping("/create")
    @Auth(role = "STUDENT")
    @Transactional
    public ApiResponse createSubmission(@ApiCredentials Credentials credentials, @ApiData JsonNode dataNode) {
        Submission submission = submissionRepo.create(credentials, dataNode.get("organizationId").asLong());
        return new ApiResponse(SUCCESS, submission);
    }
    
}
