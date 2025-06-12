package org.tdl.vireo.model.repo.custom;

import org.tdl.vireo.model.EmailRecipient;
import org.tdl.vireo.model.EmailTemplate;

public interface EmailWorkflowRuleRepoCustom<EWR, SA> {

    public EWR create(SA statusOrAction, EmailRecipient emailRecipient, EmailTemplate emailTemplate);

    public EWR create(SA statusOrAction, EmailRecipient emailRecipient, EmailTemplate emailTemplate, Boolean isSystem);

}
