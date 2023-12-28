package org.tdl.vireo.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;
import org.tdl.vireo.model.SubmissionListColumn;
import org.tdl.vireo.model.repo.SubmissionListColumnRepo;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@DependsOn("ApplicationInitialization")
public class DefaultFiltersService {

    private final Logger logger = LoggerFactory.getLogger(DefaultFiltersService.class);

    private final List<SubmissionListColumn> defaultFilters;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ResourcePatternResolver resourcePatternResolver;

    @Autowired
    private SubmissionListColumnRepo submissionListColumnRepo;

    public DefaultFiltersService() {
        this.defaultFilters = new ArrayList<SubmissionListColumn>();
    }

    @PostConstruct
    public void init() throws StreamReadException, DatabindException, IOException {
        logger.info("Loading default filter columns");

        Resource resource = resourcePatternResolver.getResource("classpath:/filter_columns/default_filter_columns.json");
        List<SubmissionListColumn> defaultFilterColumns = objectMapper.readValue(resource.getInputStream(), new TypeReference<List<SubmissionListColumn>>() {});

        for (SubmissionListColumn defaultFilterColumn : defaultFilterColumns) {
            SubmissionListColumn dbDefaultFilterColumn = submissionListColumnRepo.findByTitle(defaultFilterColumn.getTitle());
            if (dbDefaultFilterColumn != null) {
                this.defaultFilters.add(dbDefaultFilterColumn);
            } else {
                logger.warn("Unable to find default filter for column " + defaultFilterColumn.getTitle() + "!");
            }
        }
    }

    public List<SubmissionListColumn> getDefaultFilter() {
        return Collections.unmodifiableList(defaultFilters);
    }

}
