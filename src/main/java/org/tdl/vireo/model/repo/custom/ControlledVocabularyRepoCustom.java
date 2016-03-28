package org.tdl.vireo.model.repo.custom;

import org.tdl.vireo.model.ControlledVocabulary;
import org.tdl.vireo.model.Language;

public interface ControlledVocabularyRepoCustom {

    public ControlledVocabulary create(String name, Language language);
    
    public ControlledVocabulary create(String name, String entityName, Language language);
    
    public void reorder(Integer src, Integer dest);
    
    public void sort(String column);
    
    public void remove(Integer index);

}
