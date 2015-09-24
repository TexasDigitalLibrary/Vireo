package org.tdl.vireo.model.repo.impl;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.SubmissionState;
import org.tdl.vireo.model.repo.SubmissionStateRepo;
import org.tdl.vireo.model.repo.custom.SubmissionStateRepoCustom;

public class SubmissionStateRepoImpl implements SubmissionStateRepoCustom {

	@PersistenceContext
	private EntityManager entityManager;
	
	@Autowired
	private SubmissionStateRepo submissionStateRepo;
	
	@Override
	//TODO: must be create with arguments state and person
	public SubmissionState create(String name, Boolean archived, Boolean publishable, Boolean deletable, Boolean editableByReviewer, Boolean editableByStudent, Boolean active) {
		return submissionStateRepo.save(new SubmissionState(name, archived, publishable, deletable, editableByReviewer, editableByStudent, active));
	}
	
	@Override
	@Transactional
	public void delete(SubmissionState submissionState) {
		entityManager.remove(entityManager.contains(submissionState) ? submissionState : entityManager.merge(submissionState));
	}
	
}
