package org.tdl.vireo.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.tdl.vireo.model.SubmissionListColumn;

@Service
public class DefaultFiltersService {

    private final static Logger logger = LoggerFactory.getLogger(DefaultFiltersService.class);

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
        if (defaultFilter != null) {
            this.defaultFilters.add(defaultFilter);
        } else {
            logger.info("Default filter is null!!");
        }
    }

    public void removeDefaultFilter(SubmissionListColumn defaultFilter) {
        this.defaultFilters.remove(defaultFilter);
    }

}
