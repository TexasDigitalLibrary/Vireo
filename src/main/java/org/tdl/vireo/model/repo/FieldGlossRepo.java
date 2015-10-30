package org.tdl.vireo.model.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tdl.vireo.model.FieldGloss;
import org.tdl.vireo.model.repo.custom.FieldGlossRepoCustom;

public interface FieldGlossRepo extends JpaRepository<FieldGloss, Long>, FieldGlossRepoCustom {

    public FieldGloss findByValue(String value);

}
