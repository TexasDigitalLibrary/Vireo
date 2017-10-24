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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tdl.vireo.model.User;
import org.tdl.vireo.model.repo.UserRepo;

import edu.tamu.weaver.auth.annotation.WeaverCredentials;
import edu.tamu.weaver.auth.model.Credentials;
import edu.tamu.weaver.response.ApiResponse;
import edu.tamu.weaver.validation.aspect.annotation.WeaverValidatedModel;
import edu.tamu.weaver.validation.aspect.annotation.WeaverValidation;

@RestController
@RequestMapping("/user")
public class UserController {

    private final static Logger LOG = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @RequestMapping("/credentials")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public ApiResponse credentials(@WeaverCredentials Credentials credentials) {
        return new ApiResponse(SUCCESS, credentials);
    }

    @Transactional
    @RequestMapping("/all")
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    public ApiResponse allUsers() {
        return new ApiResponse(SUCCESS, userRepo.findAll());
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @RequestMapping(value = "/update", method = POST)
    @WeaverValidation(business = { @WeaverValidation.Business(value = UPDATE) })
    public ApiResponse updateRole(@WeaverValidatedModel User updatedUser) {

        User persistedUser = userRepo.findOne(updatedUser.getId());
        // Awesome BeanUtils from Apache commons, included with Spring
        // copy properties from source, arg1, to destination, arg2, excluding ..., arg3
        copyProperties(updatedUser, persistedUser, "password", "activeFilter");

        LOG.info("Updating role for " + persistedUser.getEmail());
        persistedUser = userRepo.save(persistedUser);

        userRepo.broadcast(userRepo.findAll());

        return new ApiResponse(SUCCESS, persistedUser);
    }

    @RequestMapping("/settings")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public ApiResponse getSettings(@WeaverCredentials Credentials shib) {
        User user = userRepo.findByEmail(shib.getEmail());
        return new ApiResponse(SUCCESS, user.getSettings());
    }

    @PreAuthorize("hasRole('ROLE_STUDENT')")
    @RequestMapping(value = "/settings/update", method = POST)
    public ApiResponse updateSetting(@WeaverCredentials Credentials shib, @RequestBody Map<String, String> userSettings) {
        User user = userRepo.findByEmail(shib.getEmail());
        user.setSettings(userSettings);

        userRepo.update(user);

        simpMessagingTemplate.convertAndSend("/channel/user/settings/" + user.getId(), new ApiResponse(SUCCESS, userRepo.save(user).getSettings()));
        return new ApiResponse(SUCCESS);
    }

}
