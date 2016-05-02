package org.tdl.vireo.model.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.ObjectError;
import org.tdl.vireo.model.DepositLocation;
import org.tdl.vireo.model.repo.DepositLocationRepo;
import org.tdl.vireo.model.repo.custom.DepositLocationRepoCustom;
import org.tdl.vireo.service.OrderedEntityService;
import org.tdl.vireo.service.ValidationService;

import edu.tamu.framework.validation.ModelBindingResult;

public class DepositLocationRepoImpl implements DepositLocationRepoCustom {
	
	@Autowired
    private OrderedEntityService orderedEntityService;
	
    @Autowired
    private DepositLocationRepo depositLocationRepo;
    
    @Autowired
    private ValidationService validationService;

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
    
    @Override
    public DepositLocation validateCreate(DepositLocation depositLocation) {
        DepositLocation existing = depositLocationRepo.findByName(depositLocation.getName());
        if(!depositLocation.getBindingResult().hasErrors() &&  existing != null){
            depositLocation.getBindingResult().addError(new ObjectError("depositLocation", depositLocation.getName() + " is already a deposit location!"));
        }
        return depositLocation;
    }
    
    @Override
    public DepositLocation validateUpdate(DepositLocation depositLocation) {
        if(depositLocation.getId() == null) {
            depositLocation.getBindingResult().addError(new ObjectError("depositLocation", "Cannot update a DepositLocation without an id!"));
        } else {
            DepositLocation depositLocationtoUpdate = depositLocationRepo.findOne(depositLocation.getId());
            if(depositLocationtoUpdate == null) {
                depositLocation.getBindingResult().addError(new ObjectError("depositLocation", "Cannot update a DepositLocation that doesn't exist!"));
            } else {
                depositLocationtoUpdate.setBindingResult(depositLocation.getBindingResult());
                depositLocationtoUpdate.setName(depositLocation.getName());
                depositLocationtoUpdate.setRepository(depositLocation.getRepository());
                depositLocationtoUpdate.setCollection(depositLocation.getCollection());
                depositLocationtoUpdate.setUsername(depositLocation.getUsername());
                depositLocationtoUpdate.setPassword(depositLocation.getPassword());
                depositLocationtoUpdate.setOnBehalfOf(depositLocation.getOnBehalfOf());
                depositLocationtoUpdate.setPackager(depositLocation.getPackager());
                depositLocationtoUpdate.setDepositor(depositLocation.getDepositor());
                depositLocationtoUpdate.setTimeout(depositLocation.getTimeout());
                depositLocation = depositLocationtoUpdate;
            }
        }
        
        return depositLocation;
    }
    
    @Override
    public DepositLocation validateRemove(String idString, ModelBindingResult modelBindingResult) {
        DepositLocation toRemove = null;
        Long id = validationService.validateLong(idString, "depositLocation", modelBindingResult);
        
        if(!modelBindingResult.hasErrors()){
            toRemove = depositLocationRepo.findOne(id);
            if (toRemove == null) {
                modelBindingResult.addError(new ObjectError("depositLocation", "Cannot remove deposit location, id did not exist!"));
            }
        }
        
        return toRemove;
    }
}
