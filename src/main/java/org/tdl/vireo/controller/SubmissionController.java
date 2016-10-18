package org.tdl.vireo.controller;

import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMethod;
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
import edu.tamu.framework.aspect.annotation.ApiInputStream;
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
    public ApiResponse updateSubmission(@ApiVariable("submissionId") Long submissionId, @ApiModel FieldValue fieldValue) {

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
    public ApiResponse uploadSubmission(@ApiCredentials Credentials credentials, @ApiData Map<String, String> requestHeaders, @ApiInputStream InputStream inputStream) throws IOException {    	
    	int hash = credentials.getEmail().hashCode();
    	String fileName = requestHeaders.get("fileName");

    	//TODO: folder should be a configuration
    	String uri = "private/" + hash + "/" + fileName;
    	
    	fileIOUtility.write(inputStream, uri);    	
    	return new ApiResponse(SUCCESS, uri);
    }
    
    @ApiMapping(value = "/file-info", method = RequestMethod.POST)
    public ApiResponse submissionFileInfo(@ApiCredentials Credentials credentials, @ApiData Map<String, String> dataNode) throws IOException {
    	return new ApiResponse(SUCCESS, fileIOUtility.getFile(dataNode.get("uri")));
    }

}
