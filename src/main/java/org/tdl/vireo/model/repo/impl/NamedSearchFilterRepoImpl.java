package org.tdl.vireo.model.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.NamedSearchFilter;
import org.tdl.vireo.model.User;
import org.tdl.vireo.model.repo.FilterCriterionRepo;
import org.tdl.vireo.model.repo.NamedSearchFilterRepo;
import org.tdl.vireo.model.repo.UserRepo;
import org.tdl.vireo.model.repo.custom.NamedSearchFilterRepoCustom;

public class NamedSearchFilterRepoImpl implements NamedSearchFilterRepoCustom {

    @Autowired
    private NamedSearchFilterRepo namedSearchFilterRepo;
    
    @Autowired
    private FilterCriterionRepo filterCriterionRepo;
    
    @Autowired
    private UserRepo userRepo;

    @Override
    public NamedSearchFilter create(User user) {
    	NamedSearchFilter newNamedSearchFilter = new NamedSearchFilter();
    	newNamedSearchFilter.setUser(user);
    	
        return namedSearchFilterRepo.save(newNamedSearchFilter);
    }
    
    @Override
    public NamedSearchFilter create(User user, String name) {
    	NamedSearchFilter newNamedSearchFilter = new NamedSearchFilter();
    	newNamedSearchFilter.setUser(user);
    	newNamedSearchFilter.setName(name);
        return namedSearchFilterRepo.save(newNamedSearchFilter);
    }
    
    public NamedSearchFilter clone(NamedSearchFilter newNamedSearchFilter, NamedSearchFilter namedSearchFilter) {
    	newNamedSearchFilter.setPublicFlag(namedSearchFilter.getPublicFlag());
    	newNamedSearchFilter.setUmiRelease(namedSearchFilter.getUmiRelease());
    	newNamedSearchFilter.setColumnsFlag(namedSearchFilter.getColumnsFlag());
    	namedSearchFilter.getFilterCriteria().forEach(filterCriterion -> {
    		newNamedSearchFilter.addFilterCriterion(filterCriterionRepo.cloneFilterCriterion(filterCriterion));
    	});
    	
    	if(newNamedSearchFilter.getColumnsFlag()) {
    		namedSearchFilter.getSavedColumns().forEach(column -> {
        		newNamedSearchFilter.addSavedColumn(column);
        	});
    	}

    	return newNamedSearchFilter;
    }
    
    public NamedSearchFilter createFromFilter(NamedSearchFilter namedSearchFilter) {
    	NamedSearchFilter newNamedSearchFilter = namedSearchFilterRepo.create(namedSearchFilter.getUser());
    	newNamedSearchFilter.setName(namedSearchFilter.getName());
    	
    	return namedSearchFilterRepo.save(clone(newNamedSearchFilter, namedSearchFilter));
    }
    
    @Override
    public void delete(NamedSearchFilter namedSearchFilter) {
        User user = namedSearchFilter.getUser();
        user.setActiveFilter(null);
        userRepo.save(user);
        namedSearchFilter.setUser(null);
        namedSearchFilter.setFilterCriteria(null);
        namedSearchFilter.setSavedColumns(null);
        namedSearchFilterRepo.delete(namedSearchFilter.getId());
    }

}
