package org.tdl.vireo.controller;

import static edu.tamu.framework.enums.ApiResponseType.ERROR;
import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;

import java.io.IOException;

import javax.servlet.ServletInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMethod;
import org.tdl.vireo.config.constant.ConfigurationName;
import org.tdl.vireo.model.Configuration;
import org.tdl.vireo.model.repo.ConfigurationRepo;
import org.tdl.vireo.util.FileIOUtility;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.aspect.annotation.Auth;
import edu.tamu.framework.aspect.annotation.Data;
import edu.tamu.framework.aspect.annotation.InputStream;
import edu.tamu.framework.model.ApiResponse;

@Controller
@ApiMapping("/settings/look-and-feel")
public class LookAndFeelController {
    
    private Logger logger = LoggerFactory.getLogger(this.getClass()); 

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    ConfigurationRepo configurationRepo;

    @Autowired
    private FileIOUtility fileIOUtility;

    @ApiMapping(value = "/logo/upload", method = RequestMethod.POST)
    @Auth(role = "ROLE_MANAGER")
    public ApiResponse uploadLogo(@Data String data, @InputStream ServletInputStream inputStream) {

        JsonNode dataNode = null;
        try {
            dataNode = objectMapper.readTree(data);
        } catch (IOException e) {
            return new ApiResponse(ERROR, "Unable to parse update json [" + e.getMessage() + "]");
        }

        String logoName = dataNode.get("setting").asText();
        String logoFileName = logoName + "." + dataNode.get("type").asText();
        String path = "public/" + configurationRepo.getByName(ConfigurationName.THEME_PATH).getValue() + logoFileName;

        try {
            fileIOUtility.writeImage(inputStream, path);
        } catch (IOException e) {
            e.printStackTrace();
            return new ApiResponse(ERROR, "Unable to write image file. [" + e.getMessage() + "]");
        }

        Configuration newLogoConfig = configurationRepo.createOrUpdate(logoName, path, "lookAndFeel");

        return new ApiResponse(SUCCESS, newLogoConfig);

    }

    @ApiMapping("/logo/reset")
    @Auth(role = "ROLE_MANAGER")
    public ApiResponse resetLogo(@Data String data) {

        JsonNode dataNode = null;
        try {
            dataNode = objectMapper.readTree(data);
        } catch (IOException e) {
            return new ApiResponse(ERROR, "Unable to parse update json [" + e.getMessage() + "]");
        }

        Configuration defaultLogoConfig = configurationRepo.reset(dataNode.get("setting").asText());

        return new ApiResponse(SUCCESS, defaultLogoConfig);

    }

}
