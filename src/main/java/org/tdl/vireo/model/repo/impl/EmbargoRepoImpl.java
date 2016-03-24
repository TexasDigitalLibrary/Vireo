package org.tdl.vireo.model.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.enums.EmbargoGuarantor;
import org.tdl.vireo.model.Embargo;
import org.tdl.vireo.model.repo.EmbargoRepo;
import org.tdl.vireo.model.repo.custom.EmbargoRepoCustom;
import org.tdl.vireo.service.OrderedEntityService;

public class EmbargoRepoImpl implements EmbargoRepoCustom {

    @Autowired
    private OrderedEntityService orderedEntityService;
    
    @Autowired
    private EmbargoRepo embargoRepo;
    
    @Override
    public Embargo create(String name, String description, Integer duration, boolean isActive) {
        Embargo embargo = new Embargo(name, description, duration, isActive);
        embargo.setOrder((int) embargoRepo.count());
        return embargoRepo.save(embargo);
    }
    
    @Override
    public void reorder(Integer src, Integer dest) {
        orderedEntityService.reorder(Embargo.class, src, dest);
    }
    
    @Override
    public void sort(String column, EmbargoGuarantor guarantor) {
        orderedEntityService.sort(Embargo.class, column, "guarantor", String.valueOf(guarantor.getValue()));
    }
    
    @Override
    public void remove(Integer index) {
        orderedEntityService.remove(Embargo.class, index);
    }
}
