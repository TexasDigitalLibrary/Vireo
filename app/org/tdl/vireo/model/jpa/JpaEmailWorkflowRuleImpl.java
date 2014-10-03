package org.tdl.vireo.model.jpa;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.tdl.vireo.email.RecipientType;
import org.tdl.vireo.model.AbstractWorkflowRuleCondition;
import org.tdl.vireo.model.EmailWorkflowRule;
import org.tdl.vireo.model.SettingsRepository;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.state.State;
import org.tdl.vireo.state.StateManager;

import play.modules.spring.Spring;

/**
 * Jpa specific implementation of Vireo's WorkflowEmailRule interface.
 * 
 * @author <a href="mailto:huff@library.tamu.edu">Jeremy Huff</a>
 * 
 */
@Entity
@Table(name = "email_workflow_rules")
public class JpaEmailWorkflowRuleImpl extends JpaAbstractModel<JpaEmailWorkflowRuleImpl> implements EmailWorkflowRule {

	@Column(nullable = false)
	public int displayOrder;
	
	public String associatedState;

	@Column(nullable = true)
	@Enumerated
	public RecipientType recipientType;
	
	@ManyToOne
	@JoinColumn(name="adminGroupRecipientId")
	public JpaAdministrativeGroupImpl adminGroupRecipient;
	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "conditionID")
	public JpaEmailWorkflowRuleConditionImpl condition;
		
	@ManyToOne
	@JoinColumn(name = "emailTemplateId")
	public JpaEmailTemplateImpl emailTemplate;

	/**
	 * Create a new WorkflowEmailRule model.
	 *
	 * @param AssociatedState
	 *            Workflow Email Rules's Associated State.
	 * @param condition
	 *            Workflow Email Rule's condition
	 * @param recipients
	 *            Workflow Email Rule's email addresses
	 * @param emailTemplate
	 *            Workflow Email Rule's email template
	 */
	protected JpaEmailWorkflowRuleImpl(State associatedState) {

		assertManager();

		if (associatedState == null) {
			throw new IllegalArgumentException("associatedState is required");
		}

		this.associatedState = associatedState.getBeanName();
	}

	@Override
	public JpaEmailWorkflowRuleImpl save() {
		assertManager();

		// make sure we have a display order in the order that we're created
		JpaEmailWorkflowRuleImpl ret;
		if(this.getId() == null) {
			ret = super.save();
			ret.setDisplayOrder(Integer.parseInt(String.valueOf(ret.getId())));
		} else {
			if(this.getDisplayOrder() != this.getId()){
				this.setDisplayOrder(Integer.parseInt(String.valueOf(this.getId())));
			}
		}
		ret = super.save();

		return ret;
	}

	@Override
	public JpaEmailWorkflowRuleImpl delete() {
		assertManager();

		return super.delete();
	}

	@Override
	public void setEmailTemplate(JpaEmailTemplateImpl emailTemplate) {

		assertManager();
		
		this.emailTemplate = emailTemplate;
	}
	
	@Override
	public JpaAdministrativeGroupImpl getAdminGroupRecipient() {
	    return this.adminGroupRecipient;
	}
	
	@Override
	public void setAdminGroupRecipient(JpaAdministrativeGroupImpl adminGroup) {
		
		assertManager();
		
	    this.adminGroupRecipient = adminGroup;
	}

	@Override
	public List<String> getRecipients(Submission submission) {
		
		List<String> recipients = new ArrayList<String>();
		SettingsRepository settingRepo = Spring.getBeanOfType(SettingsRepository.class);
		
		if(recipientType == null) return recipients;
		
		switch (recipientType) {
		case Student:
			if(submission.getSubmitter().getCurrentEmailAddress() != null && submission.getSubmitter().getCurrentEmailAddress().length() > 0) {
				recipients.add(submission.getSubmitter().getCurrentEmailAddress());
			}
			break;
		case Advisor:
			if(submission.getCommitteeContactEmail() != null && submission.getCommitteeContactEmail().length() > 0) {
				recipients.add(submission.getCommitteeContactEmail());
			}
			break;
		case College:
			Long collegeId = submission.getCollegeId();
			if(collegeId != null) {
				recipients.addAll(settingRepo.findCollege(collegeId).getEmails().values());
			}
			break;
		case Department:
			Long departmentId = submission.getDepartmentId();
			if(departmentId != null) {
				recipients.addAll(settingRepo.findDepartment(departmentId).getEmails().values());
			}
			break;
		case Program:
			Long programId = submission.getProgramId();
			if(programId != null) {
				recipients.addAll(settingRepo.findProgram(programId).getEmails().values());
			}
			break;
		case AdminGroup:
			if(this.adminGroupRecipient != null) {
				recipients.addAll(adminGroupRecipient.getEmails().values());
			}
			break;
		default:
			throw new UnsupportedOperationException();
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
		
		if (state == null)
			throw new IllegalArgumentException("State is required");
		assertManager();
		
		this.associatedState = state.getBeanName();
	}

	@Override
	public AbstractWorkflowRuleCondition getCondition() {
		return this.condition;
	}

	@Override
	public void setCondition(AbstractWorkflowRuleCondition condition) {
		if (condition == null)
			throw new IllegalArgumentException("Condition is required");
		assertManager();
		
		if (condition instanceof JpaEmailWorkflowRuleConditionImpl) {
			this.condition = (JpaEmailWorkflowRuleConditionImpl) condition;
		} else {
			throw new InvalidParameterException();
		}
	}

	@Override
	public JpaEmailTemplateImpl getEmailTemplate() {
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

	@Override
	public RecipientType getRecipientType() {
		return recipientType;
	}

	@Override
	public void setRecipientType(RecipientType recipientType) {
		if (recipientType == null)
			throw new IllegalArgumentException("Recipient type is required");
		assertManager();
		
		this.recipientType = recipientType;
	}

}
