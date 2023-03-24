package org.tdl.vireo.model.simple;

import java.io.Serializable;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.hibernate.annotations.Immutable;
import org.tdl.vireo.model.Sort;

@Entity
@Immutable
@Table(name = "submission_list_column")
public class SimpleSubmissionListColumn implements Serializable {

    @Transient
    private static final long serialVersionUID = 2596784770905951073L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", insertable = false, updatable = false, nullable = false)
    private Long id;

    @Immutable
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private SimpleInputType inputType;

    @Column(insertable = false, updatable = false, nullable = false)
    private String title;

    @Column(insertable = false, updatable = false, nullable = true)
    private String predicate;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SimpleInputType getInputType() {
        return inputType;
    }

    public void setInputType(SimpleInputType inputType) {
        this.inputType = inputType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPredicate() {
        return predicate;
    }

    public void setPredicate(String predicate) {
        this.predicate = predicate;
    }

    public Set<String> getFilters() {
        return filters;
    }

    public void setFilters(Set<String> filters) {
        this.filters = filters;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    public Boolean getExactMatch() {
        return exactMatch;
    }

    public void setExactMatch(Boolean exactMatch) {
        this.exactMatch = exactMatch;
    }

    public Sort getSort() {
        return sort;
    }

    public void setSort(Sort sort) {
        this.sort = sort;
    }

}
