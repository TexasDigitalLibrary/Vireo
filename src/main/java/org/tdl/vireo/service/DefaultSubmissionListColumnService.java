package org.tdl.vireo.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.tdl.vireo.model.SubmissionListColumn;

@Service
public class DefaultSubmissionListColumnService {
    
    private List<SubmissionListColumn> defaultSubmissionViewColumns = new ArrayList<SubmissionListColumn>();
    
    public DefaultSubmissionListColumnService() { }
    
    public void setDefaultSubmissionViewColumns(List<SubmissionListColumn> defaultSubmissionViewColumns) {
        this.defaultSubmissionViewColumns = defaultSubmissionViewColumns;
    }
    
    public List<SubmissionListColumn> getDefaultSubmissionViewColumns() {
        return defaultSubmissionViewColumns;
    }
    
    public void addDefaultSubmissionViewColumn(SubmissionListColumn defaultSubmissionViewColumn) {
       if(defaultSubmissionViewColumn != null) {
           this.defaultSubmissionViewColumns.add(defaultSubmissionViewColumn);
       }
       else {
           System.out.println("Default submission view column is null!!");
       }
    }
    
    public void removeDefaultSubmissionViewColumn(SubmissionListColumn defaultSubmissionViewColumn) {
        this.defaultSubmissionViewColumns.remove(defaultSubmissionViewColumn);
    }

}
