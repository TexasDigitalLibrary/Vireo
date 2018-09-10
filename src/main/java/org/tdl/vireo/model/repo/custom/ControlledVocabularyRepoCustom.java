package org.tdl.vireo.model.repo.custom;

import org.tdl.vireo.model.ControlledVocabulary;

public interface ControlledVocabularyRepoCustom {

    public ControlledVocabulary create(String name);

    public ControlledVocabulary create(String name, Boolean isEntityProperty);

}
