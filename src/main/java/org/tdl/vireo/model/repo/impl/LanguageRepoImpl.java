package org.tdl.vireo.model.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.Language;
import org.tdl.vireo.model.repo.LanguageRepo;
import org.tdl.vireo.model.repo.custom.LanguageRepoCustom;

import edu.tamu.framework.service.OrderedEntityService;

public class LanguageRepoImpl implements LanguageRepoCustom {

    @Autowired
    private OrderedEntityService orderedEntityService;
    
    @Autowired
    private LanguageRepo languageRepo;
    
    @Override
    public Language create(String name) {
        Language language = new Language(name);
        language.setPosition(languageRepo.count() + 1);
        return languageRepo.save(language);
    }
        
    @Override
    public void reorder(Long src, Long dest) {
        orderedEntityService.reorder(Language.class, src, dest);
    }
    
    @Override
    public void sort(String column) {
        orderedEntityService.sort(Language.class, column);
    }
    
    @Override
    public void remove(Language language) {
        orderedEntityService.remove(languageRepo, Language.class, language.getPosition());
    }
    
}
