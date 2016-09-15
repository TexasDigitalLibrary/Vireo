package org.tdl.vireo.model.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.NamedSearchFilterCriteria;
import org.tdl.vireo.model.User;
import org.tdl.vireo.model.repo.NamedSearchFilterRepo;
import org.tdl.vireo.model.repo.custom.NamedSearchFilterRepoCustom;

public class NamedSearchFilterRepoImpl implements NamedSearchFilterRepoCustom {

    @Autowired
    private NamedSearchFilterRepo namedSearchFilterRepo;

    @Override
    public NamedSearchFilterCriteria create(User user, String name) {
        return namedSearchFilterRepo.save(new NamedSearchFilterCriteria(user, name));
    }

}
