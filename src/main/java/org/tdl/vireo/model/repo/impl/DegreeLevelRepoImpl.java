package org.tdl.vireo.model.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.DegreeLevel;
import org.tdl.vireo.model.repo.DegreeLevelRepo;
import org.tdl.vireo.model.repo.custom.DegreeLevelRepoCustom;

import edu.tamu.weaver.data.model.repo.impl.AbstractWeaverOrderedRepoImpl;

public class DegreeLevelRepoImpl extends AbstractWeaverOrderedRepoImpl<DegreeLevel, DegreeLevelRepo> implements DegreeLevelRepoCustom {

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
    public Class<?> getModelClass() {
        return DegreeLevel.class;
    }

    @Override
    protected String getChannel() {
        return "/channel/degree-level";
    }

}
