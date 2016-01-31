package org.tdl.vireo.model.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.tdl.vireo.model.Configuration;
import org.tdl.vireo.model.repo.custom.ConfigurationRepoCustom;

@Repository
public interface ConfigurationRepo extends JpaRepository<Configuration, Long>, ConfigurationRepoCustom {

    public Configuration findByName(String name);

    public Configuration findByNameAndType(String name,String type);

    public List<Configuration> findByType(String type);

}
