package org.tdl.vireo.model;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.CascadeType.DETACH;
import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.CascadeType.REFRESH;
import static javax.persistence.FetchType.EAGER;
import static javax.persistence.FetchType.LAZY;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "name", "category_id" }) )
public class Organization extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @ManyToOne(cascade = { DETACH, REFRESH, MERGE }, fetch = EAGER, optional = false)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, scope = OrganizationCategory.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    private OrganizationCategory category;

    @OneToOne(cascade = ALL, orphanRemoval = true)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, scope = OrganizationCategory.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    private Workflow workflow;

    @ManyToMany(cascade = { DETACH, REFRESH }, fetch = LAZY)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, scope = Organization.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    private Set<Organization> parentOrganizations;

    @ManyToMany(cascade = { DETACH, REFRESH, PERSIST }, fetch = LAZY)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, scope = Organization.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    private Set<Organization> childrenOrganizations;

    @ElementCollection
    private Set<String> emails;
    
    @OneToMany(cascade = ALL, fetch=EAGER, orphanRemoval=true)
    private List<EmailWorkflowRule> emailWorkflowRules;

    public Organization() {
        setParentOrganizations(new TreeSet<Organization>());
        setChildrenOrganizations(new TreeSet<Organization>());
        setEmails(new TreeSet<String>());
        setEmailWorkflowRules(new ArrayList<EmailWorkflowRule>());
    }

    /**
     * 
     * @param name
     * @param category
     */
    public Organization(String name, OrganizationCategory category) {
        this();
        setName(name);
        setCategory(category);
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 
     * @return
     */
    public OrganizationCategory getCategory() {
        return category;
    }

    /**
     * 
     * @param catagory
     */
    public void setCategory(OrganizationCategory category) {
        this.category = category;
    }

    /**
     * @return the workflow
     */
    public Workflow getWorkflow() {
        return workflow;
    }

    /**
     * @param workflow
     *            the workflow to set
     */
    public void setWorkflow(Workflow workflow) {
        this.workflow = workflow;
    }

    /**
     * @return the parentOrganizations
     */
    public Set<Organization> getParentOrganizations() {
        return parentOrganizations;
    }

    /**
     * @param parentOrganizations
     *            the parentOrganizations to set
     */
    private void setParentOrganizations(Set<Organization> parentOrganizations) {
        this.parentOrganizations = parentOrganizations;
    }

    /**
     * 
     * @param parentOrganization
     */
    private void addParentOrganization(Organization parentOrganization) {
        getParentOrganizations().add(parentOrganization);
    }

    /**
     * 
     * @param parentOrganization
     */
    public void removeParentOrganization(Organization parentOrganization) {
        getParentOrganizations().remove(parentOrganization);
    }

    /**
     * @return the childrenOrganizations
     */
    public Set<Organization> getChildrenOrganizations() {
        return childrenOrganizations;
    }

    /**
     * @param childrenOrganizations
     *            the childrenOrganizations to set
     */
    public void setChildrenOrganizations(Set<Organization> childrenOrganizations) {
        if(childrenOrganizations != null) {
            childrenOrganizations.stream().forEach(childOrganization -> {
                childOrganization.addParentOrganization(this);
            });
            this.childrenOrganizations = childrenOrganizations;
        }
    }

    /**
     * 
     * @param childOrganization
     */
    public void addChildOrganization(Organization childOrganization) {
        childOrganization.addParentOrganization(this);
        getChildrenOrganizations().add(childOrganization);
    }

    /**
     * 
     * @param childOrganization
     */
    public void removeChildOrganization(Organization childOrganization) {
        childOrganization.removeParentOrganization(this);
        getChildrenOrganizations().remove(childOrganization);
    }

    /**
     * @return the emails
     */
    public Set<String> getEmails() {
        return emails;
    }

    /**
     * @param emails
     *            the emails to set
     */
    public void setEmails(Set<String> emails) {
        this.emails = emails;
    }

    /**
     * 
     * @param email
     */
    public void addEmail(String email) {
        getEmails().add(email);
    }

    /**
     * 
     * @param email
     */
    public void removeEmail(String email) {
        getEmails().remove(email);
    }

	/**
	 * @return the emailWorkflowRules
	 */
	public List<EmailWorkflowRule> getEmailWorkflowRules() {
		return emailWorkflowRules;
	}

	/**
	 * @param emailWorkflowRules the emailWorkflowRules to set
	 */
	public void setEmailWorkflowRules(List<EmailWorkflowRule> emailWorkflowRules) {
		this.emailWorkflowRules = emailWorkflowRules;
	}
	
	/**
     * 
     * @param emailWorkflowRule
     */
    public void addEmailWorkflowRule(EmailWorkflowRule emailWorkflowRule) {
        getEmailWorkflowRules().add(emailWorkflowRule);
    }

    /**
     * 
     * @param emailWorkflowRules
     */
    public void removeEmailWorkflowRule(EmailWorkflowRule emailWorkflowRule) {
    	getEmailWorkflowRules().remove(emailWorkflowRule);
    }

}
