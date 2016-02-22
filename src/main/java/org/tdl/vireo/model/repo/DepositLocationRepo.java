package org.tdl.vireo.model.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tdl.vireo.model.DepositLocation;
import org.tdl.vireo.model.repo.custom.DepositLocationRepoCustom;

public interface DepositLocationRepo extends JpaRepository<DepositLocation, Long>, DepositLocationRepoCustom {

    public DepositLocation findByName(String name);
    
    public DepositLocation findByOrder(Integer order);
    
}
