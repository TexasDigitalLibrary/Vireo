package org.tdl.vireo.model.repo;

import java.util.List;

public interface EntityControlledVocabularyRepo<EntityControlledVocabulary> {

    List<EntityControlledVocabulary> findAll();

}
