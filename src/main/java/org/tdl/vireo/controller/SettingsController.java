package org.tdl.vireo.controller;

import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.tdl.vireo.model.repo.ConfigurationRepo;
import org.tdl.vireo.service.DefaultSettingsService;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.aspect.annotation.Data;
import edu.tamu.framework.model.ApiResponse;

@Controller
@ApiMapping("/settings")
public class SettingsController {
    @Autowired
    ConfigurationRepo configurationRepo;

    @Autowired
    DefaultSettingsService defaultSettingsService;

    @Autowired
    private ObjectMapper objectMapper;
    
    @ApiMapping("/all")
    public ApiResponse getSettings() {   
       //a map of the type names to the full configurations for each type
       Map<String, Map<String,String>> typesToConfigPairs = new HashMap<String, Map<String,String>>();
       List<String> allTypes = defaultSettingsService.getTypes();
       for(String type:allTypes) {
           typesToConfigPairs.put(type,configurationRepo.getAllByType(type));
       }
       return new ApiResponse(SUCCESS,typesToConfigPairs);
    }
    
    @ApiMapping("/update")
    public ApiResponse updateSetting(@Data Object updateSettings) {

        return new ApiResponse(SUCCESS);
    }

}
