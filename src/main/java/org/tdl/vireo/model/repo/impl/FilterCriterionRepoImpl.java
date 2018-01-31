package org.tdl.vireo.model.repo.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.FilterCriterion;
import org.tdl.vireo.model.repo.FilterCriterionRepo;
import org.tdl.vireo.model.repo.custom.FilterCriterionRepoCustom;

import edu.tamu.weaver.data.model.repo.impl.AbstractWeaverRepoImpl;

public class FilterCriterionRepoImpl extends AbstractWeaverRepoImpl<FilterCriterion, FilterCriterionRepo> implements FilterCriterionRepoCustom {

    @Autowired
    private FilterCriterionRepo filterCriterionRepo;

    @Override
    public FilterCriterion create(String value) {
        return create(value, value);
    }

    @Override
    public FilterCriterion create(String value, String gloss) {
        Optional<String> actualGloss = Optional.ofNullable(gloss);
        if (!actualGloss.isPresent()) {
            gloss = value;
        }
        Optional<FilterCriterion> filterCriterion = filterCriterionRepo.findByValueAndGloss(value, gloss);
        if (filterCriterion.isPresent()) {
            return filterCriterion.get();
        }
        return filterCriterionRepo.save(new FilterCriterion(value, gloss));
    }

    @Override
    protected String getChannel() {
        return "/channel/filter-criterion";
    }

}
