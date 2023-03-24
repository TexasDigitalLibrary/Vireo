package org.tdl.vireo.model.simple;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.Immutable;
import org.tdl.vireo.model.ContactInfo;
import org.tdl.vireo.model.NamedSearchFilterGroup;
import org.tdl.vireo.model.Role;
import org.tdl.vireo.model.SubmissionListColumn;
import org.tdl.vireo.model.User;

@Entity
@Immutable
@Table(name = "weaver_users")
public class SimpleUser implements Serializable {

    @Transient
    private static final long serialVersionUID = -5012554086359256601L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", insertable = false, updatable = false, nullable = false)
    private Long id;

    @Column(insertable = false, updatable = false, nullable = true)
    private String netid;

    @Column(insertable = false, updatable = false, nullable = false, unique = true)
    private String email;

    @Column(insertable = false, updatable = false, nullable = false, name = "first_name")
    private String firstName;

    @Column(insertable = false, updatable = false, nullable = false, name = "last_name")
    private String lastName;

    @Column(insertable = false, updatable = false, name = "middle_name")
    private String middleName;

    @Formula("CONCAT(first_name, ' ', last_name)")
    private String name;

    @Transient
    private Map<String, String> settings;

    @Column(insertable = false, updatable = false)
    private Integer birthYear;

    @Immutable
    @OneToOne(fetch = FetchType.EAGER, optional = true)
    private ContactInfo currentContactInfo;

    @Immutable
    @OneToOne(fetch = FetchType.EAGER, optional = true)
    private ContactInfo permanentContactInfo;

    @Column(insertable = false, updatable = false, nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(insertable = false, updatable = false)
    private String orcid;

    @Column(insertable = false, updatable = false, nullable = false)
    private Integer pageSize;

    @Transient
    private List<SubmissionListColumn> submissionViewColumns;

    @Transient
    private List<SubmissionListColumn> filterColumns;

    @Transient
    private NamedSearchFilterGroup activeFilter;

    @Transient
    private List<NamedSearchFilterGroup> savedFilters;

    public static User toUser(SimpleUser simpleUser) {
        if (simpleUser == null) {
            return null;
        }

        User user = new User(simpleUser.getEmail(), simpleUser.getFirstName(), simpleUser.getLastName(), simpleUser.getRole());

        user.setId(simpleUser.getId());
        user.setNetid(simpleUser.getNetid());
        user.setMiddleName(simpleUser.getMiddleName());
        user.setName(simpleUser.getName());
        user.setSettings(simpleUser.getSettings());
        user.setBirthYear(simpleUser.getBirthYear());
        user.setCurrentContactInfo(simpleUser.getCurrentContactInfo());
        user.setPermanentContactInfo(simpleUser.getPermanentContactInfo());
        user.setOrcid(simpleUser.getOrcid());
        user.setPageSize(simpleUser.getPageSize());
        user.setSubmissionViewColumns(simpleUser.getSubmissionViewColumns());
        user.setFilterColumns(simpleUser.getFilterColumns());
        user.setActiveFilter(simpleUser.getActiveFilter());
        user.setSavedFilters(simpleUser.getSavedFilters());

        return user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNetid() {
        return netid;
    }

    public void setNetid(String netid) {
        this.netid = netid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, String> getSettings() {
        return settings;
    }

    public void setSettings(Map<String, String> settings) {
        this.settings = settings;
    }

    public Integer getBirthYear() {
        return birthYear;
    }

    public void setBirthYear(Integer birthYear) {
        this.birthYear = birthYear;
    }

    public ContactInfo getCurrentContactInfo() {
        return currentContactInfo;
    }

    public void setCurrentContactInfo(ContactInfo currentContactInfo) {
        this.currentContactInfo = currentContactInfo;
    }

    public ContactInfo getPermanentContactInfo() {
        return permanentContactInfo;
    }

    public void setPermanentContactInfo(ContactInfo permanentContactInfo) {
        this.permanentContactInfo = permanentContactInfo;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getOrcid() {
        return orcid;
    }

    public void setOrcid(String orcid) {
        this.orcid = orcid;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public List<SubmissionListColumn> getSubmissionViewColumns() {
        return submissionViewColumns;
    }

    public void setSubmissionViewColumns(List<SubmissionListColumn> submissionViewColumns) {
        this.submissionViewColumns = submissionViewColumns;
    }

    public List<SubmissionListColumn> getFilterColumns() {
        return filterColumns;
    }

    public void setFilterColumns(List<SubmissionListColumn> filterColumns) {
        this.filterColumns = filterColumns;
    }

    public NamedSearchFilterGroup getActiveFilter() {
        return activeFilter;
    }

    public void setActiveFilter(NamedSearchFilterGroup activeFilter) {
        this.activeFilter = activeFilter;
    }

    public List<NamedSearchFilterGroup> getSavedFilters() {
        return savedFilters;
    }

    public void setSavedFilters(List<NamedSearchFilterGroup> savedFilters) {
        this.savedFilters = savedFilters;
    }

}
