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
	
	private String name;

	@ManyToMany(cascade = { REFRESH, MERGE }, fetch = EAGER)
    private List<SubmissionListColumn> submissionListColumns;
    
    @ElementCollection(fetch = EAGER)
    private List<String> filterStrings;
    
    public FilterCriterion() {
    	this("default");
    }

    public FilterCriterion(String name) {
    	setName(name);
        setSubmissionListColumns(new ArrayList<SubmissionListColumn>());
        setFilterStrings(new ArrayList<String>());
    }
    
    /**
     * @return the submissionListColumn
     */
    public List<SubmissionListColumn> getSubmissionListColumns() {
        return submissionListColumns;
    }

    /**
     * @param submissionListColumn the submissionListColumn to set
     */
    public void setSubmissionListColumns(List<SubmissionListColumn> submissionListColumns) {
        this.submissionListColumns = submissionListColumns;
    }
    
    public void addSubmissionListColumn(SubmissionListColumn submissionListColumn) {
        if(!submissionListColumns.contains(submissionListColumn)) {
            submissionListColumns.add(submissionListColumn);
        }
    }
    
    public void removeSubmissionListColumn(SubmissionListColumn submissionListColumn) {
        submissionListColumns.remove(submissionListColumn);
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
    
    public void addFilterString(String filterString) {
        if(!filterStrings.contains(filterString)) {
            filterStrings.add(filterString);
        }
    }
    
    public void removeFilterString(String filterString) {
        filterStrings.remove(filterString);
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
    
}
