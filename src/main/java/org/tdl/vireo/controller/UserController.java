package org.tdl.vireo.controller;

import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;
import static edu.tamu.framework.enums.ApiResponseType.VALIDATION_ERROR;
import static edu.tamu.framework.enums.ApiResponseType.VALIDATION_WARNING;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.ObjectError;
import org.tdl.vireo.controller.model.UserControllerModel;
import org.tdl.vireo.model.User;
import org.tdl.vireo.model.repo.UserRepo;
import org.tdl.vireo.service.ValidationService;

import com.fasterxml.jackson.databind.JsonNode;

import edu.tamu.framework.aspect.annotation.ApiCredentials;
import edu.tamu.framework.aspect.annotation.ApiData;
import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.aspect.annotation.ApiValidatedModel;
import edu.tamu.framework.aspect.annotation.ApiVariable;
import edu.tamu.framework.aspect.annotation.Auth;
import edu.tamu.framework.model.ApiResponse;
import edu.tamu.framework.model.Credentials;

@Controller
@ApiMapping("/user")
public class UserController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;
    
    @Autowired
    private ValidationService validationService;

    @ApiMapping("/credentials")
    @Auth(role = "STUDENT")
    public ApiResponse credentials(@ApiCredentials Credentials shib) {
        User user = userRepo.findByEmail(shib.getEmail());

        if (user == null) {
            logger.debug("User not registered!");
            return new ApiResponse(VALIDATION_ERROR, "User not registered!");
        }

        shib.setRole(user.getRole().toString());

        return new ApiResponse(SUCCESS, shib);
    }

    @ApiMapping("/all")
    @Auth(role = "MANAGER")
    @Transactional
    public ApiResponse allUsers() {            
        return new ApiResponse(SUCCESS, getAll());
    }

    @ApiMapping("/update-role")
    @Auth(role = "MANAGER")
    @Transactional
    public ApiResponse updateRole(@ApiValidatedModel User user) {      
        
        // will attach any errors to the BindingResult when validating the incoming user
        user = userRepo.validateUpdateRole(user);
        
        // build a response based on the BindingResult state in the configuration
        ApiResponse response = validationService.buildResponse(user);
        
        Map<String, Object> retMap = new HashMap<String, Object>();
        retMap.put("list", userRepo.findAll());
        
        switch(response.getMeta().getType()){
            case SUCCESS:
            case VALIDATION_INFO:
                logger.info("Updating role for " + user.getEmail());
                userRepo.save(user);
                retMap.put("changedUserEmail", user.getEmail());
                response.getPayload().put(user.getClass().getSimpleName(), user);
                simpMessagingTemplate.convertAndSend("/channel/users", new ApiResponse(SUCCESS, retMap));
                break;
            case VALIDATION_WARNING:
                simpMessagingTemplate.convertAndSend("/channel/users", new ApiResponse(VALIDATION_WARNING, retMap));
                break;
            default:
                logger.warn("Couldn't update role for " + user.getEmail());
                break;
        }
        
        return response;
    }

    @ApiMapping("/settings")
    @Auth(role = "STUDENT")
    @Transactional
    public ApiResponse getSettings(@ApiCredentials Credentials shib) {
        User user = userRepo.findByEmail(shib.getEmail());
        
        if(user == null) {
            logger.debug("User not registered!");
            return new ApiResponse(VALIDATION_ERROR, "User not registered!");
        }
        
        return new ApiResponse(SUCCESS, user.getSettings());
    }

    @ApiMapping("/settings/{key}")
    @Auth(role = "STUDENT")
    @Transactional
    public ApiResponse setSetting(@ApiVariable String key, @ApiCredentials Credentials shib, @ApiValidatedModel UserControllerModel userSetting) {

        User user = userRepo.findByEmail(shib.getEmail());
        if (user == null) {
            userSetting.getBindingResult().addError(new ObjectError("user", "User not registered!"));
        }

        if (userSetting.getBindingResult().hasErrors()) {
            return new ApiResponse(VALIDATION_ERROR, userSetting.getBindingResult().getAll());
        }

        user.putSetting(key, userSetting.getSettingValue());

        return new ApiResponse(SUCCESS, userRepo.save(user).getSettings());
    }
    
    @ApiMapping("/settings/update")
    @Auth(role = "STUDENT")
    @Transactional
    public ApiResponse updateSetting(@ApiCredentials Credentials shib, @ApiData Map<String, String> userSettings) {

        System.out.println(userSettings);
        
        User user = userRepo.findByEmail(shib.getEmail());
        
        user.setSettings(userSettings);
        
        simpMessagingTemplate.convertAndSend("/channel/user/settings/update", new ApiResponse(SUCCESS, userRepo.save(user).getSettings()));
        
        return new ApiResponse(SUCCESS);
    }

    private Map<String,List<User>> getAll() {
        Map<String,List<User>> map = new HashMap<String,List<User>>();        
        map.put("list", userRepo.findAll());
        return map;
    }
}
