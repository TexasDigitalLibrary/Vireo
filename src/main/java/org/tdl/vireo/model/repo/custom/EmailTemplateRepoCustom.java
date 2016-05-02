package org.tdl.vireo.model.repo.custom;

import org.tdl.vireo.model.EmailTemplate;

import edu.tamu.framework.validation.ModelBindingResult;

public interface EmailTemplateRepoCustom {

    public EmailTemplate create(String name, String subject, String message);
    
    public EmailTemplate findByNameOverride(String name);

    public void reorder(Long src, Long dest);
    
    public void sort(String column);
    
    public void remove(EmailTemplate emailTemplate);
    
    public EmailTemplate validateCreate(EmailTemplate emailTemplate);
    
    public EmailTemplate validateUpdate(EmailTemplate emailTemplate);
    
    public EmailTemplate validateRemove(String idString, ModelBindingResult modelBindingResult);
}
