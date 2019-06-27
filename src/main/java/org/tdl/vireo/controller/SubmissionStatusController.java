package org.tdl.vireo.controller;

import static edu.tamu.weaver.response.ApiStatus.ERROR;
import static edu.tamu.weaver.response.ApiStatus.SUCCESS;
import static edu.tamu.weaver.validation.model.BusinessValidationType.CREATE;
import static edu.tamu.weaver.validation.model.BusinessValidationType.DELETE;
import static edu.tamu.weaver.validation.model.BusinessValidationType.UPDATE;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tdl.vireo.model.SubmissionState;
import org.tdl.vireo.model.SubmissionStatus;
import org.tdl.vireo.model.repo.SubmissionStatusRepo;

import edu.tamu.weaver.response.ApiAction;
import edu.tamu.weaver.response.ApiResponse;
import edu.tamu.weaver.validation.aspect.annotation.WeaverValidatedModel;
import edu.tamu.weaver.validation.aspect.annotation.WeaverValidation;

@RestController
@RequestMapping("/submission-status")
public class SubmissionStatusController {

    @Autowired
    protected SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private SubmissionStatusRepo submissionStatusRepo;

    @RequestMapping("/create")
    @PreAuthorize("hasRole('MANAGER')")
    @WeaverValidation(business = { @WeaverValidation.Business(value = CREATE) })
    public ApiResponse createSubmissionStatus(@WeaverValidatedModel SubmissionStatus submissionStatus) {
        return new ApiResponse(SUCCESS, submissionStatusRepo.create(submissionStatus));
    }

    @RequestMapping("/all")
    @PreAuthorize("hasRole('REVIEWER')")
    public ApiResponse getAllSubmissionStatuses() {
        return new ApiResponse(SUCCESS, submissionStatusRepo.findAll());
    }

    @RequestMapping("/default/{submissionState}")
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse getDefault(@PathVariable SubmissionState submissionState) {
        return new ApiResponse(SUCCESS, submissionStatusRepo.findBySubmissionStateAndIsDefaultTrue(submissionState));
    }

    @RequestMapping("/update")
    @PreAuthorize("hasRole('MANAGER')")
    @WeaverValidation(business = { @WeaverValidation.Business(value = UPDATE) })
    public ApiResponse updateSubmissionStatus(@WeaverValidatedModel SubmissionStatus submissionStatus) {
        SubmissionStatus defaultSubmissionStatus = null;

        if (submissionStatus.isDefault()) {
            defaultSubmissionStatus = submissionStatusRepo.findBySubmissionStateAndIsDefaultTrue(submissionStatus.getSubmissionState());
        }

        ApiResponse response = new ApiResponse(SUCCESS, submissionStatusRepo.update(submissionStatus));

        // broadcast that the previous default has been changed.
        if (submissionStatus.isDefault()) {
            defaultSubmissionStatus = submissionStatusRepo.findOne(defaultSubmissionStatus.getId());
            simpMessagingTemplate.convertAndSend("/channel/submission-status", new ApiResponse(SUCCESS, ApiAction.UPDATE, defaultSubmissionStatus));
        }

        return response;
    }

    @RequestMapping("/delete")
    @PreAuthorize("hasRole('MANAGER')")
    @WeaverValidation(business = { @WeaverValidation.Business(value = DELETE) })
    public ApiResponse deleteSubmissionStatus(@WeaverValidatedModel SubmissionStatus submissionStatus) {
        submissionStatusRepo.delete(submissionStatus);
        submissionStatusRepo.flush();

        if (!submissionStatusRepo.exists(submissionStatus.getId())) {
            simpMessagingTemplate.convertAndSend("/channel/submission-status", new ApiResponse(SUCCESS, ApiAction.DELETE, submissionStatus));
            return new ApiResponse(SUCCESS, ApiAction.DELETE, "Submission Status " + submissionStatus.getName() + " has been deleted!");
        }

        return new ApiResponse(ERROR, ApiAction.DELETE, "Submission Status " + submissionStatus.getName() + " could not be deleted!");
    }

}
