package org.tdl.vireo.model.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.ControlledVocabulary;
import org.tdl.vireo.model.Language;
import org.tdl.vireo.model.repo.ControlledVocabularyRepo;
import org.tdl.vireo.model.repo.custom.ControlledVocabularyRepoCustom;

import edu.tamu.framework.service.OrderedEntityService;

public class ControlledVocabularyRepoImpl implements ControlledVocabularyRepoCustom {

    @Autowired
    private OrderedEntityService orderedEntityService;

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
    public void reorder(Long src, Long dest) {
        orderedEntityService.reorder(ControlledVocabulary.class, src, dest);
    }

    @Override
    public void sort(String column) {
        orderedEntityService.sort(ControlledVocabulary.class, column);
    }

    @Override
    public void remove(ControlledVocabulary controlledVocabulary) {
        orderedEntityService.remove(controlledVocabularyRepo, ControlledVocabulary.class, controlledVocabulary.getPosition());
    }

}
