package org.tdl.vireo.model.repo.impl;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.Organization;
import org.tdl.vireo.model.OrganizationCategory;
import org.tdl.vireo.model.repo.OrganizationCategoryRepo;
import org.tdl.vireo.model.repo.OrganizationRepo;
import org.tdl.vireo.model.repo.custom.OrganizationRepoCustom;

public class OrganizationRepoImpl implements OrganizationRepoCustom {
	
	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private OrganizationRepo organizationRepo;
	
	@Autowired
	private OrganizationCategoryRepo organizationCategoryRepo;
	
	@Override
	public Organization create(String name, OrganizationCategory category) {
		Organization organization = organizationRepo.save(new Organization(name, category));
		category.addOrganization(organization);
		organizationCategoryRepo.save(category);
		return organization;
	}
	
	@Override
	public Organization addParent(Organization organization, Organization parentOrganization) {		
		parentOrganization.addChildOrganization(organization);		
		organizationRepo.save(parentOrganization);		
		organization.addParentOrganization(parentOrganization);		
		return organizationRepo.save(organization);
	}
	
	@Override
	public Organization addChild(Organization organization, Organization childOrganization) {		
		childOrganization.addParentOrganization(organization);		
		organizationRepo.save(childOrganization);		
		organization.addChildOrganization(childOrganization);		
		return organizationRepo.save(organization);
	}
	
//	@Override
//	public Organization update(Organization organization) {
//		return organizationRepo.update(organization);
//	}
	
	@Override
	@Transactional
	public void delete(Organization organization) {
		OrganizationCategory category = organization.getCategory();
		category.removeOrganization(organization);
		organizationCategoryRepo.save(category);
		entityManager.remove(entityManager.contains(organization) ? organization : entityManager.merge(organization));
	}
	
}
