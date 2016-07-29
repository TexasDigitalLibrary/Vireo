package org.tdl.vireo.controller;

import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;
import static edu.tamu.framework.enums.BusinessValidationType.CREATE;
import static edu.tamu.framework.enums.BusinessValidationType.DELETE;
import static edu.tamu.framework.enums.BusinessValidationType.EXISTS;
import static edu.tamu.framework.enums.BusinessValidationType.NONEXISTS;
import static edu.tamu.framework.enums.BusinessValidationType.UPDATE;
import static edu.tamu.framework.enums.MethodValidationType.REORDER;
import static edu.tamu.framework.enums.MethodValidationType.SORT;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.tdl.vireo.model.EmailTemplate;
import org.tdl.vireo.model.repo.EmailTemplateRepo;

import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.aspect.annotation.ApiValidatedModel;
import edu.tamu.framework.aspect.annotation.ApiValidation;
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
    
    @ApiMapping("/all")
    @Auth(role = "MANAGER")
    public ApiResponse allEmailTemplates() {       
        return new ApiResponse(SUCCESS, emailTemplateRepo.findAllByOrderByPositionAsc());
    }

    @ApiMapping("/create")
    @Auth(role = "MANAGER")
    @ApiValidation(business = { @ApiValidation.Business(value = CREATE), @ApiValidation.Business(value = EXISTS) })
    public ApiResponse createEmailTemplate(@ApiValidatedModel EmailTemplate emailTemplate) {
        logger.info("Creating email template with name " + emailTemplate.getName());
        emailTemplate = emailTemplateRepo.create(emailTemplate.getName(), emailTemplate.getSubject(), emailTemplate.getMessage());
        simpMessagingTemplate.convertAndSend("/channel/settings/email-template", new ApiResponse(SUCCESS, emailTemplateRepo.findAllByOrderByPositionAsc()));
        return new ApiResponse(SUCCESS, emailTemplate);
    }
    
    @ApiMapping("/update")
    @Auth(role = "MANAGER")
    @ApiValidation(business = { @ApiValidation.Business(value = UPDATE), @ApiValidation.Business(value = NONEXISTS) })
    public ApiResponse updateEmailTemplate(@ApiValidatedModel EmailTemplate emailTemplate) {
        logger.info("Updating email template with name " + emailTemplate.getName());
        emailTemplate = emailTemplateRepo.save(emailTemplate);
        simpMessagingTemplate.convertAndSend("/channel/settings/email-template", new ApiResponse(SUCCESS, emailTemplateRepo.findAllByOrderByPositionAsc()));
        return new ApiResponse(SUCCESS, emailTemplate);
    }

    @ApiMapping("/remove")
    @Auth(role = "MANAGER")
    @ApiValidation(business = { @ApiValidation.Business(value = DELETE), @ApiValidation.Business(value = NONEXISTS) })
    public ApiResponse removeEmailTemplate(@ApiValidatedModel EmailTemplate emailTemplate) {
        logger.info("Removing email template with name " + emailTemplate.getName());
        emailTemplateRepo.remove(emailTemplate);
        simpMessagingTemplate.convertAndSend("/channel/settings/email-template", new ApiResponse(SUCCESS, emailTemplateRepo.findAllByOrderByPositionAsc()));
        return new ApiResponse(SUCCESS);
    }
    
    @ApiMapping("/reorder/{src}/{dest}")
    @Auth(role = "MANAGER")
    @ApiValidation(method = { @ApiValidation.Method(value = REORDER, model = EmailTemplate.class, params = { "0", "1" }) })
    public ApiResponse reorderEmailTemplates(@ApiVariable Long src, @ApiVariable Long dest) {
        logger.info("Reordering document types");
        emailTemplateRepo.reorder(src, dest);
        simpMessagingTemplate.convertAndSend("/channel/settings/email-template", new ApiResponse(SUCCESS, emailTemplateRepo.findAllByOrderByPositionAsc()));
        return new ApiResponse(SUCCESS);
    }
    
    @ApiMapping("/sort/{column}")
    @Auth(role = "MANAGER")
    @ApiValidation(method = { @ApiValidation.Method(value = SORT, model = EmailTemplate.class, params = { "0" }) })
    public ApiResponse sortEmailTemplates(@ApiVariable String column) {
        logger.info("Sorting email templates by " + column);
        emailTemplateRepo.sort(column);
        simpMessagingTemplate.convertAndSend("/channel/settings/email-template", new ApiResponse(SUCCESS, emailTemplateRepo.findAllByOrderByPositionAsc()));
        return new ApiResponse(SUCCESS);
    }
    
}
