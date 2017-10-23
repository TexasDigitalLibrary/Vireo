package org.tdl.vireo.controller;

import static edu.tamu.weaver.response.ApiStatus.SUCCESS;
import static edu.tamu.weaver.validation.model.BusinessValidationType.CREATE;
import static edu.tamu.weaver.validation.model.BusinessValidationType.DELETE;
import static edu.tamu.weaver.validation.model.BusinessValidationType.UPDATE;
import static edu.tamu.weaver.validation.model.MethodValidationType.REORDER;
import static edu.tamu.weaver.validation.model.MethodValidationType.SORT;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tdl.vireo.model.EmailTemplate;
import org.tdl.vireo.model.repo.EmailTemplateRepo;

import edu.tamu.weaver.response.ApiResponse;
import edu.tamu.weaver.validation.aspect.annotation.WeaverValidatedModel;
import edu.tamu.weaver.validation.aspect.annotation.WeaverValidation;

@RestController
@RequestMapping("/settings/email-template")
public class EmailTemplateController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private EmailTemplateRepo emailTemplateRepo;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @RequestMapping("/all")
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse allEmailTemplates() {
        return new ApiResponse(SUCCESS, emailTemplateRepo.findAllByOrderByPositionAsc());
    }

    @PreAuthorize("hasRole('MANAGER')")
    @RequestMapping(value = "/create", method = POST)
    @WeaverValidation(business = { @WeaverValidation.Business(value = CREATE) })
    public ApiResponse createEmailTemplate(@WeaverValidatedModel EmailTemplate emailTemplate) {
        logger.info("Creating email template with name " + emailTemplate.getName());
        emailTemplate = emailTemplateRepo.create(emailTemplate.getName(), emailTemplate.getSubject(), emailTemplate.getMessage());
        simpMessagingTemplate.convertAndSend("/channel/settings/email-template", new ApiResponse(SUCCESS, emailTemplateRepo.findAllByOrderByPositionAsc()));
        return new ApiResponse(SUCCESS, emailTemplate);
    }

    @PreAuthorize("hasRole('MANAGER')")
    @RequestMapping(value = "/update", method = POST)
    @WeaverValidation(business = { @WeaverValidation.Business(value = UPDATE) })
    public ApiResponse updateEmailTemplate(@WeaverValidatedModel EmailTemplate emailTemplate) {
        logger.info("Updating email template with name " + emailTemplate.getName());
        if (emailTemplate.getSystemRequired()) {
            emailTemplate = emailTemplateRepo.create(emailTemplate.getName(), emailTemplate.getSubject(), emailTemplate.getMessage());
        } else {
            emailTemplate = emailTemplateRepo.save(emailTemplate);
        }
        simpMessagingTemplate.convertAndSend("/channel/settings/email-template", new ApiResponse(SUCCESS, emailTemplateRepo.findAllByOrderByPositionAsc()));
        return new ApiResponse(SUCCESS, emailTemplate);
    }

    @PreAuthorize("hasRole('MANAGER')")
    @RequestMapping(value = "/remove", method = POST)
    @WeaverValidation(business = { @WeaverValidation.Business(value = DELETE, path = { "systemRequired" }, restrict = "true") })
    public ApiResponse removeEmailTemplate(@WeaverValidatedModel EmailTemplate emailTemplate) {
        logger.info("Removing email template with name " + emailTemplate.getName());
        emailTemplateRepo.remove(emailTemplate);
        simpMessagingTemplate.convertAndSend("/channel/settings/email-template", new ApiResponse(SUCCESS, emailTemplateRepo.findAllByOrderByPositionAsc()));
        return new ApiResponse(SUCCESS);
    }

    @RequestMapping("/reorder/{src}/{dest}")
    @PreAuthorize("hasRole('MANAGER')")
    @WeaverValidation(method = { @WeaverValidation.Method(value = REORDER, model = EmailTemplate.class, params = { "0", "1" }) })
    public ApiResponse reorderEmailTemplates(@PathVariable Long src, @PathVariable Long dest) {
        logger.info("Reordering document types");
        emailTemplateRepo.reorder(src, dest);
        simpMessagingTemplate.convertAndSend("/channel/settings/email-template", new ApiResponse(SUCCESS, emailTemplateRepo.findAllByOrderByPositionAsc()));
        return new ApiResponse(SUCCESS);
    }

    @RequestMapping("/sort/{column}")
    @PreAuthorize("hasRole('MANAGER')")
    @WeaverValidation(method = { @WeaverValidation.Method(value = SORT, model = EmailTemplate.class, params = { "0" }) })
    public ApiResponse sortEmailTemplates(@PathVariable String column) {
        logger.info("Sorting email templates by " + column);
        emailTemplateRepo.sort(column);
        simpMessagingTemplate.convertAndSend("/channel/settings/email-template", new ApiResponse(SUCCESS, emailTemplateRepo.findAllByOrderByPositionAsc()));
        return new ApiResponse(SUCCESS);
    }

}
