package org.tdl.vireo.model.repo.impl;

import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.NamedSearchFilter;
import org.tdl.vireo.model.User;
import org.tdl.vireo.model.repo.NamedSearchFilterRepo;
import org.tdl.vireo.model.repo.custom.NamedSearchFilterRepoCustom;

public class NamedSearchFilterRepoImpl implements NamedSearchFilterRepoCustom {

    @Autowired
    private NamedSearchFilterRepo namedSearchFilterRepo;

    @Override
    public NamedSearchFilter create(User user, String name) {
        return namedSearchFilterRepo.save(new NamedSearchFilter(user, name));
    }

    @Override
    public NamedSearchFilter create(User user, String name, String value) {
        return namedSearchFilterRepo.save(new NamedSearchFilter(user, name, value));
    }
    
    @Override
    public NamedSearchFilter create(User user, String name, Calendar dateValue) {
        return namedSearchFilterRepo.save(new NamedSearchFilter(user, name, dateValue));
    }

    @Override
    public NamedSearchFilter create(User user, String name, Calendar rangeStart, Calendar rangeEnd) {
        return namedSearchFilterRepo.save(new NamedSearchFilter(user, name, rangeStart, rangeEnd));
    }

}
