package org.tdl.vireo.model.repo.impl;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.Workflow;
import org.tdl.vireo.model.repo.WorkflowRepo;
import org.tdl.vireo.model.repo.custom.WorkflowRepoCustom;

public class WorkflowRepoImpl implements WorkflowRepoCustom {

	@PersistenceContext
	private EntityManager entityManager;
	
	@Autowired
	private WorkflowRepo WorkflowRepo;
	
	@Override
	public Workflow create(String name, Boolean inheritable) {
		return WorkflowRepo.save(new Workflow(name, inheritable));
	}
	
	@Override
	@Transactional
	public void delete(Workflow workflow) {
		entityManager.remove(entityManager.contains(workflow) ? workflow : entityManager.merge(workflow));
	}
	
}
