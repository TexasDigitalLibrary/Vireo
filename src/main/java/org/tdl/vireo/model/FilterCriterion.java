package org.tdl.vireo.model;

import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.REFRESH;
import static javax.persistence.FetchType.EAGER;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;

import edu.tamu.framework.model.BaseEntity;

@Entity
public class FilterCriterion extends BaseEntity {

    @ManyToMany(cascade = { REFRESH, MERGE }, fetch = EAGER)
    private List<SubmissionListColumn> submissionListColumn;
    
    @ElementCollection(fetch = EAGER)
    private List<String> filterStrings;
    
    public FilterCriterion() {
        setSubmissionListColumn(new ArrayList<SubmissionListColumn>());
        setFilterStrings(new ArrayList<String>());
    }

    /**
     * @return the submissionListColumn
     */
    public List<SubmissionListColumn> getSubmissionListColumn() {
        return submissionListColumn;
    }

    /**
     * @param submissionListColumn the submissionListColumn to set
     */
    public void setSubmissionListColumn(List<SubmissionListColumn> submissionListColumn) {
        this.submissionListColumn = submissionListColumn;
    }

    /**
     * @return the filterStrings
     */
    public List<String> getFilterStrings() {
        return filterStrings;
    }

    /**
     * @param filterStrings the filterStrings to set
     */
    public void setFilterStrings(List<String> filterStrings) {
        this.filterStrings = filterStrings;
    }
    
}
