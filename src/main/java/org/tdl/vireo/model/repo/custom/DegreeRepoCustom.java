package org.tdl.vireo.model.repo.custom;

import org.tdl.vireo.model.Degree;
import org.tdl.vireo.model.DegreeLevel;

public interface DegreeRepoCustom {

    public Degree create(String name, DegreeLevel level);

}
