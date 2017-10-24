package org.tdl.vireo.model.repo.impl;

import org.tdl.vireo.model.FieldGloss;
import org.tdl.vireo.model.Language;
import org.tdl.vireo.model.repo.FieldGlossRepo;
import org.tdl.vireo.model.repo.custom.FieldGlossRepoCustom;

import edu.tamu.weaver.data.model.repo.impl.AbstractWeaverRepoImpl;

public class FieldGlossRepoImpl extends AbstractWeaverRepoImpl<FieldGloss, FieldGlossRepo> implements FieldGlossRepoCustom {

    @Override
    public FieldGloss create(String value, Language language) {
        return super.create(new FieldGloss(value, language));
    }

    @Override
    protected String getChannel() {
        return "/channel/field-gloss";
    }

}
