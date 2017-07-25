package org.tdl.vireo.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.tdl.vireo.AppContextInitializedHandler;
import org.tdl.vireo.model.SubmissionListColumn;

@Service
public class DefaultSubmissionListColumnService {

    final static Logger logger = LoggerFactory.getLogger(AppContextInitializedHandler.class);

    private List<SubmissionListColumn> defaultSubmissionListColumns = new ArrayList<SubmissionListColumn>();

    public DefaultSubmissionListColumnService() {
    }

    public void setDefaultSubmissionListColumns(List<SubmissionListColumn> defaultSubmissionListColumns) {
        this.defaultSubmissionListColumns = defaultSubmissionListColumns;
    }

    public List<SubmissionListColumn> getDefaultSubmissionListColumns() {
        return defaultSubmissionListColumns;
    }

    public void addDefaultSubmissionListColumn(SubmissionListColumn defaultSubmissionListColumn) {
        if (defaultSubmissionListColumn != null) {
            this.defaultSubmissionListColumns.add(defaultSubmissionListColumn);
        } else {
            logger.info("Default submission list column is null!!");
        }
    }

    public void removeDefaultSubmissionListColumn(SubmissionListColumn defaultSubmissionListColumn) {
        this.defaultSubmissionListColumns.remove(defaultSubmissionListColumn);
    }

}
