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
import org.tdl.vireo.model.Configuration;
import org.tdl.vireo.model.repo.ConfigurationRepo;
import org.tdl.vireo.service.BuildResponseService;

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
    private BuildResponseService buildResponseService;
    
    @ApiMapping("/all")
    public ApiResponse getSettings() {
       return new ApiResponse(SUCCESS,toConfigPairsMap(configurationRepo.getAll()));
    }
    
    @ApiMapping("/update")
    public ApiResponse updateSetting(@ApiValidatedModel Configuration configuration) {
        // will attach any errors to the BindingResult when validating the incoming configuration
        configuration = configurationRepo.validateUpdate(configuration);
        
        // build a response based on the BindingResult state in the configuration
        ApiResponse response = buildResponseService.buildResponse(configuration);
        
        switch(response.getMeta().getType()){
            case SUCCESS:
            case VALIDATION_INFO:
                logger.info("Updating configuration with name " + configuration.getName() + " and value " + configuration.getValue());
                configurationRepo.save(configuration);
                simpMessagingTemplate.convertAndSend("/channel/settings/configurable", new ApiResponse(SUCCESS, toConfigPairsMap(configurationRepo.getAllByType(configuration.getType()))));
                break;
            case VALIDATION_WARNING:
                simpMessagingTemplate.convertAndSend("/channel/settings/configurable", new ApiResponse(VALIDATION_WARNING, toConfigPairsMap(configurationRepo.getAllByType(configuration.getType()))));
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
        ApiResponse response = buildResponseService.buildResponse(configuration);
        
        switch(response.getMeta().getType()){
            case SUCCESS:
            case VALIDATION_INFO:
                logger.info("Resetting configuration with name " + configuration.getName() + " and value " + configuration.getValue());
                configurationRepo.reset(configuration.getName());
                simpMessagingTemplate.convertAndSend("/channel/settings/configurable", new ApiResponse(SUCCESS, toConfigPairsMap(configurationRepo.getAllByType(configuration.getType()))));
                break;
            case VALIDATION_WARNING:
                simpMessagingTemplate.convertAndSend("/channel/settings/configurable", new ApiResponse(VALIDATION_WARNING, toConfigPairsMap(configurationRepo.getAllByType(configuration.getType()))));
                break;
            default:
                logger.warn("Couldn't update configuration with name " + configuration.getName() + " and value " + configuration.getValue());
                break;
        }
        
        return response;
    }
    
    /**
     * Turns a List&lt;Configuration&gt; into a Map&lt;Configuration.type(), Map&lt;Configuration.name(), Configuration.value()&gt;&gt;
     * 
     * Assumes that the List&lt;Configuration&gt; that is passed in is pre-sorted by configuration.type
     * 
     * @param configurations
     * @return
     */
    private Map<String, Map<String, String>> toConfigPairsMap(List<Configuration> configurations) {
        Map<String, Map<String,String>> typesToConfigPairs = new HashMap<String, Map<String,String>>();
        Map<String,String> items = new HashMap<String, String>();
        // keep track of the last type.
        String lastType = "";
        for(Configuration configuration : configurations) {
            // if the last type doesn't equal the current configuration's type
            if(!lastType.equals(configuration.getType())) {
                // put all the items in the return map
                typesToConfigPairs.put(lastType, items);
                // start a new items map
                items = new HashMap<String, String>();
                // change the lastType
                lastType = configuration.getType();
            }
            // add the item to the items list
            items.put(configuration.getName(), configuration.getValue());
        }
        // the last configuration type will not be added to the return map, so we add it here.
        if(!items.isEmpty()) {
            typesToConfigPairs.put(lastType, items);
        }
        return typesToConfigPairs;
    }
}
