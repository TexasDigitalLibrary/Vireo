package org.tdl.vireo.controller;

import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;
import static edu.tamu.framework.enums.ApiResponseType.VALIDATION_WARNING;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.tdl.vireo.model.Configuration;
import org.tdl.vireo.model.repo.ConfigurationRepo;
import org.tdl.vireo.service.ValidationService;

import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.aspect.annotation.ApiValidatedModel;
import edu.tamu.framework.model.ApiResponse;

@Controller
@ApiMapping("/settings/configurable")
public class ConfigurableSettingsController {
    
    private Logger logger = LoggerFactory.getLogger(this.getClass()); 
    
    @Autowired
    private ConfigurationRepo configurationRepo;
    
    @Autowired 
    private SimpMessagingTemplate simpMessagingTemplate;
    
    @Autowired
    private ValidationService validationService;
    
    @ApiMapping("/all")
    public ApiResponse getSettings() {
        return new ApiResponse(SUCCESS, configurationRepo.getAll());
    }
    
    @ApiMapping("/update")
    public ApiResponse updateSetting(@ApiValidatedModel Configuration configuration) {
        // will attach any errors to the BindingResult when validating the incoming configuration
        configuration = configurationRepo.validateUpdate(configuration);
        
        // build a response based on the BindingResult state in the configuration
        ApiResponse response = validationService.buildResponse(configuration);
        
        switch(response.getMeta().getType()){
            case SUCCESS:
            case VALIDATION_INFO:
                logger.info("Updating configuration with name " + configuration.getName() + " and value " + configuration.getValue());
                configurationRepo.save(configuration);
                simpMessagingTemplate.convertAndSend("/channel/settings/configurable", new ApiResponse(SUCCESS, configurationRepo.getAll()));
                break;
            case VALIDATION_WARNING:
                simpMessagingTemplate.convertAndSend("/channel/settings/configurable", new ApiResponse(VALIDATION_WARNING, configurationRepo.getAll()));
                break;
            default:
                logger.warn("Couldn't update configuration with name " + configuration.getName() + " and value " + configuration.getValue() + " because: " + response.getMeta().getType());
                break;
        }

        return response;
    }
    
    @ApiMapping("/reset")
    public ApiResponse resetSetting(@ApiValidatedModel Configuration configuration) {
        // will attach any errors to the BindingResult when validating the incoming configuration
        configuration = configurationRepo.validateReset(configuration);
        
        // build a response based on the BindingResult state in the configuration
        ApiResponse response = validationService.buildResponse(configuration);
        
        switch(response.getMeta().getType()){
            case SUCCESS:
            case VALIDATION_INFO:
                logger.info("Resetting configuration with name " + configuration.getName() + " and value " + configuration.getValue());
                configurationRepo.reset(configuration);
                simpMessagingTemplate.convertAndSend("/channel/settings/configurable", new ApiResponse(SUCCESS, configurationRepo.getAll()));
                break;
            case VALIDATION_WARNING:
                simpMessagingTemplate.convertAndSend("/channel/settings/configurable", new ApiResponse(VALIDATION_WARNING, configurationRepo.getAll()));
                break;
            default:
                logger.warn("Couldn't reset configuration with name " + configuration.getName() + " and value " + configuration.getValue());
                break;
        }
        
        return response;
    }
  
}
