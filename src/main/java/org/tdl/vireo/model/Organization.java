package org.tdl.vireo.model;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.CascadeType.DETACH;
import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.CascadeType.REFRESH;
import static javax.persistence.CascadeType.REMOVE;
import static javax.persistence.FetchType.EAGER;

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
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import edu.tamu.framework.model.BaseEntity;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "name", "category_id" }) )
public class Organization extends BaseEntity {
    
    @Column(nullable = false)
    private String name;

    @ManyToOne(cascade = { DETACH, REFRESH, MERGE }, fetch = EAGER, optional = false)
    private OrganizationCategory category;

    @ManyToMany(cascade = { DETACH, REFRESH, REMOVE, PERSIST }, fetch = EAGER)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, scope = WorkflowStep.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    private List<WorkflowStep> workflowSteps;
    
    @ElementCollection(fetch = EAGER)
    private List<Long> workflowStepOrder;

    @ManyToMany(cascade = { DETACH, REFRESH }, fetch = EAGER)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, scope = Organization.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    private Set<Organization> parentOrganizations;

    @ManyToMany(cascade = { DETACH, REFRESH, MERGE, PERSIST }, fetch = EAGER)
    private Set<Organization> childrenOrganizations;

    @ElementCollection(fetch = EAGER)
    private Set<String> emails;
    
    @OneToMany(cascade = ALL, fetch = EAGER, orphanRemoval = true)
    private List<EmailWorkflowRule> emailWorkflowRules;

    public Organization() {
        setWorkflowSteps(new ArrayList<WorkflowStep>());
        setWorkflowStepOrder(new ArrayList<Long>());
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
    
    public void addWorkflowStep(WorkflowStep workflowStep)
    {
        addWorkflowStep(workflowStep, null);
    }
    
    
    public void addWorkflowStep(WorkflowStep workflowStep, Integer orderIndex) {
        //if(orderIndex < 0) orderIndex = 0;
         
        WorkflowStep ancestorStepMarkedForRemoval = null;
        
        	if(!this.workflowSteps.contains(workflowStep)) {
    	        this.workflowSteps.add(workflowStep);
    	        
    	        //if the Organization has a step that this new one descends from, get rid of that one
    	        for(WorkflowStep ws : workflowSteps)
    	        {
    	            if(workflowStep.descendsFrom(ws))
    	            {
    	                ancestorStepMarkedForRemoval = ws;
    	                break;
    	            }
    	        }
    	        
    	        workflowStep.addContainedByOrganization(this);
    	        if(orderIndex != null){
    	            workflowStepOrder.add(orderIndex, workflowStep.getId());
    	        }else{
    	            workflowStepOrder.add(workflowStep.getId());
    	        }
    	        Set<Organization> children = getChildrenOrganizations();
    	        if(!children.isEmpty()) {
    	            children.parallelStream().forEach(child -> {
    	                //TODO:  do we want to more carefully consider the index where the step gets added to children?  If they have more or fewer steps, it could be inappropriate to put it at the index that works for the parent.
    	                child.addWorkflowStep(workflowStep, orderIndex);
    	            });
    	        }
        	}
        	
        	if(ancestorStepMarkedForRemoval != null)
        	{
            	removeWorkflowStep(ancestorStepMarkedForRemoval);
            	removeWorkflowStepOrder(ancestorStepMarkedForRemoval.getId());
        	}
    }

    public void removeWorkflowStep(WorkflowStep workflowStep) {
        	if(this.workflowSteps.contains(workflowStep)) {
    	        this.workflowSteps.remove(workflowStep);
    	        //remove workflowstep id to workflowstep order
    	        //removeWorkflowStepOrder(workflowStep.getId());
    	        Set<Organization> children = getChildrenOrganizations();
    	        if(!children.isEmpty()) {
    	            children.parallelStream().forEach(child -> {
    	                child.removeWorkflowStep(workflowStep);
    	            });
    	        }
        	}
    }
    
    /**
     * @return the workflowStepOrder
     */
    public List<Long> getWorkflowStepOrder() {
        return workflowStepOrder;
    }

    /**
     * @param workflowStepOrder the workflowStepOrder to set
     */
    public void setWorkflowStepOrder(List<Long> workflowStepOrder) {
        this.workflowStepOrder = workflowStepOrder;
    }

    public void addWorkflowStepOrder(Long workflowStepId) {
        this.workflowStepOrder.add(workflowStepId);
    }
    
    public void removeWorkflowStepOrder(Long workflowStepId) {
        this.workflowStepOrder.remove(workflowStepId);
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
            if(childrenOrganizations.size() == 0)
            {
                this.childrenOrganizations = childrenOrganizations;
                return;
            }
            else
            {
                childrenOrganizations.stream().forEach(childOrganization -> {
                    //childOrganization.addParentOrganization(this);
                    addChildOrganization(childOrganization);
                });
            }            
        }        
    }

    /**
     * 
     * @param childOrganization
     */
    public void addChildOrganization(Organization childOrganization) {
        childOrganization.addParentOrganization(this);
        
        this.childrenOrganizations.add(childOrganization);
        
        List<WorkflowStep> childrenWorkflowSteps = childOrganization.getWorkflowSteps();
        
        if(childrenWorkflowSteps.isEmpty()) {
            for(WorkflowStep workflowStep : workflowSteps) {
                childrenWorkflowSteps.add(workflowStep);
            }
            childOrganization.setWorkflowSteps(childrenWorkflowSteps);
        }
        
        List<Long> duplicateOrderList = new ArrayList<Long>();
        for( Long id : this.getWorkflowStepOrder())
        {
            Long dupId = new Long(id);
            duplicateOrderList.add(dupId);
        }
        childOrganization.setWorkflowStepOrder(duplicateOrderList);
        
        
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
    	this.emails.add(email);
    }

    /**
     * 
     * @param email
     */
    public void removeEmail(String email) {
    	this.emails.remove(email);
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
    	this.emailWorkflowRules.add(emailWorkflowRule);
    }

    /**
     * 
     * @param emailWorkflowRules
     */
    public void removeEmailWorkflowRule(EmailWorkflowRule emailWorkflowRule) {
    	this.emailWorkflowRules.remove(emailWorkflowRule);
    }

    public void replaceWorkflowStep(WorkflowStep previous, WorkflowStep replacement) 
    {
        Integer pos = getPositionOfWorkflowStep(previous);
        removeWorkflowStep(previous);
        addWorkflowStep(replacement, pos);
    }
    
    public Integer getPositionOfWorkflowStep(WorkflowStep ws)
    {
        return workflowStepOrder.indexOf(ws.getId());
    }

}
