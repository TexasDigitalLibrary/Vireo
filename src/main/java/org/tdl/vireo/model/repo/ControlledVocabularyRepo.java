package org.tdl.vireo.model.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.tdl.vireo.model.ControlledVocabulary;
import org.tdl.vireo.model.repo.custom.ControlledVocabularyRepoCustom;

@Repository
public interface ControlledVocabularyRepo extends JpaRepository<ControlledVocabulary, Long>, ControlledVocabularyRepoCustom {

    public ControlledVocabulary findByName(String name);
}
