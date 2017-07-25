package org.tdl.vireo.model.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.enums.SubmissionState;
import org.tdl.vireo.model.SubmissionStatus;
import org.tdl.vireo.model.repo.SubmissionStatusRepo;
import org.tdl.vireo.model.repo.custom.SubmissionStatusRepoCustom;

public class SubmissionStatusRepoImpl implements SubmissionStatusRepoCustom {

    @Autowired
    private SubmissionStatusRepo submissionStateRepo;

    @Override
    public SubmissionStatus create(String name, Boolean archived, Boolean publishable, Boolean deletable, Boolean editableByReviewer, Boolean editableByStudent, Boolean active, SubmissionState submissionState) {
        return submissionStateRepo.save(new SubmissionStatus(name, archived, publishable, deletable, editableByReviewer, editableByStudent, active, submissionState = submissionState == null ? SubmissionState.NONE : submissionState));
    }

}
