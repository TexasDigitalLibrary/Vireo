package org.tdl.vireo.model.repo.custom;

import org.tdl.vireo.model.ControlledVocabulary;
import org.tdl.vireo.model.Language;

public interface ControlledVocabularyRepoCustom {

    public ControlledVocabulary create(String name, Language language);

    public ControlledVocabulary create(String name, Language language, Boolean isEntityProperty);

    public void reorder(Long src, Long dest);

    public void sort(String column);

    public void remove(ControlledVocabulary controlledVocabulary);

}
