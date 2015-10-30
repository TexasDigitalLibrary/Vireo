package org.tdl.vireo.model.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.VocabularyWord;
import org.tdl.vireo.model.repo.VocabularyWordRepo;
import org.tdl.vireo.model.repo.custom.VocabularyWordRepoCustom;

public class VocabularyWordRepoImpl implements VocabularyWordRepoCustom {

    @Autowired
    VocabularyWordRepo vocabularyWordRepo;

    @Override
    public VocabularyWord create(String name) {
        return vocabularyWordRepo.save(new VocabularyWord(name));
    }
    
    @Override
    public VocabularyWord create(String name, String definition) {
        return vocabularyWordRepo.save(new VocabularyWord(name, definition));
    }
    
    @Override
    public VocabularyWord create(String name, String definition, String identifier) {
        return vocabularyWordRepo.save(new VocabularyWord(name, definition, identifier));
    }

}
