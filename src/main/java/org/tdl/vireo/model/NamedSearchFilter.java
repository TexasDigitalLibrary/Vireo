package org.tdl.vireo.model;

import static javax.persistence.CascadeType.DETACH;
import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.REFRESH;
import static javax.persistence.FetchType.EAGER;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import edu.tamu.weaver.validation.model.ValidatingBaseEntity;

@Entity
public class NamedSearchFilter extends ValidatingBaseEntity {

    private String name;

    @ManyToOne(cascade = { REFRESH, MERGE }, fetch = EAGER, optional = true)
    private SubmissionListColumn submissionListColumn;

    @ManyToMany(cascade = { REFRESH, MERGE, DETACH }, fetch = EAGER)
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
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
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
        if (this.submissionListColumn != null) {
            setAllColumnSearch(false);
        } else {
            setAllColumnSearch(true);
        }
    }

    /**
     * @return The filter criterion.
     */
    public Set<FilterCriterion> getFilters() {
        return filterCriteria;
    }

    /**
     * @param filterCriteria The filter criterion to set.
     */
    public void setFilters(Set<FilterCriterion> filterCriteria) {
        this.filterCriteria = filterCriteria;
    }

    /**
     * Add the filter criterion.
     *
     * @param filterCriterion The filter criterion to add.
     */
    public void addFilter(FilterCriterion filterCriterion) {
        filterCriteria.add(filterCriterion);
    }

    /**
     * Remove the filter criterion.
     *
     * @param filterCriterion The filter criterion to remove.
     */
    public void removeFilter(FilterCriterion filterCriterion) {
        this.filterCriteria.remove(filterCriterion);
    }

    /**
     * @return the filterCriteria
     */
    public Set<FilterCriterion> getFilterCriteria() {
        return filterCriteria;
    }

    /**
     * Get the filter values from the filter criterion.
     *
     * @return A set of filter values.
     */
    public Set<String> getFilterValues() {

        Set<String> filterValues = new HashSet<String>();
        filterCriteria.forEach(filterCriterion -> {
            filterValues.add(filterCriterion.getValue());
        });

        return filterValues;
    }

    /**
     * Get the filter glosses from the filter criterion.
     *
     * @return A set of filter glosses.
     */
    public Set<String> getFilterGlosses() {

        Set<String> filterGlosses = new HashSet<String>();
        filterCriteria.forEach(filterCriterion -> {
            filterGlosses.add(filterCriterion.getGloss());
        });

        return filterGlosses;
    }

    /**
     * @param filterCriteria the filterCriteria to set
     */
    public void setFilterCriteria(Set<FilterCriterion> filterCriteria) {
        this.filterCriteria = filterCriteria;
    }

    /**
     * @return the allColumnSearch
     */
    public Boolean getAllColumnSearch() {
        return allColumnSearch;
    }

    /**
     * @param allColumnSearch the allColumnSearch to set
     */
    public void setAllColumnSearch(Boolean allColumnSearch) {
        this.allColumnSearch = allColumnSearch;
    }

    /**
     * @return the exactMatch
     */
    public Boolean getExactMatch() {
        return exactMatch;
    }

    /**
     * @param exactMatch the exactMatch to set
     */
    public void setExactMatch(Boolean exactMatch) {
        this.exactMatch = exactMatch;
    }

}
