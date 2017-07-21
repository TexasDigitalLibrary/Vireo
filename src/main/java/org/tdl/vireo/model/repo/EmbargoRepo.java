package org.tdl.vireo.model.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tdl.vireo.aspect.annotation.EntityControlledVocabulary;
import org.tdl.vireo.enums.EmbargoGuarantor;
import org.tdl.vireo.model.Embargo;
import org.tdl.vireo.model.repo.custom.EmbargoRepoCustom;

@EntityControlledVocabulary(name = "Embargos")
public interface EmbargoRepo extends JpaRepository<Embargo, Long>, EntityControlledVocabularyRepo<Embargo>, EmbargoRepoCustom {

    public Embargo findByNameAndGuarantorAndIsSystemRequired(String name, EmbargoGuarantor guarantor, Boolean isSystemRequired);

    public List<Embargo> findAllByOrderByGuarantorAscPositionAsc();
}
