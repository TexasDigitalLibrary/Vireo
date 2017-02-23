package org.tdl.vireo.model.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.EmailRecipient;
import org.tdl.vireo.model.EmailRecipientAssignee;
import org.tdl.vireo.model.EmailRecipientContact;
import org.tdl.vireo.model.EmailRecipientOrganization;
import org.tdl.vireo.model.EmailRecipientSubmitter;
import org.tdl.vireo.model.FieldPredicate;
import org.tdl.vireo.model.Organization;
import org.tdl.vireo.model.repo.AbstractEmailRecipientRepo;
import org.tdl.vireo.model.repo.custom.AbstractEmailRecipientRepoCustom;

public class AbstractEmailRecipientRepoImpl implements AbstractEmailRecipientRepoCustom {

    @Autowired
    AbstractEmailRecipientRepo abstractEmailRecipientRepo;

    @Override
    public EmailRecipient createOrganizationRecipient(Organization organization) {
        return abstractEmailRecipientRepo.save(new EmailRecipientOrganization(organization));
    }

    @Override
    public EmailRecipient createSubmitterRecipient() {
        return abstractEmailRecipientRepo.save(new EmailRecipientSubmitter());
    }

    @Override
    public EmailRecipient createAssigneeRecipient() {
        return abstractEmailRecipientRepo.save(new EmailRecipientAssignee());
    }

    @Override
    public EmailRecipient createContactRecipient(String label, FieldPredicate fieldPredicate) {
        return abstractEmailRecipientRepo.save(new EmailRecipientContact(label, fieldPredicate));
    }

}
