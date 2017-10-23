package org.tdl.vireo.model.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.Embargo;
import org.tdl.vireo.model.EmbargoGuarantor;
import org.tdl.vireo.model.repo.EmbargoRepo;
import org.tdl.vireo.model.repo.custom.EmbargoRepoCustom;

import edu.tamu.weaver.data.model.repo.impl.AbstractWeaverOrderedRepoImpl;
import edu.tamu.weaver.data.service.OrderedEntityService;

public class EmbargoRepoImpl extends AbstractWeaverOrderedRepoImpl<Embargo, EmbargoRepo> implements EmbargoRepoCustom {

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
    public Class<?> getModelClass() {
        return Embargo.class;
    }

    @Override
    protected String getChannel() {
        return "/channel/embargo";
    }

}
