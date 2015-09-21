package org.tdl.vireo.model.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.tdl.vireo.model.FieldGloss;

@Repository
public interface FieldGlossRepo extends JpaRepository<FieldGloss, Long> {

}
