package org.tdl.vireo.model.repo.impl;

import static edu.tamu.weaver.response.ApiAction.REORDER;
import static edu.tamu.weaver.response.ApiAction.SORT;
import static edu.tamu.weaver.response.ApiStatus.SUCCESS;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.tdl.vireo.model.Embargo;
import org.tdl.vireo.model.EmbargoGuarantor;
import org.tdl.vireo.model.repo.EmbargoRepo;
import org.tdl.vireo.model.repo.custom.EmbargoRepoCustom;

import edu.tamu.weaver.data.model.repo.impl.AbstractWeaverOrderedRepoImpl;
import edu.tamu.weaver.data.service.OrderedEntityService;
import edu.tamu.weaver.response.ApiResponse;

public class EmbargoRepoImpl extends AbstractWeaverOrderedRepoImpl<Embargo, EmbargoRepo> implements EmbargoRepoCustom {

    @Autowired
    private OrderedEntityService orderedEntityService;

    @Autowired
    private EmbargoRepo embargoRepo;

    @Override
    public Embargo create(String name, String description, Integer duration, EmbargoGuarantor guarantor, boolean isActive) {
        Embargo embargo = new Embargo(name, description, duration, guarantor, isActive);
        embargo.setPosition(embargoRepo.count() + 1);
        return super.create(embargo);
    }

    @Override
    @Transactional
    public void reorder(Long src, Long dest, EmbargoGuarantor guarantor) {
        orderedEntityService.reorder(Embargo.class, src, dest, "guarantor", guarantor);
        simpMessagingTemplate.convertAndSend(getChannel(), new ApiResponse(SUCCESS, REORDER, embargoRepo.findAllByOrderByPositionAsc()));
    }

    @Override
    @Transactional
    public void sort(String column, EmbargoGuarantor guarantor) {
        orderedEntityService.sort(Embargo.class, column, "guarantor", guarantor);
        simpMessagingTemplate.convertAndSend(getChannel(), new ApiResponse(SUCCESS, SORT, embargoRepo.findAllByOrderByPositionAsc()));
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
