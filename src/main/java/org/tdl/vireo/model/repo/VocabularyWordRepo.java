package org.tdl.vireo.model.repo;

import edu.tamu.weaver.data.model.repo.WeaverRepo;
import java.util.List;
import org.tdl.vireo.model.ControlledVocabulary;
import org.tdl.vireo.model.VocabularyWord;
import org.tdl.vireo.model.repo.custom.VocabularyWordRepoCustom;

public interface VocabularyWordRepo extends WeaverRepo<VocabularyWord>, VocabularyWordRepoCustom {

    VocabularyWord findByNameAndControlledVocabulary(String name, ControlledVocabulary controlledVocabulary);

    <T> List<T>findAllByNameContainsIgnoreCaseAndControlledVocabularyId(String name, Long controlledVocabularyId, Class<T> type);

}
