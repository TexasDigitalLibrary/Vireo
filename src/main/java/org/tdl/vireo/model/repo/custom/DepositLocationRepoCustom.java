package org.tdl.vireo.model.repo.custom;

import org.tdl.vireo.model.DepositLocation;

import edu.tamu.framework.validation.ModelBindingResult;

public interface DepositLocationRepoCustom {

    public DepositLocation create(String name, String repository, String collection, String username, String password, String onBehalfOf, String packager, String depositor);
    
    public void reorder(Long src, Long dest);
    
    public void remove(DepositLocation depositLocation);
    
    public DepositLocation validateCreate(DepositLocation depositLocation);
    
    public DepositLocation validateUpdate(DepositLocation depositLocation);
    
    public DepositLocation validateRemove(String idString, ModelBindingResult modelBindingResult);
}
