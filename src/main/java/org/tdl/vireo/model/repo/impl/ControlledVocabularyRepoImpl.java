package org.tdl.vireo.model.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.ControlledVocabulary;
import org.tdl.vireo.model.Language;
import org.tdl.vireo.model.repo.ControlledVocabularyRepo;
import org.tdl.vireo.model.repo.custom.ControlledVocabularyRepoCustom;

import edu.tamu.weaver.data.model.repo.impl.AbstractWeaverOrderedRepoImpl;

public class ControlledVocabularyRepoImpl extends AbstractWeaverOrderedRepoImpl<ControlledVocabulary, ControlledVocabularyRepo> implements ControlledVocabularyRepoCustom {

    @Autowired
    private ControlledVocabularyRepo controlledVocabularyRepo;

    @Override
    public ControlledVocabulary create(String name, Language language) {
        ControlledVocabulary controlledVocabulary = controlledVocabularyRepo.findByNameAndLanguage(name, language);
        if (controlledVocabulary == null) {
            controlledVocabulary = new ControlledVocabulary(name, language);
            controlledVocabulary.setPosition(controlledVocabularyRepo.count() + 1);
            controlledVocabulary = controlledVocabularyRepo.save(controlledVocabulary);
        }
        return controlledVocabulary;
    }

    @Override
    public ControlledVocabulary create(String name, Language language, Boolean isEntityProperty) {
        ControlledVocabulary controlledVocabulary = controlledVocabularyRepo.findByNameAndLanguageAndIsEntityProperty(name, language, isEntityProperty);
        if (controlledVocabulary == null) {
            controlledVocabulary = new ControlledVocabulary(name, language, isEntityProperty);
            controlledVocabulary.setPosition(controlledVocabularyRepo.count() + 1);
            controlledVocabulary = controlledVocabularyRepo.save(controlledVocabulary);
        }
        return controlledVocabulary;
    }

    @Override
    public Class<?> getModelClass() {
        return ControlledVocabulary.class;
    }

    @Override
    protected String getChannel() {
        return "/channel/controlled-vocabulary";
    }

}
