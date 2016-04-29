package org.tdl.vireo.controller;

import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;
import static edu.tamu.framework.enums.ApiResponseType.VALIDATION_ERROR;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.ObjectError;
import org.tdl.vireo.model.EmailTemplate;
import org.tdl.vireo.model.repo.EmailTemplateRepo;

import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.aspect.annotation.ApiValidatedModel;
import edu.tamu.framework.aspect.annotation.ApiVariable;
import edu.tamu.framework.aspect.annotation.Auth;
import edu.tamu.framework.model.ApiResponse;

@Controller
@ApiMapping("/settings/email-template")
public class EmailTemplateController {

    private Logger logger = LoggerFactory.getLogger(this.getClass()); 

    @Autowired
    private EmailTemplateRepo emailTemplateRepo;
    
    @Autowired 
    private SimpMessagingTemplate simpMessagingTemplate;
    
    private Map<String, List<EmailTemplate>> getAll() {
        Map<String, List<EmailTemplate>> map = new HashMap<String, List<EmailTemplate>>();
        map.put("list", emailTemplateRepo.findAllByOrderByPositionAsc());
        return map;
    }
    
    @ApiMapping("/all")
    @Auth(role = "MANAGER")
    public ApiResponse allEmailTemplates() {       
        return new ApiResponse(SUCCESS, getAll());
    }

    @ApiMapping("/create")
    @Auth(role = "MANAGER")
    public ApiResponse createEmailTemplate(@ApiValidatedModel EmailTemplate emailTemplate) {
        // TODO: this needs to go in repo.validateCreate() -- VIR-201
        if(!emailTemplate.getBindingResult().hasErrors() && emailTemplateRepo.findByNameAndIsSystemRequired(emailTemplate.getName(), emailTemplate.isSystemRequired()) != null){
            emailTemplate.getBindingResult().addError(new ObjectError("emailTemplate", emailTemplate.getName() + " is already an email template!"));
        }
        
        if(emailTemplate.getBindingResult().hasErrors()) {
            return new ApiResponse(VALIDATION_ERROR, emailTemplate.getBindingResult().getAll());
        }

        EmailTemplate newEmailTemplate = emailTemplateRepo.create(emailTemplate.getName(), emailTemplate.getSubject(), emailTemplate.getMessage());

        logger.info("Created email template with name " + newEmailTemplate.getName());
        
        simpMessagingTemplate.convertAndSend("/channel/settings/email-template", new ApiResponse(SUCCESS, getAll()));
        
        return new ApiResponse(SUCCESS);
    }
    
    @ApiMapping("/update")
    @Auth(role = "MANAGER")
    public ApiResponse updateEmailTemplate(@ApiValidatedModel EmailTemplate emailTemplate) {
        // TODO: this needs to go in repo.validateUpdate() -- VIR-201
        EmailTemplate emailTemplateToUpdate = null;
        if(emailTemplate.getId() == null) {
            emailTemplate.getBindingResult().addError(new ObjectError("emailTemplate", "Cannot update a EmailTemplate without an id!"));
        } else {
            emailTemplateToUpdate = emailTemplateRepo.findOne(emailTemplate.getId());
            if(emailTemplateToUpdate == null) {
                emailTemplate.getBindingResult().addError(new ObjectError("emailTemplate", "Cannot update a EmailTemplate with an invalid id!"));
            } else if (emailTemplate.isSystemRequired()) {
                emailTemplate.getBindingResult().addError(new ObjectError("emailTemplate", "Cannot update a system required EmailTemplate!"));
            }
        }
        
        if(emailTemplate.getBindingResult().hasErrors()) {
            return new ApiResponse(VALIDATION_ERROR, emailTemplate.getBindingResult().getAll());
        }
         
        emailTemplateToUpdate.setName(emailTemplate.getName());
        emailTemplateToUpdate.setSubject(emailTemplate.getSubject());
        emailTemplateToUpdate.setMessage(emailTemplate.getMessage());
        emailTemplateToUpdate = emailTemplateRepo.save(emailTemplateToUpdate);
        
        //TODO: logging
        
        logger.info("Created deposit location with name " + emailTemplateToUpdate.getName());
        
        simpMessagingTemplate.convertAndSend("/channel/settings/email-template", new ApiResponse(SUCCESS, getAll()));
        
        return new ApiResponse(SUCCESS);
    }

    @ApiMapping("/remove/{indexString}")
    @Auth(role = "MANAGER")
    @Transactional
    public ApiResponse removeEmailTemplate(@ApiVariable String indexString) {        
        Long index = -1L;
        
        try {
            index = Long.parseLong(indexString);
        }
        catch(NumberFormatException nfe) {
            logger.info("\n\nNOT A NUMBER " + indexString + "\n\n");
            return new ApiResponse(VALIDATION_ERROR, "Id is not a valid email template order!");
        }
        
        if(index >= 0) {
            EmailTemplate emailTemplateToRemove = emailTemplateRepo.findByPosition(index);
            if(emailTemplateToRemove != null && !emailTemplateToRemove.isSystemRequired()) {
                emailTemplateRepo.remove(emailTemplateToRemove.getPosition());
            } else if(emailTemplateToRemove == null) {
                return new ApiResponse(VALIDATION_ERROR, "Cannot remove email template that doesn't exist!");
            } else {
                return new ApiResponse(VALIDATION_ERROR, "Cannot remove system required email template!");
            }
        }
        else {
            logger.info("\n\nINDEX" + index + "\n\n");
            return new ApiResponse(VALIDATION_ERROR, "Id is not a valid deposit location order!");
        }
        
        logger.info("Deleted email template with order " + index);
        simpMessagingTemplate.convertAndSend("/channel/settings/email-template", new ApiResponse(SUCCESS, getAll()));
        
        return new ApiResponse(SUCCESS);
    }
    
    @ApiMapping("/reorder/{src}/{dest}")
    @Auth(role = "MANAGER")
    @Transactional
    public ApiResponse reorderEmailTemplates(@ApiVariable String src, @ApiVariable String dest) {
        Long intSrc = Long.parseLong(src);
        Long intDest = Long.parseLong(dest);
        emailTemplateRepo.reorder(intSrc, intDest);
        simpMessagingTemplate.convertAndSend("/channel/settings/email-template", new ApiResponse(SUCCESS, getAll()));        
        return new ApiResponse(SUCCESS);
    }
    
    @ApiMapping("/sort/{column}")
    @Auth(role = "MANAGER")
    @Transactional
    public ApiResponse sortEmailTemplates(@ApiVariable String column) {
        emailTemplateRepo.sort(column);
        simpMessagingTemplate.convertAndSend("/channel/settings/email-template", new ApiResponse(SUCCESS, getAll()));        
        return new ApiResponse(SUCCESS);
    }

}
