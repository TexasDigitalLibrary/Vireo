package org.tdl.vireo.model.repo.custom;

import org.tdl.vireo.model.SubmissionState;
import org.tdl.vireo.model.SubmissionStatus;

public interface SubmissionStatusRepoCustom {

    public SubmissionStatus create(String name, Boolean archived, Boolean publishable, Boolean deletable, Boolean editableByReviewer, Boolean editableByStudent, Boolean active, SubmissionState submissionState);

}
