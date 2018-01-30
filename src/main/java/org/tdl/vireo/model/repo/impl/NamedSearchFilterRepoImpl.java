package org.tdl.vireo.model.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.NamedSearchFilter;
import org.tdl.vireo.model.SubmissionListColumn;
import org.tdl.vireo.model.repo.NamedSearchFilterRepo;
import org.tdl.vireo.model.repo.custom.NamedSearchFilterRepoCustom;

import edu.tamu.weaver.data.model.repo.impl.AbstractWeaverRepoImpl;

public class NamedSearchFilterRepoImpl extends AbstractWeaverRepoImpl<NamedSearchFilter, NamedSearchFilterRepo> implements NamedSearchFilterRepoCustom {

    @Autowired
    private NamedSearchFilterRepo namedSearchFilterRepo;

    @Override
    public NamedSearchFilter create(SubmissionListColumn submissionListColumn) {
        NamedSearchFilter namedSearchFilter = new NamedSearchFilter();
        namedSearchFilter.setName(submissionListColumn.getTitle());
        namedSearchFilter.setSubmissionListColumn(submissionListColumn);
        return namedSearchFilterRepo.save(namedSearchFilter);
    }

    public NamedSearchFilter clone(NamedSearchFilter namedSearchFilter) {
        NamedSearchFilter newNamedSearchFilter = namedSearchFilterRepo.create(namedSearchFilter.getSubmissionListColumn());

        newNamedSearchFilter.setName(namedSearchFilter.getName());
        newNamedSearchFilter.setAllColumnSearch(namedSearchFilter.getAllColumnSearch());
        newNamedSearchFilter.setExactMatch(namedSearchFilter.getExactMatch());

        namedSearchFilter.getFilters().forEach(filter -> {
            newNamedSearchFilter.addFilter(filter);
        });

        return namedSearchFilterRepo.save(newNamedSearchFilter);
    }

    @Override
    protected String getChannel() {
        return "/channel/named-search-filter";
    }

}
