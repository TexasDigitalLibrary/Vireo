package org.tdl.vireo.model.repo;

import java.util.List;

import org.tdl.vireo.model.ControlledVocabulary;
import org.tdl.vireo.model.Language;
import org.tdl.vireo.model.repo.custom.ControlledVocabularyRepoCustom;

import edu.tamu.weaver.data.model.repo.WeaverOrderedRepo;

public interface ControlledVocabularyRepo extends WeaverOrderedRepo<ControlledVocabulary>, ControlledVocabularyRepoCustom {

    public ControlledVocabulary findByName(String name);

    public ControlledVocabulary findByNameAndLanguage(String name, Language language);

    public ControlledVocabulary findByNameAndLanguageAndIsEntityProperty(String name, Language language, Boolean isEntityProperty);

    public List<ControlledVocabulary> findAllByIsEntityProperty(boolean isEntityProperty);

}
