package org.tdl.vireo.model.repo.custom;

import java.util.Map;

import org.tdl.vireo.model.DepositLocation;
import org.tdl.vireo.model.packager.Packager;

public interface DepositLocationRepoCustom {

    public DepositLocation create(Map<String, Object> depositLocationJson);

    public DepositLocation create(String name, String repository, String collection, String username, String password, String onBehalfOf, Packager<?> packager, String depositor, int timeout);

    public DepositLocation createDetached(Map<String, Object> depositLocationJson);

    public DepositLocation createDetached(String name, String repository, String collection, String username, String password, String onBehalfOf, Packager<?> packager, String depositor, int timeout);

}
