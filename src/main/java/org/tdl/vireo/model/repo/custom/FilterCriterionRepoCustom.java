package org.tdl.vireo.model.repo.custom;

import org.tdl.vireo.model.FilterCriterion;

public interface FilterCriterionRepoCustom {

    public FilterCriterion create(String value);

    public FilterCriterion create(String value, String gloss);

}
