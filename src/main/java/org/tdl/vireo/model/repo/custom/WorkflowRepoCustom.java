package org.tdl.vireo.model.repo.custom;

import org.tdl.vireo.model.Workflow;

public interface WorkflowRepoCustom {

	public Workflow create(String name, Boolean inheritable);
	
	public Workflow update(Workflow workflow);
	
	public void delete(Workflow workflow);
	
}
