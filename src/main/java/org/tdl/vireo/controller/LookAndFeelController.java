package org.tdl.vireo.controller;

import static edu.tamu.framework.enums.ApiResponseType.ERROR;
import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.tdl.vireo.config.constant.ConfigurationName;
import org.tdl.vireo.model.Configuration;
import org.tdl.vireo.model.repo.ConfigurationRepo;
import org.tdl.vireo.util.HashedFile;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.aspect.annotation.Auth;
import edu.tamu.framework.aspect.annotation.Data;
import edu.tamu.framework.model.ApiResponse;

@Controller
@ApiMapping("/settings/look-and-feel")
public class LookAndFeelController {
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    ConfigurationRepo configurationRepo;
    
    @Autowired
    private HashedFile hashedFile;    

    @ApiMapping("/logo/upload")
    @Auth(role = "ROLE_MANAGER")
    public ApiResponse uploadLogo(@Data String data) {
     
        JsonNode dataNode = null;
        try {
            dataNode = objectMapper.readTree(data);
        } catch (IOException e) {
            return new ApiResponse(ERROR, "Unable to parse update json ["+e.getMessage()+"]");
        }
        
        String dataUri = dataNode.get("file").asText();
        String encodingPrefix = "base64,";
        int contentStartIndex = dataUri.indexOf(encodingPrefix) + encodingPrefix.length();
        byte[] imageData = Base64.decodeBase64(dataUri.substring(contentStartIndex));
        
        String type = dataNode.get("type").asText();
        String setting = dataNode.get("setting").asText();
        String newPath = setting+"."+type;
        
        BufferedImage imageBuffer = null;
        try {
            imageBuffer = ImageIO.read(new ByteArrayInputStream(imageData));
        } catch (IOException e) {
            return new ApiResponse(ERROR, "Unable to buffer image data. ["+e.getMessage()+"]");
        }
        
        File outputfile = new File(hashedFile.getStore().getAbsolutePath()+"/"+newPath);
        try {
            ImageIO.write(imageBuffer, type, outputfile);
        } catch (IOException e) {
            return new ApiResponse(ERROR, "Unable to write image file. ["+e.getMessage()+"]");
        }
        
        Configuration newLogoConfig = configurationRepo.createOrUpdate(setting,configurationRepo.getByName(ConfigurationName.APPLICATION_ATTACHMENTS_PATH)+newPath,"lookAndFeel");
        
        return new ApiResponse(SUCCESS, newLogoConfig);
        
    }
    
    @ApiMapping("/logo/reset")
    @Auth(role = "ROLE_MANAGER")
    public ApiResponse resetLogo(@Data String data) {
     
        JsonNode dataNode = null;
        try {
            dataNode = objectMapper.readTree(data);
        } catch (IOException e) {
            return new ApiResponse(ERROR, "Unable to parse update json ["+e.getMessage()+"]");
        }
                
        Configuration defaultLogoConfig = configurationRepo.reset(dataNode.get("setting").asText());
        
        return new ApiResponse(SUCCESS, defaultLogoConfig);
        
    }

    
    
}
