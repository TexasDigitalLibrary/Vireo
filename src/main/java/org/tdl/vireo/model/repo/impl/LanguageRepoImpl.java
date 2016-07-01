package org.tdl.vireo.model.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.ObjectError;
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
    
    @Override
    public Language validateCreate(Language language) {
        Language existing = languageRepo.findByName(language.getName());
        if(!language.getBindingResult().hasErrors() && existing != null){
            language.getBindingResult().addError(new ObjectError("language", language.getName() + " is already a language!"));
        }
        
        return language;
    }
    
    @Override
    public Language validateUpdate(Language language) {
        if(languageRepo.findByName(language.getName()) != null){
            language.getBindingResult().addError(new ObjectError("language", language.getName() + " is already a language!"));
        } else if(language.getId() == null) {
            language.getBindingResult().addError(new ObjectError("language", "Cannot update a language without an id!"));
        } else {
            Language languageToUpdate = languageRepo.findOne(language.getId());
            if(languageToUpdate == null) {
                language.getBindingResult().addError(new ObjectError("language", "Cannot update a language with an invalid id!"));
            } else {
                languageToUpdate.setBindingResult(language.getBindingResult());
                languageToUpdate.setName(language.getName());
                language = languageToUpdate;
            }
        }
        
        return language;
    }

}
