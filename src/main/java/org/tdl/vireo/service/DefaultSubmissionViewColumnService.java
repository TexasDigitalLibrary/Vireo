package org.tdl.vireo.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.tdl.vireo.model.SubmissionViewColumn;

@Service
public class DefaultSubmissionViewColumnService {
    
    private List<SubmissionViewColumn> defaultSubmissionViewColumns = new ArrayList<SubmissionViewColumn>();
    
    public DefaultSubmissionViewColumnService() { }
    
    public void setDefaultSubmissionViewColumns(List<SubmissionViewColumn> defaultSubmissionViewColumns) {
        this.defaultSubmissionViewColumns = defaultSubmissionViewColumns;
    }
    
    public List<SubmissionViewColumn> getDefaultSubmissionViewColumns() {
        return defaultSubmissionViewColumns;
    }
    
    public void addDefaultSubmissionViewColumn(SubmissionViewColumn defaultSubmissionViewColumn) {
        if(!this.defaultSubmissionViewColumns.contains(defaultSubmissionViewColumn)) {
            this.defaultSubmissionViewColumns.add(defaultSubmissionViewColumn);
        }
    }
    
    public void removeDefaultSubmissionViewColumn(SubmissionViewColumn defaultSubmissionViewColumn) {
        this.defaultSubmissionViewColumns.remove(defaultSubmissionViewColumn);
    }

}
