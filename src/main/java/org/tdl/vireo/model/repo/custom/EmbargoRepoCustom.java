package org.tdl.vireo.model.repo.custom;

import org.tdl.vireo.enums.EmbargoGuarantor;
import org.tdl.vireo.model.Embargo;

import edu.tamu.framework.validation.ModelBindingResult;

public interface EmbargoRepoCustom {
    
    public Embargo create(String name, String description, Integer duration, EmbargoGuarantor guarantor, boolean isActive);
    
    public void reorder(Long src, Long dest, EmbargoGuarantor guarantor);
    
    public void sort(String column, EmbargoGuarantor guarantor);
    
    public void remove(Long index);
    
    public Embargo validateCreate(Embargo embargo);
    
    public Embargo validateUpdate(Embargo embargo);
    
    public Embargo validateRemove(String idString, ModelBindingResult modelBindingResult);
}
