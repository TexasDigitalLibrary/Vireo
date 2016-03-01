package org.tdl.vireo.controller;

import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.tdl.vireo.enums.Role;
import org.tdl.vireo.model.User;
import org.tdl.vireo.model.repo.UserRepo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.aspect.annotation.Auth;
import edu.tamu.framework.aspect.annotation.Data;
import edu.tamu.framework.model.ApiResponse;

@Controller
@ApiMapping("/admin")
public class AdminController {
    
    @Autowired
    private UserRepo userRepo;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired 
    private SimpMessagingTemplate simpMessagingTemplate;
    
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
        
    @Transactional
    @ApiMapping("/add-access")
    @Auth(role = "ROLE_ADMIN")
    public ApiResponse addAccess(@Data String data){
        JsonNode dataNode = null;

        try {
            dataNode = objectMapper.readTree(data);

            String passedRole = dataNode.get("role").asText();
            if (passedRole.equals("ROLE_STUDENT")) {
                User userWithSameId = userRepo.findOne(dataNode.get("id").asLong());
                userWithSameId.setRole(Role.REVIEWER);
                userRepo.save(userWithSameId);
            }
        }
        catch (Throwable e) {
            System.out.println("Error " + e.getMessage());
            e.printStackTrace();
        }
        Map<String, Object> userMap = new HashMap<String, Object>();
        userMap.put("list", userRepo.findAll());

        this.simpMessagingTemplate.convertAndSend("/channel/users", new ApiResponse(SUCCESS, userMap));
        return new ApiResponse(SUCCESS);
    }
}
