package org.tdl.vireo.model.repo;

import org.tdl.vireo.model.OrganizationCategory;
import org.tdl.vireo.model.repo.custom.OrganizationCategoryRepoCustom;

import edu.tamu.weaver.data.model.repo.WeaverRepo;

public interface OrganizationCategoryRepo extends WeaverRepo<OrganizationCategory>, OrganizationCategoryRepoCustom {

    public OrganizationCategory findByName(String name);

}
