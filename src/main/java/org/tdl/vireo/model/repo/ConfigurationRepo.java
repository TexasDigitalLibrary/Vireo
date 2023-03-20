package org.tdl.vireo.model.repo;

import java.util.List;

import org.tdl.vireo.model.ManagedConfiguration;
import org.tdl.vireo.model.repo.custom.ConfigurationRepoCustom;

import edu.tamu.weaver.data.model.repo.WeaverRepo;

public interface ConfigurationRepo extends WeaverRepo<ManagedConfiguration>, ConfigurationRepoCustom {

    public ManagedConfiguration findByName(String name);

    public ManagedConfiguration findByNameAndType(String name, String type);

    public List<ManagedConfiguration> findByType(String type);

}
