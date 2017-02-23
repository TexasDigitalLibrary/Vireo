package org.tdl.vireo.model.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.tdl.vireo.model.Configuration;
import org.tdl.vireo.model.repo.custom.ConfigurationRepoCustom;

@Repository
public interface ConfigurationRepo extends JpaRepository<Configuration, Long>, ConfigurationRepoCustom {

    public List<Configuration> findByName(String name); // should always be 1 or 2 elements, never more -- should only be called by ConfigurationRepoCustomImpl

    public Configuration findByNameAndIsSystemRequired(String name, Boolean isSystemRequired); // used by SystemDataLoader

    public List<Configuration> findAllByIsSystemRequired(Boolean isSystemRequired); // should only be used by ConfigurationRepoImpl

    public List<Configuration> findAllByTypeAndIsSystemRequired(String type, Boolean isSystemRequired); // should only be used by ConfigurationRepoImpl
}
