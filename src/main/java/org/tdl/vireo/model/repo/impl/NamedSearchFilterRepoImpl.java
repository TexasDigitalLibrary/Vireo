package org.tdl.vireo.model.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.NamedSearchFilter;
import org.tdl.vireo.model.User;
import org.tdl.vireo.model.repo.NamedSearchFilterRepo;
import org.tdl.vireo.model.repo.custom.NamedSearchFilterRepoCustom;

public class NamedSearchFilterRepoImpl implements NamedSearchFilterRepoCustom {

    @Autowired
    NamedSearchFilterRepo namedSearchFilterRepo;

    @Override
    public NamedSearchFilter create(User creator, String name) {
        return namedSearchFilterRepo.save(new NamedSearchFilter(creator, name));
    }

}
