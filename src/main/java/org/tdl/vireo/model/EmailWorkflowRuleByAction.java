package org.tdl.vireo.model;

import static javax.persistence.FetchType.EAGER;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@Entity
public class EmailWorkflowRuleByAction extends EmailWorkflowRule {

    @ManyToOne(fetch = EAGER, optional = false)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, scope = SubmissionStatus.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    private ActionLog actionLog;

    public EmailWorkflowRuleByAction() {
        super();
    }

    public EmailWorkflowRuleByAction(ActionLog actionLog, EmailRecipient emailRecipient, EmailTemplate emailTemplate) {
        this();
        setActionLog(actionLog);
        setEmailRecipient(emailRecipient);
        setEmailTemplate(emailTemplate);
    }

    /**
     * @return the actionLog
     */
    public ActionLog getActionLog() {
        return actionLog;
    }

    /**
     * @param actionLog
     *            the actionLog to set
     */
    public void setActionLog(ActionLog actionLog) {
        this.actionLog = actionLog;
    }

}
