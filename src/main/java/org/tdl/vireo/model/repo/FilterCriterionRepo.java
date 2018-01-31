package org.tdl.vireo.model.repo;

import java.util.Optional;

import org.tdl.vireo.model.FilterCriterion;
import org.tdl.vireo.model.repo.custom.FilterCriterionRepoCustom;

import edu.tamu.weaver.data.model.repo.WeaverRepo;

public interface FilterCriterionRepo extends WeaverRepo<FilterCriterion>, FilterCriterionRepoCustom {

    public Optional<FilterCriterion> findByValueAndGloss(String value, String gloss);

}
