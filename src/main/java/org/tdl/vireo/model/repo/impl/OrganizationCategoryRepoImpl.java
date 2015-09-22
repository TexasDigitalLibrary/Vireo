package org.tdl.vireo.model.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.OrganizationCategory;
import org.tdl.vireo.model.repo.OrganizationCategoryRepo;
import org.tdl.vireo.model.repo.custom.OrganizationCategoryRepoCustom;

public class OrganizationCategoryRepoImpl implements OrganizationCategoryRepoCustom {

	@Autowired
	private OrganizationCategoryRepo organizationCategoryRepo;
	
	@Override
	public OrganizationCategory create(String name, int level) {
		return organizationCategoryRepo.save(new OrganizationCategory(name, level));
	}
	
	@Override
	public OrganizationCategory update(OrganizationCategory category) {		
		return organizationCategoryRepo.update(category);
	}
	
	@Override
	public void delete(OrganizationCategory category) {
		organizationCategoryRepo.delete(category);
	}
	
}
