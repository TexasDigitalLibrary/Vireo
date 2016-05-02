package org.tdl.vireo.model.repo.custom;

import org.tdl.vireo.model.ControlledVocabulary;
import org.tdl.vireo.model.Language;

import edu.tamu.framework.validation.ModelBindingResult;

public interface ControlledVocabularyRepoCustom {

    public ControlledVocabulary create(String name, Language language);
    
    public ControlledVocabulary create(String name, String entityName, Language language);
    
    public void reorder(Long src, Long dest);
    
    public void sort(String column);
    
    public void remove(ControlledVocabulary controlledVocabulary);
    
    public ControlledVocabulary validateCreate(ControlledVocabulary controlledVocabulary);
    
    public ControlledVocabulary validateUpdate(ControlledVocabulary controlledVocabulary);
    
    public ControlledVocabulary validateRemove(String idString, ModelBindingResult modelBindingResult);
    
    public ControlledVocabulary validateExport(String name, ModelBindingResult modelBindingResult);
    
    public ControlledVocabulary validateCompareCV(String name, ModelBindingResult modelBindingResult);
    
    public String[] validateCompareRows(Object inputStream, ModelBindingResult modelBindingResult);
    
    public ControlledVocabulary validateImport(String name, ModelBindingResult modelBindingResult);
}
