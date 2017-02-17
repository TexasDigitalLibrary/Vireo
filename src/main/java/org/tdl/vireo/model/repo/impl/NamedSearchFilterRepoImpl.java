package org.tdl.vireo.model.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.NamedSearchFilter;
import org.tdl.vireo.model.SubmissionListColumn;
import org.tdl.vireo.model.repo.NamedSearchFilterRepo;
import org.tdl.vireo.model.repo.custom.NamedSearchFilterRepoCustom;

public class NamedSearchFilterRepoImpl implements NamedSearchFilterRepoCustom {

    @Autowired
    NamedSearchFilterRepo filterCriterionRepo;

    @Override
    public NamedSearchFilter create(SubmissionListColumn submissionListColumn) {
        NamedSearchFilter fc = new NamedSearchFilter();
        fc.setName(submissionListColumn.getTitle());
        fc.setSubmissionListColumn(submissionListColumn);
        return filterCriterionRepo.save(fc);
    }

    public NamedSearchFilter cloneFilterCriterion(NamedSearchFilter filterCriterion) {
        NamedSearchFilter newFilterCriterion = filterCriterionRepo.create(filterCriterion.getSubmissionListColumn());

        newFilterCriterion.setName(filterCriterion.getName());
        filterCriterion.getFilters().forEach(filter -> {
            newFilterCriterion.addFilter(filter);
        });

        return filterCriterionRepo.save(newFilterCriterion);
    }

}
