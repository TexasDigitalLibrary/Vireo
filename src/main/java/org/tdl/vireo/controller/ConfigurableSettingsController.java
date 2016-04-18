package org.tdl.vireo.controller;

import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;
import static edu.tamu.framework.enums.ApiResponseType.VALIDATION_ERROR;
import static edu.tamu.framework.enums.ApiResponseType.VALIDATION_INFO;
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
    
    @ApiMapping("/all")
    public ApiResponse getSettings() {
       return new ApiResponse(SUCCESS,toConfigPairsMap(configurationRepo.getAll()));
    }
    
    @ApiMapping("/update")
    public ApiResponse updateSetting(@ApiValidatedModel Configuration configuration) {
        // will attach any errors to the BindingResult when validating the incoming configuration
        configuration = configurationRepo.validateUpdate(configuration);
        
        logger.info("Updating configuration with name " + configuration.getName() + " and value " + configuration.getValue());

        return updateOrReset(configuration, true);
    }
    
    @ApiMapping("/reset")
    public ApiResponse resetSetting(@ApiValidatedModel Configuration configuration) {
        // will attach any errors to the BindingResult when validating the incoming configuration
        configuration = configurationRepo.validateReset(configuration);
        
        logger.info("Resetting configuration with name " + configuration.getName() + " and value " + configuration.getValue());
        
        return updateOrReset(configuration, false);
    }
    
    private ApiResponse updateOrReset(Configuration configuration, Boolean update){
        // if errors, with no warnings
        if (configuration.getBindingResult().hasErrors()) {
            return new ApiResponse(VALIDATION_ERROR, configuration.getBindingResult().getAll());
        }
        // else if no errors, with warnings
        else if (!configuration.getBindingResult().hasErrors() && configuration.getBindingResult().hasWarnings()) {
            simpMessagingTemplate.convertAndSend("/channel/settings/configurable", new ApiResponse(VALIDATION_WARNING, toConfigPairsMap(configurationRepo.getAllByType(configuration.getType()))));
            return new ApiResponse(VALIDATION_WARNING, configuration.getBindingResult().getAllWarningsAndInfos());
        }
        // else if no errors, no warnings, maybe infos
        else {
            // if we're updating
            if(update) {
                configurationRepo.save(configuration);
            }
            // if we're resetting
            else {
                configurationRepo.reset(configuration.getName());
            }
            simpMessagingTemplate.convertAndSend("/channel/settings/configurable", new ApiResponse(SUCCESS, toConfigPairsMap(configurationRepo.getAllByType(configuration.getType()))));
            // deal with infos being set
            if (configuration.getBindingResult().hasInfos()) {
                return new ApiResponse(VALIDATION_INFO, configuration.getBindingResult().getAllInfos());
            } else {
                return new ApiResponse(SUCCESS);
            }
        }
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
