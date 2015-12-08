package org.tdl.vireo.model.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tdl.vireo.model.Organization;
import org.tdl.vireo.model.Workflow;
import org.tdl.vireo.model.repo.custom.WorkflowRepoCustom;

public interface WorkflowRepo extends JpaRepository<Workflow, Long>, WorkflowRepoCustom {

    public List<Workflow> findByName(String name);
    
    public Workflow findByNameAndOrganization(String name, Organization organization);
    
}
