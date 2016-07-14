package org.tdl.vireo.model.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.enums.EmbargoGuarantor;
import org.tdl.vireo.model.Embargo;
import org.tdl.vireo.model.repo.EmbargoRepo;
import org.tdl.vireo.model.repo.custom.EmbargoRepoCustom;

import edu.tamu.framework.service.OrderedEntityService;

public class EmbargoRepoImpl implements EmbargoRepoCustom {

    @Autowired
    private OrderedEntityService orderedEntityService;

    @Autowired
    private EmbargoRepo embargoRepo;
    
    @Override
    public Embargo create(String name, String description, Integer duration, EmbargoGuarantor guarantor, boolean isActive) {
        Embargo embargo = new Embargo(name, description, duration, guarantor, isActive);
        embargo.setPosition(embargoRepo.count() + 1);
        return embargoRepo.save(embargo);
    }

    @Override
    public void reorder(Long src, Long dest, EmbargoGuarantor guarantor) {
        orderedEntityService.reorder(Embargo.class, src, dest, "guarantor", guarantor);
    }

    @Override
    public void sort(String column, EmbargoGuarantor guarantor) {
        orderedEntityService.sort(Embargo.class, column, "guarantor", guarantor);
    }

    @Override
    public void remove(Embargo embargo) {
        orderedEntityService.remove(embargoRepo, Embargo.class, embargo.getPosition(), "guarantor", embargo.getGuarantor());
    }

}
