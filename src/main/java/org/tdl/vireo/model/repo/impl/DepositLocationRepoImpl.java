package org.tdl.vireo.model.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.DepositLocation;
import org.tdl.vireo.model.packager.Packager;
import org.tdl.vireo.model.repo.AbstractPackagerRepo;
import org.tdl.vireo.model.repo.DepositLocationRepo;
import org.tdl.vireo.model.repo.custom.DepositLocationRepoCustom;

import com.fasterxml.jackson.databind.JsonNode;

import edu.tamu.framework.service.OrderedEntityService;

public class DepositLocationRepoImpl implements DepositLocationRepoCustom {

    @Autowired
    private OrderedEntityService orderedEntityService;

    @Autowired
    private DepositLocationRepo depositLocationRepo;

    @Autowired
    private AbstractPackagerRepo packagerRepo;

    @Override
    public DepositLocation create(JsonNode depositLocationJson) {

        Packager packager = (Packager) packagerRepo.findOne(depositLocationJson.get("packager").get("id").asLong());

        String onBehalfOf = null;
        if (depositLocationJson.has("onBehalfOf")) {
            onBehalfOf = depositLocationJson.get("onBehalfOf").asText();
        }

        DepositLocation depositLocation = create(
                depositLocationJson.get("name").asText(),
                depositLocationJson.get("repository").asText(),
                depositLocationJson.get("collection").asText(),
                depositLocationJson.get("username").asText(),
                depositLocationJson.get("password").asText(),
                onBehalfOf,
                packager,
                depositLocationJson.get("depositor").asText(),
                depositLocationJson.get("timeout").asInt());
        return depositLocation;
    }

    @Override
    public DepositLocation create(String name, String repository, String collection, String username, String password, String onBehalfOf, Packager packager, String depositor, int timeout) {
        DepositLocation depositLocation = createDetached(name, repository, collection, username, password, onBehalfOf, packager, depositor, timeout);
        depositLocation.setPosition(depositLocationRepo.count() + 1);
        return depositLocationRepo.save(depositLocation);
    }

    @Override
    public DepositLocation createDetached(JsonNode depositLocationJson) {
        Packager packager = null;
        if (depositLocationJson.has("packager")) {
            packager = (Packager) packagerRepo.getOne(depositLocationJson.get("packager").get("id").asLong());
        }

        String onBehalfOf = null;
        if (depositLocationJson.has("onBehalfOf")) {
            onBehalfOf = depositLocationJson.get("onBehalfOf").asText();
        }

        Integer timeout = null;
        if (depositLocationJson.has("timeout")) {
            timeout = depositLocationJson.get("timeout").asInt();
        }

        DepositLocation depositLocation = createDetached(
                depositLocationJson.get("name").asText(),
                depositLocationJson.get("repository").asText(),
                null,
                depositLocationJson.get("username").asText(),
                depositLocationJson.get("password").asText(),
                onBehalfOf,
                packager,
                depositLocationJson.get("depositor").asText(), timeout);
        return depositLocation;
    }

    @Override
    public DepositLocation createDetached(String name, String repository, String collection, String username, String password, String onBehalfOf, Packager packager, String depositor, int timeout) {
        DepositLocation depositLocation = new DepositLocation(name, repository, collection, username, password, onBehalfOf, packager, depositor, timeout);
        return depositLocation;
    }

    @Override
    public void reorder(Long src, Long dest) {
        orderedEntityService.reorder(DepositLocation.class, src, dest);
    }

    @Override
    public void remove(DepositLocation depositLocation) {
        orderedEntityService.remove(depositLocationRepo, DepositLocation.class, depositLocation.getPosition());
    }

}
