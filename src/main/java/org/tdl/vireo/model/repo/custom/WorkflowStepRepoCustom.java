package org.tdl.vireo.model.repo.custom;

import org.tdl.vireo.model.Organization;
import org.tdl.vireo.model.WorkflowStep;

public interface WorkflowStepRepoCustom {

    public WorkflowStep create(String name, Organization owningOrganization);

    void delete(WorkflowStep workflowStep);

    WorkflowStep update(WorkflowStep workflowStep, Organization originatingOrganization);

}
