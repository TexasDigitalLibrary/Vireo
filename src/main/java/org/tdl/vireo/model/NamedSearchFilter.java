package org.tdl.vireo.model;

import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import edu.tamu.framework.model.BaseEntity;

@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "user_id", "name" }) })
public class NamedSearchFilter extends BaseEntity {
    
    @ManyToOne(optional = false)
    private User user;

    @Column(nullable = false)
    private String name;
    
    @Column(nullable = true)
    private String value;
    
    @Column(nullable = true)
    @Temporal(TemporalType.DATE)
    private Calendar dateValue;

    @Column(nullable = true)
    @Temporal(TemporalType.DATE)
    private Calendar rangeStart;

    @Column(nullable = true)
    @Temporal(TemporalType.DATE)
    private Calendar rangeEnd;
    
    @Column(nullable = false)
    private Boolean publicFlag;

    @Column(nullable = false)
    private Boolean umiRelease;
    
    @Column(nullable = false)
    private Boolean fullSearch;
    
    @ManyToOne(optional = false)
    private SubmissionListColumn submissionListColumn;

    public NamedSearchFilter() {
        setPublicFlag(false);
        setUmiRelease(false);
        setFullSearch(false);
    }
    
    public NamedSearchFilter(User user, String name, SubmissionListColumn submissionListColumn) {
        this();        
        setUser(user);
        setName(name);
        setSubmissionListColumn(submissionListColumn);
    }
    
    public NamedSearchFilter(User user, String name, SubmissionListColumn submissionListColumn, String value) {
        this(user, name, submissionListColumn);
        setValue(value);
    }
    
    public NamedSearchFilter(User user, String name, SubmissionListColumn submissionListColumn, Calendar dateValue) {
        this(user, name, submissionListColumn);
        setDateValue(dateValue);
    }
    
    public NamedSearchFilter(User user, String name, SubmissionListColumn submissionListColumn, Calendar rangeStart, Calendar rangeEnd) {
        this(user, name, submissionListColumn);
        setRangeStart(rangeStart);
        setRangeEnd(rangeEnd);
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
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * @return the dateValue
     */
    public Calendar getDateValue() {
        return dateValue;
    }

    /**
     * @param dateValue the dateValue to set
     */
    public void setDateValue(Calendar dateValue) {
        this.dateValue = dateValue;
    }

    /**
     * @return the rangeStart
     */
    public Calendar getRangeStart() {
        return rangeStart;
    }

    /**
     * @param rangeStart the rangeStart to set
     */
    public void setRangeStart(Calendar rangeStart) {
        this.rangeStart = rangeStart;
    }

    /**
     * @return the rangeEnd
     */
    public Calendar getRangeEnd() {
        return rangeEnd;
    }

    /**
     * @param rangeEnd the rangeEnd to set
     */
    public void setRangeEnd(Calendar rangeEnd) {
        this.rangeEnd = rangeEnd;
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
     * @return the fullSearch
     */
    public Boolean getFullSearch() {
        return fullSearch;
    }

    /**
     * @param fullSearch the fullSearch to set
     */
    public void setFullSearch(Boolean fullSearch) {
        this.fullSearch = fullSearch;
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
    }
    
}
