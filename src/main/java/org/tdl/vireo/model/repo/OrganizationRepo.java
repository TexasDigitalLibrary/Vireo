package org.tdl.vireo.model.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.tdl.vireo.model.Organization;
import org.tdl.vireo.model.OrganizationCategory;
import org.tdl.vireo.model.repo.custom.OrganizationRepoCustom;

@Repository
public interface OrganizationRepo extends JpaRepository<Organization, Long>, OrganizationRepoCustom {

	public Organization findByNameAndOrganizationCategory(String name, OrganizationCategory organizationCategory);
	
}
