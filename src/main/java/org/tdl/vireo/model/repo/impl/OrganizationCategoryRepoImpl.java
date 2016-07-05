package org.tdl.vireo.model.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.ObjectError;
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
    
    @Override
    public OrganizationCategory validateCreate(OrganizationCategory organizationCategory) {
        OrganizationCategory existing = organizationCategoryRepo.findByName(organizationCategory.getName());
        if(!organizationCategory.getBindingResult().hasErrors() &&  existing != null){
            organizationCategory.getBindingResult().addError(new ObjectError("organizationCategory", organizationCategory.getName() + " is already an organization category!"));
        }
        return organizationCategory;
    }
    
    @Override
    public OrganizationCategory validateUpdate(OrganizationCategory organizationCategory) {
        if(organizationCategory.getId() == null) {
            organizationCategory.getBindingResult().addError(new ObjectError("organizationCategory", "Cannot update a organization category without an id!"));
        } else {
            OrganizationCategory organizationCategoryToUpdate = organizationCategoryRepo.findOne(organizationCategory.getId());
            OrganizationCategory organizationCategoryUnique = organizationCategoryRepo.findByName(organizationCategory.getName());
            if(organizationCategoryToUpdate == null) {
                organizationCategory.getBindingResult().addError(new ObjectError("organizationCategory", "Cannot update a organization category with invalid id!"));
            } else if(organizationCategoryUnique != null) {
                organizationCategory.getBindingResult().addError(new ObjectError("organizationCategory", "Cannot update a organization category with name already in use by another!"));
            } else {
                organizationCategoryToUpdate.setBindingResult(organizationCategory.getBindingResult());
                organizationCategoryToUpdate.setName(organizationCategory.getName());
                organizationCategory = organizationCategoryToUpdate;
            }
        }
        
        return organizationCategory;
    }

}
