package org.tdl.vireo.model;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.CascadeType.DETACH;
import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.REFRESH;
import static javax.persistence.FetchType.EAGER;
import static javax.persistence.FetchType.LAZY;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToOne;
import javax.persistence.OrderColumn;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.tdl.vireo.enums.AppRole;
import org.tdl.vireo.model.validation.UserValidator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import edu.tamu.framework.model.BaseEntity;
import edu.tamu.framework.model.CoreUser;
import edu.tamu.framework.model.IRole;

@Entity
@Table(name = "users") // "user" is a keyword in sql
public class User extends BaseEntity implements CoreUser {

    // institutional identifier, brought in with framework
    @Column(nullable = true)
    private Long uin;

    // brought in with framework
    @Column(nullable = true)
    private String netid;

    @Column(nullable = false, unique = true)
    private String email;

    // encoded password
    @Column
    @JsonIgnore
    private String password;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column
    private String middleName;

    @ElementCollection(fetch = EAGER)
    @MapKeyColumn(name = "setting")
    @Column(name = "value")
    Map<String, String> settings;

    @Column
    private Integer birthYear;

    @ElementCollection(fetch = EAGER)
    @CollectionTable(name = "shibboleth_affiliations")
    private Set<String> shibbolethAffiliations;

    @OneToOne(cascade = ALL, fetch = LAZY, orphanRemoval = true, optional = true)
    private ContactInfo currentContactInfo;

    @OneToOne(cascade = ALL, fetch = LAZY, orphanRemoval = true, optional = true)
    private ContactInfo permanentContactInfo;

    @ManyToMany(cascade = { DETACH, REFRESH }, fetch = LAZY)
    private Set<Organization> organizations;

    @Column(nullable = false)
    private AppRole role;

    @Column
    private String orcid;

    @Column(nullable = false)
    private Integer pageSize;

    @ManyToMany(cascade = { REFRESH, MERGE }, fetch = EAGER)
    @OrderColumn
    private List<SubmissionListColumn> displayedSubmissionColumns;

    @ManyToMany(cascade = { REFRESH, MERGE }, fetch = EAGER)
    @OrderColumn
    private List<SubmissionListColumn> filterColumns;

    @ManyToOne(cascade = { REFRESH, MERGE }, fetch = EAGER, optional = true)
    private NamedSearchFilter activeFilter;

    @Fetch(FetchMode.SELECT)
    @ManyToMany(cascade = { REFRESH, MERGE }, fetch = EAGER)
    private List<NamedSearchFilter> savedFilters;

    /**
     * 
     */
    public User() {
        setModelValidator(new UserValidator());
        setSettings(new TreeMap<String, String>());
        setOrganizations(new TreeSet<Organization>());
        setShibbolethAffiliations(new TreeSet<String>());
        setSubmissionViewColumns(new ArrayList<SubmissionListColumn>());
        setFilterColumns(new ArrayList<SubmissionListColumn>());
        setSavedFilters(new ArrayList<NamedSearchFilter>());
        setPageSize(10);
    }

    /**
     * 
     * @param email
     * @param firstName
     * @param lastName
     * @param role
     */
    public User(String email, String firstName, String lastName, AppRole role) {
        this();
        setEmail(email);
        setFirstName(firstName);
        setLastName(lastName);
        setRole(role);
    }

    /**
     * 
     * @param email
     * @param firstName
     * @param lastName
     * @param role
     * @param displayedSubmissionColumns
     */
    public User(String email, String firstName, String lastName, AppRole role, List<SubmissionListColumn> displayedSubmissionColumns) {
        this(email, firstName, lastName, role);
        setSubmissionViewColumns(displayedSubmissionColumns);
    }

    /**
     * @return the netid
     */
    public String getNetid() {
        return netid;
    }

