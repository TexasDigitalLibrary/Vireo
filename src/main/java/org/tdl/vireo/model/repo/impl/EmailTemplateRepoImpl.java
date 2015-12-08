package org.tdl.vireo.model.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.EmailTemplate;
import org.tdl.vireo.model.repo.EmailTemplateRepo;
import org.tdl.vireo.model.repo.custom.EmailTemplateRepoCustom;

public class EmailTemplateRepoImpl implements EmailTemplateRepoCustom {

    @Autowired
    EmailTemplateRepo emailTemplateRepo;

    @Override
    public EmailTemplate create(String name, String subject, String message) {
        return emailTemplateRepo.save(new EmailTemplate(name, subject, message));
    }
    
    @Override
    public EmailTemplate findByNameOverride(String name) {
        EmailTemplate emailTemplate = emailTemplateRepo.findByNameAndIsSystemRequired(name, false);        
        if(emailTemplate == null) {
            emailTemplate = emailTemplateRepo.findByNameAndIsSystemRequired(name, true);
        }        
        return emailTemplate;
    }

}
