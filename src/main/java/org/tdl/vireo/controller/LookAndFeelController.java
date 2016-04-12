package org.tdl.vireo.controller;

import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;
import static edu.tamu.framework.enums.ApiResponseType.VALIDATION_ERROR;

import java.io.IOException;

import javax.servlet.ServletInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMethod;
import org.tdl.vireo.config.constant.ConfigurationName;
import org.tdl.vireo.controller.model.LookAndFeelControllerModel;
import org.tdl.vireo.model.Configuration;
import org.tdl.vireo.model.repo.ConfigurationRepo;
import org.tdl.vireo.util.FileIOUtility;

import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.aspect.annotation.ApiValidatedModel;
import edu.tamu.framework.aspect.annotation.Auth;
import edu.tamu.framework.aspect.annotation.InputStream;
import edu.tamu.framework.model.ApiResponse;

@Controller
@ApiMapping("/settings/look-and-feel")
public class LookAndFeelController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ConfigurationRepo configurationRepo;

    @Autowired
    private FileIOUtility fileIOUtility;

    @ApiMapping(value = "/logo/upload", method = RequestMethod.POST)
    @Auth(role = "ROLE_MANAGER")
    public ApiResponse uploadLogo(@ApiValidatedModel LookAndFeelControllerModel lfModel, @InputStream ServletInputStream inputStream) {

        // TODO: this needs to go in repo.validateCreate() -- VIR-201
        if (lfModel.getBindingResult().hasErrors()) {
            return new ApiResponse(VALIDATION_ERROR, lfModel.getBindingResult().getAll());
        }

        String logoName = lfModel.getSetting();
        String logoFileName = logoName + "." + lfModel.getFileType();
        String path = "public/" + configurationRepo.getByName(ConfigurationName.THEME_PATH).getValue() + logoFileName;

        try {
            fileIOUtility.writeImage(inputStream, path);
        } catch (IOException e) {
            e.printStackTrace();
            return new ApiResponse(VALIDATION_ERROR, "Unable to write image file. [" + e.getMessage() + "]");
        }

        Configuration newLogoConfig = configurationRepo.createOrUpdate(logoName, path, "lookAndFeel");

        logger.info("Changing logo " + newLogoConfig.getName());

        return new ApiResponse(SUCCESS, newLogoConfig);

    }

    @ApiMapping("/logo/reset")
    @Auth(role = "ROLE_MANAGER")
    public ApiResponse resetLogo(@ApiValidatedModel LookAndFeelControllerModel lfModel) {

        // TODO: this needs to go in repo.validateCreate() -- VIR-201
        if (lfModel.getBindingResult().hasErrors()) {
            return new ApiResponse(VALIDATION_ERROR, lfModel.getBindingResult().getAll());
        }

        Configuration defaultLogoConfig = configurationRepo.reset(lfModel.getSetting());

        logger.info("resetting logo " + defaultLogoConfig.getName());

        return new ApiResponse(SUCCESS, defaultLogoConfig);

    }
}
