package org.tdl.vireo.model.jpa;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.tdl.vireo.email.RecipientType;
import org.tdl.vireo.model.AbstractWorkflowRuleCondition;
import org.tdl.vireo.model.College;
import org.tdl.vireo.model.SettingsRepository;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.WorkflowEmailRule;
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
@Table(name = "WorkflowEmailRule")
public class JpaWorkflowEmailRuleImpl extends JpaAbstractModel<JpaWorkflowEmailRuleImpl> implements WorkflowEmailRule {

	@Column(nullable = false)
	public int displayOrder;
	
	public String associatedState;

	@Column(nullable = true)
	@Enumerated
	public RecipientType recipientType;
	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "conditionID")
	public JpaEmailWorkflowRuleConditionImpl condition;
		
	@OneToOne
	@JoinColumn(name = "templateId")
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
	protected JpaWorkflowEmailRuleImpl(State associatedState) {

		assertManager();

		if (associatedState == null) {
			throw new IllegalArgumentException("associatedState is required");
		}

		this.associatedState = associatedState.getBeanName();

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
	public void setEmailTemplate(JpaEmailTemplateImpl emailTemplate) {
		this.emailTemplate = emailTemplate;
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
		this.associatedState = state.getBeanName();
	}

	@Override
	public AbstractWorkflowRuleCondition getCondition() {
		return this.condition;
	}

	@Override
	public void setCondition(AbstractWorkflowRuleCondition condition) {
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
		this.recipientType = recipientType;
	}

}
