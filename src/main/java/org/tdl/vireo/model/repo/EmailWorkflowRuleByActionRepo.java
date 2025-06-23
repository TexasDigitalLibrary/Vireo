package org.tdl.vireo.model.repo;

import java.util.List;

import org.tdl.vireo.model.Action;
import org.tdl.vireo.model.EmailRecipient;
import org.tdl.vireo.model.EmailTemplate;
import org.tdl.vireo.model.EmailWorkflowRuleByAction;
import org.tdl.vireo.model.repo.custom.EmailWorkflowRuleRepoCustom;

import edu.tamu.weaver.data.model.repo.WeaverRepo;

public interface EmailWorkflowRuleByActionRepo extends WeaverRepo<EmailWorkflowRuleByAction>, EmailWorkflowRuleRepoCustom<EmailWorkflowRuleByAction, Action> {

    public List<EmailWorkflowRuleByAction> findByEmailRecipientAndIsDisabled(EmailRecipient emailRecipient, Boolean isDisabled);

    public EmailWorkflowRuleByAction findByActionAndEmailRecipientAndEmailTemplate(Action action, EmailRecipient emailRecipient, EmailTemplate emailTemplate);

}
