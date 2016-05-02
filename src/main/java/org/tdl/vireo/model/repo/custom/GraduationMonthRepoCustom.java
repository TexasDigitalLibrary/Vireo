package org.tdl.vireo.model.repo.custom;

import org.tdl.vireo.model.GraduationMonth;

import edu.tamu.framework.validation.ModelBindingResult;

public interface GraduationMonthRepoCustom {

    public GraduationMonth create(int month);
    
    public void reorder(Long src, Long dest);
    
    public void sort(String column);
    
    public void remove(GraduationMonth graduationMonth);
    
    public GraduationMonth validateCreate(GraduationMonth graduationMonth);
    
    public GraduationMonth validateUpdate(GraduationMonth graduationMonth);
    
    public GraduationMonth validateRemove(String idString, ModelBindingResult modelBindingResult);
}
