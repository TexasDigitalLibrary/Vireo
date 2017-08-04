package org.tdl.vireo.controller;

import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;
import static edu.tamu.framework.enums.BusinessValidationType.RESET;

import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMethod;
import org.tdl.vireo.config.constant.ConfigurationName;
import org.tdl.vireo.controller.model.LookAndFeelControllerModel;
import org.tdl.vireo.model.ManagedConfiguration;
import org.tdl.vireo.model.repo.ConfigurationRepo;
import org.tdl.vireo.util.FileIOUtility;

import edu.tamu.framework.aspect.annotation.ApiInputStream;
import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.aspect.annotation.ApiModel;
import edu.tamu.framework.aspect.annotation.ApiValidation;
import edu.tamu.framework.aspect.annotation.Auth;
import edu.tamu.framework.model.ApiResponse;

@Controller
@ApiMapping("/settings/look-and-feel")
public class LookAndFeelController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ConfigurationRepo configurationRepo;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private FileIOUtility fileIOUtility;

    @ApiMapping(value = "/logo/upload", method = RequestMethod.POST)
    @Auth(role = "MANAGER")
    public ApiResponse uploadLogo(@ApiModel LookAndFeelControllerModel lfModel, @ApiInputStream InputStream inputStream) throws IOException {

        String logoName = lfModel.getSetting();
        String logoFileName = logoName + "." + lfModel.getFileType();

        // TODO: folder should be a configuration
        String path = "public/" + configurationRepo.getByName(ConfigurationName.THEME_PATH).getValue() + logoFileName;

        logger.info("Changing logo " + logoName);

        fileIOUtility.write(inputStream, path);

        ManagedConfiguration newLogoConfig = configurationRepo.create(logoName, path, "lookAndFeel");

        simpMessagingTemplate.convertAndSend("/channel/settings/configurable", new ApiResponse(SUCCESS, configurationRepo.findAll()));
        return new ApiResponse(SUCCESS, newLogoConfig);
    }

    @ApiMapping("/logo/reset")
    @Auth(role = "MANAGER")
    @ApiValidation(business = { @ApiValidation.Business(value = RESET) })
    public ApiResponse resetLogo(@ApiModel LookAndFeelControllerModel lfModel) {
        logger.info("Resetting logo " + lfModel.getSetting());
        ManagedConfiguration systemLogo = configurationRepo.getByName(lfModel.getSetting());
        ManagedConfiguration defaultLogoConfig = configurationRepo.reset(systemLogo);
        simpMessagingTemplate.convertAndSend("/channel/settings/configurable", new ApiResponse(SUCCESS, configurationRepo.findAll()));
        return new ApiResponse(SUCCESS, defaultLogoConfig);
    }
}
