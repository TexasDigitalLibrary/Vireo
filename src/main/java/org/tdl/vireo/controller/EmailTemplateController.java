package org.tdl.vireo.controller;

import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;
import static edu.tamu.framework.enums.ApiResponseType.VALIDATION_WARNING;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.tdl.vireo.model.EmailTemplate;
import org.tdl.vireo.model.repo.EmailTemplateRepo;
import org.tdl.vireo.service.ValidationService;

import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.aspect.annotation.ApiValidatedModel;
import edu.tamu.framework.aspect.annotation.ApiVariable;
import edu.tamu.framework.aspect.annotation.Auth;
import edu.tamu.framework.model.ApiResponse;
import edu.tamu.framework.validation.ModelBindingResult;

@Controller
@ApiMapping("/settings/email-template")
public class EmailTemplateController {

    private Logger logger = LoggerFactory.getLogger(this.getClass()); 

    @Autowired
    private EmailTemplateRepo emailTemplateRepo;
    
    @Autowired 
    private SimpMessagingTemplate simpMessagingTemplate;
    
    @Autowired
    private ValidationService validationService;
    
    @ApiMapping("/all")
    @Auth(role = "MANAGER")
    public ApiResponse allEmailTemplates() {       
        return new ApiResponse(SUCCESS, getAll());
    }

    @ApiMapping("/create")
    @Auth(role = "MANAGER")
    public ApiResponse createEmailTemplate(@ApiValidatedModel EmailTemplate emailTemplate) {
        
        // will attach any errors to the BindingResult when validating the incoming emailTemplate
        emailTemplate = emailTemplateRepo.validateCreate(emailTemplate);
        
        // build a response based on the BindingResult state
        ApiResponse response = validationService.buildResponse(emailTemplate);
        
        switch(response.getMeta().getType()){
            case SUCCESS:
            case VALIDATION_INFO:
                logger.info("Creating email template with name " + emailTemplate.getName());
                emailTemplateRepo.create(emailTemplate.getName(), emailTemplate.getSubject(), emailTemplate.getMessage());
                simpMessagingTemplate.convertAndSend("/channel/settings/email-template", new ApiResponse(SUCCESS, getAll()));
                break;
            case VALIDATION_WARNING:
                simpMessagingTemplate.convertAndSend("/channel/settings/email-template", new ApiResponse(VALIDATION_WARNING, getAll()));
                break;
            default:
                logger.warn("Couldn't create email template with name " + emailTemplate.getName() + " because: " + response.getMeta().getType());
                break;
        }

        return response;
    }
    
    @ApiMapping("/update")
    @Auth(role = "MANAGER")
    public ApiResponse updateEmailTemplate(@ApiValidatedModel EmailTemplate emailTemplate) {
        
        // will attach any errors to the BindingResult when validating the incoming emailTemplate
        emailTemplate = emailTemplateRepo.validateUpdate(emailTemplate);
        
        // build a response based on the BindingResult state
        ApiResponse response = validationService.buildResponse(emailTemplate);
        
        switch(response.getMeta().getType()){
            case SUCCESS:
            case VALIDATION_INFO:
                logger.info("Updating email template with name " + emailTemplate.getName());
                emailTemplateRepo.save(emailTemplate);
                simpMessagingTemplate.convertAndSend("/channel/settings/email-template", new ApiResponse(SUCCESS, getAll()));
                break;
            case VALIDATION_WARNING:
                simpMessagingTemplate.convertAndSend("/channel/settings/email-template", new ApiResponse(VALIDATION_WARNING, getAll()));
                break;
            default:
                logger.warn("Couldn't update email template with name " + emailTemplate.getName() + " because: " + response.getMeta().getType());
                break;
        }

        return response;
    }

