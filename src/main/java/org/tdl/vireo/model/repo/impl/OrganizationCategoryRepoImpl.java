package org.tdl.vireo.model.repo.impl;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

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
		return organizationCategoryRepo.save(new OrganizationCategory(name, level));
	}
	
	@Override
	@Transactional
	public void delete(OrganizationCategory organizationCategory) {
		entityManager.remove(entityManager.contains(organizationCategory) ? organizationCategory : entityManager.merge(organizationCategory));
	}
	
}
