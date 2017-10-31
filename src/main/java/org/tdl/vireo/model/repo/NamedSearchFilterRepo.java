package org.tdl.vireo.model.repo;

import org.tdl.vireo.model.NamedSearchFilter;
import org.tdl.vireo.model.repo.custom.NamedSearchFilterRepoCustom;

import edu.tamu.weaver.data.model.repo.WeaverRepo;

public interface NamedSearchFilterRepo extends WeaverRepo<NamedSearchFilter>, NamedSearchFilterRepoCustom {

}
