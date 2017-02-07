package org.tdl.vireo.model.repo.custom;

import org.tdl.vireo.model.DepositLocation;
import org.tdl.vireo.model.packager.Packager;

public interface DepositLocationRepoCustom {

    public DepositLocation create(String name, String repository, String collection, String username, String password, String onBehalfOf, Packager packager, String depositor);
    
    public void reorder(Long src, Long dest);
    
    public void remove(DepositLocation depositLocation);

}
