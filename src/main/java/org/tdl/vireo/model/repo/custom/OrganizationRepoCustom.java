package org.tdl.vireo.model.repo.custom;

import org.tdl.vireo.model.Organization;
import org.tdl.vireo.model.OrganizationCategory;
import org.tdl.vireo.model.WorkflowStep;

public interface OrganizationRepoCustom {

    public Organization create(String name, OrganizationCategory category);
    
    public Organization create(String name, Organization parent, OrganizationCategory category);
    
    public Organization reorderWorkflowSteps(Organization organization, WorkflowStep ws1, WorkflowStep ws2);

    public void delete(Organization organization);

}
