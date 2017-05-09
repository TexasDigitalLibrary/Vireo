package org.tdl.vireo.model.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tdl.vireo.model.FieldValue;
import org.tdl.vireo.model.repo.custom.FieldValueRepoCustom;

public interface FieldValueRepo extends JpaRepository<FieldValue, Long>, FieldValueRepoCustom {

}
