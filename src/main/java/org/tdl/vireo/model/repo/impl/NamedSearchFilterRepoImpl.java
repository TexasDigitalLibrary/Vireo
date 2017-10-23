package org.tdl.vireo.model.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.NamedSearchFilter;
import org.tdl.vireo.model.SubmissionListColumn;
import org.tdl.vireo.model.repo.NamedSearchFilterRepo;
import org.tdl.vireo.model.repo.custom.NamedSearchFilterRepoCustom;

import edu.tamu.weaver.data.model.repo.impl.AbstractWeaverRepoImpl;

public class NamedSearchFilterRepoImpl extends AbstractWeaverRepoImpl<NamedSearchFilter, NamedSearchFilterRepo> implements NamedSearchFilterRepoCustom {

    @Autowired
    private NamedSearchFilterRepo filterCriterionRepo;

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

    @Override
    protected String getChannel() {
        return "/channel/named-search-filter";
    }

}
