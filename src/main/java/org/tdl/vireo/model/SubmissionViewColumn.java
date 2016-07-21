package org.tdl.vireo.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.OrderColumn;

import org.tdl.vireo.enums.Sort;
import org.tdl.vireo.model.validation.SubmissionViewColumnValidator;

import edu.tamu.framework.model.BaseEntity;

@Entity
public class SubmissionViewColumn extends BaseEntity {
    
    @Column(unique = true, nullable = false)
    private String title;
    
    @Column(nullable = false)
    private Sort sort;
    
    @Column(nullable = true)
    private Integer sortOrder;
    
    @ElementCollection
    @OrderColumn
    private List<String> path;
    
    @ElementCollection
    @OrderColumn
    private List<String> filters;
    
    private String status;
    
    public SubmissionViewColumn() {
        setModelValidator(new SubmissionViewColumnValidator());
        this.filters = new ArrayList<String>();
    }
    
    public SubmissionViewColumn(String title, Sort sort, List<String> path) {
        this();
        this.title = title;
        this.sort = sort;
        this.path = path;
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
     * @return the path
     */
    public List<String> getPath() {
        return path;
    }

    /**
     * @param path the path to set
     */
    public void setPath(List<String> path) {
        this.path = path;
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
    
    public void addFilter(String filterValue) {
        if(!this.filters.contains(filterValue)) {
            this.filters.add(filterValue);
        }
    }
    
    public void removeFilter(String filterValue) {
        this.filters.remove(filterValue);
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
