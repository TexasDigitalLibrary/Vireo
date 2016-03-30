package org.tdl.vireo.model.repo.custom;

import org.tdl.vireo.model.DepositLocation;

public interface DepositLocationRepoCustom {

    public DepositLocation create(String name);
    
    public void reorder(Long src, Long dest);
    
    public void remove(Long index);

}
