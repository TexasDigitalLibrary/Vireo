package org.tdl.vireo.model.repo.custom;

import org.tdl.vireo.model.NamedSearchFilterGroup;
import org.tdl.vireo.model.User;

public interface NamedSearchFilterGroupRepoCustom {

    public NamedSearchFilterGroup create(User user);

    public NamedSearchFilterGroup create(User user, String name);

    public NamedSearchFilterGroup clone(NamedSearchFilterGroup newNamedSearchFilter, NamedSearchFilterGroup namedSearchFilterGroup);

    public NamedSearchFilterGroup createFromFilter(NamedSearchFilterGroup namedSearchFilterGroup);

    public void delete(NamedSearchFilterGroup namedSearchFilterGroup);

}
