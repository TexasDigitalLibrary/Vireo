package org.tdl.vireo.controller;

import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.aspect.annotation.Auth;
import edu.tamu.framework.model.ApiResponse;

@RestController
@ApiMapping("/settings/look-and-feel")
public class LookAndFeelController {
    
    @Autowired
    private ObjectMapper objectMapper;
    

    @ApiMapping(value = "/logo/upload", method = RequestMethod.POST)
    @Auth(role = "ROLE_MANAGER")
    @Transactional
    public ApiResponse uploadLogo() {
     
//        JsonNode dataNode;
//        try {
//            dataNode = objectMapper.readTree(data);
//        } catch (IOException e) {
//            return new ApiResponse(ERROR, "Unable to parse update json ["+e.getMessage()+"]");
//        }
//        
//        System.out.println(dataNode);
//        System.out.println(inputStream);
        System.out.println("\n\n\n\n\nDFLKJSDLFJSDLKFJLSDKJLKSJDLJDSLSD:SDL:KVJ\n\n\n\n\n");
        return new ApiResponse(SUCCESS, "cool");
        
    }

    
    
}
