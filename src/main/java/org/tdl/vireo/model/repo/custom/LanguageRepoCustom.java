package org.tdl.vireo.model.repo.custom;

import org.tdl.vireo.model.Language;

public interface LanguageRepoCustom {

    public Language create(String name);
        
    public void reorder(Long src, Long dest);
    
    public void sort(String column);
    
    public void remove(Language language);
    
    public Language validateCreate(Language language);
    
    public Language validateUpdate(Language language);

}
