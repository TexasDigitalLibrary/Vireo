package org.tdl.vireo.model.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.Embargo;
import org.tdl.vireo.model.repo.EmbargoRepo;
import org.tdl.vireo.model.repo.custom.EmbargoRepoCustom;

public class EmbargoRepoImpl implements EmbargoRepoCustom {

    @Autowired
    private EmbargoRepo embargoRepo;

    @Override
    public Embargo create(String name, String description, Integer duration) {
        return embargoRepo.save(new Embargo(name, description, duration));
    }
    
    @Override
    public Embargo create(String name, String description, Integer duration, boolean isActive) {
        return embargoRepo.save(new Embargo(name, description, duration, isActive));
    }

}
