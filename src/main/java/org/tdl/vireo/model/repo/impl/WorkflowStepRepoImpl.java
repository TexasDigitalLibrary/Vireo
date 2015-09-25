package org.tdl.vireo.model.repo.impl;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.WorkflowStep;
import org.tdl.vireo.model.repo.WorkflowStepRepo;
import org.tdl.vireo.model.repo.custom.WorkflowStepRepoCustom;

public class WorkflowStepRepoImpl implements WorkflowStepRepoCustom {

	@PersistenceContext
	private EntityManager entityManager;
	
	@Autowired
	private WorkflowStepRepo workflowStepRepo;
	
	@Override
	public WorkflowStep create(String name) {
		return workflowStepRepo.save(new WorkflowStep(name));
	}
	
}
