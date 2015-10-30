package org.tdl.vireo.model.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tdl.vireo.model.Organization;
import org.tdl.vireo.model.OrganizationCategory;
import org.tdl.vireo.model.repo.custom.OrganizationRepoCustom;

public interface OrganizationRepo extends JpaRepository<Organization, Long>, OrganizationRepoCustom {

    public Organization findByNameAndCategory(String name, OrganizationCategory category);

    public void delete(Organization organization);

}
