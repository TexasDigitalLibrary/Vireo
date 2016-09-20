package org.tdl.vireo.model.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tdl.vireo.model.NamedSearchFilter;
import org.tdl.vireo.model.repo.custom.NamedSearchFilterCriteriaRepoCustom;

public interface NamedSearchFilterCriteriaRepo extends JpaRepository<NamedSearchFilter, Long>, NamedSearchFilterCriteriaRepoCustom {

}
