package org.tdl.vireo.controller;

import static edu.tamu.framework.enums.ApiResponseType.ERROR;
import static edu.tamu.framework.enums.ApiResponseType.VALIDATION_ERROR;
import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.tdl.vireo.model.User;
import org.tdl.vireo.model.repo.UserRepo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.aspect.annotation.ApiValidatedModel;
import edu.tamu.framework.aspect.annotation.ApiVariable;
import edu.tamu.framework.aspect.annotation.Auth;
import edu.tamu.framework.aspect.annotation.Data;
import edu.tamu.framework.aspect.annotation.Shib;
import edu.tamu.framework.model.ApiResponse;
import edu.tamu.framework.model.Credentials;
import edu.tamu.framework.validation.ModelBindingResult;

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
    
    @ApiMapping("/all")
    @Auth(role="ROLE_MANAGER")
    @Transactional
    public ApiResponse allUsers() {            
        Map<String,List<User>> map = new HashMap<String,List<User>>();        
        map.put("list", userRepo.findAll());
        return new ApiResponse(SUCCESS, map);
    }
    
    @ApiMapping("/update-role")
    @Auth(role="ROLE_MANAGER")
    @Transactional
    public ApiResponse updateRole(@Data String data) throws Exception {
        
        Map<String,String> map = new HashMap<String,String>();      
        try {
            map = objectMapper.readValue(data, new TypeReference<HashMap<String,String>>(){});
        } catch (Exception e) {
            e.printStackTrace();
        }       
        
        User user = userRepo.findByEmail(map.get("email"));
        
        user.setRole(map.get("role"));
        
        user = userRepo.save(user);
        
        Map<String, Object> userMap = new HashMap<String, Object>();
        userMap.put("list", userRepo.findAll());
        userMap.put("changedUserEmail", map.get("email"));
        
        this.simpMessagingTemplate.convertAndSend("/channel/users", new ApiResponse(SUCCESS, userMap));
        
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
    public ApiResponse setSetting(@Shib Credentials shib, @ApiVariable String key, @ApiValidatedModel UserSettingModel userSetting) {
        
        // This will only work to change your own user settings
        // Email would need to be obtained off of the dataNode to change anothers settings
        User user = userRepo.findByEmail(shib.getEmail());
        
        if(userSetting.getBindingResult().hasErrors()) {
            return new ApiResponse(VALIDATION_ERROR, userSetting.getBindingResult().getAll());
        }
        
        user.putSetting(key, userSetting.getSettingValue());        
        
        return new ApiResponse(SUCCESS, userRepo.save(user).getSettings());
    }
    
    public class UserSettingModel {
        private ModelBindingResult bindingResult;
        
        @NotEmpty
        private String settingValue;
        
        public UserSettingModel() { }

        /**
         * @return the settingValue
         */
        public String getSettingValue() {
            return settingValue;
        }

        /**
         * @param settingValue the settingValue to set
         */
        public void setSettingValue(String settingValue) {
            this.settingValue = settingValue;
        }

        /**
         * @return the bindingResult
         */
        public ModelBindingResult getBindingResult() {
            return bindingResult;
        }

        /**
         * @param bindingResult the bindingResult to set
         */
        public void setBindingResult(ModelBindingResult bindingResult) {
            this.bindingResult = bindingResult;
        }
    }    
}
