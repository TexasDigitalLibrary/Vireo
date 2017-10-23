package org.tdl.vireo.model.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.AbstractEmailRecipient;
import org.tdl.vireo.model.EmailRecipient;
import org.tdl.vireo.model.EmailRecipientAssignee;
import org.tdl.vireo.model.EmailRecipientContact;
import org.tdl.vireo.model.EmailRecipientOrganization;
import org.tdl.vireo.model.EmailRecipientSubmitter;
import org.tdl.vireo.model.FieldPredicate;
import org.tdl.vireo.model.Organization;
import org.tdl.vireo.model.repo.AbstractEmailRecipientRepo;
import org.tdl.vireo.model.repo.custom.AbstractEmailRecipientRepoCustom;

import edu.tamu.weaver.data.model.repo.impl.AbstractWeaverRepoImpl;

public class AbstractEmailRecipientRepoImpl extends AbstractWeaverRepoImpl<AbstractEmailRecipient, AbstractEmailRecipientRepo> implements AbstractEmailRecipientRepoCustom {

    @Autowired
    private AbstractEmailRecipientRepo abstractEmailRecipientRepo;

    @Override
    public EmailRecipient createOrganizationRecipient(Organization organization) {
        EmailRecipient organizationRecipient = findOneByName(organization.getName());
        return organizationRecipient != null ? organizationRecipient : abstractEmailRecipientRepo.save(new EmailRecipientOrganization(organization));
    }

    @Override
    public EmailRecipient createSubmitterRecipient() {
        EmailRecipient submitterRecipient = findOneByName("Submitter");
        return submitterRecipient != null ? submitterRecipient : abstractEmailRecipientRepo.save(new EmailRecipientSubmitter());
    }

    @Override
    public EmailRecipient createAssigneeRecipient() {
        EmailRecipient assigneeRecipient = findOneByName("Assignee");
        return assigneeRecipient != null ? assigneeRecipient : abstractEmailRecipientRepo.save(new EmailRecipientAssignee());
    }

    @Override
    public EmailRecipient createContactRecipient(String label, FieldPredicate fieldPredicate) {
        EmailRecipient contactRecipient = findOneByName(label);
        return contactRecipient != null ? contactRecipient : abstractEmailRecipientRepo.save(new EmailRecipientContact(label, fieldPredicate));
    }

    private EmailRecipient findOneByName(String name) {
        // Because this repo is a repository of abstract members,
        // Named Method Queries fail. This solution is naive and less performant
        // then using the database to look this up, but it is expedient,
        // and functional. Another option would be to implement a custom
        // query using jpql
        
        // TODO: this should not be needed, named queries should work
        return (EmailRecipient) abstractEmailRecipientRepo.findAll().stream().filter(r -> r.getName().equals(name)).findFirst().orElse(null);
    }

    @Override
    protected String getChannel() {
        return "/channel/abstract-email-recipient";
    }

}
