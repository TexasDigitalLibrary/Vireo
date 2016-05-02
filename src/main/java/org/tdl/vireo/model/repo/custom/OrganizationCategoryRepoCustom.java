package org.tdl.vireo.model.repo.custom;

import org.tdl.vireo.model.OrganizationCategory;

import edu.tamu.framework.validation.ModelBindingResult;

public interface OrganizationCategoryRepoCustom {

    public OrganizationCategory create(String name);
    
    public void remove(OrganizationCategory organizationCategory);
    
    public OrganizationCategory validateCreate(OrganizationCategory organizationCategory);
    
    public OrganizationCategory validateUpdate(OrganizationCategory organizationCategory);
    
    public OrganizationCategory validateRemove(String idString, ModelBindingResult modelBindingResult);
}
