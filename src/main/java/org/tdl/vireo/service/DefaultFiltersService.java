package org.tdl.vireo.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.tdl.vireo.model.SubmissionListColumn;

@Service
public class DefaultFiltersService {

    private List<SubmissionListColumn> defaultFilters = new ArrayList<SubmissionListColumn>();

    public DefaultFiltersService() {

    }

    public void setDefaultFilter(List<SubmissionListColumn> defaultFilter) {
        this.defaultFilters = defaultFilter;
    }

    public List<SubmissionListColumn> getDefaultFilter() {
        return defaultFilters;
    }

    public void addDefaultFilter(SubmissionListColumn defaultFilter) {
        this.defaultFilters.add(defaultFilter);
    }

    public void removeDefaultFilter(SubmissionListColumn defaultFilter) {
        this.defaultFilters.remove(defaultFilter);
    }

}
