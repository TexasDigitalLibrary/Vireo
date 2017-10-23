package org.tdl.vireo.controller;

import static edu.tamu.weaver.response.ApiStatus.SUCCESS;
import static edu.tamu.weaver.validation.model.BusinessValidationType.RESET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.tdl.vireo.config.constant.ConfigurationName;
import org.tdl.vireo.controller.model.LookAndFeelControllerModel;
import org.tdl.vireo.model.Configuration;
import org.tdl.vireo.model.ManagedConfiguration;
import org.tdl.vireo.model.repo.ConfigurationRepo;
import org.tdl.vireo.utility.FileIOUtility;

import edu.tamu.weaver.response.ApiResponse;
import edu.tamu.weaver.validation.aspect.annotation.WeaverValidation;

@RestController
@RequestMapping("/settings/look-and-feel")
public class LookAndFeelController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ConfigurationRepo configurationRepo;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private FileIOUtility fileIOUtility;

    private String lookAndFeelType = "lookAndFeel";

    @RequestMapping(value = "/logo/upload", method = RequestMethod.POST)
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse uploadLogo(@RequestBody LookAndFeelControllerModel lfModel, HttpServletRequest request) throws IOException {

        InputStream inputStream = request.getInputStream();

        String logoName = lfModel.getSetting();
        String logoFileName = logoName + "." + lfModel.getFileType();

        // TODO: folder should be a configuration
        String path = "public/" + configurationRepo.getByNameAndType(ConfigurationName.THEME_PATH, lookAndFeelType).getValue() + logoFileName;

        logger.info("Changing logo " + logoName);

        fileIOUtility.write(inputStream, path);

        ManagedConfiguration newLogoConfig = configurationRepo.create(logoName, path, "lookAndFeel");

        simpMessagingTemplate.convertAndSend("/channel/settings/configurable", new ApiResponse(SUCCESS, configurationRepo.findAll()));
        return new ApiResponse(SUCCESS, newLogoConfig);
    }

    @PreAuthorize("hasRole('MANAGER')")
    @RequestMapping(value = "/logo/reset", method = POST)
    @WeaverValidation(business = { @WeaverValidation.Business(value = RESET) })
    public ApiResponse resetLogo(@RequestBody LookAndFeelControllerModel lfModel) {
        logger.info("Resetting logo " + lfModel.getSetting());
        Configuration systemLogo = configurationRepo.getByNameAndType(lfModel.getSetting(), lookAndFeelType);
        Configuration defaultLogoConfig = configurationRepo.reset((ManagedConfiguration) systemLogo);
        simpMessagingTemplate.convertAndSend("/channel/settings/configurable", new ApiResponse(SUCCESS, configurationRepo.findAll()));
        return new ApiResponse(SUCCESS, defaultLogoConfig);
    }
}
