package org.tdl.vireo.model.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;

import org.tdl.vireo.model.DepositLocation;
import org.tdl.vireo.model.repo.DepositLocationRepo;
import org.tdl.vireo.model.repo.custom.DepositLocationRepoCustom;
import org.tdl.vireo.service.OrderedEntityService;

public class DepositLocationRepoImpl implements DepositLocationRepoCustom {
	
	@Autowired
    private OrderedEntityService orderedEntityService;
	
    @Autowired
    private DepositLocationRepo depositLocationRepo;

    @Override
    public DepositLocation create(String name) {
        return depositLocationRepo.save(new DepositLocation(name));
    }
    
    @Override
    public void reorder(Long src, Long dest) {
    	orderedEntityService.reorder(DepositLocation.class, src, dest);
    }
    
    @Override
    public void remove(Long index) {
        orderedEntityService.remove(DepositLocation.class, index);
    }

}
