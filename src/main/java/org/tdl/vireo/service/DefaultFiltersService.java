package org.tdl.vireo.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.tdl.vireo.model.SubmissionListColumn;

@Service
public class DefaultFiltersService {

    private final List<SubmissionListColumn> defaultFilters;

    public DefaultFiltersService() {
        this.defaultFilters = new ArrayList<SubmissionListColumn>();
    }

    public List<SubmissionListColumn> getDefaultFilter() {
        return defaultFilters;
    }

    public void addDefaultFilter(SubmissionListColumn defaultFilter) {
        this.defaultFilters.add(defaultFilter);
    }

}
