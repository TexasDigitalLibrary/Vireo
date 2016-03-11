package org.tdl.vireo.model.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tdl.vireo.model.ControlledVocabulary;
import org.tdl.vireo.model.VocabularyWord;
import org.tdl.vireo.model.repo.custom.VocabularyWordRepoCustom;

public interface VocabularyWordRepo extends JpaRepository<VocabularyWord, Long>, VocabularyWordRepoCustom {

    VocabularyWord findByNameAndControlledVocabulary(String name, ControlledVocabulary controlledVocabulary);
        
}
