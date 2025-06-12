package org.tdl.vireo.model;

import static javax.persistence.FetchType.EAGER;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@Entity
@Table(name = "email_workflow_rule")
public class EmailWorkflowRuleByStatus extends EmailWorkflowRule {

    @ManyToOne(fetch = EAGER, optional = false)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, scope = SubmissionStatus.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    private SubmissionStatus submissionStatus;

    public EmailWorkflowRuleByStatus() {
        super();
    }

    public EmailWorkflowRuleByStatus(SubmissionStatus submissionStatus, EmailRecipient emailRecipient, EmailTemplate emailTemplate) {
        this();
        setSubmissionStatus(submissionStatus);
        setEmailRecipient(emailRecipient);
        setEmailTemplate(emailTemplate);
    }

    public EmailWorkflowRuleByStatus(SubmissionStatus submissionStatus, EmailRecipient emailRecipient, EmailTemplate emailTemplate, Boolean isSystem) {
        this(submissionStatus, emailRecipient, emailTemplate);
        isSystem(isSystem);
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

}
