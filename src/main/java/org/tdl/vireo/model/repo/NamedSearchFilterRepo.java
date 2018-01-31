package org.tdl.vireo.model.repo;

import java.util.List;

import org.tdl.vireo.model.NamedSearchFilter;
import org.tdl.vireo.model.repo.custom.NamedSearchFilterRepoCustom;

import edu.tamu.weaver.data.model.repo.WeaverRepo;

public interface NamedSearchFilterRepo extends WeaverRepo<NamedSearchFilter>, NamedSearchFilterRepoCustom {

    List<NamedSearchFilter> findByFilterCriteriaId(Long id);

}
