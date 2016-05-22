package org.tdl.vireo.model.repo.impl;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.Organization;
import org.tdl.vireo.model.OrganizationCategory;
import org.tdl.vireo.model.repo.OrganizationCategoryRepo;
import org.tdl.vireo.model.repo.OrganizationRepo;
import org.tdl.vireo.model.repo.custom.OrganizationRepoCustom;

public class OrganizationRepoImpl implements OrganizationRepoCustom {
	
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
    public Organization create(String name, Organization parent, OrganizationCategory category) {       
        Organization organization = create(name, category);
        parent.addChildOrganization(organization);
        organizationRepo.save(parent);
        parent.getWorkflow().forEach(ws -> {
            organization.addStepToWorkflow(ws);
        });
        return  organization;
    }

    @Override
    public void delete(Organization organization) {
        OrganizationCategory category = organization.getCategory();
        category.removeOrganization(organization);
        organizationCategoryRepo.save(category);
        
        Set<Organization> parentOrganizations = organization.getParentOrganizations();
        
        // reconstructing tree when intermediate organization removed
        // i.e. grandparent becomes new parent of children
        organization.getChildrenOrganizations().parallelStream().forEach(childOrganization -> {
            childOrganization.removeParentOrganization(organization);
            organizationRepo.save(childOrganization);
            
            parentOrganizations.parallelStream().forEach(parentOrganization -> {
                parentOrganization.addChildOrganization(childOrganization);
                organizationRepo.save(parentOrganization);
            });
            
        });

        organization.getParentOrganizations().parallelStream().forEach(parentOrganization -> {
            parentOrganization.removeChildOrganization(organization);
            organizationRepo.save(parentOrganization);
        });
        
        organizationRepo.delete(organization.getId());
    }

}
