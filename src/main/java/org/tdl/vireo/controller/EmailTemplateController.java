package org.tdl.vireo.controller;

import static edu.tamu.framework.enums.ApiResponseType.ERROR;
import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.tdl.vireo.model.EmailTemplate;
import org.tdl.vireo.model.repo.EmailTemplateRepo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.aspect.annotation.ApiVariable;
import edu.tamu.framework.aspect.annotation.Auth;
import edu.tamu.framework.aspect.annotation.Data;
import edu.tamu.framework.model.ApiResponse;

@Controller
@ApiMapping("/settings/email-template")
public class EmailTemplateController {

    private final Logger logger = Logger.getLogger(this.getClass());

    @Autowired
    private EmailTemplateRepo emailTemplateRepo;

    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired 
    private SimpMessagingTemplate simpMessagingTemplate;
    
    private Map<String, List<EmailTemplate>> getAll() {
        Map<String, List<EmailTemplate>> map = new HashMap<String, List<EmailTemplate>>();
        map.put("list", emailTemplateRepo.findAllByOrderByOrderAsc());
        return map;
    }
    
    @ApiMapping("/all")
    @Auth(role = "ROLE_MANAGER")
    public ApiResponse allEmailTemplates() {       
        return new ApiResponse(SUCCESS, getAll());
    }

    @ApiMapping("/create")
    @Auth(role = "ROLE_MANAGER")
    public ApiResponse createEmailTemplate(@Data String data) {
        
        JsonNode dataNode;
        try {
            dataNode = objectMapper.readTree(data);
        } catch (IOException e) {
            return new ApiResponse(ERROR, "Unable to parse update json ["+e.getMessage()+"]");
        }
        
        EmailTemplate newEmailTemplate = null;
        String newEmailTemplateName = null;
        String newEmailTemplateSubject = null;
        String newEmailTemplateMessageBody = null;
                
        JsonNode pontentialName = dataNode.get("name");
        if(pontentialName == null || pontentialName.asText().length() <= 0) { // Because of short circuit testing, pontentialName will be nonnull whenever pontentialName.asText() is called.
           return new ApiResponse(ERROR, "Name required to create an email template!");
        }else{
            newEmailTemplateName = pontentialName.asText();
        }
        JsonNode potentialSubject = dataNode.get("subject");
        if(potentialSubject == null || potentialSubject.asText().length() <= 0) { // Because of short circuit testing, potentialSubject will be nonnull whenever potentialSubject.asText() is called.
           return new ApiResponse(ERROR, "Subject required to create an email template!");
        }else{
            newEmailTemplateSubject = potentialSubject.asText();
        }
        JsonNode potentialMessageBody = dataNode.get("messageBody");
        if(potentialMessageBody == null || potentialMessageBody.asText().length() <= 0) { // Because of short circuit testing, potentialMessageBody will be nonnull whenever potentialMessageBody.asText() is called.
           return new ApiResponse(ERROR, "MessageBody required to create an email template!");
        }else{
            newEmailTemplateMessageBody = potentialMessageBody.asText();
        }

        newEmailTemplate = emailTemplateRepo.create(newEmailTemplateName, newEmailTemplateSubject, newEmailTemplateMessageBody); //With parameters checked, it is now safe to create the new email template.

        newEmailTemplate.setOrder((int) emailTemplateRepo.count());

        newEmailTemplate = emailTemplateRepo.save(newEmailTemplate);

        logger.info("Created email template with name " + newEmailTemplate.getName());
        
        simpMessagingTemplate.convertAndSend("/channel/settings/email-template", new ApiResponse(SUCCESS, getAll()));
        
        return new ApiResponse(SUCCESS);
    }
    
