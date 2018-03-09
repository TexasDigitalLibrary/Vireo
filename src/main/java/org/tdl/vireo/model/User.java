package org.tdl.vireo.model;

import static javax.persistence.CascadeType.DETACH;
import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.REFRESH;
import static javax.persistence.CascadeType.REMOVE;
import static javax.persistence.FetchType.EAGER;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderColumn;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.tdl.vireo.model.validation.UserValidator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import edu.tamu.weaver.auth.model.AbstractWeaverUserDetails;
import edu.tamu.weaver.response.ApiView;
import edu.tamu.weaver.user.model.IRole;

@Entity
public class User extends AbstractWeaverUserDetails {

    private static final long serialVersionUID = -614285536644750464L;

    @JsonView(ApiView.Partial.class)
    @Column(nullable = true)
    private String netid;

    @JsonView(ApiView.Partial.class)
    @Column(nullable = false, unique = true)
    private String email;

    @Column
    @JsonIgnore
    private String password;

    @JsonView(ApiView.Partial.class)
    @Column(nullable = false)
    private String firstName;

    @JsonView(ApiView.Partial.class)
    @Column(nullable = false)
    private String lastName;

    @JsonView(ApiView.Partial.class)
    @Column
    private String middleName;

    @ElementCollection(fetch = EAGER)
    @MapKeyColumn(name = "setting")
    @Column(name = "value")
    private Map<String, String> settings;

    @JsonView(ApiView.Partial.class)
    @Column
    private Integer birthYear;

    @ElementCollection(fetch = EAGER)
    @CollectionTable(name = "shibboleth_affiliations")
    private Set<String> shibbolethAffiliations;

    @OneToOne(cascade = { DETACH, MERGE, REMOVE }, fetch = EAGER, orphanRemoval = true, optional = true)
    private ContactInfo currentContactInfo;

    @OneToOne(cascade = { DETACH, MERGE, REMOVE }, fetch = EAGER, orphanRemoval = true, optional = true)
    private ContactInfo permanentContactInfo;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column
    private String orcid;

    @Column(nullable = false)
    private Integer pageSize;

    @OrderColumn
    @ManyToMany(cascade = { REFRESH }, fetch = EAGER)
    private List<SubmissionListColumn> displayedSubmissionColumns;

    @OrderColumn
    @ManyToMany(cascade = { REFRESH }, fetch = EAGER)
    private List<SubmissionListColumn> filterColumns;

    @ManyToOne(cascade = { REFRESH, MERGE }, fetch = EAGER, optional = true)
    private NamedSearchFilterGroup activeFilter;

    @Fetch(FetchMode.SELECT)
    @OneToMany(cascade = { REFRESH }, fetch = EAGER)
    private List<NamedSearchFilterGroup> savedFilters;

    public User() {
        setModelValidator(new UserValidator());
        setSettings(new TreeMap<String, String>());
        setShibbolethAffiliations(new TreeSet<String>());
        setSubmissionViewColumns(new ArrayList<SubmissionListColumn>());
        setFilterColumns(new ArrayList<SubmissionListColumn>());
        setSavedFilters(new ArrayList<NamedSearchFilterGroup>());
        setPageSize(10);
    }

    /**
     *
     * @param email
     * @param firstName
     * @param lastName
     * @param role
     */
    public User(String email, String firstName, String lastName, Role role) {
        this();
        setEmail(email);
        setUsername(email);
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
     * @param password
     */
    public User(String email, String firstName, String lastName, String password, Role role) {
        this(email, firstName, lastName, role);
        setPassword(password);
    }

    public User(User user) {
        this(user.getEmail(), user.getFirstName(), user.getLastName(), (Role) user.getRole());
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
     * Stores an encoded password
     *
     * @param password
     *            the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

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

    @Override
    @JsonDeserialize(as = Role.class)
    public void setRole(IRole role) {
        this.role = (Role) role;
    }

    @Override
    @JsonSerialize(as = Role.class)
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
    public NamedSearchFilterGroup getActiveFilter() {
        return activeFilter;
    }

    /**
     * @param activeFilter
     *            the activeFilter to set
     */
    public void setActiveFilter(NamedSearchFilterGroup activeFilter) {
        this.activeFilter = activeFilter;
    }

    /**
     * @return the savedFilters
     */
    public List<NamedSearchFilterGroup> getSavedFilters() {
        return savedFilters;
    }

    /**
     * @param savedFilters
     *            the savedFilters to set
     */
    public void setSavedFilters(List<NamedSearchFilterGroup> savedFilters) {
        this.savedFilters = savedFilters;
    }

    public void addSavedFilter(NamedSearchFilterGroup savedFilter) {
        if (!this.savedFilters.contains(savedFilter)) {
            this.savedFilters.add(savedFilter);
        }
    }

    public void removeSavedFilter(NamedSearchFilterGroup savedFilter) {
        this.savedFilters.remove(savedFilter);
    }

    public void loadActiveFilter(NamedSearchFilterGroup filter) {

        this.activeFilter.setSavedColumns(filter.getSavedColumns());
        this.activeFilter.setNamedSearchFilters(filter.getNamedSearchFilters());
        this.activeFilter.setPublicFlag(filter.getPublicFlag());
        this.activeFilter.setColumnsFlag(filter.getColumnsFlag());

    }

    public List<SubmissionListColumn> getFilterColumns() {
        return filterColumns;
    }

    public void setFilterColumns(List<SubmissionListColumn> filterColumns) {
        this.filterColumns = filterColumns;
    }

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(this.getRole().toString());
        authorities.add(authority);
        return authorities;
    }

    @Override
    @JsonIgnore
    public String getPassword() {
        return password;
    }

    @Override
    @JsonIgnore
    public String getUsername() {
        return getEmail();
    }

}
