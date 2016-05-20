package org.tdl.vireo.model.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tdl.vireo.model.Organization;
import org.tdl.vireo.model.WorkflowStep;
import org.tdl.vireo.model.repo.custom.WorkflowStepRepoCustom;

public interface WorkflowStepRepo extends JpaRepository<WorkflowStep, Long>, WorkflowStepRepoCustom {

    public List<WorkflowStep> findByName(String name);
    
    public WorkflowStep findByNameAndOriginatingOrganizationId(String name, Long originatingOrganizationId);
    
    public List<WorkflowStep> findByOriginatingOrganization(Organization originatingOrganization);
    
    public List<WorkflowStep> findByOriginatingWorkflowStep(WorkflowStep originatingWorkflowStep);
    
    public void delete(WorkflowStep workflowStep);
    
}
