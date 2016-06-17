package org.tdl.vireo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.repo.SubmissionRepo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.aspect.annotation.ApiVariable;
import edu.tamu.framework.aspect.annotation.Auth;
import edu.tamu.framework.aspect.annotation.Data;
import edu.tamu.framework.aspect.annotation.Shib;
import edu.tamu.framework.model.ApiResponse;
import edu.tamu.framework.model.Credentials;

import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;
import static edu.tamu.framework.enums.ApiResponseType.ERROR;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@ApiMapping("/submission")
public class SubmissionController {

    @Autowired
    private SubmissionRepo submissionRepo;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @ApiMapping("/all")
    @Auth(role = "STUDENT")
    public ApiResponse getAll() {
        
        Map<String, List<Submission>> allSubmissions = new HashMap<String, List<Submission>>();
        
        allSubmissions.put("list", submissionRepo.findAll());
        
        return new ApiResponse(SUCCESS, allSubmissions);
        
    }
    
    @ApiMapping("/get-one/{submissionId}")
    @Auth(role = "STUDENT")
    @Transactional
    public ApiResponse getOne(@ApiVariable String submissionId) {
        return new ApiResponse(SUCCESS, submissionRepo.findOne(Long.parseLong(submissionId)));
    }
    
    @ApiMapping("/create")
    @Auth(role = "STUDENT")
    @Transactional
    public ApiResponse createSubmission(@Shib Credentials credentials, @Data String data) {
        
        JsonNode dataNode = null;
        try {
            dataNode = objectMapper.readTree(data);
        } catch (IOException e) {
            new ApiResponse(ERROR, "could not parse data string ["+e.getMessage()+"]");
        }
                
        Submission submission = submissionRepo.create(credentials, dataNode.get("organizationId").asLong());
        
        return new ApiResponse(SUCCESS, submission);
    }
    
}
