package org.tdl.vireo.model.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.Action;
import org.tdl.vireo.model.EmailRecipient;
import org.tdl.vireo.model.EmailTemplate;
import org.tdl.vireo.model.EmailWorkflowRuleByAction;
import org.tdl.vireo.model.repo.EmailWorkflowRuleByActionRepo;
import org.tdl.vireo.model.repo.custom.EmailWorkflowRuleRepoCustom;

import edu.tamu.weaver.data.model.repo.impl.AbstractWeaverRepoImpl;

public class EmailWorkflowRuleByActionRepoImpl extends AbstractWeaverRepoImpl<EmailWorkflowRuleByAction, EmailWorkflowRuleByActionRepo> implements EmailWorkflowRuleRepoCustom<EmailWorkflowRuleByAction, Action> {

    @Autowired
    private EmailWorkflowRuleByActionRepo emailWorkflowRuleRepo;

    @Override
    public EmailWorkflowRuleByAction create(Action action, EmailRecipient emailRecipient, EmailTemplate emailTemplate) {
        return emailWorkflowRuleRepo.save(new EmailWorkflowRuleByAction(action, emailRecipient, emailTemplate));
    }

    @Override
    public EmailWorkflowRuleByAction create(Action action, EmailRecipient emailRecipient, EmailTemplate emailTemplate, Boolean isSystem) {
        return emailWorkflowRuleRepo.save(new EmailWorkflowRuleByAction(action, emailRecipient, emailTemplate, isSystem));
    }

    @Override
    protected String getChannel() {
        return "/channel/embargo-workflow-rule";
    }

}
