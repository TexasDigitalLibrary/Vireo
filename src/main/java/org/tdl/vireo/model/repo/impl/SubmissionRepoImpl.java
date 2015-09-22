package org.tdl.vireo.model.repo.impl;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.SubmissionState;
import org.tdl.vireo.model.repo.SubmissionRepo;
import org.tdl.vireo.model.repo.custom.SubmissionRepoCustom;

public class SubmissionRepoImpl implements SubmissionRepoCustom {

	@PersistenceContext
	private EntityManager entityManager;
	
	@Autowired
	private SubmissionRepo submissionRepo;
	
	@Override
	//TODO: must be create with arguments state and person
	public Submission create(SubmissionState state) {
		return submissionRepo.save(new Submission(state));
	}
	
}
