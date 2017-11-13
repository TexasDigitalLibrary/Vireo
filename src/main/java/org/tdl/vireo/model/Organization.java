package org.tdl.vireo.model;

import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.REFRESH;
import static javax.persistence.CascadeType.REMOVE;
import static javax.persistence.FetchType.EAGER;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdl.vireo.model.validation.OrganizationValidator;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import edu.tamu.weaver.validation.model.ValidatingBaseEntity;

@Entity
@JsonIgnoreProperties(value = { "aggregateWorkflowSteps", "childrenOrganizations" }, allowGetters = true)
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "name", "category_id", "parent_organization_id" }))

public class Organization extends ValidatingBaseEntity {
    
    @Transient
    private Logger LOG = LoggerFactory.getLogger(this.getClass());

    @Column(nullable = false)
    private String name;

    @ManyToOne(cascade = REFRESH, fetch = EAGER, optional = false)
    private OrganizationCategory category;

    private Boolean acceptsSubmissions = true;

    @OneToMany(cascade = { REFRESH, REMOVE }, fetch = EAGER, orphanRemoval = true, mappedBy = "originatingOrganization")
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, scope = WorkflowStep.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    @Fetch(FetchMode.SELECT)
    private List<WorkflowStep> originalWorkflowSteps;

    @ManyToMany(cascade = REFRESH, fetch = EAGER)
    @CollectionTable(uniqueConstraints = @UniqueConstraint(columnNames = { "organization_id", "aggregate_workflow_steps_id", "aggregateWorkflowSteps_order" }))
    @OrderColumn
    private List<WorkflowStep> aggregateWorkflowSteps;

    @ManyToOne(cascade = REFRESH, fetch = EAGER, optional = true)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, scope = Organization.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    private Organization parentOrganization;

    @OneToMany(cascade = { REFRESH, MERGE }, fetch = EAGER)
    @Fetch(FetchMode.SELECT)
    private Set<Organization> childrenOrganizations;

    @ElementCollection(fetch = EAGER)
    private List<String> emails;

    @OneToMany(cascade = { REFRESH, MERGE, REMOVE }, fetch = EAGER)
    private List<EmailWorkflowRule> emailWorkflowRules;

    public Organization() {
        setModelValidator(new OrganizationValidator());
        setOriginalWorkflowSteps(new ArrayList<WorkflowStep>());
        setAggregateWorkflowSteps(new ArrayList<WorkflowStep>());
        setParentOrganization(null);
        setChildrenOrganizations(new TreeSet<Organization>());
        setEmails(new ArrayList<String>());
        setEmailWorkflowRules(new ArrayList<EmailWorkflowRule>());
    }

    /**
     *
     * @param name
     */
    public Organization(String name) {
        this();
        setName(name);
        setCategory(category);
    }

