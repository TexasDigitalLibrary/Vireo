package org.tdl.vireo.model.repo.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.DepositLocation;
import org.tdl.vireo.model.packager.Packager;
import org.tdl.vireo.model.repo.AbstractPackagerRepo;
import org.tdl.vireo.model.repo.DepositLocationRepo;
import org.tdl.vireo.model.repo.custom.DepositLocationRepoCustom;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.tamu.weaver.data.model.repo.impl.AbstractWeaverOrderedRepoImpl;

public class DepositLocationRepoImpl extends AbstractWeaverOrderedRepoImpl<DepositLocation, DepositLocationRepo> implements DepositLocationRepoCustom {

    @Autowired
    private DepositLocationRepo depositLocationRepo;

    @Autowired
    private AbstractPackagerRepo packagerRepo;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public DepositLocation create(Map<String, Object> depositLocationJson) {

        Packager<?> packager = (Packager<?>) packagerRepo.findOne(objectMapper.convertValue(depositLocationJson.get("packager"), JsonNode.class).get("id").asLong());

        String onBehalfOf = null;
        if (depositLocationJson.get("onBehalfOf") != null) {
            onBehalfOf = (String) depositLocationJson.get("onBehalfOf");
        }

        return create((String) depositLocationJson.get("name"), (String) depositLocationJson.get("repository"), (String) depositLocationJson.get("collection"), (String) depositLocationJson.get("username"), (String) depositLocationJson.get("password"), onBehalfOf, packager, (String) depositLocationJson.get("depositorName"), (Integer) depositLocationJson.get("timeout"));
    }

    @Override
    public DepositLocation create(String name, String repository, String collection, String username, String password, String onBehalfOf, Packager<?> packager, String depositor, int timeout) {
        DepositLocation depositLocation = createDetached(name, repository, collection, username, password, onBehalfOf, packager, depositor, timeout);
        depositLocation.setPosition(depositLocationRepo.count() + 1);
        return super.create(depositLocation);
    }

    @Override
    public DepositLocation createDetached(Map<String, Object> depositLocationJson) {
        Packager<?> packager = null;
        if (depositLocationJson.get("packager") != null) {
            packager = (Packager<?>) packagerRepo.getOne(objectMapper.convertValue(depositLocationJson.get("packager"), JsonNode.class).get("id").asLong());
        }

        String onBehalfOf = null;
        if (depositLocationJson.get("onBehalfOf") != null) {
            onBehalfOf = (String) depositLocationJson.get("onBehalfOf");
        }

        Integer timeout = null;
        if (depositLocationJson.get("timeout") != null) {
            timeout = (Integer) depositLocationJson.get("timeout");
        }

        return createDetached((String) depositLocationJson.get("name"), (String) depositLocationJson.get("repository"), null, (String) depositLocationJson.get("username"), (String) depositLocationJson.get("password"), onBehalfOf, packager, (String) depositLocationJson.get("depositorName"), timeout);
    }

    @Override
    public DepositLocation createDetached(String name, String repository, String collection, String username, String password, String onBehalfOf, Packager<?> packager, String depositor, int timeout) {
        return new DepositLocation(name, repository, collection, username, password, onBehalfOf, packager, depositor, timeout);
    }

    @Override
    public Class<?> getModelClass() {
        return DepositLocation.class;
    }

    @Override
    protected String getChannel() {
        return "/channel/deposit-location";
    }

}
