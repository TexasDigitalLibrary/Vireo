package org.tdl.vireo.model;

import static javax.persistence.FetchType.EAGER;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OrderColumn;
import javax.persistence.Transient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.tdl.vireo.model.validation.SubmissionListColumnValidator;

import edu.tamu.weaver.validation.model.ValidatingBaseEntity;

@Entity
public class SubmissionListColumn extends ValidatingBaseEntity {

    @ManyToOne(fetch = EAGER, optional = false)
    private InputType inputType;

    @Column(nullable = false, unique = true)
    private String title;

    @Column(nullable = true)
    private String predicate;

    @ElementCollection(fetch = EAGER)
    @OrderColumn
    private List<String> valuePath;

    @Transient
    private Set<String> filters;

    @Transient
    private Integer sortOrder;

    @Transient
    private Boolean visible;

    @Transient
    private Boolean exactMatch;

    @Transient
    private Sort sort;

    @JsonIgnore
    private String status;

    public SubmissionListColumn() {
        setModelValidator(new SubmissionListColumnValidator());
        this.exactMatch = false;
        this.visible = false;
        this.sortOrder = 0;
        this.sort = Sort.NONE;
        this.filters = new HashSet<String>();
    }

    public SubmissionListColumn(String title, Sort sort) {
        this();
        this.title = title;
        this.sort = sort;
    }

    public SubmissionListColumn(String title, Sort sort, List<String> valuePath) {
        this(title, sort);
        this.valuePath = valuePath;
    }

    public SubmissionListColumn(String title, Sort sort, List<String> valuePath, InputType inputType) {
        this(title, sort, valuePath);
        this.inputType = inputType;
    }

    public SubmissionListColumn(String title, Sort sort, String predicate) {
        this(title, sort);
        this.predicate = predicate;
        this.valuePath = new ArrayList<String>(Arrays.asList(new String[] { "fieldValues", "value" }));
    }

    public SubmissionListColumn(String title, Sort sort, String predicate, InputType inputType) {
        this(title, sort, predicate);
        this.inputType = inputType;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the predicate
     */
    public String getPredicate() {
        return predicate;
    }

    /**
     * @param predicate the predicate to set
     */
    public void setPredicate(String predicate) {
        this.predicate = predicate;
    }

    /**
     * @return the valuePath
     */
    public List<String> getValuePath() {
        return valuePath;
    }

    /**
     * @param valuePath the valuePath to set
     */
    public void setValuePath(List<String> valuePath) {
        this.valuePath = valuePath;
    }

    /**
     * @return the filters
     */
    public Set<String> getFilters() {
        return filters;
    }

    /**
     * @param filters the filters to set
     */
    public void setFilters(Set<String> filters) {
        this.filters = filters;
    }

    /**
     * @param filter
     */
    public void addFilter(String filter) {
        if (!filters.contains(filter)) {
            filters.add(filter);
        }
    }

    /**
     * @param filters
     */
    public void addAllFilters(Set<String> filters) {
        this.filters.addAll(filters);
    }

    /**
     * @param filter
     */
    public void removeFilter(String filter) {
        filters.remove(filter);
    }

    /**
     * @return the inputType
     */
    public InputType getInputType() {
        return inputType;
    }

    /**
     * @param inputType the inputType to set
     */
    public void setInputType(InputType inputType) {
        this.inputType = inputType;
    }

    /**
     * @return the sortOrder
     */
    public Integer getSortOrder() {
        return sortOrder;
    }

    /**
     * @param sortOrder the sortOrder to set
     */
    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    /**
     * @return the visible
     */
    public Boolean getVisible() {
        return visible;
    }

    /**
     * @param visible the visible to set
     */
    public void setVisible(Boolean visible) {
        this.visible = visible;
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

    /**
     * @return the sort
     */
    public Sort getSort() {
        return sort;
    }

    /**
     * @param sort the sort to set
     */
    public void setSort(Sort sort) {
        this.sort = sort;
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

}