    @ApiMapping("/remove/{idString}")
    @Auth(role = "MANAGER")
    @Transactional
    public ApiResponse removeEmailTemplate(@ApiVariable String idString) {
        
        // create a ModelBindingResult since we have an @ApiVariable coming in (and not a @ApiValidatedModel)
        ModelBindingResult modelBindingResult = new ModelBindingResult(idString, "email_template_id");
        
        // will attach any errors to the BindingResult when validating the incoming idString
        EmailTemplate emailTemplate = emailTemplateRepo.validateRemove(idString, modelBindingResult);
        
        // build a response based on the BindingResult state
        ApiResponse response = validationService.buildResponse(modelBindingResult);
        
        switch(response.getMeta().getType()){
            case SUCCESS:
            case VALIDATION_INFO:
                logger.info("Removing email template with id " + idString);
                emailTemplateRepo.remove(emailTemplate);
                simpMessagingTemplate.convertAndSend("/channel/settings/email-template", new ApiResponse(SUCCESS, getAll()));
                break;
            case VALIDATION_WARNING:
                simpMessagingTemplate.convertAndSend("/channel/settings/email-template", new ApiResponse(VALIDATION_WARNING, getAll()));
                break;
            default:
                logger.warn("Couldn't remove email template with id " + idString + " because: " + response.getMeta().getType());
                break;
        }
        
        return response;
    }
    
    @ApiMapping("/reorder/{src}/{dest}")
    @Auth(role = "MANAGER")
    @Transactional
    public ApiResponse reorderEmailTemplates(@ApiVariable Long src, @ApiVariable Long dest) {
        
        // create a ModelBindingResult since we have an @ApiVariable coming in (and not a @ApiValidatedModel)
        ModelBindingResult modelBindingResult = new ModelBindingResult(src, "emailTemplate");
                
        // build a response based on the BindingResult state
        ApiResponse response = validationService.buildResponse(modelBindingResult);
        
        switch(response.getMeta().getType()){
            case SUCCESS:
            case VALIDATION_INFO:
                logger.info("Reordering document types");
                emailTemplateRepo.reorder(src, dest);
                simpMessagingTemplate.convertAndSend("/channel/settings/email-template", new ApiResponse(SUCCESS, getAll()));
                break;
            case VALIDATION_WARNING:
                simpMessagingTemplate.convertAndSend("/channel/settings/email-template", new ApiResponse(VALIDATION_WARNING, getAll()));
                break;
            default:
                logger.warn("Couldn't reorder document types because: " + response.getMeta().getType());
                break;
        }
        
        return response;
    }
    
    @ApiMapping("/sort/{column}")
    @Auth(role = "MANAGER")
    @Transactional
    public ApiResponse sortEmailTemplates(@ApiVariable String column) {
        
        // create a ModelBindingResult since we have an @ApiVariable coming in (and not a @ApiValidatedModel)
        ModelBindingResult modelBindingResult = new ModelBindingResult(column, "emailTemplate");
        
        // will attach any errors to the BindingResult when validating the incoming column
        validationService.validateColumn(EmailTemplate.class, column, modelBindingResult);
        
        // build a response based on the BindingResult state
        ApiResponse response = validationService.buildResponse(modelBindingResult);
        
        switch(response.getMeta().getType()){
            case SUCCESS:
            case VALIDATION_INFO:
                logger.info("Sorting email templates by " + column);
                emailTemplateRepo.sort(column);
                simpMessagingTemplate.convertAndSend("/channel/settings/email-template", new ApiResponse(SUCCESS, getAll()));
                break;
            case VALIDATION_WARNING:
                simpMessagingTemplate.convertAndSend("/channel/settings/email-template", new ApiResponse(VALIDATION_WARNING, getAll()));
                break;
            default:
                logger.warn("Couldn't sort email templates because: " + response.getMeta().getType());
                break;
        }
        
        return response;
    }
    
    private Map<String, List<EmailTemplate>> getAll() {
        Map<String, List<EmailTemplate>> map = new HashMap<String, List<EmailTemplate>>();
        map.put("list", emailTemplateRepo.findAllByOrderByPositionAsc());
        return map;
    }
}
