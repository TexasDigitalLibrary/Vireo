package org.tdl.vireo.model.simple;

import static javax.persistence.FetchType.EAGER;

import java.io.Serializable;
import java.util.List;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.hibernate.annotations.Immutable;
import org.tdl.vireo.model.EmailWorkflowRule;
import org.tdl.vireo.model.Organization;
import org.tdl.vireo.model.WorkflowStep;

@Entity
@Immutable
@Table(name = "organization")
public class SimpleOrganization implements Serializable {

    @Transient
    private static final long serialVersionUID = -6190834548753026763L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", insertable = false, updatable = false, nullable = false)
    private Long id;

    @Column(insertable = false, updatable = false, nullable = false)
    private String name;

    @Immutable
    @ManyToOne(fetch = EAGER, optional = false)
    private SimpleOrganizationCategory category;

    @Column(insertable = false, updatable = false, nullable = false)
    private Boolean acceptsSubmissions = true;

    @Transient
    private List<WorkflowStep> originalWorkflowSteps;

    @Transient
    private List<WorkflowStep> aggregateWorkflowSteps;

    @Column(name = "parent_organization_id", insertable = false, updatable = false, nullable = true)
    private Long parentOrganizationId;

    @Transient
    private Organization parentOrganization;

    @Transient
    private Set<Organization> childrenOrganizations;

    @Transient
    private List<String> emails;

    @Transient
    private List<EmailWorkflowRule> emailWorkflowRules;

    public static Organization toOrganization(SimpleOrganization simpleOrganization) {
        if (simpleOrganization == null) {
            return null;
        }

        Organization organization = new Organization();

        organization.setId(simpleOrganization.getId());
        organization.setName(simpleOrganization.getName());
        organization.setCategory(SimpleOrganizationCategory.toOrganizationCategory(simpleOrganization.getCategory()));
        organization.setAcceptsSubmissions(simpleOrganization.getAcceptsSubmissions());
        organization.setOriginalWorkflowSteps(simpleOrganization.getOriginalWorkflowSteps());
        organization.setAggregateWorkflowSteps(simpleOrganization.getAggregateWorkflowSteps());
        organization.setChildrenOrganizations(simpleOrganization.getChildrenOrganizations());
        organization.setEmails(simpleOrganization.getEmails());
        organization.setEmailWorkflowRules(simpleOrganization.getEmailWorkflowRules());

        // The @JsonIdentityReference on parentOrganization of Organization is set to alwaysAsId.
        if (simpleOrganization.getParentOrganization() == null) {
            if (simpleOrganization.getParentOrganizationId() != null) {
                Organization parent = new Organization();
                parent.setId(simpleOrganization.getParentOrganizationId());
                organization.setParentOrganization(parent);
            }
        } else {
            organization.setParentOrganization(simpleOrganization.getParentOrganization());
        }

        return organization;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SimpleOrganizationCategory getCategory() {
        return category;
    }

    public void setCategory(SimpleOrganizationCategory category) {
        this.category = category;
    }

    public Boolean getAcceptsSubmissions() {
        return acceptsSubmissions;
    }

    public void setAcceptsSubmissions(Boolean acceptsSubmissions) {
        this.acceptsSubmissions = acceptsSubmissions;
    }

    public List<WorkflowStep> getOriginalWorkflowSteps() {
        return originalWorkflowSteps;
    }

    public void setOriginalWorkflowSteps(List<WorkflowStep> originalWorkflowSteps) {
        this.originalWorkflowSteps = originalWorkflowSteps;
    }

    public List<WorkflowStep> getAggregateWorkflowSteps() {
        return aggregateWorkflowSteps;
    }

    public void setAggregateWorkflowSteps(List<WorkflowStep> aggregateWorkflowSteps) {
        this.aggregateWorkflowSteps = aggregateWorkflowSteps;
    }

    public Long getParentOrganizationId() {
        return parentOrganizationId;
    }

    public void setParentOrganizationId(Long parentOrganizationId) {
        this.parentOrganizationId = parentOrganizationId;
    }

    public Organization getParentOrganization() {
        return parentOrganization;
    }

    public void setParentOrganization(Organization parentOrganization) {
        this.parentOrganization = parentOrganization;
    }

    public Set<Organization> getChildrenOrganizations() {
        return childrenOrganizations;
    }

    public void setChildrenOrganizations(Set<Organization> childrenOrganizations) {
        this.childrenOrganizations = childrenOrganizations;
    }

    public List<String> getEmails() {
        return emails;
    }

    public void setEmails(List<String> emails) {
        this.emails = emails;
    }

    public List<EmailWorkflowRule> getEmailWorkflowRules() {
        return emailWorkflowRules;
    }

    public void setEmailWorkflowRules(List<EmailWorkflowRule> emailWorkflowRules) {
        this.emailWorkflowRules = emailWorkflowRules;
    }

}