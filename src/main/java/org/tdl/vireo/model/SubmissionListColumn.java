package org.tdl.vireo.model;

import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.REFRESH;
import static javax.persistence.FetchType.EAGER;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.tdl.vireo.enums.Sort;
import org.tdl.vireo.model.validation.SubmissionListColumnValidator;

import com.fasterxml.jackson.annotation.JsonIgnore;

import edu.tamu.framework.model.BaseEntity;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "title", "predicate", "input_type_id" }))
public class SubmissionListColumn extends BaseEntity {

    @ManyToOne(cascade = { REFRESH, MERGE }, fetch = EAGER, optional = false)
    private InputType inputType;

    @Column(unique = true, nullable = false)
    private String title;

    @Column(nullable = true)
    private String predicate;

    @ElementCollection(fetch = EAGER)
    @OrderColumn
    private List<String> predicatePath;

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

    public SubmissionListColumn(String title, Sort sort, List<String> valuePath) {
        this();
        this.title = title;
        this.sort = sort;
        this.valuePath = valuePath;
    }

    public SubmissionListColumn(String title, Sort sort, List<String> valuePath, InputType inputType) {
        this(title, sort, valuePath);
        this.inputType = inputType;
    }

    public SubmissionListColumn(String title, Sort sort, String predicate, List<String> predicatePath, List<String> valuePath) {
        this(title, sort, valuePath);
        this.predicate = predicate;
        this.predicatePath = predicatePath;
    }

    public SubmissionListColumn(String title, Sort sort, String predicate, List<String> predicatePath, List<String> valuePath, InputType inputType) {
        this(title, sort, predicate, predicatePath, valuePath);
        this.inputType = inputType;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title
     *            the title to set
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
     * @param predicate
     *            the predicate to set
     */
    public void setPredicate(String predicate) {
        this.predicate = predicate;
    }

    /**
     * @return the predicatePath
     */
    public List<String> getPredicatePath() {
        return predicatePath;
    }

    /**
     * @param predicatePath
     *            the predicatePath to set
     */
    public void setPredicatePath(List<String> predicatePath) {
        this.predicatePath = predicatePath;
    }

    /**
     * @return the valuePath
     */
    public List<String> getValuePath() {
        return valuePath;
    }

    /**
     * @param valuePath
     *            the valuePath to set
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
     * @param filters
     *            the filters to set
     */
    public void setFilters(Set<String> filters) {
        this.filters = filters;
    }

    /**
     *
     * @param filter
     */
    public void addFilter(String filter) {
        if (!filters.contains(filter)) {
            filters.add(filter);
        }
    }

    /**
     *
     * @param filters
     */
    public void addAllFilters(Set<String> filters) {
        this.filters.addAll(filters);
    }

    /**
     *
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
     * @param inputType
     *            the inputType to set
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
     * @param sortOrder
     *            the sortOrder to set
     */
    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    /**
     * @return the sort
     */
    public Sort getSort() {
        return sort;
    }

    /**
     * @param sort
     *            the sort to set
     */
    public void setSort(Sort sort) {
        this.sort = sort;
    }

    /**
     *
     * @return
     */
    public Boolean getVisible() {
        return visible;
    }

    /**
     *
     * @param visible
     */
    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    /**
     *
     * @return
     */
    public Boolean getExactMatch() {
        return exactMatch;
    }

    /**
     *
     * @param exactMatch
     */
    public void setExactMatch(Boolean exactMatch) {
        this.exactMatch = exactMatch;
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status
     *            the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

}
