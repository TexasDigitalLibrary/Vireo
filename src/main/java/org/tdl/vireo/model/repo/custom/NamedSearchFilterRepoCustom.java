package org.tdl.vireo.model.repo.custom;

import java.util.Calendar;

import org.tdl.vireo.model.NamedSearchFilter;
import org.tdl.vireo.model.User;

public interface NamedSearchFilterRepoCustom {

    public NamedSearchFilter create(User user, String name);
    
    public NamedSearchFilter create(User user, String name, String value);
    
    public NamedSearchFilter create(User user, String name, Calendar dateValue);
    
    public NamedSearchFilter create(User user, String name, Calendar rangeStart, Calendar rangeEnd);

}
