package org.tdl.vireo.model.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tdl.vireo.model.packager.AbstractPackager;
import org.tdl.vireo.model.repo.custom.AbstractPackagerRepoCustom;

public interface AbstractPackagerRepo extends JpaRepository<AbstractPackager, Long>, AbstractPackagerRepoCustom {
    
    public AbstractPackager findByName(String name);

}
