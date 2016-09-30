package org.tdl.vireo.model.repo.custom;

import org.tdl.vireo.model.NamedSearchFilter;
import org.tdl.vireo.model.User;

public interface NamedSearchFilterRepoCustom {

    public NamedSearchFilter create(User user);
    
    public NamedSearchFilter create(User user, String name);
    
    public NamedSearchFilter clone(NamedSearchFilter newNamedSearchFilter, NamedSearchFilter namedSearchFilter);
    
    public NamedSearchFilter createFromFilter(NamedSearchFilter namedSearchFilter);
    
    public void delete(NamedSearchFilter namedSearchFilter);

}
