package org.tdl.vireo.model.inheritence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

@SuppressWarnings("rawtypes")
public interface HeritableJpaRepo<M extends HeritableComponent> extends JpaRepository<M, Long> {

	public List<M> findByOriginating(M originating);

}
