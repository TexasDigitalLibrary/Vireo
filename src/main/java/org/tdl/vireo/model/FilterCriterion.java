package org.tdl.vireo.model;

import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.REFRESH;
import static javax.persistence.FetchType.EAGER;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import edu.tamu.framework.model.BaseEntity;

@Entity
public class FilterCriterion extends BaseEntity {
	
	private String name;

    @ManyToOne(cascade = { REFRESH, MERGE }, fetch = EAGER, optional = true)
    private SubmissionListColumn submissionListColumn;
    
    @ElementCollection(fetch = EAGER)
    private Set<String> filters;
    
    @Column(nullable = false)
    private Boolean allColumnSearch;
    
    public FilterCriterion() {
        setFilters(new HashSet<String>());
        setAllColumnSearch(true);
    }
    
    public FilterCriterion(SubmissionListColumn submissionListColumn) {
        this();
        setAllColumnSearch(false);
        setSubmissionListColumn(submissionListColumn);
    }
    
    /**
     * @return the submissionListColumn
     */
    public SubmissionListColumn getSubmissionListColumn() {
        return submissionListColumn;
    }

    /**
     * @param submissionListColumn the submissionListColumn to set
     */
    public void setSubmissionListColumn(SubmissionListColumn submissionListColumn) {
        this.submissionListColumn = submissionListColumn;
        if(this.submissionListColumn != null) {
            setAllColumnSearch(false);
        }
        else {
            setAllColumnSearch(true);
        }
    }
    /**
     * @return the filters
     */
    public Set<String> getFilters() {
        return filters;
    }

    /**
     * @param filterStrings the filterStrings to set
     */
    public void setFilters(Set<String> filters) {
        this.filters = filters;
    }
    
    public void addFilter(String filter) {
    	filters.add(filter);
    }
    
    public void removeFilter(String filter) {
    	filters.remove(filter);
    }
    
    public Boolean getAllColumnSearch() {
    	return allColumnSearch;
    }
    
    public void setAllColumnSearch(Boolean allColumnSearch) {
    	this.allColumnSearch = allColumnSearch;
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
    
}
