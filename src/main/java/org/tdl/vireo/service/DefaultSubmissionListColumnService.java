package org.tdl.vireo.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.tdl.vireo.model.SubmissionListColumn;

@Service
public class DefaultSubmissionListColumnService {

    private List<SubmissionListColumn> defaultSubmissionListColumns = new ArrayList<SubmissionListColumn>();

    public void setDefaultSubmissionListColumns(List<SubmissionListColumn> defaultSubmissionListColumns) {
        this.defaultSubmissionListColumns = defaultSubmissionListColumns;
    }

    public List<SubmissionListColumn> getDefaultSubmissionListColumns() {
        return defaultSubmissionListColumns;
    }

    public void addDefaultSubmissionListColumn(SubmissionListColumn defaultSubmissionListColumn) {
        this.defaultSubmissionListColumns.add(defaultSubmissionListColumn);
    }

    public void removeDefaultSubmissionListColumn(SubmissionListColumn defaultSubmissionListColumn) {
        this.defaultSubmissionListColumns.remove(defaultSubmissionListColumn);
    }

}
