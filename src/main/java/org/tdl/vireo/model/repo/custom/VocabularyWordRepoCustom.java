package org.tdl.vireo.model.repo.custom;

import org.tdl.vireo.model.VocabularyWord;

public interface VocabularyWordRepoCustom {

    public VocabularyWord create(String name);
    
    public VocabularyWord create(String name, String definition);
    
    public VocabularyWord create(String name, String definition, String identifier);

}
