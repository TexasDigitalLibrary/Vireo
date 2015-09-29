package org.tdl.vireo.model.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.tdl.vireo.model.Language;
import org.tdl.vireo.model.repo.custom.LanguageRepoCustom;

@Repository
public interface LanguageRepo extends JpaRepository<Language, Long>, LanguageRepoCustom {

    public Language findByName(String name);
}
