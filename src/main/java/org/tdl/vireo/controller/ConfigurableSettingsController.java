package org.tdl.vireo.controller;

import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;
import static edu.tamu.framework.enums.ApiResponseType.ERROR;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.tdl.vireo.model.Configuration;
import org.tdl.vireo.model.repo.ConfigurationRepo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.aspect.annotation.Data;
import edu.tamu.framework.model.ApiResponse;

@Controller
@ApiMapping("/settings/configurable")
public class ConfigurableSettingsController {
    @Autowired
    ConfigurationRepo configurationRepo;

    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired 
    private SimpMessagingTemplate simpMessagingTemplate;
    
    @ApiMapping("/all")
    public ApiResponse getSettings() {
       return new ApiResponse(SUCCESS,toConfigPairsMap(configurationRepo.getAll()));
    }
    
    @ApiMapping("/update")
    public ApiResponse updateSetting(@Data String data) {
        
        JsonNode dataNode;
        try {
            dataNode = objectMapper.readTree(data);
        } catch (IOException e) {
            return new ApiResponse(ERROR, "Unable to parse update json ["+e.getMessage()+"]");
        }
        
        configurationRepo.createOrUpdate(dataNode.get("setting").asText(),dataNode.get("value").asText(),dataNode.get("type").asText());
        
        this.simpMessagingTemplate.convertAndSend("/channel/settings", new ApiResponse(SUCCESS, toConfigPairsMap(configurationRepo.getAllByType(dataNode.get("type").asText()))));

        return new ApiResponse(SUCCESS);
    }
    
    @ApiMapping("/reset")
    public ApiResponse resetSetting(@Data String data) {
        Map<String,String> map = new HashMap<String,String>();      
        try {
            map = objectMapper.readValue(data, new TypeReference<HashMap<String,String>>(){});
        } catch (Exception e) {
            e.printStackTrace();
        }
        Configuration deletableOverride = configurationRepo.getByName(map.get("setting"));
        
        if (deletableOverride != null) {
            System.out.println(deletableOverride.getName());
            configurationRepo.delete(deletableOverride);
        }
        
        this.simpMessagingTemplate.convertAndSend("/channel/settings", new ApiResponse(SUCCESS, toConfigPairsMap(configurationRepo.getAllByType(map.get("type")))));
        
        return new ApiResponse(SUCCESS);
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
        String lastType = "";
        for(Configuration configuration : configurations) {
            if(!lastType.equals(configuration.getType())) {
                typesToConfigPairs.put(lastType, items);
                items = new HashMap<String, String>();
                lastType = configuration.getType();
            }
            items.put(configuration.getName(), configuration.getValue());
        }
        return typesToConfigPairs;
    }
}
