package org.tdl.vireo.model.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.Language;
import org.tdl.vireo.model.repo.LanguageRepo;
import org.tdl.vireo.model.repo.custom.LanguageRepoCustom;
import org.tdl.vireo.service.OrderedEntityService;

public class LanguageRepoImpl implements LanguageRepoCustom {

    @Autowired
    private OrderedEntityService orderedEntityService;
    
    @Autowired
    private LanguageRepo languageRepo;

    @Override
    public Language create(String name) {
        return languageRepo.save(new Language(name, (int) languageRepo.count() + 1));
    }
        
    @Override
    public void reorder(Integer src, Integer dest) {
        orderedEntityService.reorder(Language.class, src, dest);
    }
    
    @Override
    public void sort(String column) {
        orderedEntityService.sort(Language.class, column);
    }
    
    @Override
    public void remove(Integer index) {
        orderedEntityService.remove(Language.class, index);
    }

}
