package org.tdl.vireo.model.repo;

import org.tdl.vireo.model.ControlledVocabulary;
import org.tdl.vireo.model.VocabularyWord;
import org.tdl.vireo.model.repo.custom.VocabularyWordRepoCustom;

import edu.tamu.weaver.data.model.repo.WeaverRepo;

public interface VocabularyWordRepo extends WeaverRepo<VocabularyWord>, VocabularyWordRepoCustom {

    VocabularyWord findByNameAndControlledVocabulary(String name, ControlledVocabulary controlledVocabulary);

}
