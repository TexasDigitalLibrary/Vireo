package org.tdl.vireo.model.repo.impl;

import org.tdl.vireo.model.OrganizationCategory;
import org.tdl.vireo.model.repo.OrganizationCategoryRepo;
import org.tdl.vireo.model.repo.custom.OrganizationCategoryRepoCustom;

import edu.tamu.weaver.data.model.repo.impl.AbstractWeaverRepoImpl;

public class OrganizationCategoryRepoImpl extends AbstractWeaverRepoImpl<OrganizationCategory, OrganizationCategoryRepo> implements OrganizationCategoryRepoCustom {

    @Override
    public OrganizationCategory create(String name) {
        return super.create(new OrganizationCategory(name));
    }

    @Override
    public void delete(OrganizationCategory organizationCategory) {
        super.delete(organizationCategory);
    }

    @Override
    protected String getChannel() {
        return "/channel/organization-category";
    }

}
