package org.tdl.vireo.model.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.ObjectError;
import org.tdl.vireo.model.CustomActionDefinition;
import org.tdl.vireo.model.repo.CustomActionDefinitionRepo;
import org.tdl.vireo.model.repo.custom.CustomActionDefinitionRepoCustom;
import org.tdl.vireo.service.OrderedEntityService;

public class CustomActionDefinitionRepoImpl implements CustomActionDefinitionRepoCustom {
    
    @Autowired
    private OrderedEntityService orderedEntityService;

    @Autowired
    private CustomActionDefinitionRepo customActionDefinitionRepo;
    
    @Override
    public CustomActionDefinition create(String label, Boolean isStudentVisible) {
        CustomActionDefinition customActionDefinition = new CustomActionDefinition(label, isStudentVisible);
        customActionDefinition.setPosition(customActionDefinitionRepo.count() + 1);
        return customActionDefinitionRepo.save(customActionDefinition);
    }
    
    @Override
    public void reorder(Long src, Long dest) {
        orderedEntityService.reorder(CustomActionDefinition.class, src, dest);
    }
    
    @Override
    public void remove(CustomActionDefinition customActionDefinition) {
        orderedEntityService.remove(customActionDefinitionRepo, CustomActionDefinition.class, customActionDefinition.getPosition());
    }
    
    @Override
    public CustomActionDefinition validateCreate(CustomActionDefinition customActionDefinition) {
        CustomActionDefinition existing = customActionDefinitionRepo.findByLabel(customActionDefinition.getLabel());
        if(!customActionDefinition.getBindingResult().hasErrors() &&  existing != null){
            customActionDefinition.getBindingResult().addError(new ObjectError("customActionDefinition", customActionDefinition.getLabel() + " is already a custom action!"));
        }
        return customActionDefinition;
    }
    
    @Override
    public CustomActionDefinition validateUpdate(CustomActionDefinition customActionDefinition) {
        if(customActionDefinition.getId() == null) {
            customActionDefinition.getBindingResult().addError(new ObjectError("customActionDefinition", "Cannot update a CustomActionDefinition without an id!"));
        } else {
            CustomActionDefinition customActionDefinitionToUpdate = customActionDefinitionRepo.findOne(customActionDefinition.getId());
            CustomActionDefinition customActionDefinitionUnique = customActionDefinitionRepo.findByLabel(customActionDefinition.getLabel());
            if(customActionDefinitionToUpdate == null) {
                customActionDefinition.getBindingResult().addError(new ObjectError("customActionDefinition", "Cannot update a CustomActionDefinition with invalid id!"));
            } else if(customActionDefinitionUnique != null) {
                customActionDefinition.getBindingResult().addError(new ObjectError("customActionDefinition", "Cannot update a CustomActionDefinition with label already in use by another!"));
            } else {
                customActionDefinitionToUpdate.setBindingResult(customActionDefinition.getBindingResult());
                customActionDefinitionToUpdate.setLabel(customActionDefinition.getLabel());
                customActionDefinitionToUpdate.isStudentVisible(customActionDefinition.isStudentVisible());
                customActionDefinition = customActionDefinitionToUpdate;
            }
        }
        
        return customActionDefinition;
    }

}
