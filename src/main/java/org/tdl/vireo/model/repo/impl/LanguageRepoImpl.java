package org.tdl.vireo.model.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.Language;
import org.tdl.vireo.model.repo.LanguageRepo;
import org.tdl.vireo.model.repo.custom.LanguageRepoCustom;

public class LanguageRepoImpl implements LanguageRepoCustom {

    @Autowired
    private LanguageRepo languageRepo;

    @Override
    public Language create(String name) {
        return languageRepo.save(new Language(name));
    }
}
