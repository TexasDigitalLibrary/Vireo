package org.tdl.vireo.model.repo;

import org.tdl.vireo.model.DegreeLevel;
import org.tdl.vireo.model.repo.custom.DegreeLevelRepoCustom;

import edu.tamu.weaver.data.model.repo.WeaverOrderedRepo;

public interface DegreeLevelRepo extends WeaverOrderedRepo<DegreeLevel>, DegreeLevelRepoCustom {

    public DegreeLevel findByName(String name);

}
