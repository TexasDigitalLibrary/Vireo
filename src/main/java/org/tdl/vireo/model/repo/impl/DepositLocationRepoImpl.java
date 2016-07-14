package org.tdl.vireo.model.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.DepositLocation;
import org.tdl.vireo.model.repo.DepositLocationRepo;
import org.tdl.vireo.model.repo.custom.DepositLocationRepoCustom;

import edu.tamu.framework.service.OrderedEntityService;

public class DepositLocationRepoImpl implements DepositLocationRepoCustom {
	
	@Autowired
    private OrderedEntityService orderedEntityService;
	
    @Autowired
    private DepositLocationRepo depositLocationRepo;

    @Override
    public DepositLocation create(String name, String repository, String collection, String username, String password, String onBehalfOf, String packager, String depositor) {
        DepositLocation depositLocation = new DepositLocation(name, repository, collection, username, password, onBehalfOf, packager, depositor);
        depositLocation.setPosition(depositLocationRepo.count() + 1);
        return depositLocationRepo.save(depositLocation);                
    }
    
    @Override
    public void reorder(Long src, Long dest) {
    	orderedEntityService.reorder(DepositLocation.class, src, dest);
    }
    
    @Override
    public void remove(DepositLocation depositLocation) {
        orderedEntityService.remove(depositLocationRepo, DepositLocation.class, depositLocation.getPosition());
    }
    
}
