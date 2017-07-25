package org.tdl.vireo.model.repo.custom;

import org.tdl.vireo.model.Degree;
import org.tdl.vireo.model.DegreeLevel;

public interface DegreeRepoCustom {

    public Degree create(String name, DegreeLevel level);

    public void reorder(Long src, Long dest);

    public void sort(String column);

    public void remove(Degree degree);

}
