package org.tdl.vireo.controller;

import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;
import static edu.tamu.framework.enums.ApiResponseType.VALIDATION_ERROR;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.ObjectError;
import org.tdl.vireo.model.CustomActionDefinition;
import org.tdl.vireo.model.repo.CustomActionDefinitionRepo;

import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.aspect.annotation.ApiValidatedModel;
import edu.tamu.framework.aspect.annotation.ApiVariable;
import edu.tamu.framework.aspect.annotation.Auth;
import edu.tamu.framework.model.ApiResponse;

@Controller
@ApiMapping("/settings/custom-action")
public class CustomActionSettingsController {
    
    private Logger logger = LoggerFactory.getLogger(this.getClass()); 
    
    @Autowired
    CustomActionDefinitionRepo customActionDefinitionRepo;
    
    @Autowired 
    private SimpMessagingTemplate simpMessagingTemplate;
    
    private Map<String, List<CustomActionDefinition>> getAll() {
        Map<String, List<CustomActionDefinition>> allRet = new HashMap<String, List<CustomActionDefinition>>();
        allRet.put("list", customActionDefinitionRepo.findAllByOrderByPositionAsc());
        return allRet;
    }
    
    @ApiMapping("/all")
    public ApiResponse getCustomActions() {
       return new ApiResponse(SUCCESS,getAll());
    }
    
    @ApiMapping("/create")
    public ApiResponse createCustomAction(@ApiValidatedModel CustomActionDefinition customActionDefinition) {
        if(customActionDefinition.getBindingResult().hasErrors()){
            return new ApiResponse(VALIDATION_ERROR, customActionDefinition.getBindingResult().getAll());
        }
        
        //TODO: this needs to go in repo.validateCreate() -- VIR-201
        if(customActionDefinitionRepo.findByLabel(customActionDefinition.getLabel()) != null) {
            return new ApiResponse(VALIDATION_ERROR, customActionDefinition.getLabel() + " is already a custom action!");
        }
        
        customActionDefinitionRepo.create(customActionDefinition.getLabel(), customActionDefinition.isStudentVisible());
        
        this.simpMessagingTemplate.convertAndSend("/channel/settings/custom-actions", new ApiResponse(SUCCESS, getAll()));
        return new ApiResponse(SUCCESS);
    }
    
    @ApiMapping("/update")
    public ApiResponse updateCustomAction(@ApiValidatedModel CustomActionDefinition customActionDefinition) {
        //TODO: this needs to go in repo.validateUpdate() -- VIR-201
        if(customActionDefinition.getId() == null) {
            customActionDefinition.getBindingResult().addError(new ObjectError("customActionDefinition", "Cannot update a CustomActionDefinition without an id!"));
        }
        
        if(customActionDefinition.getBindingResult().hasErrors()){
            return new ApiResponse(VALIDATION_ERROR, customActionDefinition.getBindingResult().getAll());
        } 
        
        CustomActionDefinition customActionToUpdate = customActionDefinitionRepo.findOne(customActionDefinition.getId());
        
        customActionToUpdate.setLabel(customActionDefinition.getLabel());
        customActionToUpdate.isStudentVisible(customActionDefinition.isStudentVisible());
        
        customActionDefinitionRepo.save(customActionToUpdate);
        this.simpMessagingTemplate.convertAndSend("/channel/settings/custom-actions", new ApiResponse(SUCCESS, getAll()));
        return new ApiResponse(SUCCESS);
        
    }
    
    @ApiMapping("/remove/{indexString}")
    @Auth(role = "ROLE_MANAGER")
    @Transactional
    public ApiResponse removeCustomAction(@ApiVariable String indexString) {        
        Long index = -1L;
        
        try {
            index = Long.parseLong(indexString);
        }
        catch(NumberFormatException nfe) {
            logger.info("\n\nNOT A NUMBER " + indexString + "\n\n");
            return new ApiResponse(VALIDATION_ERROR, "Id is not a valid custom action order!");
        }
        
        if(index >= 0) {               
            customActionDefinitionRepo.remove(index);
        }
        else {
            logger.info("\n\nINDEX" + index + "\n\n");
            return new ApiResponse(VALIDATION_ERROR, "Id is not a valid custom action order!");
        }
        
        logger.info("Custom Action with order " + index);
        
        simpMessagingTemplate.convertAndSend("/channel/settings/custom-actions", new ApiResponse(SUCCESS, getAll()));
        
        return new ApiResponse(SUCCESS);
    }
    
    @ApiMapping("/reorder/{src}/{dest}")
    @Auth(role = "ROLE_MANAGER")
    @Transactional
    public ApiResponse reorderCustomActions(@ApiVariable String src, @ApiVariable String dest) {
        Long intSrc = Long.parseLong(src);
        Long intDest = Long.parseLong(dest);
        customActionDefinitionRepo.reorder(intSrc, intDest);
        simpMessagingTemplate.convertAndSend("/channel/settings/custom-actions", new ApiResponse(SUCCESS, getAll()));        
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
