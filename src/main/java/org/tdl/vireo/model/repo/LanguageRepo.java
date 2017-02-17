package org.tdl.vireo.model.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tdl.vireo.model.Language;
import org.tdl.vireo.model.repo.custom.LanguageRepoCustom;

public interface LanguageRepo extends JpaRepository<Language, Long>, LanguageRepoCustom {

    public Language findByName(String name);

    public List<Language> findAllByOrderByPositionAsc();

}
