package org.tdl.vireo.model.repo.custom;

import org.tdl.vireo.model.Organization;
import org.tdl.vireo.model.WorkflowStep;
import org.tdl.vireo.model.repo.impl.WorkflowStepNonOverrideableException;

public interface WorkflowStepRepoCustom {
        
    public WorkflowStep create(String name, Organization originatingOrganization);

    public WorkflowStep update(WorkflowStep workflowStep, Organization requestingOrganization) throws WorkflowStepNonOverrideableException;
    
    public void delete(WorkflowStep workflowStep);

}
