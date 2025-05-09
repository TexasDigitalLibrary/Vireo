package org.tdl.vireo.controller;

import static edu.tamu.weaver.response.ApiStatus.SUCCESS;
import static edu.tamu.weaver.validation.model.BusinessValidationType.UPDATE;
import static org.springframework.beans.BeanUtils.copyProperties;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.tdl.vireo.model.Role;
import org.tdl.vireo.model.User;
import org.tdl.vireo.model.repo.UserRepo;
import org.tdl.vireo.model.request.FilteredPageRequest;

import edu.tamu.weaver.auth.annotation.WeaverCredentials;
import edu.tamu.weaver.auth.annotation.WeaverUser;
import edu.tamu.weaver.auth.model.Credentials;
import edu.tamu.weaver.response.ApiResponse;
import edu.tamu.weaver.user.model.IRole;
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

    @RequestMapping("/all")
    @PreAuthorize("hasRole('ROLE_REVIEWER')")
    public ApiResponse allUsers() {
        return new ApiResponse(SUCCESS, userRepo.findAll());
    }

    @RequestMapping("/page")
    @PreAuthorize("hasRole('ROLE_REVIEWER')")
    public ApiResponse page(@RequestBody FilteredPageRequest filteredPageRequest) {
        return new ApiResponse(SUCCESS, userRepo.findAll(filteredPageRequest.getUserSpecification(), filteredPageRequest.getPageRequest()));
    }

    @GetMapping("/assignable")
    @PreAuthorize("hasRole('ROLE_REVIEWER')")
    public ApiResponse allAssignableUsers(@RequestParam(defaultValue = "0", name = "size") Integer size, @RequestParam(defaultValue = "", name = "name") String name, @PageableDefault(direction = Direction.ASC, sort = { "firstName", "LastName" }) Pageable pageable) {
        List<IRole> roles = Arrays.asList(new IRole[] {
            Role.ROLE_ADMIN,
            Role.ROLE_MANAGER,
            Role.ROLE_REVIEWER
        });

        // Pageable's size is not allowed to be 0, but by adding a requestParam the 0 can be intercepted and used for disabling pagination.
        if (size == 0) {
            if (StringUtils.isEmpty(name)) {
                return new ApiResponse(SUCCESS, userRepo.findAllByRoleIn(roles, pageable.getSort()));
            }

            return new ApiResponse(SUCCESS, userRepo.findAllByRoleInAndNameContainsIgnoreCase(roles, name, pageable.getSort()));
        }

        if (StringUtils.isEmpty(name)) {
            return new ApiResponse(SUCCESS, userRepo.findAllByRoleIn(roles, pageable));
        }

        return new ApiResponse(SUCCESS, userRepo.findAllByRoleInAndNameContainsIgnoreCase(roles, name, pageable));
    }

    @GetMapping("/assignable/total")
    @PreAuthorize("hasRole('ROLE_REVIEWER')")
    public ApiResponse countAssignableUsers(@RequestParam(defaultValue = "", name = "name") String name) {
        List<IRole> roles = Arrays.asList(new IRole[] {
            Role.ROLE_ADMIN,
            Role.ROLE_MANAGER,
            Role.ROLE_REVIEWER
        });

        if (StringUtils.isEmpty(name)) {
            return new ApiResponse(SUCCESS, userRepo.countByRoleIn(roles));
        }

        return new ApiResponse(SUCCESS, userRepo.countByRoleInAndNameContainsIgnoreCase(roles, name));
    }

    @GetMapping("/unassignable")
    @PreAuthorize("hasRole('ROLE_REVIEWER')")
    public ApiResponse allUnassignableUsers(@RequestParam(defaultValue = "0", name = "size") Integer size, @RequestParam(defaultValue = "", name = "name") String name, @PageableDefault(direction = Direction.ASC, sort = { "firstName", "LastName" }) Pageable pageable) {
        List<IRole> roles = Arrays.asList(new IRole[] {
            Role.ROLE_STUDENT,
            Role.ROLE_ANONYMOUS
        });

        // Pageable's size is not allowed to be 0, but by adding a requestParam the 0 can be intercepted and used for disabling pagination.
        if (size == 0) {
            if (StringUtils.isEmpty(name)) {
                return new ApiResponse(SUCCESS, userRepo.findAllByRoleIn(roles, pageable.getSort()));
            }

            return new ApiResponse(SUCCESS, userRepo.findAllByRoleInAndNameContainsIgnoreCase(roles, name, pageable.getSort()));
        }

        if (StringUtils.isEmpty(name)) {
            return new ApiResponse(SUCCESS, userRepo.findAllByRoleIn(roles, pageable));
        }

        return new ApiResponse(SUCCESS, userRepo.findAllByRoleInAndNameContainsIgnoreCase(roles, name, pageable));
    }

    @GetMapping("/unassignable/total")
    @PreAuthorize("hasRole('ROLE_REVIEWER')")
    public ApiResponse countUnassignableUsers(@RequestParam(defaultValue = "", name = "name") String name) {
        List<IRole> roles = Arrays.asList(new IRole[] {
            Role.ROLE_STUDENT,
            Role.ROLE_ANONYMOUS
        });

        if (StringUtils.isEmpty(name)) {
            return new ApiResponse(SUCCESS, userRepo.countByRoleIn(roles));
        }

        return new ApiResponse(SUCCESS, userRepo.countByRoleInAndNameContainsIgnoreCase(roles, name));
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @RequestMapping(value = "/update", method = POST)
    @WeaverValidation(business = { @WeaverValidation.Business(value = UPDATE) })
    public ApiResponse update(@WeaverValidatedModel User updatedUser) {

        User persistedUser = userRepo.findById(updatedUser.getId()).get();
        // Awesome BeanUtils from Apache commons, included with Spring
        // copy properties from source, arg1, to destination, arg2, excluding ..., arg3
        copyProperties(updatedUser, persistedUser, "password", "activeFilter", "savedFilters");

        LOG.info("Updating user with email " + persistedUser.getEmail());
        persistedUser = userRepo.update(persistedUser);
        LOG.info("Successfully updated user with email " + persistedUser.getEmail());

        return new ApiResponse(SUCCESS, persistedUser);
    }

    @RequestMapping("/settings")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public ApiResponse getSettings(@WeaverUser User user) {
        return new ApiResponse(SUCCESS, user.getSettings());
    }

    @PreAuthorize("hasRole('ROLE_STUDENT')")
    @RequestMapping(value = "/settings/update", method = POST)
    public ApiResponse updateSetting(@WeaverUser User user, @RequestBody Map<String, String> userSettings) {
        user.setSettings(userSettings);
        user = userRepo.update(user);
        simpMessagingTemplate.convertAndSend("/channel/user/settings/" + user.getId(), new ApiResponse(SUCCESS, user.getSettings()));
        return new ApiResponse(SUCCESS);
    }

}
