package org.tdl.vireo.model.repo.custom;

import org.tdl.vireo.model.Organization;
import org.tdl.vireo.model.WorkflowStep;

public interface WorkflowStepRepoCustom {
        
    public WorkflowStep create(String name, Organization originatingOrganization);
    
    public WorkflowStep update(WorkflowStep workflowStep, Organization requestingOrganization);
    
    public void delete(WorkflowStep workflowStep);

}
