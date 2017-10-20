package org.tdl.vireo.model.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.Language;
import org.tdl.vireo.model.repo.LanguageRepo;
import org.tdl.vireo.model.repo.custom.LanguageRepoCustom;

import edu.tamu.weaver.data.model.repo.impl.AbstractWeaverOrderedRepoImpl;

public class LanguageRepoImpl extends AbstractWeaverOrderedRepoImpl<Language, LanguageRepo> implements LanguageRepoCustom {

    @Autowired
    private LanguageRepo languageRepo;

    @Override
    public Language create(String name) {
        Language language = new Language(name);
        language.setPosition(languageRepo.count() + 1);
        return languageRepo.save(language);
    }

    @Override
    public Class<?> getModelClass() {
        return Language.class;
    }

    @Override
    protected String getChannel() {
        return "/channel/language";
    }

}
