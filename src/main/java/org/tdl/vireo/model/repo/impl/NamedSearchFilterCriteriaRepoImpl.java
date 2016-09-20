package org.tdl.vireo.model.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.NamedSearchFilter;
import org.tdl.vireo.model.User;
import org.tdl.vireo.model.repo.NamedSearchFilterCriteriaRepo;
import org.tdl.vireo.model.repo.custom.NamedSearchFilterCriteriaRepoCustom;

public class NamedSearchFilterCriteriaRepoImpl implements NamedSearchFilterCriteriaRepoCustom {

    @Autowired
    private NamedSearchFilterCriteriaRepo namedSearchFilterCriteriaRepo;

    @Override
    public NamedSearchFilter create(User user) {
    	NamedSearchFilter newNamedSearchFilter = new NamedSearchFilter();
    	newNamedSearchFilter.setUser(user);
        return namedSearchFilterCriteriaRepo.save(newNamedSearchFilter);
    }
    
    @Override
    public NamedSearchFilter create(User user, String name) {
    	NamedSearchFilter newNamedSearchFilter = new NamedSearchFilter();
    	newNamedSearchFilter.setUser(user);
    	newNamedSearchFilter.setName(name);
        return namedSearchFilterCriteriaRepo.save(newNamedSearchFilter);
    }

}
