package org.tdl.vireo.controller;

import static edu.tamu.framework.enums.ApiResponseType.ERROR;
import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.tdl.vireo.model.Organization;
import org.tdl.vireo.model.User;
import org.tdl.vireo.model.repo.OrganizationRepo;
import org.tdl.vireo.model.repo.UserRepo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.aspect.annotation.ApiVariable;
import edu.tamu.framework.aspect.annotation.Auth;
import edu.tamu.framework.aspect.annotation.Data;
import edu.tamu.framework.aspect.annotation.Shib;
import edu.tamu.framework.model.ApiResponse;
import edu.tamu.framework.model.Credentials;

@Controller
@ApiMapping("/organization")
public class OrganizationController {
    
    @Autowired
    private OrganizationRepo organizationRepo;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired 
    private SimpMessagingTemplate simpMessagingTemplate;
    
    private static final Logger logger = Logger.getLogger(OrganizationController.class);
        
    @ApiMapping("/all")
    @Auth(role="ROLE_MANAGER")
    public ApiResponse allOrganizations() {
        Map<String,List<Organization>> map = new HashMap<String,List<Organization>>();        
        map.put("list", organizationRepo.findAll());
        return new ApiResponse(SUCCESS, map);
    }

    @ApiMapping("/create")
    @Auth(role="ROLE_MANAGER")
    public ApiResponse createOrganization(@Data String data) {
        Map<String,String> map = new HashMap<String,String>();      
        try {
            map = objectMapper.readValue(data, new TypeReference<HashMap<String,String>>(){});
            System.out.println(map.get("newOrganization").toString());
        } catch (Exception e) {
            e.printStackTrace();
        } 
        return new ApiResponse(SUCCESS, map);
    }
}
