package org.tdl.vireo.model.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.tdl.vireo.model.ManagedConfiguration;
import org.tdl.vireo.model.repo.custom.ConfigurationRepoCustom;

@Repository
public interface ConfigurationRepo extends JpaRepository<ManagedConfiguration, Long>, ConfigurationRepoCustom {

    public ManagedConfiguration findByName(String name);

    public ManagedConfiguration findByNameAndType(String name,String type);

    public List<ManagedConfiguration> findByType(String type);
    
}
