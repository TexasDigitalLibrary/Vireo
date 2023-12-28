package org.tdl.vireo.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.tdl.vireo.model.SubmissionListColumn;

@Service
public class DefaultSubmissionListColumnService {

    private final List<SubmissionListColumn> defaultSubmissionListColumns;

    public DefaultSubmissionListColumnService() {
        this.defaultSubmissionListColumns = new ArrayList<SubmissionListColumn>();
    }

    public List<SubmissionListColumn> getDefaultSubmissionListColumns() {
        return defaultSubmissionListColumns;
    }

    public void addDefaultSubmissionListColumn(SubmissionListColumn defaultSubmissionListColumn) {
        this.defaultSubmissionListColumns.add(defaultSubmissionListColumn);
    }

}
