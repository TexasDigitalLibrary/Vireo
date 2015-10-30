package org.tdl.vireo.model.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.DepositLocation;
import org.tdl.vireo.model.repo.DepositLocationRepo;
import org.tdl.vireo.model.repo.custom.DepositLocationRepoCustom;

public class DepositLocationRepoImpl implements DepositLocationRepoCustom {

    @Autowired
    private DepositLocationRepo depositLocationRepo;

    @Override
    public DepositLocation create(String name) {
        return depositLocationRepo.save(new DepositLocation(name));
    }

}
