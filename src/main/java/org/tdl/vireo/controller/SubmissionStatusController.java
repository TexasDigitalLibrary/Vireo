package org.tdl.vireo.controller;

import static edu.tamu.weaver.response.ApiStatus.SUCCESS;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tdl.vireo.model.repo.SubmissionStatusRepo;

import edu.tamu.weaver.response.ApiResponse;

@RestController
@RequestMapping("/submission-status")
public class SubmissionStatusController {

    @Autowired
    private SubmissionStatusRepo submissionStatusRepo;

    @RequestMapping("/all")
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse getAllSubmissionStatuses() {
        return new ApiResponse(SUCCESS, submissionStatusRepo.findAll());
    }

}
