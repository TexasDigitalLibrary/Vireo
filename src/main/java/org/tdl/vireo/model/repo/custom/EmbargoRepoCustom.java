package org.tdl.vireo.model.repo.custom;

import org.tdl.vireo.enums.EmbargoGuarantor;
import org.tdl.vireo.model.Embargo;

public interface EmbargoRepoCustom {
    
    public Embargo create(String name, String description, Integer duration, EmbargoGuarantor guarantor, boolean isActive);
    
    public void reorder(Integer src, Integer dest);
    
    public void sort(String column, EmbargoGuarantor guarantor);
    
    public void remove(Integer index);    
}
