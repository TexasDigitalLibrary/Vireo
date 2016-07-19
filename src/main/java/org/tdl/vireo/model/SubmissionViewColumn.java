package org.tdl.vireo.model;

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
    
    @ElementCollection
    @OrderColumn
    private List<String> path;
    
    private String status;
    
    public SubmissionViewColumn() {
        setModelValidator(new SubmissionViewColumnValidator());
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
