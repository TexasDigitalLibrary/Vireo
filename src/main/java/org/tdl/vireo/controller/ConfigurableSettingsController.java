package org.tdl.vireo.controller;

import static edu.tamu.weaver.response.ApiStatus.SUCCESS;
import static edu.tamu.weaver.validation.model.BusinessValidationType.UPDATE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tdl.vireo.model.Configuration;
import org.tdl.vireo.model.ManagedConfiguration;
import org.tdl.vireo.model.repo.ConfigurationRepo;

import edu.tamu.weaver.response.ApiResponse;
import edu.tamu.weaver.validation.aspect.annotation.WeaverValidatedModel;
import edu.tamu.weaver.validation.aspect.annotation.WeaverValidation;

@RestController
@RequestMapping("/settings/configurable")
public class ConfigurableSettingsController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ConfigurationRepo configurationRepo;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @RequestMapping("/all")
    public ApiResponse getSettings() {
        return new ApiResponse(SUCCESS, configurationRepo.getCurrentConfigurations());
    }

    @PreAuthorize("hasRole('MANAGER')")
    @RequestMapping(value = "/update", method = POST)
    @WeaverValidation(business = { @WeaverValidation.Business(value = UPDATE) })
    public ApiResponse updateSetting(@WeaverValidatedModel ManagedConfiguration configuration) {
        logger.info("Updating configuration with name " + configuration.getName() + " and value " + configuration.getValue());
        return new ApiResponse(SUCCESS, configurationRepo.create(configuration));
    }

    @PreAuthorize("hasRole('MANAGER')")
    @RequestMapping(value = "/reset", method = POST)
    public ApiResponse resetSetting(@RequestBody ManagedConfiguration configuration) {
        logger.info("Resetting configuration with name " + configuration.getName() + " and value " + configuration.getValue());
        Configuration defaultConfiguration = configurationRepo.reset(configuration);
        simpMessagingTemplate.convertAndSend("/channel/settings/configurable", new ApiResponse(SUCCESS, defaultConfiguration));
        return new ApiResponse(SUCCESS, defaultConfiguration);
    }

}
