package org.tdl.vireo.model.repo.custom;

import org.tdl.vireo.model.GraduationMonth;

public interface GraduationMonthRepoCustom {

    public GraduationMonth create(int month);
    
    public void reorder(Integer src, Integer dest);
    
    public void remove(Integer index);
    
}
