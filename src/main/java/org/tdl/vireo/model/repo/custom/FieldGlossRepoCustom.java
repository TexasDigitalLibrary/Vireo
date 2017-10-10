package org.tdl.vireo.model.repo.custom;

import org.tdl.vireo.model.FieldGloss;
import org.tdl.vireo.model.Language;

public interface FieldGlossRepoCustom {

    public FieldGloss create(String value, Language language);

}
