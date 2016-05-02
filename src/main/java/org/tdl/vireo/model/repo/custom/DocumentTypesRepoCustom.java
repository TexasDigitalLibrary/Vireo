package org.tdl.vireo.model.repo.custom;

import org.tdl.vireo.enums.DegreeLevel;
import org.tdl.vireo.model.DocumentType;

import edu.tamu.framework.validation.ModelBindingResult;

public interface DocumentTypesRepoCustom {

    public DocumentType create(String name, DegreeLevel degreeLevel);
    
    public void reorder(Long src, Long dest);
    
    public void sort(String column);

    public void remove(DocumentType documentType);
    
    public DocumentType validateCreate(DocumentType documentType);
    
    public DocumentType validateUpdate(DocumentType documentType);
    
    public DocumentType validateRemove(String idString, ModelBindingResult modelBindingResult);
}
