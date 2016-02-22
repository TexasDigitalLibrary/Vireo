package org.tdl.vireo.controller;

import static edu.tamu.framework.enums.ApiResponseType.ERROR;
import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.tdl.vireo.model.CustomActionDefinition;
import org.tdl.vireo.model.repo.CustomActionDefinitionRepo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.aspect.annotation.Data;
import edu.tamu.framework.model.ApiResponse;

@Controller
@ApiMapping("/settings/custom-action")
public class CustomActionSettingsController {
    @Autowired
    CustomActionDefinitionRepo customActionDefinitionRepo;

    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired 
    private SimpMessagingTemplate simpMessagingTemplate;
    
    private Map<String, List<CustomActionDefinition>> getAll() {
        Map<String, List<CustomActionDefinition>> allRet = new HashMap<String, List<CustomActionDefinition>>();
        allRet.put("list", customActionDefinitionRepo.findAll());
        return allRet;
    }
    
    @ApiMapping("/all")
    public ApiResponse getSettings() {
       return new ApiResponse(SUCCESS,getAll());
    }
    
    @ApiMapping("/create")
    public ApiResponse updateSetting(@Data String data) {
        
        JsonNode dataNode;
        try {
            dataNode = objectMapper.readTree(data);
        } catch (IOException e) {
            return new ApiResponse(ERROR, "Unable to parse update json ["+e.getMessage()+"]");
        }
        
        customActionDefinitionRepo.create(dataNode.get("label").asText(), dataNode.get("isStudentVisible").asBoolean());
        this.simpMessagingTemplate.convertAndSend("/channel/settings/custom-actions", new ApiResponse(SUCCESS, getAll()));
        return new ApiResponse(SUCCESS);
    }
    
//    @ApiMapping("/reset")
//    public ApiResponse resetSetting(@Data String data) {
//        Map<String,String> map = new HashMap<String,String>();      
//        try {
//            map = objectMapper.readValue(data, new TypeReference<HashMap<String,String>>(){});
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        Configuration deletableOverride = customActionDefinitionRepo.findByNameAndType(map.get("setting"),map.get("type"));
//        if (deletableOverride != null) {
//            System.out.println(deletableOverride.getName());
//            customActionDefinitionRepo.delete(deletableOverride);
//        }
//        Map<String, Map<String,String>> typeToConfigPair = new HashMap<String, Map<String,String>>();
//        typeToConfigPair.put(map.get("type"),customActionDefinitionRepo.getAllByType(map.get("type")));
//        this.simpMessagingTemplate.convertAndSend("/channel/settings", new ApiResponse(SUCCESS, typeToConfigPair));
//        
//        return new ApiResponse(SUCCESS);
//    }
}
