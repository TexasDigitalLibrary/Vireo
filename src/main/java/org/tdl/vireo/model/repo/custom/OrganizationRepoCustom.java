package org.tdl.vireo.model.repo.custom;

import org.tdl.vireo.model.Organization;
import org.tdl.vireo.model.OrganizationCategory;

public interface OrganizationRepoCustom {

    public Organization create(String name, OrganizationCategory category);
    
    public Organization create(String name, Organization parent, OrganizationCategory category);

    public void delete(Organization organization);

}
