package org.tdl.vireo.model;

import static javax.persistence.CascadeType.DETACH;
import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.REFRESH;
import static javax.persistence.CascadeType.REMOVE;
import static javax.persistence.FetchType.EAGER;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import edu.tamu.framework.model.BaseEntity;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "name", "category_id" }) )
public class Organization extends BaseEntity {
    
    @Column(nullable = false)
    private String name;

    @ManyToOne(cascade = { REFRESH }, fetch = EAGER, optional = false)
    private OrganizationCategory category;

    @OneToMany(cascade = { REFRESH, MERGE, REMOVE }, fetch = EAGER, orphanRemoval = true, mappedBy = "originatingOrganization")
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, scope = WorkflowStep.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    @Fetch(FetchMode.SELECT)
    private List<WorkflowStep> workflowSteps;
    
    @ManyToMany(cascade = { REFRESH }, fetch = EAGER)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, scope = WorkflowStep.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    @CollectionTable(uniqueConstraints = @UniqueConstraint(columnNames = { "organization_id", "workflow_order", "workflow_id" }))
    @OrderColumn
    private List<WorkflowStep> workflow;
    
    @ManyToMany(cascade = { REFRESH }, fetch = EAGER)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, scope = Organization.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    private Set<Organization> parentOrganizations;

    @ManyToMany(cascade = { REFRESH, MERGE }, fetch = EAGER)
    private Set<Organization> childrenOrganizations;

    @ElementCollection(fetch = EAGER)
    private Set<String> emails;
    
    @OneToMany(cascade = { REFRESH, REMOVE }, orphanRemoval = true, fetch = EAGER)
    private List<EmailWorkflowRule> emailWorkflowRules;

    public Organization() {
        setWorkflowSteps(new ArrayList<WorkflowStep>());
        setWorkflow(new ArrayList<WorkflowStep>());
        setParentOrganizations(new TreeSet<Organization>());
        setChildrenOrganizations(new TreeSet<Organization>());
        setEmails(new TreeSet<String>());
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
     * @return the workflowSteps
     */
    public List<WorkflowStep> getWorkflowSteps() {
        return workflowSteps;
    }

    /**
     * @param workflowSteps the workflowSteps to set
     */
    public void setWorkflowSteps(List<WorkflowStep> workflowSteps) {
        this.workflowSteps = workflowSteps;
    }
    
    public void addWorkflowStep(WorkflowStep workflowStep) {
        if(!getWorkflowSteps().contains(workflowStep)) {
            getWorkflowSteps().add(workflowStep);
        }
        addStepToWorkflow(workflowStep);
    }
    
    public void removeWorkflowStep(WorkflowStep workflowStep) {
        getWorkflowSteps().remove(workflowStep);
        removeStepFromWorkflow(workflowStep);
    }
    
    public boolean replaceStepInWorkflow(WorkflowStep ws1, WorkflowStep ws2) {    	
    	boolean res = false;
    	int pos = 0;
    	for(WorkflowStep ws : getWorkflow()) {
    		if(ws.getId().equals(ws1.getId())) {
    			getWorkflow().remove(ws1);
    			getWorkflow().add(pos, ws2);
    			res = true;
    			break;
    		}
    		pos++;
    	}
    	return res;
    }
    
    public boolean replaceWorkflowStep(WorkflowStep ws1, WorkflowStep ws2) {
    	boolean res = false;
    	int pos = 0;
    	for(WorkflowStep ws : getWorkflowSteps()) {    		
    		if(ws.getId().equals(ws1.getId())) {
    			getWorkflowSteps().remove(ws1);
    			getWorkflowSteps().add(pos, ws2);
    			res = true;
    			break;
    		}
    		pos++;
    	}
    	replaceStepInWorkflow(ws1, ws2);
    	return res;
    }

    /**
     * @return the workflowSteps
     */
    public List<WorkflowStep> getWorkflow() {
        return workflow;
    }

    /**
     * @param workflowSteps the workflowSteps to set
     */
    public void setWorkflow(List<WorkflowStep> workflow) {
        this.workflow = workflow;
    }
    
    public void addStepToWorkflow(WorkflowStep workflowStep) {
        if(!getWorkflow().contains(workflowStep)) {
            getWorkflow().add(workflowStep);
        }
        getChildrenOrganizations().forEach(childOrganization -> {
            childOrganization.addStepToWorkflow(workflowStep);
        });
    }
    
    public void removeStepFromWorkflow(WorkflowStep workflowStep) {
        getWorkflow().remove(workflowStep);
        getChildrenOrganizations().forEach(childOrganization -> {
            childOrganization.removeStepFromWorkflow(workflowStep);
        });
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
        this.childrenOrganizations = childrenOrganizations;
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
