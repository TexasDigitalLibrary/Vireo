package org.tdl.vireo.model.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.Organization;
import org.tdl.vireo.model.Workflow;
import org.tdl.vireo.model.repo.WorkflowRepo;
import org.tdl.vireo.model.repo.custom.WorkflowRepoCustom;

public class WorkflowRepoImpl implements WorkflowRepoCustom {

    @Autowired
    private WorkflowRepo workflowRepo;
    
    @Override
    public Workflow create(String name, Boolean inheritable, Organization organization) {
        return workflowRepo.save(new Workflow(name, inheritable, organization));
    }
    
}
