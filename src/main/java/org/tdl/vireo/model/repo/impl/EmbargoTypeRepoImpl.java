package org.tdl.vireo.model.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.EmbargoType;
import org.tdl.vireo.model.repo.EmbargoTypeRepo;
import org.tdl.vireo.model.repo.custom.EmbargoTypeRepoCustom;

public class EmbargoTypeRepoImpl implements EmbargoTypeRepoCustom {

    @Autowired
    private EmbargoTypeRepo embargoTypeRepo;

    @Override
    public EmbargoType create(String name, String description, Integer duration) {
        return embargoTypeRepo.save(new EmbargoType(name, description, duration));
    }
}
