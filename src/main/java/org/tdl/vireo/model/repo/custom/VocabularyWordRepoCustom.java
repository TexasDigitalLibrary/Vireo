package org.tdl.vireo.model.repo.custom;

import java.util.List;

import org.tdl.vireo.model.ControlledVocabulary;
import org.tdl.vireo.model.VocabularyWord;

public interface VocabularyWordRepoCustom {

    public VocabularyWord create(ControlledVocabulary controlledVocabulary, String name, String definition, String identifier);

    public VocabularyWord create(ControlledVocabulary controlledVocabulary, String name, String definition, String identifier, List<String> contacts);

}
