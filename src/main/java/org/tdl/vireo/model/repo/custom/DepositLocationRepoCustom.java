package org.tdl.vireo.model.repo.custom;

import org.tdl.vireo.model.DepositLocation;

public interface DepositLocationRepoCustom {

    public DepositLocation create(String name, String repository, String collection, String username, String password, String onBehalfOf, String packager, String depositor);

    public void reorder(Long src, Long dest);

    public void remove(DepositLocation depositLocation);

}
