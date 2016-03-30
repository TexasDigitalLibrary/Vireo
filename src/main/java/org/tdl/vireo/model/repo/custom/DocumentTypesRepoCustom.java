package org.tdl.vireo.model.repo.custom;

import org.tdl.vireo.enums.DegreeLevel;
import org.tdl.vireo.model.DocumentType;

public interface DocumentTypesRepoCustom {

    public DocumentType create(String name, DegreeLevel degreeLevel);
    
    public void reorder(Long src, Long dest);
    
    public void sort(String column);

    public void remove(Long index);
}
