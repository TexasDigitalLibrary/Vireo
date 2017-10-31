package org.tdl.vireo.model.inheritance;

import java.util.List;

import edu.tamu.weaver.data.model.repo.WeaverRepo;

@SuppressWarnings("rawtypes")
public interface HeritableRepo<M extends HeritableComponent> extends WeaverRepo<M> {

    public List<M> findByOriginating(M originating);

}
