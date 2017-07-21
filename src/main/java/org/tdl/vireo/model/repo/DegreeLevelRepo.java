package org.tdl.vireo.model.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import org.tdl.vireo.model.DegreeLevel;
import org.tdl.vireo.model.repo.custom.DegreeLevelRepoCustom;

public interface DegreeLevelRepo extends JpaRepository<DegreeLevel, Long>, DegreeLevelRepoCustom {

    public DegreeLevel findByName(String name);

    public DegreeLevel findByPosition(Long position);

    public List<DegreeLevel> findAllByOrderByPositionAsc();

}
