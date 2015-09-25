package org.tdl.vireo.model.repo.custom;

import org.tdl.vireo.model.SubmissionState;

public interface SubmissionStateRepoCustom {

	public SubmissionState create(String name, Boolean archived, Boolean publishable, Boolean deletable, Boolean editableByReviewer, Boolean editableByStudent, Boolean active);
	
}
