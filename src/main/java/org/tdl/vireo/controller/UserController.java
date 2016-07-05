package org.tdl.vireo.controller;

import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;
import static edu.tamu.framework.enums.ApiResponseType.VALIDATION_ERROR;
import static edu.tamu.framework.enums.ApiResponseType.VALIDATION_WARNING;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.tdl.vireo.model.User;
import org.tdl.vireo.model.repo.UserRepo;
import org.tdl.vireo.service.ValidationService;

import edu.tamu.framework.aspect.annotation.ApiCredentials;
import edu.tamu.framework.aspect.annotation.ApiData;
import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.aspect.annotation.ApiValidatedModel;
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
    
    // TODO: make global static method, redundant method in interceptors and here
    public Credentials getAnonymousCredentials() {
        Credentials anonymousCredentials = new Credentials();
        anonymousCredentials.setAffiliation("NA");
        anonymousCredentials.setLastName("Anonymous");
        anonymousCredentials.setFirstName("Role");
        anonymousCredentials.setNetid("anonymous-" + Math.round(Math.random() * 100000));
        anonymousCredentials.setUin("000000000");
        anonymousCredentials.setExp("1436982214754");
        anonymousCredentials.setEmail("helpdesk@library.tamu.edu");
        anonymousCredentials.setRole("NONE");
        return anonymousCredentials;
    }

    @ApiMapping("/credentials")
    @Auth(role = "NONE")
    public ApiResponse credentials(@ApiCredentials Credentials shib) {
        User user = userRepo.findByEmail(shib.getEmail());

        if (user == null) {
            logger.debug("User not registered! Responding with anonymous credentials!");
            return new ApiResponse(SUCCESS, getAnonymousCredentials());
        }

        shib.setRole(user.getRole().toString());

        return new ApiResponse(SUCCESS, shib);
    }

    @ApiMapping("/all")
    @Auth(role = "MANAGER")
    @Transactional
    public ApiResponse allUsers() {
        return new ApiResponse(SUCCESS, userRepo.findAll());
    }

    @ApiMapping("/update")
    @Auth(role = "MANAGER")
    @Transactional
    public ApiResponse updateRole(@ApiValidatedModel User user) {
        
        // build a response based on the BindingResult state in the configuration
        ApiResponse response = validationService.buildResponse(user);
        
        // get the persisted user for its encoded password        
        User persistedUser = userRepo.findOne(user.getId());
        if(persistedUser != null) {
            user.setPassword(persistedUser.getPassword());
        }
        
        // all other properties should be on the user from the client request
        
        switch(response.getMeta().getType()){
            case SUCCESS:
            case VALIDATION_INFO:
                logger.info("Updating role for " + user.getEmail());
                user = userRepo.save(user);
                response.getPayload().put(user.getClass().getSimpleName(), user);
                simpMessagingTemplate.convertAndSend("/channel/user", new ApiResponse(SUCCESS, userRepo.findAll()));
                break;
            case VALIDATION_WARNING:
                simpMessagingTemplate.convertAndSend("/channel/user", new ApiResponse(VALIDATION_WARNING, userRepo.findAll()));
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
    
    @ApiMapping("/settings/update")
    @Auth(role = "STUDENT")
    @Transactional
    public ApiResponse updateSetting(@ApiCredentials Credentials shib, @ApiData Map<String, String> userSettings) {
        
        User user = userRepo.findByEmail(shib.getEmail());
        
        user.setSettings(userSettings);
        
        simpMessagingTemplate.convertAndSend("/channel/user/settings/" + user.getId(), new ApiResponse(SUCCESS, userRepo.save(user).getSettings()));
        
        return new ApiResponse(SUCCESS);
    }

}
