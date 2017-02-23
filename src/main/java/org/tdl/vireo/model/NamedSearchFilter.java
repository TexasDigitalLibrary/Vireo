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
public class NamedSearchFilter extends BaseEntity {

    private String name;

    @ManyToOne(cascade = { REFRESH, MERGE }, fetch = EAGER, optional = true)
    private SubmissionListColumn submissionListColumn;

    @ElementCollection(fetch = EAGER)
    private Set<FilterCriterion> filterCriteria;

    @Column(nullable = false)
    private Boolean allColumnSearch;

    @Column(nullable = false)
    private Boolean exactMatch;

    public NamedSearchFilter() {
        setFilters(new HashSet<FilterCriterion>());
        setAllColumnSearch(true);
        setExactMatch(false);
    }

    public NamedSearchFilter(SubmissionListColumn submissionListColumn) {
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
     * @param submissionListColumn
     *            the submissionListColumn to set
     */
    public void setSubmissionListColumn(SubmissionListColumn submissionListColumn) {
        this.submissionListColumn = submissionListColumn;
        if (this.submissionListColumn != null) {
            setAllColumnSearch(false);
        } else {
            setAllColumnSearch(true);
        }
    }

    /**
     * @return the filters
     */
    public Set<FilterCriterion> getFilters() {
        return filterCriteria;
    }

    /**
     * @param filterStrings
     *            the filterStrings to set
     */
    public void setFilters(Set<FilterCriterion> filterCriteria) {
        this.filterCriteria = filterCriteria;
    }

    public void addFilter(FilterCriterion filterCriterion) {
        filterCriteria.add(filterCriterion);
    }

    public void addFilter(String filterValue, String filterGloss) {
        addFilter(filterGloss == null ? new FilterCriterion(filterValue) : new FilterCriterion(filterValue, filterGloss));
    }

    public void removeFilter(FilterCriterion filter) {
        filterCriteria.remove(filter);
    }

    public Boolean getAllColumnSearch() {
        return allColumnSearch;
    }

    public void setAllColumnSearch(Boolean allColumnSearch) {
        this.allColumnSearch = allColumnSearch;
    }

    public Boolean getExactMatch() {
        return exactMatch;
    }

    public void setExactMatch(Boolean exactMatch) {
        this.exactMatch = exactMatch;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<String> getFilterValues() {

        Set<String> filterValues = new HashSet<String>();
        filterCriteria.forEach(filterCriterion -> {
            filterValues.add(filterCriterion.getValue());
        });

        return filterValues;
    }

    public Set<String> getFilterGlosses() {

        Set<String> filterGlosses = new HashSet<String>();
        filterCriteria.forEach(filterCriterion -> {
            filterGlosses.add(filterCriterion.getGloss());
        });

        return filterGlosses;
    }

}
