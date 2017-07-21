package org.tdl.vireo.model.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.DegreeLevel;
import org.tdl.vireo.model.repo.DegreeLevelRepo;
import org.tdl.vireo.model.repo.custom.DegreeLevelRepoCustom;

import edu.tamu.framework.service.OrderedEntityService;

public class DegreeLevelRepoImpl implements DegreeLevelRepoCustom {

    @Autowired
    private OrderedEntityService orderedEntityService;

    @Autowired
    private DegreeLevelRepo degreeLevelRepo;

    @Override
    public DegreeLevel create(String name) {
        DegreeLevel degreeLevel = degreeLevelRepo.findByName(name);
        if (degreeLevel == null) {
            degreeLevel = new DegreeLevel(name);
        }
        degreeLevel.setPosition(degreeLevelRepo.count() + 1);
        return degreeLevelRepo.save(degreeLevel);
    }

    @Override
    public void reorder(Long src, Long dest) {
        orderedEntityService.reorder(DegreeLevel.class, src, dest);
    }

    @Override
    public void sort(String column) {
        orderedEntityService.sort(DegreeLevel.class, column);
    }

    @Override
    public void remove(DegreeLevel degreeLevel) {
        orderedEntityService.remove(degreeLevelRepo, DegreeLevel.class, degreeLevel.getPosition());
    }

}
