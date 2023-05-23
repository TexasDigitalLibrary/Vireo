package org.tdl.vireo.model.repo;

import edu.tamu.weaver.data.model.repo.WeaverRepo;
import java.util.List;
import org.tdl.vireo.model.NamedSearchFilter;
import org.tdl.vireo.model.NamedSearchFilterGroup;
import org.tdl.vireo.model.User;
import org.tdl.vireo.model.repo.custom.NamedSearchFilterGroupRepoCustom;

public interface NamedSearchFilterGroupRepo extends WeaverRepo<NamedSearchFilterGroup>, NamedSearchFilterGroupRepoCustom {

    Long countByNamedSearchFilters(NamedSearchFilter namedSearchFilter);

    public List<NamedSearchFilterGroup> findByUserIsNotAndPublicFlagTrue(User user);

    public List<NamedSearchFilterGroup> findByUserAndNameIsNull(User user);

    public NamedSearchFilterGroup findByNameAndPublicFlagTrue(String name);

}
