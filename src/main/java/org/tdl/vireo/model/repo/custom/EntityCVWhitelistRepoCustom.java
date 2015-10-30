package org.tdl.vireo.model.repo.custom;

import java.util.List;

import org.tdl.vireo.model.EntityCVWhitelist;

public interface EntityCVWhitelistRepoCustom {
    
    public EntityCVWhitelist create(String entityName);

    public EntityCVWhitelist create(String entityName, List<String> propertyNames);

}
