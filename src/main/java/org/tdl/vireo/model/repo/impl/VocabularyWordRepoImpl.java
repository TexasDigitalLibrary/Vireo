package org.tdl.vireo.model.repo.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.ControlledVocabulary;
import org.tdl.vireo.model.VocabularyWord;
import org.tdl.vireo.model.repo.ControlledVocabularyRepo;
import org.tdl.vireo.model.repo.VocabularyWordRepo;
import org.tdl.vireo.model.repo.custom.VocabularyWordRepoCustom;

public class VocabularyWordRepoImpl implements VocabularyWordRepoCustom {

    @Autowired
    private VocabularyWordRepo vocabularyWordRepo;

    @Autowired
    private ControlledVocabularyRepo controlledVocabularyRepo;

    @Override
    public VocabularyWord create(ControlledVocabulary controlledVocabulary, String name, String definition, String identifier) {
        controlledVocabulary.addValue(new VocabularyWord(controlledVocabulary, name, definition, identifier));
        controlledVocabulary = controlledVocabularyRepo.save(controlledVocabulary);
        return vocabularyWordRepo.findByNameAndControlledVocabulary(name, controlledVocabulary);
    }

    @Override
    public VocabularyWord create(ControlledVocabulary controlledVocabulary, String name, String definition, String identifier, List<String> contacts) {
        controlledVocabulary.addValue(new VocabularyWord(controlledVocabulary, name, definition, identifier, contacts));
        controlledVocabulary = controlledVocabularyRepo.save(controlledVocabulary);
        return vocabularyWordRepo.findByNameAndControlledVocabulary(name, controlledVocabulary);
    }

    @Override
    public void delete(VocabularyWord vocabularyWord) {
        ControlledVocabulary cv = vocabularyWord.getControlledVocabulary();

        cv.removeValue(vocabularyWord);

        cv = controlledVocabularyRepo.save(cv);
    }

}
