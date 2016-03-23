package org.tdl.vireo.model.repo.custom;

import org.tdl.vireo.model.Language;

public interface LanguageRepoCustom {

    public Language create(String name);
    
    public Language create(String name, Integer order);
    
    public void reorder(Integer src, Integer dest);
    
    public void sort(String column);
    
    public void remove(Integer index);

}
