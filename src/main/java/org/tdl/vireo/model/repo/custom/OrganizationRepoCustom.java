package org.tdl.vireo.model.repo.custom;

import org.tdl.vireo.model.Organization;
import org.tdl.vireo.model.OrganizationCategory;

public interface OrganizationRepoCustom {

	public Organization create(String name, OrganizationCategory category);
	
	public Organization addParent(Organization organization, Organization parentOrganization);
	
	public Organization addChild(Organization organization, Organization childOrganization);
	
//	public Organization update(Organization organization);
	
	public void delete(Organization organization);
	
}
