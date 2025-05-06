package org.tdl.vireo.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Entity
public class EmailWorkflowRuleByAction extends EmailWorkflowRule {

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Action action;

    public EmailWorkflowRuleByAction() {
        super();
    }

    public EmailWorkflowRuleByAction(Action action, EmailRecipient emailRecipient, EmailTemplate emailTemplate) {
        this();
        setAction(action);
        setEmailRecipient(emailRecipient);
        setEmailTemplate(emailTemplate);
    }

    public EmailWorkflowRuleByAction(Action action, EmailRecipient emailRecipient, EmailTemplate emailTemplate, boolean isSystem) {
        this(action, emailRecipient, emailTemplate);
        isSystem(isSystem);
    }

    /**
     * @return the action
     */
    public Action getAction() {
        return action;
    }

    /**
     * @param action
     *            the action to set
     */
    public void setAction(Action action) {
        this.action = action;
    }

}