    /**
     * @param netid
     *            the netid to set
     */
    public void setNetid(String netid) {
        this.netid = netid;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email
     *            the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return the encoded password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Stores an encoded password
     * 
     * @param password
     *            the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the institutionalIdentifier
     */
    // public String getInstitutionalIdentifier() {
    // return institutionalIdentifier;
    // }

    /**
     * @param institutionalIdentifier
     *            the institutionalIdentifier to set
     */
    // public void setInstitutionalIdentifier(String institutionalIdentifier) {
    // this.institutionalIdentifier = institutionalIdentifier;
    // }

    /**
     * @return the firstName
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * @param firstName
     *            the firstName to set
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * @return the lastName
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * @param lastName
     *            the lastName to set
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * @return the middleName
     */
    public String getMiddleName() {
        return middleName;
    }

    /**
     * @param middleName
     *            the middleName to set
     */
    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    /**
     * @param Key
     *            return the setting by key
     */
    public String getSetting(String key) {
        return settings.get(key);
    }

    /**
     * @param Key
     *            return the setting by key
     */
    public void putSetting(String key, String value) {
        settings.put(key, value);
    }

    /**
     * returns the settings map
     */
    public Map<String, String> getSettings() {
        return settings;
    }

    /**
     * sets the settings map
     */
    public void setSettings(Map<String, String> settings) {
        this.settings = settings;
    }

    /**
     * @return the birthYear
     */
    public Integer getBirthYear() {
        return birthYear;
    }

    /**
     * @param birthYear
     *            the birthYear to set
     */
    public void setBirthYear(Integer birthYear) {
        this.birthYear = birthYear;
    }

    /**
     * @return the shibbolethAffiliations
     */
    public Set<String> getShibbolethAffiliations() {
        return shibbolethAffiliations;
    }

    /**
     * @param shibbolethAffiliations
     *            the shibbolethAffiliations to set
     */
    public void setShibbolethAffiliations(Set<String> shibbolethAffiliations) {
        this.shibbolethAffiliations = shibbolethAffiliations;
    }

    /**
     * 
     * @param shibbolethAffiliation
     */
    public void addShibbolethAffiliation(String shibbolethAffiliation) {
        getShibbolethAffiliations().add(shibbolethAffiliation);
    }

    /**
     * 
     * @param shibbolethAffiliation
     */
    public void removeShibbolethAffiliation(String shibbolethAffiliation) {
        getShibbolethAffiliations().remove(shibbolethAffiliation);
    }

    /**
     * @return the currentContactInfo
     */
    public ContactInfo getCurrentContactInfo() {
        return currentContactInfo;
    }

    /**
     * @param currentContactInfo
     *            the currentContactInfo to set
     */
    public void setCurrentContactInfo(ContactInfo currentContactInfo) {
        this.currentContactInfo = currentContactInfo;
    }

    /**
     * @return the permanentContactInfo
     */
    public ContactInfo getPermanentContactInfo() {
        return permanentContactInfo;
    }

    /**
     * @param permanentContactInfo
     *            the permanentContactInfo to set
     */
    public void setPermanentContactInfo(ContactInfo permanentContactInfo) {
        this.permanentContactInfo = permanentContactInfo;
    }

    /**
     * @return the organizations
     */
    public Set<Organization> getOrganizations() {
        return organizations;
    }

    /**
     * @param organizations
     *            the organizations to set
     */
    public void setOrganizations(Set<Organization> organizations) {
        this.organizations = organizations;
    }

    /**
     * 
     * @param organization
     */
    public void addOrganization(Organization organization) {
        getOrganizations().add(organization);
    }

    /**
     * 
     * @param organization
     */
    public void removeOrganization(Organization organization) {
        getOrganizations().remove(organization);
    }

    /**
     * @return the orcid
     */
    public String getOrcid() {
        return orcid;
    }

    /**
     * @param orcid
     *            the orcid to set
     */
    public void setOrcid(String orcid) {
        this.orcid = orcid;
    }

    /**
     * 
     */
    @Override
    public void setUin(Long uin) {
        this.uin = uin;
    }

    /**
     * 
     */
    @Override
    public Long getUin() {
        return uin;
    }

    @Override
    @JsonDeserialize(as = AppRole.class)
    public void setRole(IRole role) {
        this.role = (AppRole) role;
    }

    @Override
    @JsonSerialize(as = AppRole.class)
    public IRole getRole() {
        return role;
    }

    /**
     * @return the pageSize
     */
    public Integer getPageSize() {
        return pageSize;
    }

    /**
     * @param pageSize
     *            the pageSize to set
     */
    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * @return the submissionViewColumn
     */
    public List<SubmissionListColumn> getSubmissionViewColumns() {
        return displayedSubmissionColumns;
    }

    /**
     * @param submissionViewColumn
     *            the submissionViewColumn to set
     */
    public void setSubmissionViewColumns(List<SubmissionListColumn> displayedSubmissionColumn) {
        this.displayedSubmissionColumns = displayedSubmissionColumn;
    }

    public void addSubmissionViewColumn(SubmissionListColumn displayedSubmissionColumn) {
        if (!this.displayedSubmissionColumns.contains(displayedSubmissionColumn)) {
            this.displayedSubmissionColumns.add(displayedSubmissionColumn);
        }
    }

    public void removeSubmissionViewColumn(SubmissionListColumn displayedSubmissionColumn) {
        this.displayedSubmissionColumns.remove(displayedSubmissionColumn);
    }

    /**
     * @return the activeFilter
     */
    public NamedSearchFilter getActiveFilter() {
        return activeFilter;
    }

    /**
     * @param activeFilter
     *            the activeFilter to set
     */
    public void setActiveFilter(NamedSearchFilter activeFilter) {
        this.activeFilter = activeFilter;
    }

    /**
     * @return the savedFilters
     */
    public List<NamedSearchFilter> getSavedFilters() {
        return savedFilters;
    }

    /**
     * @param savedFilters
     *            the savedFilters to set
     */
    public void setSavedFilters(List<NamedSearchFilter> savedFilters) {
        this.savedFilters = savedFilters;
    }

    public void addSavedFilter(NamedSearchFilter savedFilter) {
        if (!this.savedFilters.contains(savedFilter)) {
            this.savedFilters.add(savedFilter);
        }
    }

    public void removeSavedFilter(NamedSearchFilter savedFilter) {
        this.savedFilters.remove(savedFilter);
    }

    public void loadActiveFilter(NamedSearchFilter filter) {

        this.activeFilter.setSavedColumns(filter.getSavedColumns());
        this.activeFilter.setFilterCriteria(filter.getFilterCriteria());
        this.activeFilter.setPublicFlag(filter.getPublicFlag());
        this.activeFilter.setColumnsFlag(filter.getColumnsFlag());

    }

    public List<SubmissionListColumn> getFilterColumns() {
        return filterColumns;
    }

    public void setFilterColumns(List<SubmissionListColumn> filterColumns) {
        this.filterColumns = filterColumns;
    }
}
