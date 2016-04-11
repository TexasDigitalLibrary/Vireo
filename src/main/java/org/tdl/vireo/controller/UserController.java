package org.tdl.vireo.controller;

import static edu.tamu.framework.enums.ApiResponseType.ERROR;
import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;

import java.io.IOException;
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
import org.tdl.vireo.model.User;
import org.tdl.vireo.model.repo.UserRepo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.aspect.annotation.ApiValidatedModel;
import edu.tamu.framework.aspect.annotation.ApiVariable;
import edu.tamu.framework.aspect.annotation.Auth;
import edu.tamu.framework.aspect.annotation.Data;
import edu.tamu.framework.aspect.annotation.Shib;
import edu.tamu.framework.enums.ApiResponseType;
import edu.tamu.framework.model.ApiResponse;
import edu.tamu.framework.model.Credentials;

@Controller
@ApiMapping("/user")
public class UserController {
    
    private Logger logger = LoggerFactory.getLogger(this.getClass()); 
    
    @Autowired
    private UserRepo userRepo;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired 
    private SimpMessagingTemplate simpMessagingTemplate;
        
    @ApiMapping("/credentials")
    @Auth
    public ApiResponse credentials(@Shib Credentials shib) {        
        User user = userRepo.findByEmail(shib.getEmail());
        
        if(user == null) {
            logger.debug("User not registered!");
            return new ApiResponse(ERROR, "User not registered!");
        }

        shib.setRole(user.getRole());
        
        return new ApiResponse(SUCCESS, shib);
    }
    
    private Map<String,List<User>> allUsersHelper() {
        Map<String,List<User>> map = new HashMap<String,List<User>>();        
        map.put("list", userRepo.findAll());
        return map;
    }
    
    @ApiMapping("/all")
    @Auth(role="ROLE_MANAGER")
    @Transactional
    public ApiResponse allUsers() {            
        return new ApiResponse(SUCCESS, allUsersHelper());
    }
    
    @ApiMapping("/update-role")
    @Auth(role="ROLE_MANAGER")
    @Transactional
    public ApiResponse updateRole(@ApiValidatedModel User user) {      
        
        User possiblyExistingUser = userRepo.findByEmail(user.getEmail());
        if (possiblyExistingUser == null) {
            user.getBindingResult().addError(new ObjectError("user", "cannot update a role on a nonexistant user!"));
        }
        if (user.getBindingResult().hasErrors()) {
            return new ApiResponse(ApiResponseType.VALIDATION_ERROR, user.getBindingResult().getAll());
        }
        
        possiblyExistingUser.setRole(user.getRole());

        this.simpMessagingTemplate.convertAndSend("/channel/users", new ApiResponse(SUCCESS, allUsersHelper()));
        
        return new ApiResponse(SUCCESS, user);
    }
    
    @ApiMapping("/settings")
    @Auth(role="ROLE_USER")
    @Transactional
    public ApiResponse getSettings(@Shib Credentials shib) {
        User user = userRepo.findByEmail(shib.getEmail());
        return new ApiResponse(SUCCESS, user.getSettings());
    }
    
    @ApiMapping("/settings/{key}")
    @Auth(role="ROLE_USER")
    @Transactional
    public ApiResponse setSetting(@Shib Credentials shib, @ApiVariable String key, @Data String data) {
        
        // This will only work to change your own user settings
        // Email would need to be obtained off of the dataNode to change anothers settings
        User user = userRepo.findByEmail(shib.getEmail());
        
        JsonNode dataNode = null;
        try {
            dataNode = objectMapper.readTree(data);
        } catch (IOException e) {
            return new ApiResponse(ERROR, "Could not parse the data object.");
        }
        
        user.putSetting(key, dataNode.get("settingValue").asText());        
        
        return new ApiResponse(SUCCESS, userRepo.save(user).getSettings());
    }
    
}
