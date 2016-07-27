package org.tdl.vireo.inheritence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

@SuppressWarnings("rawtypes")
public interface HeritableJpaRepo<M extends HeritableBehavior> extends JpaRepository<M, Long> {

    public List<M> findByOriginating(M originatingHeritableModel);
    
}
