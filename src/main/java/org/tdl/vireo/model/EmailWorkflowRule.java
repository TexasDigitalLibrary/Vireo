package org.tdl.vireo.model;

import static javax.persistence.CascadeType.DETACH;
import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.REFRESH;
import static javax.persistence.FetchType.EAGER;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import org.tdl.vireo.model.validation.EmailWorkflowRuleValidator;

import com.fasterxml.jackson.annotation.JsonProperty;

import edu.tamu.framework.model.BaseEntity;

@Entity
public class EmailWorkflowRule extends BaseEntity {

	@Column
	@JsonProperty("isSystem")
	private Boolean isSystem;

	@Column
	@JsonProperty("isDisabled")
	private Boolean isDisabled;
	
	@OneToOne(targetEntity=AbstractEmailRecipient.class, orphanRemoval=true)
    private EmailRecipient emailRecipient;

	@ManyToOne(cascade = { DETACH, REFRESH, MERGE }, fetch = EAGER, optional = false)
	private SubmissionState submissionState;
	
	@ManyToOne(cascade = { DETACH, REFRESH, MERGE }, fetch = EAGER, optional = false)
	@JoinColumn(name = "emailTemplateId")
	private EmailTemplate emailTemplate;
	
	public EmailWorkflowRule() {
	    setModelValidator(new EmailWorkflowRuleValidator());
		isSystem(false);
		isDisabled(true);
	}
	
	public EmailWorkflowRule(SubmissionState submissionState, EmailRecipient emailRecipient, EmailTemplate emailTemplate) {
		this();
		setSubmissionState(submissionState);
		setEmailRecipient(emailRecipient);
		setEmailTemplate(emailTemplate);
	}
	
	public EmailWorkflowRule(SubmissionState submissionState, EmailRecipient emailRecipient, EmailTemplate emailTemplate, Boolean isSystem) {
        this(submissionState, emailRecipient, emailTemplate);
        isSystem(isSystem);
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
	 * @return the emailRecipient
	 */
	public EmailRecipient getEmailRecipient() {
		return emailRecipient;
	}

	/**
	 * @param emailRecipient the emailRecipient to set
	 */
	public void setEmailRecipient(EmailRecipient emailRecipient) {
		this.emailRecipient = emailRecipient;
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

	
}
