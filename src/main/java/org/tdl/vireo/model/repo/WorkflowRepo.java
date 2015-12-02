package org.tdl.vireo.model.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tdl.vireo.model.Workflow;
import org.tdl.vireo.model.repo.custom.WorkflowRepoCustom;

public interface WorkflowRepo extends JpaRepository<Workflow, Long>, WorkflowRepoCustom {

    public Workflow findByName(String name);
    
}
