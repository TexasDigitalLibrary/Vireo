package org.tdl.vireo.service;

import java.io.IOException;
import java.util.ArrayList;
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
public class DefaultSubmissionListColumnService {

    private final Logger logger = LoggerFactory.getLogger(DefaultSubmissionListColumnService.class);

    private final List<SubmissionListColumn> defaultSubmissionListColumns;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ResourcePatternResolver resourcePatternResolver;

    @Autowired
    private SubmissionListColumnRepo submissionListColumnRepo;

    DefaultSubmissionListColumnService() {
        this.defaultSubmissionListColumns = new ArrayList<SubmissionListColumn>();
    }

    @PostConstruct
    void init() throws StreamReadException, DatabindException, IOException {
        logger.info("Loading default Submission List Columns");

        Resource resource = resourcePatternResolver.getResource("classpath:/submission_list_columns/SYSTEM_Default_Submission_List_Column_Titles.json");
        String[] defaultSubmissionListColumnTitles = objectMapper.readValue(resource.getInputStream(), new TypeReference<String[]>() { });

        for (String defaultTitle : defaultSubmissionListColumnTitles) {
            SubmissionListColumn dbSubmissionListColumn = submissionListColumnRepo.findByTitle(defaultTitle);
            if (dbSubmissionListColumn != null) {
                this.defaultSubmissionListColumns.add(dbSubmissionListColumn);
            } else {
                logger.warn("Unable to find submission list column with title " + defaultTitle);
            }
        }
    }

    public List<SubmissionListColumn> getDefaultSubmissionListColumns() {
        return new ArrayList<>(defaultSubmissionListColumns);
    }

}
