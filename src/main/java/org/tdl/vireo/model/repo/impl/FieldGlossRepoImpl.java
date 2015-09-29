package org.tdl.vireo.model.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.FieldGloss;
import org.tdl.vireo.model.Language;
import org.tdl.vireo.model.repo.FieldGlossRepo;
import org.tdl.vireo.model.repo.custom.FieldGlossRepoCustom;

public class FieldGlossRepoImpl implements FieldGlossRepoCustom {

    @Autowired
    private FieldGlossRepo fieldGlossRepo;

    @Override
    public FieldGloss create(String value, Language language) {
        return fieldGlossRepo.save(new FieldGloss(value, language));
    }
}
