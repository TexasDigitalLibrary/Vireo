package org.tdl.vireo.model;

import static javax.persistence.CascadeType.DETACH;
import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.REFRESH;
import static javax.persistence.FetchType.EAGER;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
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
	
	@ManyToMany(cascade = { DETACH, REFRESH, MERGE }, fetch = EAGER)
    private Set<Organization> organizations;

	@ManyToOne
	@JoinColumn(name = "emailTemplateId")
	private EmailTemplate emailTemplate;
	
	public EmailWorkflowRule() {
		isSystem(false);
		isDisabled(true);
	}
	
	public EmailWorkflowRule(SubmissionState submissionState, Set<Organization> organizations, RecipientType recipientType, EmailTemplate emailTemplate) {
		this();
		setSubmissionState(submissionState);
		setOrganizations(organizations);
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
	 * @return the organizations
	 */
	public Set<Organization> getOrganizations() {
		return organizations;
	}

	/**
	 * @param organizations the organizations to set
	 */
	public void setOrganizations(Set<Organization> organizations) {
		this.organizations = organizations;
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
	
	
	//TODO - delete the section below

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
	/*protected JpaEmailWorkflowRuleImpl(State associatedState) {

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
*/
}
