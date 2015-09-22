package org.tdl.vireo.model.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.SubmissionState;
import org.tdl.vireo.model.repo.SubmissionRepo;
import org.tdl.vireo.model.repo.custom.SubmissionRepoCustom;

public class SubmissionRepoImpl implements SubmissionRepoCustom {

	@Autowired
	private SubmissionRepo submissionRepo;
	
	@Override
	//TODO: must be create with arguments state and person
	public Submission create(SubmissionState state) {
		return submissionRepo.save(new Submission(state));
	}
	
//	@Override
//	public Submission update(Submission submission) {	
//		return submissionRepo.update(submission);
//	}
//	
//	@Override
//	public void delete(Submission submission) {
//		submissionRepo.delete(submission);
//	}
	
}
