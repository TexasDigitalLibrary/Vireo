package org.tdl.vireo.model.repo.custom;

import org.tdl.vireo.model.CustomActionDefinition;

import edu.tamu.framework.validation.ModelBindingResult;

public interface CustomActionDefinitionRepoCustom {

    public CustomActionDefinition create(String label, Boolean isStudentVisible);
    
    public void reorder(Long src, Long dest);
    
    public void remove(CustomActionDefinition customActionDefinition);
    
    public CustomActionDefinition validateCreate(CustomActionDefinition customActionDefinition);
    
    public CustomActionDefinition validateUpdate(CustomActionDefinition customActionDefinition);
    
    public CustomActionDefinition validateRemove(String idString, ModelBindingResult modelBindingResult);
}
