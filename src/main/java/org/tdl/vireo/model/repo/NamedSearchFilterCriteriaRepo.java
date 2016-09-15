package org.tdl.vireo.model.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tdl.vireo.model.NamedSearchFilterCriteria;
import org.tdl.vireo.model.repo.custom.NamedSearchFilterCriteriaRepoCustom;

public interface NamedSearchFilterCriteriaRepo extends JpaRepository<NamedSearchFilterCriteria, Long>, NamedSearchFilterCriteriaRepoCustom {

}
