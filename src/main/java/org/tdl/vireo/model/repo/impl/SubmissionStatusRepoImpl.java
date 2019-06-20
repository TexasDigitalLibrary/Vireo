package org.tdl.vireo.model.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.SubmissionState;
import org.tdl.vireo.model.SubmissionStatus;
import org.tdl.vireo.model.repo.SubmissionStatusRepo;
import org.tdl.vireo.model.repo.custom.SubmissionStatusRepoCustom;

import edu.tamu.weaver.data.model.repo.impl.AbstractWeaverRepoImpl;

public class SubmissionStatusRepoImpl extends AbstractWeaverRepoImpl<SubmissionStatus, SubmissionStatusRepo> implements SubmissionStatusRepoCustom {

    @Autowired
    private SubmissionStatusRepo submissionStatusRepo;

    @Override
    public SubmissionStatus create(String name, Boolean archived, Boolean publishable, Boolean deletable, Boolean editableByReviewer, Boolean editableByStudent, Boolean active, Boolean isDefault, SubmissionState submissionState) {
        if (isDefault == true) {
            submissionStatusRepo.updateDefaultsToFalse(submissionState);
        }

        return submissionStatusRepo.save(new SubmissionStatus(name, archived, publishable, deletable, editableByReviewer, editableByStudent, active, isDefault, submissionState == null ? SubmissionState.NONE : submissionState));
    }

    @Override
    public SubmissionStatus update(SubmissionStatus submissionStatus) {
        if (submissionStatus.isDefault() == true) {
            submissionStatusRepo.updateDefaultsToFalse(submissionStatus.getSubmissionState());
        }

        return super.update(submissionStatus);
    }

    @Override
    public void delete(SubmissionStatus submissionStatus) {
        submissionStatusRepo.deletePreserveDefault(submissionStatus.getId());
    }

    @Override
    protected String getChannel() {
        return "/channel/submission-status";
    }

}
