package org.tdl.vireo.model.repo.impl;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.Workflow;
import org.tdl.vireo.model.repo.WorkflowRepo;
import org.tdl.vireo.model.repo.custom.WorkflowRepoCustom;

public class WorkflowRepoImpl implements WorkflowRepoCustom {

	@PersistenceContext
	private EntityManager entityManager;
	
	@Autowired
	private WorkflowRepo workflowRepo;
	
	@Override
	public Workflow create(String name, Boolean inheritable) {
		return workflowRepo.save(new Workflow(name, inheritable));
	}
	
}
