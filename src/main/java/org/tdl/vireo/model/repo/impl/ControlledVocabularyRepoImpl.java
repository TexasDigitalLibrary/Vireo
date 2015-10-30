package org.tdl.vireo.model.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.ControlledVocabulary;
import org.tdl.vireo.model.Language;
import org.tdl.vireo.model.repo.ControlledVocabularyRepo;
import org.tdl.vireo.model.repo.custom.ControlledVocabularyRepoCustom;

public class ControlledVocabularyRepoImpl implements ControlledVocabularyRepoCustom {

    @Autowired
    private ControlledVocabularyRepo controlledVocabularyRepo;

    @Override
    public ControlledVocabulary create(String name, Language language) {
        return controlledVocabularyRepo.save(new ControlledVocabulary(name, language));
    }
    
    @Override
    public ControlledVocabulary create(String name, String entityName, Language language) {
        return controlledVocabularyRepo.save(new ControlledVocabulary(name, entityName, language));
    }

}
