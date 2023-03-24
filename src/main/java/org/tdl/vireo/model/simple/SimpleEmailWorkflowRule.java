package org.tdl.vireo.model.simple;

import static javax.persistence.CascadeType.DETACH;
import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.REFRESH;
import static javax.persistence.FetchType.EAGER;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.hibernate.annotations.Immutable;
import org.tdl.vireo.model.AbstractEmailRecipient;
import org.tdl.vireo.model.EmailRecipient;
import org.tdl.vireo.model.SubmissionStatus;

@Entity
@Immutable
@Table(name = "email_workflow_rule")
public class SimpleEmailWorkflowRule implements Serializable {

    @Transient
    private static final long serialVersionUID = 4224051361965594236L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", insertable = false, updatable = false, nullable = false)
    private Long id;

    @Column
    private Boolean isSystem;

    @Column
    private Boolean isDisabled;

    @Immutable
    @ManyToOne(targetEntity = AbstractEmailRecipient.class)
    private EmailRecipient emailRecipient;

    @Immutable
    @ManyToOne(fetch = EAGER, optional = false)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, scope = SubmissionStatus.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    private SimpleSubmissionStatus submissionStatus;

    @Immutable
    @ManyToOne(cascade = { DETACH, REFRESH, MERGE }, fetch = EAGER, optional = false)
    @JoinColumn(name = "emailTemplateId")
    private SimpleEmailTemplate emailTemplate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getIsSystem() {
        return isSystem;
    }

    public void setIsSystem(Boolean isSystem) {
        this.isSystem = isSystem;
    }

    public Boolean getIsDisabled() {
        return isDisabled;
    }

    public void setIsDisabled(Boolean isDisabled) {
        this.isDisabled = isDisabled;
    }

    public EmailRecipient getEmailRecipient() {
        return emailRecipient;
    }

    public void setEmailRecipient(EmailRecipient emailRecipient) {
        this.emailRecipient = emailRecipient;
    }

    public SimpleSubmissionStatus getSubmissionStatus() {
        return submissionStatus;
    }

    public void setSubmissionStatus(SimpleSubmissionStatus submissionStatus) {
        this.submissionStatus = submissionStatus;
    }

    public SimpleEmailTemplate getEmailTemplate() {
        return emailTemplate;
    }

    public void setEmailTemplate(SimpleEmailTemplate emailTemplate) {
        this.emailTemplate = emailTemplate;
    }

}
