package org.tdl.vireo.model.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.NamedSearchFilterCriteria;
import org.tdl.vireo.model.User;
import org.tdl.vireo.model.repo.NamedSearchFilterCriteriaRepo;
import org.tdl.vireo.model.repo.custom.NamedSearchFilterCriteriaRepoCustom;

public class NamedSearchFilterCriteriaRepoImpl implements NamedSearchFilterCriteriaRepoCustom {

    @Autowired
    private NamedSearchFilterCriteriaRepo namedSearchFilterCriteriaRepo;

    @Override
    public NamedSearchFilterCriteria create(User user, String name) {
        return namedSearchFilterCriteriaRepo.save(new NamedSearchFilterCriteria(user, name));
    }

}
