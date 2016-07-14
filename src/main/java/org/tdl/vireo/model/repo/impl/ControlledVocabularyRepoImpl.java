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
        ControlledVocabulary controlledVocabulary = new ControlledVocabulary(name, language);
        controlledVocabulary.setPosition(controlledVocabularyRepo.count() + 1);
        return controlledVocabularyRepo.save(controlledVocabulary);
    }
    
    @Override
    public ControlledVocabulary create(String name, String entityName, Language language) {
        ControlledVocabulary controlledVocabulary = new ControlledVocabulary(name, entityName, language);
        controlledVocabulary.setPosition(controlledVocabularyRepo.count() + 1);
        return controlledVocabularyRepo.save(controlledVocabulary);
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