    @ApiMapping("/update")
    @Auth(role = "ROLE_MANAGER")
    public ApiResponse updateEmailTemplate(@Data String data) {
        
        JsonNode dataNode;
        try {
            dataNode = objectMapper.readTree(data);
        } catch (IOException e) {
            return new ApiResponse(ERROR, "Unable to parse update json ["+e.getMessage()+"]");
        }
        
        EmailTemplate oldEmailTemplate = null;
                
        JsonNode potentiallyExistingId = dataNode.get("id");
        if(potentiallyExistingId != null) {
            Long potentiallyExistingIdLong = -1L;
            try {
                potentiallyExistingIdLong = potentiallyExistingId.asLong();
            }
            catch(NumberFormatException nfe) {
                return new ApiResponse(ERROR, "id required to update email template!");
            }
            oldEmailTemplate = emailTemplateRepo.findOne(potentiallyExistingIdLong);
        }
        else {
            return new ApiResponse(ERROR, "id of existing email template required to update!");
        }
        
        JsonNode name = dataNode.get("name");
        if(name != null) {
            String nameString = name.asText();
            if(nameString != null && nameString.length() > 0) 
                oldEmailTemplate.setName(nameString);
        }
        
        JsonNode subject = dataNode.get("subject");
        if(subject != null) {
            String subjectString = subject.asText();
            if(subjectString != null && subjectString.length() > 0) 
                oldEmailTemplate.setSubject(subjectString);
        }
        
        JsonNode messageBody = dataNode.get("message");
        if(messageBody != null) {
            String messageBodyString = messageBody.asText();
            if(messageBodyString != null && messageBodyString.length() > 0) 
                oldEmailTemplate.setMessage(messageBodyString);
        }
        
        oldEmailTemplate = emailTemplateRepo.save(oldEmailTemplate);
        
        //TODO: logging
        
        logger.info("Created deposit location with name " + oldEmailTemplate.getName());
        
        simpMessagingTemplate.convertAndSend("/channel/settings/email-template", new ApiResponse(SUCCESS, getAll()));
        
        return new ApiResponse(SUCCESS);
    }

    @ApiMapping("/remove/{indexString}")
    @Auth(role = "ROLE_MANAGER")
    @Transactional
    public ApiResponse removeEmailTemplate(@ApiVariable String indexString) {        
        Integer index = -1;
        
        try {
            index = Integer.parseInt(indexString);
        }
        catch(NumberFormatException nfe) {
            logger.info("\n\nNOT A NUMBER " + indexString + "\n\n");
            return new ApiResponse(ERROR, "Id is not a valid deposit location order!");
        }
        
        if(index >= 0) {               
            emailTemplateRepo.remove(index);
        }
        else {
            logger.info("\n\nINDEX" + index + "\n\n");
            return new ApiResponse(ERROR, "Id is not a valid deposit location order!");
        }
        
        logger.info("Deleted email template with order " + index);
        simpMessagingTemplate.convertAndSend("/channel/settings/email-template", new ApiResponse(SUCCESS, getAll()));
        
        return new ApiResponse(SUCCESS);
    }
    
    @ApiMapping("/reorder/{src}/{dest}")
    @Auth(role = "ROLE_MANAGER")
    @Transactional
    public ApiResponse reorderEmailTemplates(@ApiVariable String src, @ApiVariable String dest) {
        Integer intSrc = Integer.parseInt(src);
        Integer intDest = Integer.parseInt(dest);
        emailTemplateRepo.reorder(intSrc, intDest);
        simpMessagingTemplate.convertAndSend("/channel/settings/email-template", new ApiResponse(SUCCESS, getAll()));        
        return new ApiResponse(SUCCESS);
    }
    
    @ApiMapping("/sort/{column}")
    @Auth(role = "ROLE_MANAGER")
    @Transactional
    public ApiResponse sortEmailTemplates(@ApiVariable String column) {
        emailTemplateRepo.sort(column);
        simpMessagingTemplate.convertAndSend("/channel/settings/email-template", new ApiResponse(SUCCESS, getAll()));        
        return new ApiResponse(SUCCESS);
    }

}
