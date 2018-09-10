package org.tdl.vireo.model.repo;

import java.util.List;

import org.tdl.vireo.model.ControlledVocabulary;
import org.tdl.vireo.model.repo.custom.ControlledVocabularyRepoCustom;

import edu.tamu.weaver.data.model.repo.WeaverOrderedRepo;

public interface ControlledVocabularyRepo extends WeaverOrderedRepo<ControlledVocabulary>, ControlledVocabularyRepoCustom {

    public ControlledVocabulary findByName(String name);

    public List<ControlledVocabulary> findAllByIsEntityProperty(boolean isEntityProperty);

}
