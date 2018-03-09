package org.tdl.vireo.model.repo;

import org.tdl.vireo.model.packager.AbstractPackager;
import org.tdl.vireo.model.repo.custom.AbstractPackagerRepoCustom;

import edu.tamu.weaver.data.model.repo.WeaverRepo;

public interface AbstractPackagerRepo extends WeaverRepo<AbstractPackager<?>>, AbstractPackagerRepoCustom {

    public AbstractPackager<?> findByName(String name);

}
