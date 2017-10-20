package org.tdl.vireo.model.repo;

import java.util.List;

import org.tdl.vireo.model.NamedSearchFilterGroup;
import org.tdl.vireo.model.repo.custom.NamedSearchFilterGroupRepoCustom;

import edu.tamu.weaver.data.model.repo.WeaverRepo;

public interface NamedSearchFilterGroupRepo extends WeaverRepo<NamedSearchFilterGroup>, NamedSearchFilterGroupRepoCustom {

    public List<NamedSearchFilterGroup> findByPublicFlagTrue();

    public NamedSearchFilterGroup findByNameAndPublicFlagTrue(String name);

}
