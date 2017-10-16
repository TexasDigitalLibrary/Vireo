package org.tdl.vireo.model;

import static javax.persistence.CascadeType.REFRESH;
import static javax.persistence.CascadeType.REMOVE;
import static javax.persistence.FetchType.EAGER;

import java.util.Set;
import java.util.TreeSet;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.tdl.vireo.model.validation.OrganizationCategoryValidator;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import edu.tamu.weaver.validation.model.ValidatingBaseEntity;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "name" }))
public class OrganizationCategory extends ValidatingBaseEntity {

    @Column(nullable = false)
    private String name;

    @OneToMany(cascade = { REFRESH, REMOVE }, fetch = EAGER, mappedBy = "category")
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, scope = Organization.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    private Set<Organization> organizations;

    public OrganizationCategory() {
        setOrganizations(new TreeSet<Organization>());
        setModelValidator(new OrganizationCategoryValidator());
    }

    /**
     *
     * @param name
     * @param level
     */
    public OrganizationCategory(String name) {
        this();
        setName(name);
    }

    /**
     *
     * @return String name
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return Set<Organization> organizations
     */
    public Set<Organization> getOrganizations() {
        return organizations;
    }

    /**
     *
     * @param organizations
     */
    public void setOrganizations(Set<Organization> organizations) {
        this.organizations = organizations;
    }

    /**
     *
     * @param organization
     * @return boolean success
     */
    public boolean addOrganization(Organization organization) {
        return getOrganizations().add(organization);
    }

    /**
     *
     * @param organization
     * @return boolean success
     */
    public boolean removeOrganization(Organization organization) {
        return getOrganizations().remove(organization);
    }

}
