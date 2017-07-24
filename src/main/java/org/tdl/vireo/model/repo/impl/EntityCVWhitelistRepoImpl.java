package org.tdl.vireo.model.repo.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.EntityCVWhitelist;
import org.tdl.vireo.model.repo.EntityCVWhitelistRepo;
import org.tdl.vireo.model.repo.custom.EntityCVWhitelistRepoCustom;

public class EntityCVWhitelistRepoImpl implements EntityCVWhitelistRepoCustom {

    @Autowired
    EntityCVWhitelistRepo entityCVWhitelistRepo;

    @Override
    public EntityCVWhitelist create(String entityName) {
        return entityCVWhitelistRepo.save(new EntityCVWhitelist(entityName));
    }

    @Override
    public EntityCVWhitelist create(String entityName, List<String> propertyNames) {
        return entityCVWhitelistRepo.save(new EntityCVWhitelist(entityName, propertyNames));
    }

}
