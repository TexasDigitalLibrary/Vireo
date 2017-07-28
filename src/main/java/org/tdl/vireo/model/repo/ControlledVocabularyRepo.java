package org.tdl.vireo.model.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tdl.vireo.model.ControlledVocabulary;
import org.tdl.vireo.model.Language;
import org.tdl.vireo.model.repo.custom.ControlledVocabularyRepoCustom;

public interface ControlledVocabularyRepo extends JpaRepository<ControlledVocabulary, Long>, ControlledVocabularyRepoCustom {

    public ControlledVocabulary findByName(String name);

    public ControlledVocabulary findByNameAndLanguage(String name, Language language);

    public ControlledVocabulary findByNameAndLanguageAndIsEntityProperty(String name, Language language, Boolean isEntityProperty);

    public List<ControlledVocabulary> findAllByOrderByPositionAsc();

    public List<ControlledVocabulary> findAllByIsEntityProperty(boolean isEntityProperty);

    public void delete(ControlledVocabulary controlledVocabulary);

}
