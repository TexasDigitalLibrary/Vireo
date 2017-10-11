package org.tdl.vireo.model.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.Degree;
import org.tdl.vireo.model.DegreeLevel;
import org.tdl.vireo.model.repo.DegreeRepo;
import org.tdl.vireo.model.repo.custom.DegreeRepoCustom;

import edu.tamu.weaver.data.model.repo.impl.AbstractWeaverOrderedRepoImpl;

public class DegreeRepoImpl extends AbstractWeaverOrderedRepoImpl<Degree, DegreeRepo> implements DegreeRepoCustom {

    @Autowired
    private DegreeRepo degreeRepo;

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
    protected String getChannel() {
        return "/channel/degree";
    }

    @Override
    public Class<?> getModelClass() {
        return Degree.class;
    }

}
