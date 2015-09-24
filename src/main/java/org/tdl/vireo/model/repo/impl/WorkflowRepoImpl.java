package org.tdl.vireo.model.repo.impl;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.Workflow;
import org.tdl.vireo.model.WorkflowStep;
import org.tdl.vireo.model.repo.WorkflowRepo;
import org.tdl.vireo.model.repo.WorkflowStepRepo;
import org.tdl.vireo.model.repo.custom.WorkflowRepoCustom;

public class WorkflowRepoImpl implements WorkflowRepoCustom {

	@PersistenceContext
	private EntityManager entityManager;
	
	@Autowired
	private WorkflowRepo workflowRepo;
	
	@Autowired
	private WorkflowStepRepo workflowStepRepo;
	
	@Override
	public Workflow create(String name, Boolean inheritable) {
		return workflowRepo.save(new Workflow(name, inheritable));
	}
	
	@Override
	public Workflow update(Workflow newWorkflow) {		
		Workflow oldWorkflow = workflowRepo.findOne(newWorkflow.getId());
		newWorkflow = workflowRepo.save(newWorkflow);
		for(WorkflowStep workflowStep : oldWorkflow.getWorkflowSteps()) {
			if(newWorkflow.getWorkflowStepById(workflowStep.getId()) == null) {
				workflowStepRepo.delete(workflowStep);
			}
		}
		return newWorkflow;
	}
	
	@Override
	@Transactional
	public void delete(Workflow workflow) {
		entityManager.remove(entityManager.contains(workflow) ? workflow : entityManager.merge(workflow));
	}
	
}
