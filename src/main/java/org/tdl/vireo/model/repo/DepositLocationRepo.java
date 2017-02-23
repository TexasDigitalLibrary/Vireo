package org.tdl.vireo.model.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tdl.vireo.model.DepositLocation;
import org.tdl.vireo.model.repo.custom.DepositLocationRepoCustom;

public interface DepositLocationRepo extends JpaRepository<DepositLocation, Long>, DepositLocationRepoCustom {

    public DepositLocation findByName(String name);

    public DepositLocation findByPosition(Long position);

    public List<DepositLocation> findAllByOrderByPositionAsc();

}
