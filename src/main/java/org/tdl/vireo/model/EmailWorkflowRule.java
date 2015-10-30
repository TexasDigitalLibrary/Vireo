package org.tdl.vireo.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.tdl.vireo.enums.RecipientType;

@Entity
public class EmailWorkflowRule extends BaseEntity {

	@Column
	private Boolean isSystem;

	@Column
	private Boolean isDisabled;

	@ManyToOne
	private SubmissionState submissionState;
	
	//TODO - Several combinations here actually, of whether the organization, student, assignee, and/or advisor should or should not be recipients.
	@Column(nullable = true)
	@Enumerated
	private RecipientType recipientType;

	@ManyToOne
	@JoinColumn(name = "emailTemplateId")
	private EmailTemplate emailTemplate;
	
	public EmailWorkflowRule() {
		isSystem(false);
		isDisabled(true);
	}
	
	public EmailWorkflowRule(SubmissionState submissionState, RecipientType recipientType, EmailTemplate emailTemplate) {
		this();
		setSubmissionState(submissionState);
		setRecipientType(recipientType);
		setEmailTemplate(emailTemplate);
	}

	/**
	 * @return the isSystem
	 */
	public Boolean isSystem() {
		return isSystem;
	}

	/**
	 * @param isSystem the isSystem to set
	 */
	public void isSystem(Boolean isSystem) {
		this.isSystem = isSystem;
	}

	/**
	 * @return the isDisabled
	 */
	public Boolean isDisabled() {
		return isDisabled;
	}

	/**
	 * @param isDisabled the isDisabled to set
	 */
	public void isDisabled(Boolean isDisabled) {
		this.isDisabled = isDisabled;
	}

	/**
	 * @return the submissionState
	 */
	public SubmissionState getSubmissionState() {
		return submissionState;
	}

	/**
	 * @param submissionState the submissionState to set
	 */
	public void setSubmissionState(SubmissionState submissionState) {
		this.submissionState = submissionState;
	}

	/**
	 * @return the recipientType
	 */
	public RecipientType getRecipientType() {
		return recipientType;
	}

	/**
	 * @param recipientType the recipientType to set
	 */
	public void setRecipientType(RecipientType recipientType) {
		this.recipientType = recipientType;
	}

	/**
	 * @return the emailTemplate
	 */
	public EmailTemplate getEmailTemplate() {
		return emailTemplate;
	}

	/**
	 * @param emailTemplate the emailTemplate to set
	 */
	public void setEmailTemplate(EmailTemplate emailTemplate) {
		this.emailTemplate = emailTemplate;
	}

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
	/*
	protected JpaEmailWorkflowRuleImpl(State associatedState) {

		assertManager();

		if (associatedState == null) {
			throw new IllegalArgumentException("associatedState is required");
		}

		this.associatedState = associatedState.getBeanName();
	}

	//TODO - gets list of emails
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
	*/
	
}
