package org.tdl.vireo.model.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.SubmissionState;
import org.tdl.vireo.model.User;
import org.tdl.vireo.model.repo.SubmissionRepo;
import org.tdl.vireo.model.repo.custom.SubmissionRepoCustom;

public class SubmissionRepoImpl implements SubmissionRepoCustom {

    @Autowired
    private SubmissionRepo submissionRepo;

    @Override
    public Submission create(User submitter, SubmissionState state) {
        return submissionRepo.save(new Submission(submitter, state));
    }

}
