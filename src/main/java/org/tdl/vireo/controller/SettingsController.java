package org.tdl.vireo.controller;

import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.tdl.vireo.service.DefaultSettingsService;
import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.model.ApiResponse;

@Controller
@ApiMapping("/settings")
public class SettingsController {
    @Autowired
    DefaultSettingsService defaultSettingsService;
    
    @ApiMapping("/all")
    public ApiResponse getSettings() {   
       
       //a map to store configuration for each type as we iterate
       Map<String,String> typeMap;
       
       //a map of the type names to the full configurations for each type
       Map<String, Map<String, String>> typesToConfigPairs = new HashMap<String, Map<String, String>>();
      
        List<String> allTypes = defaultSettingsService.getTypes();
        for(String type:allTypes) {
            typeMap = defaultSettingsService.getSettingsByType(type);
            for(String key: typeMap.keySet()) {
                typesToConfigPairs.put(type, typeMap);
            }
        }
        return new ApiResponse(SUCCESS,typesToConfigPairs);
        
    }

}
