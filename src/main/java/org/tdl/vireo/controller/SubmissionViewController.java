package org.tdl.vireo.controller;

import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.tdl.vireo.model.SubmissionViewColumn;
import org.tdl.vireo.model.User;
import org.tdl.vireo.model.repo.SubmissionViewColumnRepo;
import org.tdl.vireo.model.repo.UserRepo;

import edu.tamu.framework.aspect.annotation.ApiCredentials;
import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.aspect.annotation.ApiModel;
import edu.tamu.framework.aspect.annotation.Auth;
import edu.tamu.framework.model.ApiResponse;
import edu.tamu.framework.model.Credentials;

@Controller
@ApiMapping("/submission-view")
public class SubmissionViewController {
    
    @Autowired
    private SubmissionViewColumnRepo submissionViewColumnRepo;
    
    @Autowired
    private UserRepo userRepo;
    
    @ApiMapping("/all-columns")
    @Auth(role = "STUDENT")
    @Transactional
    public ApiResponse getSubmissionViewColumns() {
        return new ApiResponse(SUCCESS, submissionViewColumnRepo.findAll());
    }
    
    @ApiMapping("/columns-by-user")
    @Auth(role = "STUDENT")
    @Transactional
    public ApiResponse getSubmissionViewColumnsByUser(@ApiCredentials Credentials credentials) {
        User user = userRepo.findByEmail(credentials.getEmail());    
        return new ApiResponse(SUCCESS, user.getSubmissionViewColumns());
    }
    
    @ApiMapping("/update-user-columns")
    @Auth(role = "STUDENT")
    @Transactional
    public ApiResponse updateUserSubmissionViewColumns(@ApiCredentials Credentials credentials, @ApiModel List<SubmissionViewColumn> submissionViewColumns) {
        User user = userRepo.findByEmail(credentials.getEmail());
        user.setSubmissionViewColumns(submissionViewColumns);
        user = userRepo.save(user);
        return new ApiResponse(SUCCESS, user.getSubmissionViewColumns());
    }
    
}
