package org.tdl.vireo.model.jpa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.tdl.vireo.model.AbstractModel;
import org.tdl.vireo.model.AbstractOrderedModel;
import org.tdl.vireo.model.College;
import org.tdl.vireo.model.Department;
import org.tdl.vireo.model.EmailGroup;
import org.tdl.vireo.model.EmailTemplate;
import org.tdl.vireo.model.Person;
import org.tdl.vireo.model.Program;
import org.tdl.vireo.model.RoleType;
import org.tdl.vireo.model.WorkflowEmailRule;
import org.tdl.vireo.state.State;
import org.tdl.vireo.state.StateManager;
import org.tdl.vireo.state.impl.StateManagerImpl;

import play.modules.spring.Spring;

/**
 * Jpa specific implementation of Vireo's WorkflowEmailRule interface.
 * 
 * @author Jeremy Huff, huff@library.tamu.edu
 * 
 */
@Entity
@Table(name = "WorkflowEmailRule")
public class JpaWorkflowEmailRuleImpl extends JpaAbstractModel<JpaWorkflowEmailRuleImpl> implements WorkflowEmailRule {
	
	public String associatedState;
	
	public String condition;
	
    @Column
	@ElementCollection(targetClass=String.class)
	public List<String> emailAddresses;
	
    @OneToOne(targetEntity = JpaEmailTemplateImpl.class)
	public EmailTemplate emailTemplate;

	@Override
	public EmailTemplate getEmailTemplate() {
		return this.emailTemplate;
	}
	
	/**
	 * Create a new WorkflowEmailRule model. 
	 *
	 * @param AssociatedState
	 *            Workflow Email Rules's Associated State.
	 * @param condition
	 * 			  Workflow Email Rule's condition
	 * @param emailAddresses
	 * 			  Workflow Email Rule's email addresses   
	 * @param emailTemplate
	 * 			  Workflow Email Rule's email template                
	 */
	protected JpaWorkflowEmailRuleImpl(State associatedState, AbstractOrderedModel condition, 
			List<String> emailAddresses, EmailTemplate emailTemplate) {
		
		assertManager();
		
		if (associatedState == null) {
			throw new IllegalArgumentException("associatedState is required");
		} 
				
		if (emailAddresses == null) {
			this.emailAddresses = new ArrayList<String>();
		} else {
			this.emailAddresses = emailAddresses;
		}
		
		if (condition == emailAddresses) {
			this.emailAddresses = new ArrayList<String>();
		} else {
			this.emailAddresses = emailAddresses;
		}
		
		this.associatedState = associatedState.getBeanName();
		this.condition = condition.toString();
		this.emailTemplate = emailTemplate;	

	}

	@Override
	public void setEmailTemplate(EmailTemplate emailTemplate) {
		this.emailTemplate = emailTemplate;
	}

	@Override
	public void setEmail(String emailAddress) {
		this.emailAddresses.add(emailAddress);
	}

	@Override
	public void setEmail(EmailGroup emailGroup) {	
	 	for(String emailAddress: emailGroup.getEmails().values()) {
	 		this.emailAddresses.add(emailAddress);
	 	}
	}

	@Override
	public List<String> getEmails() {
		return this.emailAddresses;
	}

	@Override
	public State getAssociatedState() {
		StateManager stateManager = Spring.getBeanOfType(StateManager.class);
		return stateManager.getState(this.associatedState);
	}

	@Override
	public void setAssociatedState(State state) {
		this.associatedState = state.getBeanName();
	}

	@Override
	public String getCondition() {
		return this.condition;
	}

	@Override
	public void setCondition(College condition) {
		this.condition = condition.getName();
	}

	@Override
	public void setCondition(Department condition) {
		this.condition = condition.getName();		
	}

	@Override
	public void setCondition(Program condition) {
		this.condition = condition.getName();		
	}

}
