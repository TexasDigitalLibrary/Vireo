package org.tdl.vireo.model.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.tdl.vireo.model.ManagedConfiguration;
import org.tdl.vireo.model.repo.custom.ConfigurationRepoCustom;

@Repository
public interface ConfigurationRepo extends JpaRepository<ManagedConfiguration, Long>, ConfigurationRepoCustom {

    public List<ManagedConfiguration> findByName(String name); // should always be 1 or 2 elements, never more -- should only be called by ConfigurationRepoCustomImpl

    public ManagedConfiguration findByNameAndIsSystemRequired(String name, Boolean isSystemRequired); // used by SystemDataLoader

    public List<ManagedConfiguration> findAllByIsSystemRequired(Boolean isSystemRequired); // should only be used by ConfigurationRepoImpl

    public List<ManagedConfiguration> findAllByTypeAndIsSystemRequired(String type, Boolean isSystemRequired); // should only be used by ConfigurationRepoImpl
    
    public List<ManagedConfiguration> findByType(String type);
    
}
