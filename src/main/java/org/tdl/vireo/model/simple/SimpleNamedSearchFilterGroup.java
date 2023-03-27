package org.tdl.vireo.model.simple;

import java.io.Serializable;
import java.util.List;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.hibernate.annotations.Immutable;
import org.tdl.vireo.model.NamedSearchFilter;
import org.tdl.vireo.model.Sort;
import org.tdl.vireo.model.SubmissionListColumn;

@Entity
@Immutable
@Table(name = "named_search_filter_group")
public class SimpleNamedSearchFilterGroup implements Serializable {

    @Transient
    private static final long serialVersionUID = 483874224486014344L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", insertable = false, updatable = false, nullable = false)
    private Long id;

    @Column(name = "user_id", insertable = false, updatable = false, nullable = false)
    private Long userId;

    @Column(insertable = false, updatable = false, nullable = true)
    private String name;

    @Column(insertable = false, updatable = false, nullable = false)
    private Boolean publicFlag;

    @Column(insertable = false, updatable = false, nullable = false)
    private Boolean columnsFlag;

    @Column(insertable = false, updatable = false, nullable = false)
    private Boolean umiRelease;

    @Column(insertable = false, updatable = false, nullable = true)
    private String sortColumnTitle;

    @Column(insertable = false, updatable = false, nullable = true)
    @Enumerated(EnumType.STRING)
    private Sort sortDirection;

    @Transient
    private List<SubmissionListColumn> savedColumns;

    @Transient
    private Set<NamedSearchFilter> namedSearchFilters;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getPublicFlag() {
        return publicFlag;
    }

    public void setPublicFlag(Boolean publicFlag) {
        this.publicFlag = publicFlag;
    }

    public Boolean getColumnsFlag() {
        return columnsFlag;
    }

    public void setColumnsFlag(Boolean columnsFlag) {
        this.columnsFlag = columnsFlag;
    }

    public Boolean getUmiRelease() {
        return umiRelease;
    }

    public void setUmiRelease(Boolean umiRelease) {
        this.umiRelease = umiRelease;
    }

    public String getSortColumnTitle() {
        return sortColumnTitle;
    }

    public void setSortColumnTitle(String sortColumnTitle) {
        this.sortColumnTitle = sortColumnTitle;
    }

    public Sort getSortDirection() {
        return sortDirection;
    }

    public void setSortDirection(Sort sortDirection) {
        this.sortDirection = sortDirection;
    }

    public List<SubmissionListColumn> getSavedColumns() {
        return savedColumns;
    }

    public void setSavedColumns(List<SubmissionListColumn> savedColumns) {
        this.savedColumns = savedColumns;
    }

    public Set<NamedSearchFilter> getNamedSearchFilters() {
        return namedSearchFilters;
    }

    public void setNamedSearchFilters(Set<NamedSearchFilter> namedSearchFilters) {
        this.namedSearchFilters = namedSearchFilters;
    }

}
