package org.tdl.vireo.model.repo;

import org.tdl.vireo.model.DepositLocation;
import org.tdl.vireo.model.repo.custom.DepositLocationRepoCustom;

import edu.tamu.weaver.data.model.repo.WeaverOrderedRepo;

public interface DepositLocationRepo extends WeaverOrderedRepo<DepositLocation>, DepositLocationRepoCustom {

    public DepositLocation findByName(String name);

}
