package org.tdl.vireo.model.repo.custom;

import org.tdl.vireo.model.Workflow;
import org.tdl.vireo.model.WorkflowStep;

public interface WorkflowStepRepoCustom {

    public WorkflowStep create(String name, Workflow workflow);

}
