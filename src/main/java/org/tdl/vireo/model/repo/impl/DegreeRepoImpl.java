package org.tdl.vireo.model.repo.impl;

import static edu.tamu.weaver.response.ApiAction.*;
import static edu.tamu.weaver.response.ApiStatus.SUCCESS;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.tdl.vireo.model.Degree;
import org.tdl.vireo.model.DegreeLevel;
import org.tdl.vireo.model.repo.DegreeRepo;
import org.tdl.vireo.model.repo.custom.DegreeRepoCustom;

import edu.tamu.weaver.data.service.OrderedEntityService;
import edu.tamu.weaver.response.ApiResponse;
import edu.tamu.weaver.data.model.repo.impl.AbstractWeaverRepoImpl;

public class DegreeRepoImpl extends AbstractWeaverRepoImpl<Degree, DegreeRepo> implements DegreeRepoCustom {

    @Autowired
    private DegreeRepo degreeRepo;

    @Autowired
    private OrderedEntityService orderedEntityService;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Override
    public Degree create(String name, DegreeLevel level) {
        Degree degree = degreeRepo.findByNameAndLevel(name, level);
        if (degree == null) {
            degree = new Degree(name, level);
        }
        degree.setPosition(degreeRepo.count() + 1);
        return super.create(degree);
    }

    @Override
    public void reorder(Long src, Long dest) {
        orderedEntityService.reorder(Degree.class, src, dest);
        simpMessagingTemplate.convertAndSend(getChannel(), new ApiResponse(SUCCESS, REORDER, degreeRepo.findAllByOrderByPositionAsc()));
    }

    @Override
    public void sort(String column) {
        orderedEntityService.sort(Degree.class, column);
        simpMessagingTemplate.convertAndSend(getChannel(), new ApiResponse(SUCCESS, SORT, degreeRepo.findAllByOrderByPositionAsc()));
    }

    @Override
    public void remove(Degree degree) {
        orderedEntityService.remove(degreeRepo, Degree.class, degree.getPosition());
        simpMessagingTemplate.convertAndSend(getChannel(), new ApiResponse(SUCCESS, REMOVE, degreeRepo.findAllByOrderByPositionAsc()));
    }

    @Override
    protected String getChannel() {
        return "/channel/degree";
    }

}
