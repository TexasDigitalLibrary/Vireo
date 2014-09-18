package org.tdl.vireo.model.jpa;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.tdl.vireo.model.SettingsRepository;
import org.tdl.vireo.model.WorkflowEmailRule;
import org.tdl.vireo.state.State;
import org.tdl.vireo.state.StateManager;
import org.tdl.vireo.state.impl.StateManagerImpl;

import play.modules.spring.Spring;

/**
 * Jpa specific implementation of Vireo's WorkflowEmailRule interface.
 * 
 * @author <a href="mailto:huff@library.tamu.edu">Jeremy Huff</a>
 * 
 */
@Entity
@Table(name = "WorkflowEmailRule")
public class JpaWorkflowEmailRuleImpl extends JpaAbstractModel<JpaWorkflowEmailRuleImpl> implements WorkflowEmailRule {
	
	@Column(nullable = false)
	public int displayOrder;
	
	public String associatedState;
	
	public String condition;
	
	public String recipientIndividual;
		
	public String recipientGroup;
	
	public String recipientGroupName;
	
    @OneToOne(targetEntity = JpaEmailTemplateImpl.class)
	public EmailTemplate emailTemplate;
	
	/**
	 * Create a new WorkflowEmailRule model. 
	 *
	 * @param AssociatedState
	 *            Workflow Email Rules's Associated State.
	 * @param condition
	 * 			  Workflow Email Rule's condition
	 * @param recipients
	 * 			  Workflow Email Rule's email addresses   
	 * @param emailTemplate
	 * 			  Workflow Email Rule's email template                
	 */
	protected JpaWorkflowEmailRuleImpl(State associatedState, String conditionCategory, 
			Long conditionID, String recipient, EmailTemplate template) {
		
		assertManager();
		
		if (associatedState == null) {
			throw new IllegalArgumentException("associatedState is required");
		} 	

		this.associatedState = associatedState.getBeanName();
		
		if(conditionID == null) {
			this.condition = "";
		} else {
			this.condition = conditionID.toString();
		}
		
		this.emailTemplate = template;
		
		this.displayOrder = 0;
	
	}
	
	@Override
	public JpaWorkflowEmailRuleImpl save() {
		assertManager();

		return super.save();
	}
	
	@Override
	public JpaWorkflowEmailRuleImpl delete() {
		assertManager();

		return super.delete();
	}

	@Override
	public void setEmailTemplate(EmailTemplate emailTemplate) {
		this.emailTemplate = emailTemplate;
	}

	@Override
	public void setRecipient(String emailAddress) {
		this.recipientIndividual = emailAddress;
	}

	@Override
	public void setRecipient(EmailGroup emailGroup) {
		this.recipientGroupName = emailGroup.getEmailGroupName();
	 	this.recipientGroup = emailGroup.getName();
	}

	@Override
	public List<String> getRecipients() {
		List<String> recipients = new ArrayList<String>();
		
		if(this.recipientIndividual != null) {
			recipients.add(this.recipientIndividual);
		} else if(this.recipientGroup != null) {
			
			SettingsRepository settingRepo = Spring.getBeanOfType(SettingsRepository.class);
			
			EmailGroup emailGroup = null;
			
			if(this.recipientGroupName.equals("College"))
				emailGroup = settingRepo.findCollege(Long.parseLong(this.recipientGroup));
			
			if(this.recipientGroupName.equals("Department"))
				emailGroup = settingRepo.findDepartment(Long.parseLong(this.recipientGroup));
			
			if(this.recipientGroupName.equals("Program"))
				emailGroup = settingRepo.findProgram(Long.parseLong(this.recipientGroup));
			
			if(emailGroup.equals(null))
				return recipients;
			
			Collection<String> emailAddresses = emailGroup.getEmails().values();
			
			for(String emailAddress: emailAddresses) {
				recipients.add(emailAddress);
			}
		}
		
		return recipients;
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
	public void setCondition(EmailGroup condition) {
		this.condition = condition.getName();
	}
	
	@Override
	public EmailTemplate getEmailTemplate() {
		return this.emailTemplate;
	}

	@Override
	public int getDisplayOrder() {
		return displayOrder;
	}

	@Override
	public void setDisplayOrder(int displayOrder) {
		
		assertManager();
		this.displayOrder = displayOrder;
	}
	
}
