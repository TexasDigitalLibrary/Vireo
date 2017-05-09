package org.tdl.vireo.model.repo.custom;

import org.tdl.vireo.model.DepositLocation;
import org.tdl.vireo.model.packager.Packager;

import com.fasterxml.jackson.databind.JsonNode;

public interface DepositLocationRepoCustom {

    public DepositLocation create(JsonNode depositLocationJson);

    public DepositLocation create(String name, String repository, String collection, String username, String password, String onBehalfOf, Packager packager, String depositor, int timeout);

    public DepositLocation createDetached(JsonNode depositLocationJson);

    public DepositLocation createDetached(String name, String repository, String collection, String username, String password, String onBehalfOf, Packager packager, String depositor, int timeout);

    public void reorder(Long src, Long dest);

    public void remove(DepositLocation depositLocation);

}
