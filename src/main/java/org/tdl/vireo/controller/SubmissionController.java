package org.tdl.vireo.controller;

import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;
import static edu.tamu.framework.enums.ApiResponseType.ERROR;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.tdl.vireo.model.FieldValue;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.SubmissionListColumn;
import org.tdl.vireo.model.SubmissionState;
import org.tdl.vireo.model.User;
import org.tdl.vireo.model.repo.FieldValueRepo;
import org.tdl.vireo.model.repo.OrganizationRepo;
import org.tdl.vireo.model.repo.SubmissionRepo;
import org.tdl.vireo.model.repo.SubmissionStateRepo;
import org.tdl.vireo.model.repo.UserRepo;
import org.tdl.vireo.util.FileIOUtility;

import com.fasterxml.jackson.databind.JsonNode;

import edu.tamu.framework.aspect.annotation.ApiCredentials;
import edu.tamu.framework.aspect.annotation.ApiData;
import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.aspect.annotation.ApiModel;
import edu.tamu.framework.aspect.annotation.ApiVariable;
import edu.tamu.framework.aspect.annotation.Auth;
import edu.tamu.framework.model.ApiResponse;
import edu.tamu.framework.model.Credentials;

@Controller
@ApiMapping("/submission")
public class SubmissionController {
    
    private static final String STARTING_SUBMISSION_STATE_NAME = "In Progress";

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private SubmissionRepo submissionRepo;

    @Autowired
    private FieldValueRepo fieldValueRepo;
    
    @Autowired
    private OrganizationRepo organizationRepo;

    @Autowired
    private SubmissionStateRepo submissionStateRepo;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;
    
    @Autowired
    private FileIOUtility fileIOUtility;

    @Transactional
    @ApiMapping("/all")
    @Auth(role = "MANAGER")
    public ApiResponse getAll() {
        return new ApiResponse(SUCCESS, submissionRepo.findAll());
    }

    @Transactional
    @ApiMapping("/all-by-user")
    @Auth(role = "STUDENT")
    public ApiResponse getAllByUser(@ApiCredentials Credentials credentials) {
        User submitter = userRepo.findByEmail(credentials.getEmail());
        return new ApiResponse(SUCCESS, submissionRepo.findAllBySubmitter(submitter));
    }

    @Transactional
    @ApiMapping("/get-one/{submissionId}")
    @Auth(role = "STUDENT")
    public ApiResponse getOne(@ApiVariable Long submissionId) {
        return new ApiResponse(SUCCESS, submissionRepo.findOne(submissionId));
    }

    @Transactional
    @ApiMapping("/create")
    @Auth(role = "STUDENT")
    public ApiResponse createSubmission(@ApiCredentials Credentials credentials, @ApiData JsonNode dataNode) {
        Submission submission = submissionRepo.create(userRepo.findByEmail(credentials.getEmail()), organizationRepo.findOne(dataNode.get("organizationId").asLong()), submissionStateRepo.findByName(STARTING_SUBMISSION_STATE_NAME));
        simpMessagingTemplate.convertAndSend("/channel/submission", new ApiResponse(SUCCESS, submissionRepo.findAll()));
        return new ApiResponse(SUCCESS, submission);
    }

    @Transactional
    @ApiMapping("/{submissionId}/update-field-value")
    @Auth(role = "STUDENT")
    public ApiResponse updateFieldValue(@ApiVariable("submissionId") Long submissionId, @ApiModel FieldValue fieldValue) {

        Submission submission = submissionRepo.findOne(submissionId);

        if (fieldValue.getId() == null) {
            submission.addFieldValue(fieldValue);
            submission = submissionRepo.save(submission);
            fieldValue = submission.getFieldValueByValueAndPredicate(fieldValue.getValue().equals("null") ? "" : fieldValue.getValue(), fieldValue.getFieldPredicate());
        } else {
            fieldValue = fieldValueRepo.save(fieldValue);
        }

        return new ApiResponse(SUCCESS, fieldValue);
    }
    
    @Transactional
    @ApiMapping("/{submissionId}/remove-field-value")
    @Auth(role = "STUDENT")
    public ApiResponse removeFieldValue(@ApiVariable("submissionId") Long submissionId, @ApiModel FieldValue fieldValue) {
        Submission submission = submissionRepo.findOne(submissionId);        
        submission.removeFieldValue(fieldValue);        
        submissionRepo.save(submission);
        return new ApiResponse(SUCCESS);
    }

