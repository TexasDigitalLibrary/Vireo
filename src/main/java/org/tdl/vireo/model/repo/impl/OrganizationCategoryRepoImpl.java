package org.tdl.vireo.model.repo.impl;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.OrganizationCategory;
import org.tdl.vireo.model.repo.OrganizationCategoryRepo;
import org.tdl.vireo.model.repo.custom.OrganizationCategoryRepoCustom;

public class OrganizationCategoryRepoImpl implements OrganizationCategoryRepoCustom {

	@PersistenceContext
	private EntityManager entityManager;
	
	@Autowired
	private OrganizationCategoryRepo organizationCategoryRepo;
	
	@Override
	public OrganizationCategory create(String name, int level) {
		OrganizationCategory organizationCategory = organizationCategoryRepo.findByNameAndLevel(name, level);
		if(organizationCategory == null) {
			return organizationCategoryRepo.save(new OrganizationCategory(name, level));
		}
		return organizationCategory;
	}
	
}
