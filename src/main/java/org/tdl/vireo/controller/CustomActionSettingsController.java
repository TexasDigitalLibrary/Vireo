package org.tdl.vireo.controller;

import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;
import static edu.tamu.framework.enums.ApiResponseType.VALIDATION_WARNING;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.tdl.vireo.model.CustomActionDefinition;
import org.tdl.vireo.model.repo.CustomActionDefinitionRepo;
import org.tdl.vireo.service.ValidationService;

import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.aspect.annotation.ApiValidatedModel;
import edu.tamu.framework.aspect.annotation.ApiVariable;
import edu.tamu.framework.aspect.annotation.Auth;
import edu.tamu.framework.model.ApiResponse;
import edu.tamu.framework.validation.ModelBindingResult;

@Controller
@ApiMapping("/settings/custom-actions")
public class CustomActionSettingsController {
    
    private Logger logger = LoggerFactory.getLogger(this.getClass()); 
    
    @Autowired
    CustomActionDefinitionRepo customActionDefinitionRepo;
    
    @Autowired 
    private SimpMessagingTemplate simpMessagingTemplate;
    
    @Autowired
    private ValidationService validationService;
    
    @ApiMapping("/all")
    public ApiResponse getCustomActions() {
       return new ApiResponse(SUCCESS, customActionDefinitionRepo.findAllByOrderByPositionAsc());
    }
    
    @ApiMapping("/create")
    public ApiResponse createCustomAction(@ApiValidatedModel CustomActionDefinition customActionDefinition) {
        
        // will attach any errors to the BindingResult when validating the incoming customActionDefinition
        customActionDefinition = customActionDefinitionRepo.validateCreate(customActionDefinition);
        
        // build a response based on the BindingResult state
        ApiResponse response = validationService.buildResponse(customActionDefinition);
        
        switch(response.getMeta().getType()){
            case SUCCESS:
            case VALIDATION_INFO:
                logger.info("Creating custom action definition with label " + customActionDefinition.getLabel());
                customActionDefinitionRepo.create(customActionDefinition.getLabel(), customActionDefinition.isStudentVisible());
                simpMessagingTemplate.convertAndSend("/channel/settings/custom-actions", new ApiResponse(SUCCESS, customActionDefinitionRepo.findAllByOrderByPositionAsc()));
                break;
            case VALIDATION_WARNING:
                simpMessagingTemplate.convertAndSend("/channel/settings/custom-actions", new ApiResponse(VALIDATION_WARNING, customActionDefinitionRepo.findAllByOrderByPositionAsc()));
                break;
            default:
                logger.warn("Couldn't create custom action definition with label " + customActionDefinition.getLabel() + " because: " + response.getMeta().getType());
                break;
        }

        return response;
    }
    
    @ApiMapping("/update")
    public ApiResponse updateCustomAction(@ApiValidatedModel CustomActionDefinition customActionDefinition) {
        
        // will attach any errors to the BindingResult when validating the incoming customActionDefinition
        customActionDefinition = customActionDefinitionRepo.validateUpdate(customActionDefinition);
        
        // build a response based on the BindingResult state
        ApiResponse response = validationService.buildResponse(customActionDefinition);
        
        switch(response.getMeta().getType()){
            case SUCCESS:
            case VALIDATION_INFO:
                logger.info("Updating custom action definition with label " + customActionDefinition.getLabel());
                customActionDefinitionRepo.save(customActionDefinition);
                simpMessagingTemplate.convertAndSend("/channel/settings/custom-actions", new ApiResponse(SUCCESS, customActionDefinitionRepo.findAllByOrderByPositionAsc()));
                break;
            case VALIDATION_WARNING:
                simpMessagingTemplate.convertAndSend("/channel/settings/custom-actions", new ApiResponse(VALIDATION_WARNING, customActionDefinitionRepo.findAllByOrderByPositionAsc()));
                break;
            default:
                logger.warn("Couldn't update custom action definition with label " + customActionDefinition.getLabel() + " because: " + response.getMeta().getType());
                break;
        }

        return response;
    }
    
    @ApiMapping("/remove")
    @Auth(role = "MANAGER")
    @Transactional
    public ApiResponse removeCustomAction(@ApiValidatedModel CustomActionDefinition customActionDefinition) {

        // build a response based on the BindingResult state
        ApiResponse response = validationService.buildResponse(customActionDefinition);
        
        switch(response.getMeta().getType()){
            case SUCCESS:
            case VALIDATION_INFO:
                logger.info("Removing custom action definition with label " + customActionDefinition.getLabel());
                customActionDefinitionRepo.remove(customActionDefinition);
                simpMessagingTemplate.convertAndSend("/channel/settings/custom-actions", new ApiResponse(SUCCESS, customActionDefinitionRepo.findAllByOrderByPositionAsc()));
                break;
            case VALIDATION_WARNING:
                simpMessagingTemplate.convertAndSend("/channel/settings/custom-actions", new ApiResponse(VALIDATION_WARNING, customActionDefinitionRepo.findAllByOrderByPositionAsc()));
                break;
            default:
                logger.warn("Couldn't remove custom action definition with label " + customActionDefinition.getLabel() + " because: " + response.getMeta().getType());
                break;
        }
        
        return response;
    }
    
    @ApiMapping("/reorder/{src}/{dest}")
    @Auth(role = "MANAGER")
    @Transactional
    public ApiResponse reorderCustomActions(@ApiVariable String src, @ApiVariable String dest) {
        
        // create a ModelBindingResult since we have an @ApiVariable coming in (and not a @ApiValidatedModel)
        ModelBindingResult modelBindingResult = new ModelBindingResult(src, "customActionDefinition");
        
        // will attach any errors to the BindingResult when validating the incoming src, dest
        Long longSrc = validationService.validateLong(src, "position", modelBindingResult);
        Long longDest = validationService.validateLong(dest, "position", modelBindingResult);
        
        // build a response based on the BindingResult state
        ApiResponse response = validationService.buildResponse(modelBindingResult);
        
        switch(response.getMeta().getType()){
            case SUCCESS:
            case VALIDATION_INFO:
                logger.info("Reordering custom action definitions");
                customActionDefinitionRepo.reorder(longSrc, longDest);
                simpMessagingTemplate.convertAndSend("/channel/settings/custom-actions", new ApiResponse(SUCCESS, customActionDefinitionRepo.findAllByOrderByPositionAsc()));
                break;
            case VALIDATION_WARNING:
                simpMessagingTemplate.convertAndSend("/channel/settings/custom-actions", new ApiResponse(VALIDATION_WARNING, customActionDefinitionRepo.findAllByOrderByPositionAsc()));
                break;
            default:
                logger.warn("Couldn't reorder custom action definitions because: " + response.getMeta().getType());
                break;
        }
        
        return response;
    }
    
}
