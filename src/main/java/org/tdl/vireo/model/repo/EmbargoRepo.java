package org.tdl.vireo.model.repo;

import java.util.List;

import org.tdl.vireo.aspect.annotation.EntityCV;
import org.tdl.vireo.model.Embargo;
import org.tdl.vireo.model.EmbargoGuarantor;
import org.tdl.vireo.model.repo.custom.EmbargoRepoCustom;

import edu.tamu.weaver.data.model.repo.WeaverOrderedRepo;

@EntityCV(name = "Embargos", subsets = {
    @EntityCV.Subset(name = "Default Embargos", filters = { @EntityCV.Filter(path = "guarantor", value = "DEFAULT") }),
    @EntityCV.Subset(name = "Proquest Embargos", filters = { @EntityCV.Filter(path = "guarantor", value = "PROQUEST") })
})
public interface EmbargoRepo extends WeaverOrderedRepo<Embargo>, EntityControlledVocabularyRepo<Embargo>, EmbargoRepoCustom {

    public Embargo findByNameAndGuarantorAndIsSystemRequired(String name, EmbargoGuarantor guarantor, Boolean isSystemRequired);

    public List<Embargo> findAllByOrderByGuarantorAscPositionAsc();
    
    
}
