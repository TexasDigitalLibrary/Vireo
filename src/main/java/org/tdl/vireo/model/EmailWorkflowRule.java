package org.tdl.vireo.model;

import static javax.persistence.CascadeType.DETACH;
import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.REFRESH;
import static javax.persistence.FetchType.EAGER;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.tdl.vireo.model.validation.EmailWorkflowRuleValidator;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import edu.tamu.weaver.validation.model.ValidatingBaseEntity;

@Entity
public class EmailWorkflowRule extends ValidatingBaseEntity {

    @Column
    @JsonProperty("isSystem")
    private Boolean isSystem;

    @Column
    @JsonProperty("isDisabled")
    private Boolean isDisabled;

    @ManyToOne(targetEntity = AbstractEmailRecipient.class)
    private EmailRecipient emailRecipient;

    @ManyToOne(cascade = { DETACH, REFRESH, MERGE }, fetch = EAGER, optional = false)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, scope = SubmissionStatus.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    private SubmissionStatus submissionStatus;

    @ManyToOne(cascade = { DETACH, REFRESH, MERGE }, fetch = EAGER, optional = false)
    @JoinColumn(name = "emailTemplateId")
    private EmailTemplate emailTemplate;

    public EmailWorkflowRule() {
        setModelValidator(new EmailWorkflowRuleValidator());
        isSystem(false);
        isDisabled(true);
    }

    public EmailWorkflowRule(SubmissionStatus submissionStatus, EmailRecipient emailRecipient, EmailTemplate emailTemplate) {
        this();
        setSubmissionStatus(submissionStatus);
        setEmailRecipient(emailRecipient);
        setEmailTemplate(emailTemplate);
    }

    public EmailWorkflowRule(SubmissionStatus submissionStatus, EmailRecipient emailRecipient, EmailTemplate emailTemplate, Boolean isSystem) {
        this(submissionStatus, emailRecipient, emailTemplate);
        isSystem(isSystem);
    }

    /**
     * @return the isSystem
     */
    public Boolean isSystem() {
        return isSystem;
    }

    /**
     * @param isSystem
     *            the isSystem to set
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
     * @param isDisabled
     *            the isDisabled to set
     */
    public void isDisabled(Boolean isDisabled) {
        this.isDisabled = isDisabled;
    }

    /**
     * @return the submissionStatus
     */
    public SubmissionStatus getSubmissionStatus() {
        return submissionStatus;
    }

    /**
     * @param submissionStatus
     *            the submissionStatus to set
     */
    public void setSubmissionStatus(SubmissionStatus submissionStatus) {
        this.submissionStatus = submissionStatus;
    }

    /**
     * @return the emailRecipient
     */
    public EmailRecipient getEmailRecipient() {
        return emailRecipient;
    }

    /**
     * @param emailRecipient
     *            the emailRecipient to set
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
     * @param emailTemplate
     *            the emailTemplate to set
     */
    public void setEmailTemplate(EmailTemplate emailTemplate) {
        this.emailTemplate = emailTemplate;
    }

}
