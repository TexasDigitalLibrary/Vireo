package org.tdl.vireo.model.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.Organization;
import org.tdl.vireo.model.WorkflowStep;
import org.tdl.vireo.model.repo.WorkflowStepRepo;
import org.tdl.vireo.model.repo.custom.WorkflowStepRepoCustom;

public class WorkflowStepRepoImpl implements WorkflowStepRepoCustom {

    @Autowired
    private WorkflowStepRepo workflowStepRepo;

    @Override
    public WorkflowStep create(String name, Organization owningOrginization) {
        return workflowStepRepo.save(new WorkflowStep(name, owningOrginization));
    }

}
