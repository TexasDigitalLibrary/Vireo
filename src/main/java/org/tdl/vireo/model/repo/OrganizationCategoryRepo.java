package org.tdl.vireo.model.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tdl.vireo.model.OrganizationCategory;
import org.tdl.vireo.model.repo.custom.OrganizationCategoryRepoCustom;

public interface OrganizationCategoryRepo extends JpaRepository<OrganizationCategory, Long>, OrganizationCategoryRepoCustom {

    public OrganizationCategory findByName(String name);

}
