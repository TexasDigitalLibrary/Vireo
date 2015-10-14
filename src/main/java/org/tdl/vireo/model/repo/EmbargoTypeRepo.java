package org.tdl.vireo.model.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tdl.vireo.enums.EmbargoGuarantor;
import org.tdl.vireo.model.EmbargoType;
import org.tdl.vireo.model.repo.custom.EmbargoTypeRepoCustom;

public interface EmbargoTypeRepo extends JpaRepository<EmbargoType, Long>, EmbargoTypeRepoCustom {
    public EmbargoType findByNameAndGuarantorAndIsSystemRequired(String name, EmbargoGuarantor guarantor, Boolean isSystemRequired);
}
