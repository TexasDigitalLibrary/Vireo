package org.tdl.vireo.model.jpa;

import java.security.InvalidParameterException;
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
import org.tdl.vireo.model.AdministrativeGroup;
import org.tdl.vireo.model.EmailTemplate;
import org.tdl.vireo.model.EmailWorkflowRule;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.services.EmailByRecipientType;
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
	private int displayOrder;

	@Column
	private boolean isSystem = false;

	@Column
	private boolean isDisabled = true;

	@Column
	private String associatedState;

	@Column(nullable = true)
	@Enumerated
	private RecipientType recipientType;

	@ManyToOne
	@JoinColumn(name = "adminGroupRecipientId")
	private JpaAdministrativeGroupImpl adminGroupRecipient;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "conditionID")
	private JpaEmailWorkflowRuleConditionImpl condition;

	@ManyToOne
	@JoinColumn(name = "emailTemplateId")
	private JpaEmailTemplateImpl emailTemplate;

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
	public void setIsSystem(boolean isSystem) {
		assertManager();

		this.isSystem = isSystem;
	}

	@Override
	public boolean isSystem() {
		return this.isSystem;
	}

	@Override
	public void disable() {
		assertManager();

		this.isDisabled = true;
	}

	@Override
	public void enable() {
		assertManager();

		this.isDisabled = false;
	}

	@Override
	public boolean isDisabled() {
		return this.isDisabled;
	}

	@Override
	public JpaEmailWorkflowRuleImpl save() {
		assertManager();

		// make sure we have a display order in the order that we're created
		JpaEmailWorkflowRuleImpl ret;
		if (this.getId() == null) {
			ret = super.save();
			ret.setDisplayOrder(Integer.parseInt(String.valueOf(ret.getId())));
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
	public void setEmailTemplate(EmailTemplate emailTemplate) {

		assertManager();

		this.emailTemplate = (JpaEmailTemplateImpl) emailTemplate;
	}

	@Override
	public AdministrativeGroup getAdminGroupRecipient() {
		return this.adminGroupRecipient;
	}

	@Override
	public void setAdminGroupRecipient(AdministrativeGroup adminGroup) {

		assertManager();

		this.adminGroupRecipient = (JpaAdministrativeGroupImpl) adminGroup;
	}

	@Override
	public List<String> getRecipients(Submission submission) {
		return EmailByRecipientType.getRecipients(submission, recipientType, this);
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
