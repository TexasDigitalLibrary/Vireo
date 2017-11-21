package org.tdl.vireo.controller;

import static edu.tamu.weaver.response.ApiStatus.SUCCESS;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.tdl.vireo.config.constant.ConfigurationName;
import org.tdl.vireo.model.Configuration;
import org.tdl.vireo.model.ManagedConfiguration;
import org.tdl.vireo.model.repo.ConfigurationRepo;
import org.tdl.vireo.utility.FileIOUtility;

import edu.tamu.weaver.response.ApiResponse;

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

    @PreAuthorize("hasRole('MANAGER')")
    @RequestMapping(value = "/logo/upload/{setting}/{fileType}", method = RequestMethod.POST)
    public ApiResponse uploadLogo(@PathVariable String setting, @PathVariable String fileType, @RequestParam MultipartFile file) throws IOException {

        String logoFileName = setting + "." + fileType;

        // TODO: folder should be a configuration
        String path = "public/" + configurationRepo.getByNameAndType(ConfigurationName.THEME_PATH, lookAndFeelType).getValue() + logoFileName;

        logger.info("Changing logo " + setting);

        fileIOUtility.write(file.getBytes(), path);

        ManagedConfiguration newLogoConfig = configurationRepo.create(setting, path, "lookAndFeel");

        simpMessagingTemplate.convertAndSend("/channel/settings/configurable", new ApiResponse(SUCCESS, newLogoConfig));
        return new ApiResponse(SUCCESS, newLogoConfig);
    }

    @PreAuthorize("hasRole('MANAGER')")
    @RequestMapping("/logo/reset/{setting}")
    public ApiResponse resetLogo(@PathVariable String setting) {
        logger.info("Resetting logo " + setting);
        Configuration systemLogo = configurationRepo.getByNameAndType(setting, lookAndFeelType);
        Configuration defaultLogoConfig = configurationRepo.reset((ManagedConfiguration) systemLogo);
        simpMessagingTemplate.convertAndSend("/channel/settings/configurable", new ApiResponse(SUCCESS, defaultLogoConfig));
        return new ApiResponse(SUCCESS, defaultLogoConfig);
    }

}
