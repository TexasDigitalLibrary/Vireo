package org.tdl.vireo.model;

import static javax.persistence.CascadeType.DETACH;
import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.REFRESH;
import static javax.persistence.FetchType.EAGER;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.tdl.vireo.model.validation.EmailWorkflowRuleValidator;

import edu.tamu.weaver.validation.model.ValidatingBaseEntity;

@MappedSuperclass
public abstract class EmailWorkflowRule extends ValidatingBaseEntity {

    @Column
    @JsonProperty("isSystem")
    private Boolean isSystem;

    @Column
    @JsonProperty("isDisabled")
    private Boolean isDisabled;

    @ManyToOne(targetEntity = AbstractEmailRecipient.class)
    private EmailRecipient emailRecipient;

    @ManyToOne(cascade = { DETACH, REFRESH, MERGE }, fetch = EAGER, optional = false)
    @JoinColumn(name = "emailTemplateId")
    private EmailTemplate emailTemplate;

    public EmailWorkflowRule() {
        setModelValidator(new EmailWorkflowRuleValidator());
        isSystem(false);
        isDisabled(true);
    }

    /**
     * @return the isSystem
     */
    public Boolean isSystem() {
        return isSystem;
    }

    /**
     * @param isSystem
     *                 the isSystem to set
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
     *                   the isDisabled to set
     */
    public void isDisabled(Boolean isDisabled) {
        this.isDisabled = isDisabled;
    }

    /**
     * @return the emailRecipient
     */
    public EmailRecipient getEmailRecipient() {
        return emailRecipient;
    }

    /**
     * @param emailRecipient
     *                       the emailRecipient to set
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
     *                      the emailTemplate to set
     */
    public void setEmailTemplate(EmailTemplate emailTemplate) {
        this.emailTemplate = emailTemplate;
    }

}
