package org.tdl.vireo.controller;

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
import org.tdl.vireo.service.ValidationService;

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
    private ConfigurationRepo configurationRepo;
    
    @Autowired
    private ValidationService validationService;

    @ApiMapping(value = "/logo/upload", method = RequestMethod.POST)
    @Auth(role = "MANAGER")
    public ApiResponse uploadLogo(@ApiValidatedModel LookAndFeelControllerModel lfModel, @InputStream ServletInputStream inputStream) {
        
        String logoName = lfModel.getSetting();
        String logoFileName = logoName + "." + lfModel.getFileType();
        String path = "public/" + configurationRepo.getByName(ConfigurationName.THEME_PATH).getValue() + logoFileName;

        // will attach any errors to the BindingResult when validating the incoming lfModel
        lfModel = configurationRepo.validateUploadLogo(lfModel, inputStream, path);
        
        // build a response based on the BindingResult state
        ApiResponse response = validationService.buildResponse(lfModel);
        
        switch(response.getMeta().getType()){
            case SUCCESS:
            case VALIDATION_INFO:
                logger.info("Changing logo " + logoName);
                Configuration newLogoConfig = configurationRepo.create(logoName, path, "lookAndFeel");
                response.getPayload().put(newLogoConfig.getClass().getSimpleName(), newLogoConfig);
                //simpMessagingTemplate.convertAndSend("/channel/settings/languages", new ApiResponse(SUCCESS, getAll()));
                break;
            case VALIDATION_WARNING:
                //simpMessagingTemplate.convertAndSend("/channel/settings/languages", new ApiResponse(VALIDATION_WARNING, getAll()));
                break;
            default:
                logger.warn("Couldn't update logo " + logoName + " because: " + response.getMeta().getType());
                break;
        }

        return response;
    }

    @ApiMapping("/logo/reset")
    @Auth(role = "MANAGER")
    public ApiResponse resetLogo(@ApiValidatedModel LookAndFeelControllerModel lfModel) {
        
        // will attach any errors to the BindingResult when validating the incoming lfModel
        lfModel = configurationRepo.validateResetLogo(lfModel);
        
        // build a response based on the BindingResult state in the configuration
        ApiResponse response = validationService.buildResponse(lfModel);
        
        switch(response.getMeta().getType()){
            case SUCCESS:
            case VALIDATION_INFO:
                logger.info("Resetting logo " + lfModel.getSetting());
                Configuration defaultLogoConfig = configurationRepo.reset(lfModel.getSetting());
                response.getPayload().put(defaultLogoConfig.getClass().getSimpleName(), defaultLogoConfig);
                //simpMessagingTemplate.convertAndSend("/channel/settings/configurable", new ApiResponse(SUCCESS, toConfigPairsMap(configurationRepo.getAllByType(configuration.getType()))));
                break;
            case VALIDATION_WARNING:
                //simpMessagingTemplate.convertAndSend("/channel/settings/configurable", new ApiResponse(VALIDATION_WARNING, toConfigPairsMap(configurationRepo.getAllByType(configuration.getType()))));
                break;
            default:
                logger.warn("Couldn't reset logo with name " + lfModel.getSetting());
                break;
        }
        
        return response;
    }
}
