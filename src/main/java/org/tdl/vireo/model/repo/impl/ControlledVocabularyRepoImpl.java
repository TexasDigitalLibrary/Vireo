package org.tdl.vireo.model.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.ControlledVocabulary;
import org.tdl.vireo.model.Language;
import org.tdl.vireo.model.repo.ControlledVocabularyRepo;
import org.tdl.vireo.model.repo.custom.ControlledVocabularyRepoCustom;
import org.tdl.vireo.service.OrderedEntityService;

public class ControlledVocabularyRepoImpl implements ControlledVocabularyRepoCustom {

    @Autowired
    private OrderedEntityService orderedEntityService;
    
    @Autowired
    private ControlledVocabularyRepo controlledVocabularyRepo;

    @Override
    public ControlledVocabulary create(String name, Language language) {
        return controlledVocabularyRepo.save(new ControlledVocabulary(name, language, (int) controlledVocabularyRepo.count() + 1));
    }
    
    @Override
    public ControlledVocabulary create(String name, String entityName, Language language) {
        return controlledVocabularyRepo.save(new ControlledVocabulary(name, entityName, language, (int) controlledVocabularyRepo.count() + 1));
    }
    
    @Override
    public void reorder(Integer src, Integer dest) {
        orderedEntityService.reorder(ControlledVocabulary.class, src, dest);
    }
    
    @Override
    public void sort(String column) {
        orderedEntityService.sort(ControlledVocabulary.class, column);
    }
    
    @Override
    public void remove(Integer index) {
        orderedEntityService.remove(ControlledVocabulary.class, index);
    }

}
