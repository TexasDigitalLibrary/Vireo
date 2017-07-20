package org.tdl.vireo.model.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.Degree;
import org.tdl.vireo.model.repo.DegreeRepo;
import org.tdl.vireo.model.repo.custom.DegreeRepoCustom;

import edu.tamu.framework.service.OrderedEntityService;

public class DegreeRepoImpl implements DegreeRepoCustom {

    @Autowired
    private OrderedEntityService orderedEntityService;

    @Autowired
    private DegreeRepo degreeRepo;

    @Override
    public Degree create(String name, String proquestCode) {
        Degree degree = degreeRepo.findByNameAndProquestCode(name, proquestCode);
        if (degree == null) {
            degree = new Degree(name, proquestCode);
        }
        degree.setPosition(degreeRepo.count() + 1);
        return degreeRepo.save(degree);
    }

    @Override
    public void reorder(Long src, Long dest) {
        orderedEntityService.reorder(Degree.class, src, dest);
    }

    @Override
    public void sort(String column) {
        orderedEntityService.sort(Degree.class, column);
    }

    @Override
    public void remove(Degree degree) {
        orderedEntityService.remove(degreeRepo, Degree.class, degree.getPosition());
    }

}
