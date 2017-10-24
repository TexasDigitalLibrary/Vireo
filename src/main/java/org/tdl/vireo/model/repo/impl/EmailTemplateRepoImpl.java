package org.tdl.vireo.model.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.EmailTemplate;
import org.tdl.vireo.model.repo.EmailTemplateRepo;
import org.tdl.vireo.model.repo.custom.EmailTemplateRepoCustom;

import edu.tamu.weaver.data.model.repo.impl.AbstractWeaverOrderedRepoImpl;

public class EmailTemplateRepoImpl extends AbstractWeaverOrderedRepoImpl<EmailTemplate, EmailTemplateRepo> implements EmailTemplateRepoCustom {

    @Autowired
    private EmailTemplateRepo emailTemplateRepo;

    @Override
    public EmailTemplate create(String name, String subject, String message) {
        EmailTemplate emailTemplate = new EmailTemplate(name, subject, message);
        emailTemplate.setPosition(emailTemplateRepo.count() + 1);
        return super.create(emailTemplate);
    }

    @Override
    public EmailTemplate findByNameOverride(String name) {
        EmailTemplate emailTemplate = emailTemplateRepo.findByNameAndSystemRequired(name, false);
        if (emailTemplate == null) {
            emailTemplate = emailTemplateRepo.findByNameAndSystemRequired(name, true);
        }
        return emailTemplate;
    }

    @Override
    public Class<?> getModelClass() {
        return EmailTemplate.class;
    }

    @Override
    protected String getChannel() {
        return "/channel/email-template";
    }

}
