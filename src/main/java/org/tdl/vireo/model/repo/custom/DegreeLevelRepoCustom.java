package org.tdl.vireo.model.repo.custom;

import org.tdl.vireo.model.DegreeLevel;

public interface DegreeLevelRepoCustom {

    public DegreeLevel create(String name);

    public void reorder(Long src, Long dest);

    public void sort(String column);

    public void remove(DegreeLevel degree);

}
