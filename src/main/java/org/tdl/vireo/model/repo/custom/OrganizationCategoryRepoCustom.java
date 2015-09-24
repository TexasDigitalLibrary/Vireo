package org.tdl.vireo.model.repo.custom;

import org.tdl.vireo.model.OrganizationCategory;

public interface OrganizationCategoryRepoCustom {

	public OrganizationCategory create(String name, int level);
	
	public void delete(OrganizationCategory organizationCategory);
	
}
