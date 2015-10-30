package org.tdl.vireo.model.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tdl.vireo.model.EntityCVWhitelist;
import org.tdl.vireo.model.repo.custom.EntityCVWhitelistRepoCustom;

public interface EntityCVWhitelistRepo extends JpaRepository<EntityCVWhitelist, Long>, EntityCVWhitelistRepoCustom {

    EntityCVWhitelist findByEntityName(String entityName);
    
    Long deleteByEntityName(String entityName);
    
}