    /**
     *
     * @param name
     * @param category
     */
    public Organization(String name, OrganizationCategory category) {
        this(name);
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
     * @return true if this Organization accepts submissions, false otherwise
     */
    public Boolean getAcceptsSubmissions() {
        return acceptsSubmissions;
    }

    /**
     * @param acceptsSubmissions
     *            whether or not this Organization can accept submissions
     */
    public void setAcceptsSubmissions(Boolean acceptsSubmissions) {
        this.acceptsSubmissions = acceptsSubmissions;
    }

    /**
     * @return the originalWorkflowSteps
     */
    public List<WorkflowStep> getOriginalWorkflowSteps() {
        return originalWorkflowSteps;
    }

    /**
     * @param originalWorkflowSteps
     *            the originalWorkflowSteps to set
     */
    public void setOriginalWorkflowSteps(List<WorkflowStep> originalWorkflowSteps) {
        this.originalWorkflowSteps = originalWorkflowSteps;
    }

    /**
     *
     * @param originalWorkflowStep
     */
    public void addOriginalWorkflowStep(WorkflowStep originalWorkflowStep) {
        if (!getOriginalWorkflowSteps().contains(originalWorkflowStep)) {
            getOriginalWorkflowSteps().add(originalWorkflowStep);
        }
        addAggregateWorkflowStep(originalWorkflowStep);
    }

    /**
     *
     * @param originalWorkflowStep
     */
    public void removeOriginalWorkflowStep(WorkflowStep originalWorkflowStep) {
        getOriginalWorkflowSteps().remove(originalWorkflowStep);
        removeAggregateWorkflowStep(originalWorkflowStep);
    }

    /**
     *
     * @param ws1
     * @param ws2
     * @return
     */
    public boolean replaceOriginalWorkflowStep(WorkflowStep ws1, WorkflowStep ws2) {
        boolean res = false;
        int pos = 0;
        for (WorkflowStep ws : getOriginalWorkflowSteps()) {
            if (ws.getId().equals(ws1.getId())) {
                getOriginalWorkflowSteps().remove(ws1);
                getOriginalWorkflowSteps().add(pos, ws2);
                res = true;
                break;
            }
            pos++;
        }
        replaceAggregateWorkflowStep(ws1, ws2);
        return res;
    }

    /**
     * @return the aggregateWorkflowSteps
     */
    public List<WorkflowStep> getAggregateWorkflowSteps() {
        return aggregateWorkflowSteps;
    }

    /**
     * @param aggregateWorkflowSteps
     *            the aggregateWorkflowSteps to set
     */
    public void setAggregateWorkflowSteps(List<WorkflowStep> aggregateWorkflowSteps) {
        this.aggregateWorkflowSteps = aggregateWorkflowSteps;
    }

    /**
     *
     * @param aggregateWorkflowStep
     */
    public void addAggregateWorkflowStep(WorkflowStep aggregateWorkflowStep) {
        addAggregateWorkflowStep(aggregateWorkflowStep, getAggregateWorkflowSteps().size());
    }

    /**
     *
     * @param aggregateWorkflowStep
     * @param indexOf
     */
    public void addAggregateWorkflowStep(WorkflowStep aggregateWorkflowStep, int indexOf) {
        if (!getAggregateWorkflowSteps().contains(aggregateWorkflowStep)) {
            getAggregateWorkflowSteps().add(indexOf, aggregateWorkflowStep);
        }
        getChildrenOrganizations().forEach(childOrganization -> {
            childOrganization.addAggregateWorkflowStep(aggregateWorkflowStep);
        });

    }

    /**
     *
     * @param aggregateWorkflowStep
     */
    public void removeAggregateWorkflowStep(WorkflowStep aggregateWorkflowStep) {
        getAggregateWorkflowSteps().remove(aggregateWorkflowStep);
        getChildrenOrganizations().forEach(childOrganization -> {
            childOrganization.removeAggregateWorkflowStep(aggregateWorkflowStep);
        });
    }

    /**
     *
     * @param ws1
     * @param ws2
     * @return
     */
    public boolean replaceAggregateWorkflowStep(WorkflowStep ws1, WorkflowStep ws2) {
        boolean res = false;
        int pos = 0;
        for (WorkflowStep ws : getAggregateWorkflowSteps()) {
            if (ws.getId().equals(ws1.getId())) {
                getAggregateWorkflowSteps().remove(ws1);
                getAggregateWorkflowSteps().add(pos, ws2);
                res = true;

                getChildrenOrganizations().forEach(childOrganization -> {
                    childOrganization.replaceAggregateWorkflowStep(ws1, ws2);
                });
                break;
            }
            pos++;
        }
        return res;
    }

    /**
     *
     * @param ws1
     * @param ws2
     * @return
     */
    public boolean swapAggregateWorkflowStep(WorkflowStep ws1, WorkflowStep ws2) {
        boolean res = false;
        int pos1 = getAggregateWorkflowSteps().indexOf(ws1), pos2 = getAggregateWorkflowSteps().indexOf(ws2);
        if (pos1 >= 0 && pos2 >= 0) {
            Collections.swap(getAggregateWorkflowSteps(), pos1, pos2);
            res = true;

            getChildrenOrganizations().forEach(childOrganization -> {
                childOrganization.swapAggregateWorkflowStep(ws1, ws2);
            });
        }
        return res;
    }

    /**
     * @return the parentOrganizations
     */
    public Organization getParentOrganization() {
        return parentOrganization;
    }

    /**
     * @param parentOrganizations
     *            the parentOrganizations to set
     */
    public void setParentOrganization(Organization parentOrganization) {
        this.parentOrganization = parentOrganization;
    }

    /**
     * @return the childrenOrganizations
     */
    public Set<Organization> getChildrenOrganizations() {
        return childrenOrganizations;
    }

    /**
     * 
     * @param childrenOrganizations
     */
    public void setChildrenOrganizations(Set<Organization> childrenOrganizations) {
        this.childrenOrganizations = childrenOrganizations;
    }

    /**
     *
     * @param childOrganization
     */
    public void addChildOrganization(Organization childOrganization) {
        LOG.debug("Organization.addChildOrganization(): Adding child organization " + childOrganization.getName() + "(" + childOrganization.getId() + ") to organization " + this.getName() + "(" + this.getId() + ")");
        childOrganization.setParentOrganization(this);
        getChildrenOrganizations().add(childOrganization);
    }

    /**
     *
     * @param childOrganization
     */
    public void removeChildOrganization(Organization childOrganization) {
        childOrganization.setParentOrganization(null);
        getChildrenOrganizations().remove(childOrganization);
    }

    /**
     * @return the emails
     */
    public List<String> getEmails() {
        return emails;
    }

    /**
     * @param emails
     *            the emails to set
     */
    public void setEmails(List<String> emails) {
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
     * @param emailWorkflowRules
     *            the emailWorkflowRules to set
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

    @JsonIgnore
    public List<EmailWorkflowRule> getAggregateEmailWorkflowRules() {
        
        

        List<EmailWorkflowRule> aggregateEmailWorkflowRules = new ArrayList<EmailWorkflowRule>();
        List<EmailWorkflowRule> newRules = new ArrayList<EmailWorkflowRule>();
        
        aggregateEmailWorkflowRules.addAll(getEmailWorkflowRules());
        
        
        
        if(getParentOrganization() != null) {
            for(EmailWorkflowRule potentialEmailWorkflowRule : getParentOrganization().getAggregateEmailWorkflowRules()) {
                System.out.println("At the ancestor organization " + getParentOrganization().getName() + " considering addition of the rule " + potentialEmailWorkflowRule + " to pass back to caller (org's child or submission controller)");
                
                boolean rejectDuplicateRule = false;
                for(EmailWorkflowRule currentEmailWorkflowRule : aggregateEmailWorkflowRules) {
                        String currentEmailRecipientName = ((AbstractEmailRecipient) currentEmailWorkflowRule.getEmailRecipient()).getName();
                        String potentialEmailRecipientName = ((AbstractEmailRecipient) potentialEmailWorkflowRule.getEmailRecipient()).getName();
    
                        String currentEmailTemplateName = currentEmailWorkflowRule.getEmailTemplate().getName();
                        String potentialEmailTemplateName = potentialEmailWorkflowRule.getEmailTemplate().getName();
    
                        LOG.debug("Current email recepient name: " + currentEmailRecipientName);
                        LOG.debug("Potential email recepient name: " + potentialEmailRecipientName);
    
                        LOG.debug("Current email template name: " + currentEmailTemplateName);
                        LOG.debug("Potential email template name: " + potentialEmailTemplateName);
                        
                        if((currentEmailRecipientName.equals(potentialEmailRecipientName) & currentEmailTemplateName.equals(potentialEmailTemplateName))) {
                            LOG.debug("Potential matches a current one for both recipient and template - must reject");
                            rejectDuplicateRule = true;
                        }
                    }
                if( !rejectDuplicateRule ) {
                    LOG.debug("\tThe rule was not a duplicate - adding rule " + potentialEmailWorkflowRule);
                    newRules.add(potentialEmailWorkflowRule);
                }
                else
                {
                    LOG.debug("\tThe rule was a duplicate - ignoring rule " + potentialEmailWorkflowRule);
                }
                    
            }            
            aggregateEmailWorkflowRules.addAll(newRules);            
        }

        return aggregateEmailWorkflowRules;
    }

    @JsonIgnore
    public List<Organization> getAncestorOrganizations() {

        List<Organization> parentOrganizationHiarchy = new ArrayList<Organization>();

        Organization parent = getParentOrganization();

        if (parent != null && !parent.equals(this)) {
            parentOrganizationHiarchy.add(parent);
            parentOrganizationHiarchy.addAll(parent.getAncestorOrganizations());
        }

        return parentOrganizationHiarchy;
    }

    public void clearAllWorkflowSteps() {

        List<WorkflowStep> originals = new ArrayList<WorkflowStep>(getOriginalWorkflowSteps());
        List<WorkflowStep> aggregets = new ArrayList<WorkflowStep>(getAggregateWorkflowSteps());

        originals.forEach(owfs -> {
            removeOriginalWorkflowStep(owfs);
        });

        aggregets.forEach(awfs -> {
            removeAggregateWorkflowStep(awfs);
        });
    }

    public void clearAggregatedWorkflowStepsFromHiarchy() {
        // Clear this organization's worlkflow steps
        clearAllWorkflowSteps();

        // Clear all steps from the children organization
        this.getChildrenOrganizations().forEach(childOrg -> {
            childOrg.clearAggregatedWorkflowStepsFromHiarchy();
        });
    }

}