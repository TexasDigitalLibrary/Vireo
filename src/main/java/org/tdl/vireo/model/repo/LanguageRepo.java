package org.tdl.vireo.model.repo;

import org.tdl.vireo.aspect.annotation.EntityCV;
import org.tdl.vireo.model.Language;
import org.tdl.vireo.model.repo.custom.LanguageRepoCustom;

import edu.tamu.weaver.data.model.repo.WeaverOrderedRepo;

@EntityCV(name = "Languages")
public interface LanguageRepo extends WeaverOrderedRepo<Language>, EntityControlledVocabularyRepo<Language>, LanguageRepoCustom {

    public Language findByName(String name);

}
