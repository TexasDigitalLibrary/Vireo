package org.tdl.vireo.controller;

import static edu.tamu.weaver.response.ApiStatus.SUCCESS;
import static edu.tamu.weaver.validation.model.BusinessValidationType.UPDATE;
import static org.springframework.beans.BeanUtils.copyProperties;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import org.tdl.vireo.model.User;
import org.tdl.vireo.model.repo.UserRepo;
import org.tdl.vireo.service.UserCredentialsService;

import edu.tamu.framework.aspect.annotation.ApiCredentials;
import edu.tamu.framework.aspect.annotation.ApiData;
import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.aspect.annotation.Auth;
import edu.tamu.framework.model.Credentials;
import edu.tamu.weaver.response.ApiResponse;
import edu.tamu.weaver.validation.aspect.annotation.WeaverValidatedModel;
import edu.tamu.weaver.validation.aspect.annotation.WeaverValidation;

@RestController
@ApiMapping("/user")
public class UserController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private UserCredentialsService userCredentialsService;

    @ApiMapping("/credentials")
    @Auth(role = "NONE")
    public ApiResponse credentials(@ApiCredentials Credentials credentials) {
        User user = userRepo.findByEmail(credentials.getEmail());
        if (user == null) {
            logger.debug("User not registered! Responding with anonymous credentials!");
            return new ApiResponse(SUCCESS, userCredentialsService.buildAnonymousCredentials());
        }
        credentials.setRole(user.getRole().toString());
        return new ApiResponse(SUCCESS, credentials);
    }

    @Transactional
    @ApiMapping("/all")
    @Auth(role = "MANAGER")
    public ApiResponse allUsers() {
        return new ApiResponse(SUCCESS, userRepo.findAll());
    }

    @Auth(role = "MANAGER")
    @ApiMapping(value = "/update", method = POST)
    @WeaverValidation(business = { @WeaverValidation.Business(value = UPDATE) })
    public ApiResponse updateRole(@WeaverValidatedModel User updatedUser) {

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

    @Auth(role = "STUDENT")
    @ApiMapping(value = "/settings/update", method = POST)
    public ApiResponse updateSetting(@ApiCredentials Credentials shib, @ApiData Map<String, String> userSettings) {
        User user = userRepo.findByEmail(shib.getEmail());
        user.setSettings(userSettings);
        simpMessagingTemplate.convertAndSend("/channel/user/settings/" + user.getId(), new ApiResponse(SUCCESS, userRepo.save(user).getSettings()));
        return new ApiResponse(SUCCESS);
    }

}
