package org.tdl.vireo.controller;

import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;
import static edu.tamu.framework.enums.BusinessValidationType.NONEXISTS;
import static edu.tamu.framework.enums.BusinessValidationType.UPDATE;

import static org.springframework.beans.BeanUtils.copyProperties;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.tdl.vireo.model.User;
import org.tdl.vireo.model.repo.UserRepo;

import edu.tamu.framework.aspect.annotation.ApiCredentials;
import edu.tamu.framework.aspect.annotation.ApiData;
import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.aspect.annotation.ApiValidatedModel;
import edu.tamu.framework.aspect.annotation.ApiValidation;
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
        shib.setModelValidator(user.getModelValidator());
        return new ApiResponse(SUCCESS, shib);
    }

    @Transactional
    @ApiMapping("/all")
    @Auth(role = "MANAGER")
    public ApiResponse allUsers() {
        return new ApiResponse(SUCCESS, userRepo.findAll());
    }

    @ApiMapping("/update")
    @Auth(role = "MANAGER")
    @ApiValidation(business = { @ApiValidation.Business(value = UPDATE), @ApiValidation.Business(value = NONEXISTS) })
    public ApiResponse updateRole(@ApiValidatedModel User updatedUser) {

    	User persistedUser = userRepo.findOne(updatedUser.getId());
    	// Awesome BeanUtils from Apache commons, included with Spring
    	// copy properties from source, arg1, to destination, arg2, excluding ..., arg3
    	copyProperties(updatedUser, persistedUser, "password", "activeFilter");

        logger.info("Updating role for " + persistedUser.getEmail());
        persistedUser = userRepo.save(persistedUser);

        simpMessagingTemplate.convertAndSend("/channel/user", new ApiResponse(SUCCESS, userRepo.findAll()));

        return new ApiResponse(SUCCESS, persistedUser);
    }

    @ApiMapping("/settings")
    @Auth(role = "STUDENT")
    public ApiResponse getSettings(@ApiCredentials Credentials shib) {
        User user = userRepo.findByEmail(shib.getEmail());
        return new ApiResponse(SUCCESS, user.getSettings());
    }

    @ApiMapping("/settings/update")
    @Auth(role = "STUDENT")
    public ApiResponse updateSetting(@ApiCredentials Credentials shib, @ApiData Map<String, String> userSettings) {
        User user = userRepo.findByEmail(shib.getEmail());
        user.setSettings(userSettings);
        simpMessagingTemplate.convertAndSend("/channel/user/settings/" + user.getId(), new ApiResponse(SUCCESS, userRepo.save(user).getSettings()));
        return new ApiResponse(SUCCESS);
    }

}
