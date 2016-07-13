package org.tdl.vireo.controller;

import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;
import static edu.tamu.framework.enums.BusinessValidationType.NONEXISTS;
import static edu.tamu.framework.enums.BusinessValidationType.RESET;
import static edu.tamu.framework.enums.BusinessValidationType.UPDATE;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.tdl.vireo.model.Configuration;
import org.tdl.vireo.model.repo.ConfigurationRepo;

import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.aspect.annotation.ApiValidatedModel;
import edu.tamu.framework.aspect.annotation.ApiValidation;
import edu.tamu.framework.model.ApiResponse;

@Controller
@ApiMapping("/settings/configurable")
public class ConfigurableSettingsController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ConfigurationRepo configurationRepo;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @ApiMapping("/all")
    public ApiResponse getSettings() {
        return new ApiResponse(SUCCESS, configurationRepo.findAll());
    }

    @Transactional
    @ApiMapping("/update")
    @ApiValidation(business = { @ApiValidation.Business(value = UPDATE), @ApiValidation.Business(value = NONEXISTS) })
    public ApiResponse updateSetting(@ApiValidatedModel Configuration configuration) {
        logger.info("Updating configuration with name " + configuration.getName() + " and value " + configuration.getValue());
        configuration = configurationRepo.save(configuration);
        simpMessagingTemplate.convertAndSend("/channel/settings/configurable", new ApiResponse(SUCCESS, configurationRepo.findAll()));
        return new ApiResponse(SUCCESS, configuration);
    }

    @Transactional
    @ApiMapping("/reset")
    @ApiValidation(business = { @ApiValidation.Business(value = RESET) })
    public ApiResponse resetSetting(@ApiValidatedModel Configuration configuration) {
        logger.info("Resetting configuration with name " + configuration.getName() + " and value " + configuration.getValue());
        configuration = configurationRepo.reset(configuration);
        simpMessagingTemplate.convertAndSend("/channel/settings/configurable", new ApiResponse(SUCCESS, configurationRepo.findAll()));
        return new ApiResponse(SUCCESS, configuration);
    }

}
