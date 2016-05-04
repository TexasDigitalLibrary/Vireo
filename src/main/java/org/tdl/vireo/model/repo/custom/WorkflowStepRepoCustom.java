package org.tdl.vireo.model.repo.custom;

import org.tdl.vireo.model.Organization;
import org.tdl.vireo.model.WorkflowStep;
import org.tdl.vireo.model.repo.impl.WorkflowStepNonOverrideableException;

public interface WorkflowStepRepoCustom {

    public WorkflowStep create(String name, Organization owningOrganization);

    void delete(WorkflowStep workflowStep);

    WorkflowStep update(WorkflowStep workflowStep, Organization originatingOrganization) throws WorkflowStepNonOverrideableException;

    WorkflowStep create(String name, Organization originatingOrganization, Integer orderIndex);

    WorkflowStep create(String name, Organization originatingOrganization, Integer orderIndex, WorkflowStep originatingWorkflowStep);

}
