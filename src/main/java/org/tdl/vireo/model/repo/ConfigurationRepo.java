package org.tdl.vireo.model.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tdl.vireo.model.Configuration;
import org.tdl.vireo.model.repo.custom.ConfigurationRepoCustom;

public interface ConfigurationRepo extends JpaRepository<Configuration, Long>, ConfigurationRepoCustom {

    public Configuration findByName(String name);

}
