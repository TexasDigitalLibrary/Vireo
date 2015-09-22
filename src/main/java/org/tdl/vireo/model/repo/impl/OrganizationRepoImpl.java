package org.tdl.vireo.model.repo.impl;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.Organization;
import org.tdl.vireo.model.OrganizationCategory;
import org.tdl.vireo.model.repo.OrganizationRepo;
import org.tdl.vireo.model.repo.custom.OrganizationRepoCustom;

public class OrganizationRepoImpl implements OrganizationRepoCustom {

	@PersistenceContext
	private EntityManager entityManager;
	
	@Autowired
	private OrganizationRepo organizationRepo;
	
	@Override
	public Organization create(String name, OrganizationCategory category) {
		Organization organization = organizationRepo.findByNameAndCategory(name, category);
		if(organization == null) {
			return organizationRepo.save(new Organization(name, category));
		}
		return organization;
	}
	
}
