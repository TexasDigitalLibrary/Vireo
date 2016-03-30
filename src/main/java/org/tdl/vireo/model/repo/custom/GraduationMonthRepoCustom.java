package org.tdl.vireo.model.repo.custom;

import org.tdl.vireo.model.GraduationMonth;

public interface GraduationMonthRepoCustom {

    public GraduationMonth create(int month);
    
    public void reorder(Long src, Long dest);
    
    public void sort(String column);
    
    public void remove(Long index);
    
}
