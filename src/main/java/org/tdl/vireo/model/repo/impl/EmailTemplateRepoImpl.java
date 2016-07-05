package org.tdl.vireo.model.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.ObjectError;
import org.tdl.vireo.model.EmailTemplate;
import org.tdl.vireo.model.repo.EmailTemplateRepo;
import org.tdl.vireo.model.repo.custom.EmailTemplateRepoCustom;
import org.tdl.vireo.service.OrderedEntityService;

import edu.tamu.framework.validation.ModelBindingResult;

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
    
    @Override
    public EmailTemplate validateCreate(EmailTemplate emailTemplate) {
        EmailTemplate existing = emailTemplateRepo.findByNameAndIsSystemRequired(emailTemplate.getName(), emailTemplate.isSystemRequired());
        if(!emailTemplate.getBindingResult().hasErrors() && existing != null){
            emailTemplate.getBindingResult().addError(new ObjectError("emailTemplate", emailTemplate.getName() + " is already an email template!"));
        }
        
        return emailTemplate;
    }
    
    @Override
    public EmailTemplate validateUpdate(EmailTemplate emailTemplate) {
        if (emailTemplate.getId() != null) {
            EmailTemplate emailTemplateToUpdate = emailTemplateRepo.findOne(emailTemplate.getId());
            if (emailTemplateToUpdate != null) {
                // make sure we're not editing a system required one
                if (emailTemplateToUpdate.isSystemRequired()) {
                    EmailTemplate customEmailTemplate = emailTemplateRepo.findByNameAndIsSystemRequired(emailTemplateToUpdate.getName(), false);
                    // if we're editing a system required one and a custom one with the same name doesn't exist, create it with the incoming parameters
                    if (customEmailTemplate == null && !emailTemplate.getBindingResult().hasErrors()) {
                        // if we didn't have any pre-existing errors with the incoming emailTemplate (missing properties)
                        // make a copy of the system emailTemplate since we're trying to change it
                        // make sure we copy the binding result to the new emailTemplate... for the controller to use if it needs it
                        ModelBindingResult bindingResult = emailTemplate.getBindingResult();
                        emailTemplate = emailTemplateRepo.create(emailTemplate.getName(), emailTemplate.getSubject(), emailTemplate.getMessage());
                        bindingResult.addWarning(new ObjectError("emailTemplate", "System Email Template cannot be edited, a custom user-copy has been made!"));
                        emailTemplate.setBindingResult(bindingResult);
                    } else {
                        emailTemplate.getBindingResult().addError(new ObjectError("emailTemplate", "System Email Template cannot be edited and a custom one with this name already exists!"));
                    }
                }
                // we're allowed to edit!
                else {
                    emailTemplateToUpdate.setBindingResult(emailTemplate.getBindingResult());
                    emailTemplateToUpdate.setName(emailTemplate.getName());
                    emailTemplateToUpdate.setSubject(emailTemplate.getSubject());
                    emailTemplateToUpdate.setMessage(emailTemplate.getMessage());
                    emailTemplate = emailTemplateToUpdate;
                }
            } else {
                emailTemplate.getBindingResult().addError(new ObjectError("emailTemplate", "Cannot edit Email Template that doesn't exist!"));
            }
        } else {
            emailTemplate.getBindingResult().addError(new ObjectError("emailTemplate", "Cannot edit Email Template, no id was passed in!"));
        }
        return emailTemplate;
    }
    
    @Override
    public EmailTemplate validateRemove(EmailTemplate emailTemplate) {            
        if (emailTemplate.isSystemRequired()) {
            emailTemplate.getBindingResult().addError(new ObjectError("emailTemplate", "Cannot remove a System email template!"));
        }        
        return emailTemplate;
    }
    
}
