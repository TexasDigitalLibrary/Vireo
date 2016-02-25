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
import org.tdl.vireo.model.CustomActionDefinition;
import org.tdl.vireo.model.repo.CustomActionDefinitionRepo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.aspect.annotation.ApiVariable;
import edu.tamu.framework.aspect.annotation.Auth;
import edu.tamu.framework.aspect.annotation.Data;
import edu.tamu.framework.model.ApiResponse;

@Controller
@ApiMapping("/settings/custom-action")
public class CustomActionSettingsController {
    
    private final Logger logger = Logger.getLogger(this.getClass());
    
    @Autowired
    CustomActionDefinitionRepo customActionDefinitionRepo;

    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired 
    private SimpMessagingTemplate simpMessagingTemplate;
    
    private Map<String, List<CustomActionDefinition>> getAll() {
        Map<String, List<CustomActionDefinition>> allRet = new HashMap<String, List<CustomActionDefinition>>();
        allRet.put("list", customActionDefinitionRepo.findAllByOrderByOrderAsc());
        return allRet;
    }
    
    @ApiMapping("/all")
    public ApiResponse getCustomActions() {
       return new ApiResponse(SUCCESS,getAll());
    }
    
    @ApiMapping("/create")
    public ApiResponse createCustomAction(@Data String data) {
        
        JsonNode dataNode;
        try {
            dataNode = objectMapper.readTree(data);
        } catch (IOException e) {
            return new ApiResponse(ERROR, "Unable to parse update json ["+e.getMessage()+"]");
        }
               
        CustomActionDefinition newCustomAction = customActionDefinitionRepo.create(dataNode.get("label").asText(), dataNode.get("isStudentVisible").asBoolean());
        newCustomAction.setOrder((int) customActionDefinitionRepo.count());
        customActionDefinitionRepo.save(newCustomAction);
        
        this.simpMessagingTemplate.convertAndSend("/channel/settings/custom-actions", new ApiResponse(SUCCESS, getAll()));
        return new ApiResponse(SUCCESS);
    }
    
    @ApiMapping("/update")
    public ApiResponse updateCustomAction(@Data String data) {
               
        JsonNode dataNode;
        try {
            dataNode = objectMapper.readTree(data);
        } catch (IOException e) {
            return new ApiResponse(ERROR, "Unable to parse update json ["+e.getMessage()+"]");
        }
       
        CustomActionDefinition customActionToUpdate = customActionDefinitionRepo.findOne(dataNode.get("id").asLong());
        
        if(dataNode.get("label") != null) customActionToUpdate.setLabel(dataNode.get("label").asText());
        if(dataNode.get("isStudentVisible") != null) customActionToUpdate.isStudentVisible(dataNode.get("isStudentVisible").asBoolean());
        
        customActionDefinitionRepo.save(customActionToUpdate);
        this.simpMessagingTemplate.convertAndSend("/channel/settings/custom-actions", new ApiResponse(SUCCESS, getAll()));
        return new ApiResponse(SUCCESS);
        
    }
    
    @ApiMapping("/remove/{indexString}")
    @Auth(role = "ROLE_MANAGER")
    @Transactional
    public ApiResponse removeCustomAction(@ApiVariable String indexString) {        
        Integer index = -1;
        
        try {
            index = Integer.parseInt(indexString);
        }
        catch(NumberFormatException nfe) {
            logger.info("\n\nNOT A NUMBER " + indexString + "\n\n");
            return new ApiResponse(ERROR, "Id is not a valid custom action order!");
        }
        
        if(index >= 0) {               
            customActionDefinitionRepo.remove(index);
        }
        else {
            logger.info("\n\nINDEX" + index + "\n\n");
            return new ApiResponse(ERROR, "Id is not a valid custom action order!");
        }
        
        logger.info("Custom Action with order " + index);
        
        simpMessagingTemplate.convertAndSend("/channel/settings/custom-actions", new ApiResponse(SUCCESS, getAll()));
        
        return new ApiResponse(SUCCESS);
    }
    
    @ApiMapping("/reorder/{src}/{dest}")
    @Auth(role = "ROLE_MANAGER")
    @Transactional
    public ApiResponse reorderCustomActions(@ApiVariable String src, @ApiVariable String dest) {
        Integer intSrc = Integer.parseInt(src);
        Integer intDest = Integer.parseInt(dest);
        customActionDefinitionRepo.reorder(intSrc, intDest);
        simpMessagingTemplate.convertAndSend("/channel/settings/custom-action", new ApiResponse(SUCCESS, getAll()));        
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
