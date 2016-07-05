package org.tdl.vireo.model.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.ObjectError;
import org.tdl.vireo.model.GraduationMonth;
import org.tdl.vireo.model.repo.GraduationMonthRepo;
import org.tdl.vireo.model.repo.custom.GraduationMonthRepoCustom;
import org.tdl.vireo.service.OrderedEntityService;

public class GraduationMonthRepoImpl implements GraduationMonthRepoCustom {

    @Autowired
    private OrderedEntityService orderedEntityService;
    
    @Autowired
    private GraduationMonthRepo graduationMonthRepo;
    
    @Override
    public GraduationMonth create(int month) {
        GraduationMonth graduationMonth = new GraduationMonth(month);
        graduationMonth.setPosition(graduationMonthRepo.count() + 1);
        return graduationMonthRepo.save(graduationMonth);
    }
    
    @Override
    public void reorder(Long src, Long dest) {
        orderedEntityService.reorder(GraduationMonth.class, src, dest);
    }
    
    @Override
    public void sort(String column) {
        orderedEntityService.sort(GraduationMonth.class, column);
    }
    
    @Override
    public void remove(GraduationMonth graduationMonth) {
        orderedEntityService.remove(graduationMonthRepo, GraduationMonth.class, graduationMonth.getPosition());
    }
    
    @Override
    public GraduationMonth validateCreate(GraduationMonth graduationMonth) {
        GraduationMonth existing = graduationMonthRepo.findByMonth(graduationMonth.getMonth());
        if(!graduationMonth.getBindingResult().hasErrors() &&  existing != null){
            graduationMonth.getBindingResult().addError(new ObjectError("graduationMonth", graduationMonth.getMonth() + " is already a graduation month!"));
        }
        
        return graduationMonth;
    }
    
    @Override
    public GraduationMonth validateUpdate(GraduationMonth graduationMonth) {
        // make sure we're not trying set the month to one that already has that month
        GraduationMonth existing = graduationMonthRepo.findByMonth(graduationMonth.getMonth());
        if(existing != null) {
            graduationMonth.getBindingResult().addError(new ObjectError("graduationMonth", "Cannot update a GraduationMonth with an already existing month!"));
        } else if(graduationMonth.getId() == null) {
            graduationMonth.getBindingResult().addError(new ObjectError("graduationMonth", "Cannot update a GraduationMonth without an id!"));
        } else {
            GraduationMonth graduationMonthToUpdate = graduationMonthRepo.findOne(graduationMonth.getId());
            if(graduationMonthToUpdate == null) {
                graduationMonth.getBindingResult().addError(new ObjectError("graduationMonth", "Cannot update a GraduationMonth with an invalid id!"));
            } else {
                graduationMonthToUpdate.setBindingResult(graduationMonth.getBindingResult());
                graduationMonthToUpdate.setMonth(graduationMonth.getMonth());
                graduationMonth = graduationMonthToUpdate;
            }
        }
        
        return graduationMonth;
    }

}
