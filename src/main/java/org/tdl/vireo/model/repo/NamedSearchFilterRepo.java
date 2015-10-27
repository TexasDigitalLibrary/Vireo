package org.tdl.vireo.model.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tdl.vireo.model.NamedSearchFilter;
import org.tdl.vireo.model.repo.custom.NamedSearchFilterRepoCustom;

public interface NamedSearchFilterRepo extends JpaRepository<NamedSearchFilter, Long> , NamedSearchFilterRepoCustom{
	
}
