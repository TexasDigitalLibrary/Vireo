package org.tdl.vireo.model.repo.custom;

import org.tdl.vireo.model.Organization;
import org.tdl.vireo.model.Workflow;

public interface WorkflowRepoCustom {

    public Workflow create(String name, Boolean inheritable, Organization organization);

}
