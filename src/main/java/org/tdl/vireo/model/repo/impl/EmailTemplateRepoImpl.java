package org.tdl.vireo.model.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.DepositLocation;
import org.tdl.vireo.model.EmailTemplate;
import org.tdl.vireo.model.GraduationMonth;
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
        return emailTemplateRepo.save(new EmailTemplate(name, subject, message, (int)emailTemplateRepo.count()+1));
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
    public void reorder(Integer src, Integer dest) {
        orderedEntityService.reorder(EmailTemplate.class, src, dest);
    }

    @Override
    public void remove(Integer index) {
        orderedEntityService.remove(EmailTemplate.class, index);
    }
    
}