    @Transactional
    @ApiMapping("/query/{page}/{size}")
    @Auth(role = "MANAGER")
    public ApiResponse querySubmission(@ApiCredentials Credentials credentials, @ApiVariable Integer page, @ApiVariable Integer size, @ApiModel List<SubmissionListColumn> submissionListColumns) {
        User user = userRepo.findByEmail(credentials.getEmail());       
        return new ApiResponse(SUCCESS, submissionRepo.pageableDynamicSubmissionQuery(user.getActiveFilter(), submissionListColumns, new PageRequest(page, size)));
    }

    @ApiMapping("/all-submission-state")
    @Auth(role = "MANAGER")
    public ApiResponse getAllSubmissionStates() {
        return new ApiResponse(SUCCESS, submissionStateRepo.findAll());
    }

    @Transactional
    @ApiMapping("/batch-update-state")
    @Auth(role = "MANAGER")
    public ApiResponse batchUpdateSubmissionStates(@ApiCredentials Credentials credentials, @ApiModel SubmissionState submissionState) {
        User user = userRepo.findByEmail(credentials.getEmail());
        submissionRepo.batchDynamicSubmissionQuery(user.getActiveFilter(), user.getSubmissionViewColumns()).forEach(sub -> {
            sub.setState(submissionState);
            submissionRepo.save(sub);
        });
        return new ApiResponse(SUCCESS);
    }

    @Transactional
    @ApiMapping("/batch-assign-to")
    @Auth(role = "MANAGER")
    public ApiResponse batchAssignTo(@ApiCredentials Credentials credentials, @ApiModel User assignee) {
        User user = userRepo.findByEmail(credentials.getEmail());
        submissionRepo.batchDynamicSubmissionQuery(user.getActiveFilter(), user.getSubmissionViewColumns()).forEach(sub -> {
            sub.setAssignee(assignee);
            submissionRepo.save(sub);
        });
        return new ApiResponse(SUCCESS);
    }

    @ApiMapping(value = "/upload", method = RequestMethod.POST)
    @Auth(role = "STUDENT")
    public ApiResponse uploadSubmission(@ApiCredentials Credentials credentials, @RequestParam("file") MultipartFile file) throws IOException {    	
    	int hash = credentials.getEmail().hashCode();
        String fileName = file.getOriginalFilename();
        String uri = "private/" + hash + "/" + System.currentTimeMillis() + "-" + fileName;
        fileIOUtility.write(file.getBytes(), uri);        
    	return new ApiResponse(SUCCESS, uri);
    }
    
    @ApiMapping(value = "/file")
    @Auth(role = "STUDENT")
    public void submissionFile(HttpServletResponse response, @ApiCredentials Credentials credentials, @ApiData Map<String, String> requestHeaders) throws IOException {
    	response.setContentType(requestHeaders.get("fileType")); 
    	response.addHeader("Content-Disposition", "attachment; filename="+requestHeaders.get("fileName"));
    	Path path = fileIOUtility.getFilePath(requestHeaders.get("uri"));
    	Files.copy(path, response.getOutputStream());
    	response.getOutputStream().flush();
    }
    
    @ApiMapping(value = "/remove-file")
    @Auth(role = "STUDENT")
    public ApiResponse submissionFile(@ApiCredentials Credentials credentials, @ApiData Map<String, String> requestHeaders) throws IOException {
    	ApiResponse apiResponse = null;    	
    	int hash = credentials.getEmail().hashCode();
        String uri = requestHeaders.get("uri");        
        if(uri.contains(String.valueOf(hash))) {
        	fileIOUtility.deleteFile(uri);
        	apiResponse = new ApiResponse(SUCCESS);
        }
        else {
        	apiResponse = new ApiResponse(ERROR, "This is not your file to delete!"); 
        }        
        return apiResponse;
    }
    
    @ApiMapping(value = "/file-info")
    @Auth(role = "STUDENT")
    public ApiResponse submissionFileInfo(@ApiCredentials Credentials credentials, @ApiData Map<String, String> requestHeaders) throws IOException {
    	return new ApiResponse(SUCCESS, fileIOUtility.getFileInfo(requestHeaders.get("uri")));
    }

}
