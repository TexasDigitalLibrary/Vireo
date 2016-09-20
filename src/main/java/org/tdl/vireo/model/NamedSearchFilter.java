package org.tdl.vireo.model;

import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.REFRESH;
import static javax.persistence.FetchType.EAGER;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.tdl.vireo.model.validation.NamedSearchFilterValidator;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import edu.tamu.framework.model.BaseEntity;

@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "user_id", "name" }) })
public class NamedSearchFilter extends BaseEntity {
    
    @ManyToOne(optional = false)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, scope = User.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    private User user;

    @Column(nullable = true)
    private String name;

    @Column(nullable = false)
    private Boolean publicFlag;
    
    @Column(nullable = false)
    private Boolean columnsFlag;

    @Column(nullable = false)
    private Boolean umiRelease;
    
    @ManyToMany(cascade = { REFRESH, MERGE }, fetch = EAGER)
    @OrderColumn
    private List<SubmissionListColumn> savedColumns;
    
    @Fetch(FetchMode.SELECT)
    @OneToMany(cascade = {REFRESH, MERGE}, fetch = EAGER, orphanRemoval = true)
    private List<FilterCriterion> filterCriteria;
    
    public NamedSearchFilter() {
        setPublicFlag(false);
        setColumnsFlag(false);
        setUmiRelease(false);
        setFilterCriteria(new ArrayList<FilterCriterion>());
        setModelValidator(new NamedSearchFilterValidator());
    }
    

    /**
     * @return the user
     */
    public User getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(User user) {
        this.user = user;
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
     * @return the publicFlag
     */
    public Boolean getPublicFlag() {
        return publicFlag;
    }

    /**
     * @param publicFlag the publicFlag to set
     */
    public void setPublicFlag(Boolean publicFlag) {
        this.publicFlag = publicFlag;
    }

    public Boolean getColumnsFlag() {
		return columnsFlag;
	}

	public void setColumnsFlag(Boolean columnsFlag) {
		this.columnsFlag = columnsFlag;
	}

	/**
     * @return the umiRelease
     */
    public Boolean getUmiRelease() {
        return umiRelease;
    }

    /**
     * @param umiRelease the umiRelease to set
     */
    public void setUmiRelease(Boolean umiRelease) {
        this.umiRelease = umiRelease;
    }

    /**
     * @return the filterCriteria
     */
    public List<FilterCriterion> getFilterCriteria() {
        return filterCriteria;
    }

    /**
     * @param filterCriteria the filterCriteria to set
     */
    public void setFilterCriteria(List<FilterCriterion> filterCriteria) {
        this.filterCriteria = filterCriteria;
    }
    
    public void addFilterCriterion(FilterCriterion filterCriterion) {
        if(!filterCriteria.contains(filterCriterion)) {
            filterCriteria.add(filterCriterion);
        }
    }
    
    public void removeFilterCriterion(FilterCriterion filterCriterion) {
        filterCriteria.remove(filterCriterion);
    }

	public FilterCriterion getFilterCriterion(Long criteriaId) {
		for (FilterCriterion filterCriterion:filterCriteria) {
			if (filterCriterion.getId() == criteriaId) {
				return filterCriterion;
			}
		}
		return null;
	}
    
}
