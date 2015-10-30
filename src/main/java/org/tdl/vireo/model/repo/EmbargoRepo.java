package org.tdl.vireo.model.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tdl.vireo.enums.EmbargoGuarantor;
import org.tdl.vireo.model.Embargo;
import org.tdl.vireo.model.repo.custom.EmbargoRepoCustom;

public interface EmbargoRepo extends JpaRepository<Embargo, Long>, EmbargoRepoCustom {
    public Embargo findByNameAndGuarantorAndIsSystemRequired(String name, EmbargoGuarantor guarantor, Boolean isSystemRequired);
}
