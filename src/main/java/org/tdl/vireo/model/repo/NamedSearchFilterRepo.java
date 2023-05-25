package org.tdl.vireo.model.repo;

import edu.tamu.weaver.data.model.repo.WeaverRepo;
import org.tdl.vireo.model.NamedSearchFilter;
import org.tdl.vireo.model.repo.custom.NamedSearchFilterRepoCustom;

public interface NamedSearchFilterRepo extends WeaverRepo<NamedSearchFilter>, NamedSearchFilterRepoCustom {

    Long countByFilterCriteriaId(Long id);

}
