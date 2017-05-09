package org.tdl.vireo.model.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.OrganizationCategory;
import org.tdl.vireo.model.repo.OrganizationCategoryRepo;
import org.tdl.vireo.model.repo.custom.OrganizationCategoryRepoCustom;

public class OrganizationCategoryRepoImpl implements OrganizationCategoryRepoCustom {

    @Autowired
    private OrganizationCategoryRepo organizationCategoryRepo;

    @Override
    public OrganizationCategory create(String name) {
        return organizationCategoryRepo.save(new OrganizationCategory(name));
    }

    @Override
    public void remove(OrganizationCategory organizationCategory) {
        organizationCategoryRepo.delete(organizationCategory);
    }

}
