package org.tdl.vireo.model.repo.impl;

import static edu.tamu.weaver.response.ApiAction.REMOVE;
import static edu.tamu.weaver.response.ApiAction.REORDER;
import static edu.tamu.weaver.response.ApiAction.SORT;
import static edu.tamu.weaver.response.ApiStatus.SUCCESS;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
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


    @Lazy
    @Autowired
    private EntityManager entityManager;

    private static final Long one = Long.valueOf(1);

    @Override
    public Embargo create(String name, String description, Integer duration, EmbargoGuarantor guarantor, boolean isActive) {
        Embargo embargo = new Embargo(name, description, duration, guarantor, isActive);

        Long embargoPosition = 1L;
        if (embargoRepo.count() > 0) {
            Embargo lastEmbargo = embargoRepo.findFirst1ByGuarantorOrderByPositionDesc(guarantor);
            if (lastEmbargo != null) {
                embargoPosition = lastEmbargo.getPosition()+1;
            }
        }
        embargo.setPosition(embargoPosition);
        return super.create(embargo);
    }

    @Override
    @Transactional
    public void reorder(Long src, Long dest, EmbargoGuarantor guarantor) {
        orderedEntityService.reorder(Embargo.class, src, dest, "guarantor", guarantor);
        simpMessagingTemplate.convertAndSend(getChannel(), new ApiResponse(SUCCESS, REORDER, embargoRepo.findAllByOrderByGuarantorAscPositionAsc()));
    }

    @Override
    @Transactional
    public void sort(String column, EmbargoGuarantor guarantor) {
        orderedEntityService.sort(Embargo.class, column, "guarantor", guarantor);
        simpMessagingTemplate.convertAndSend(getChannel(), new ApiResponse(SUCCESS, SORT, embargoRepo.findAllByOrderByGuarantorAscPositionAsc()));
    }

    @Override
    @Transactional
    @SuppressWarnings("unchecked")
    public void remove(Embargo embargo) {
        Long id = embargo.getId();
        Long position = embargo.getPosition();
        String whereProp = "guarantor";
        Object whereVal = embargo.getGuarantor();
        embargoRepo.deleteById(id);
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        Class<?> clazz = embargoRepo.getModelClass();
        CriteriaUpdate<Object> update = (CriteriaUpdate<Object>) cb.createCriteriaUpdate(clazz);
        Root<?> e = update.from((Class<Object>) clazz);
        Path<Long> path = e.get("position");
        update.set(path, cb.sum(path, -one));
        List<Predicate> predicates = new ArrayList<Predicate>();
        predicates.add(cb.greaterThan(path, position));
        if (whereProp != null && whereVal != null) {
            predicates.add(cb.equal(e.get(whereProp), whereVal));
        }
        update.where(predicates.toArray(new Predicate[] {}));
        entityManager.createQuery(update).executeUpdate();
        simpMessagingTemplate.convertAndSend(getChannel(), new ApiResponse(SUCCESS, REMOVE, embargoRepo.findAllByOrderByPositionAsc()));
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
