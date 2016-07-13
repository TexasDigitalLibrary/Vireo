package org.tdl.vireo.model.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.EmailTemplate;
import org.tdl.vireo.model.repo.EmailTemplateRepo;
import org.tdl.vireo.model.repo.custom.EmailTemplateRepoCustom;
import org.tdl.vireo.service.OrderedEntityService;

public class EmailTemplateRepoImpl implements EmailTemplateRepoCustom {

    @Autowired
    EmailTemplateRepo emailTemplateRepo;
    
    @Autowired
    private OrderedEntityService orderedEntityService;

    @Override
    public EmailTemplate create(String name, String subject, String message) {
        EmailTemplate emailTemplate = new EmailTemplate(name, subject, message);
        emailTemplate.setPosition(emailTemplateRepo.count()+1);
        return emailTemplateRepo.save(emailTemplate);
    }
    
    @Override
    public EmailTemplate findByNameOverride(String name) {
        EmailTemplate emailTemplate = emailTemplateRepo.findByNameAndIsSystemRequired(name, false);        
        if(emailTemplate == null) {
            emailTemplate = emailTemplateRepo.findByNameAndIsSystemRequired(name, true);
        }        
        return emailTemplate;
    }
    
    @Override
    public void reorder(Long src, Long dest) {
        orderedEntityService.reorder(EmailTemplate.class, src, dest);
    }
    
    @Override
    public void sort(String column) {
        orderedEntityService.sort(EmailTemplate.class, column);
    }

    @Override
    public void remove(EmailTemplate emailTemplate) {
        orderedEntityService.remove(emailTemplateRepo, EmailTemplate.class, emailTemplate.getPosition());
    }
    
}
