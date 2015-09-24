package org.tdl.vireo.model.repo.custom;

import org.tdl.vireo.model.WorkflowStep;

public interface WorkflowStepRepoCustom {

	public WorkflowStep create(String name);
	
	public void delete(WorkflowStep workflowStep);
	
}
