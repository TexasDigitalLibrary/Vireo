package org.tdl.vireo.model.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.tdl.vireo.model.ControlledVocabulary;

@Repository
public interface ControlledVocabularyRepo extends JpaRepository<ControlledVocabulary, Long> {

}
