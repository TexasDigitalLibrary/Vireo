package org.tdl.vireo.model.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.tdl.vireo.model.Workflow;
import org.tdl.vireo.model.repo.custom.WorkflowRepoCustom;

@Repository
public interface WorkflowRepo extends JpaRepository<Workflow, Long>, WorkflowRepoCustom {

	public Workflow create(String name, Boolean inheritable);
	
	public Workflow update(Workflow workflow);
	
	public void delete(Workflow workflow);
	
}
