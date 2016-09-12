package org.tdl.vireo.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.tdl.vireo.enums.Sort;
import org.tdl.vireo.model.validation.SubmissionListColumnValidator;

import com.fasterxml.jackson.annotation.JsonIgnore;

import edu.tamu.framework.model.BaseEntity;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "title", "predicate" }) )
public class SubmissionListColumn extends BaseEntity {
    
    @Column(unique = true, nullable = false)
    private String title;
    
    @Column(nullable = false)
    private Sort sort;
    
    @Column(nullable = false)
    private Integer sortOrder;
    
    @Column(nullable = true)
    private String predicate;
    
    @ElementCollection
    @OrderColumn
    private List<String> predicatePath;
    
    @ElementCollection
    @OrderColumn
    private List<String> valuePath;
    
    @ElementCollection
    @OrderColumn
    private List<String> filters;
    
    @JsonIgnore
    private String status;
    
    public SubmissionListColumn() {
        setModelValidator(new SubmissionListColumnValidator());
        this.sortOrder = 0;
        this.filters = new ArrayList<String>();
    }
    
    public SubmissionListColumn(String title, Sort sort, List<String> valuePath) {
        this();
        this.title = title;
        this.sort = sort;
        this.valuePath = valuePath;
    }
    
    public SubmissionListColumn(String title, Sort sort, String predicate, List<String> predicatePath, List<String> valuePath) {
        this(title, sort, valuePath);
        this.predicate = predicate;
        this.predicatePath = predicatePath;
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
     * @return the predicatePath
     */
    public List<String> getPredicatePath() {
        return predicatePath;
    }

    /**
     * @param predicatePath the predicatePath to set
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
     * @param valuePath the valuePath to set
     */
    public void setValuePath(List<String> valuePath) {
        this.valuePath = valuePath;
    }

    /**
     * @return the filters
     */
    public List<String> getFilters() {
        return filters;
    }

    /**
     * @param filters the filters to set
     */
    public void setFilters(List<String> filters) {
        this.filters = filters;
    }
    
    public void addFilter(String filter) {
        if(!filters.contains(filter)) {
            filters.add(filter);
        }
    }
    
    public void removeFilter(String filter) {
        filters.remove(filter);
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
