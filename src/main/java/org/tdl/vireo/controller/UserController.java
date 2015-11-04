package org.tdl.vireo.controller;

import static edu.tamu.framework.enums.ApiResponseType.ERROR;
import static edu.tamu.framework.enums.ApiResponseType.REFRESH;
import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.tdl.vireo.model.User;
import org.tdl.vireo.model.repo.UserRepo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.aspect.annotation.Auth;
import edu.tamu.framework.aspect.annotation.Data;
import edu.tamu.framework.aspect.annotation.Parameters;
import edu.tamu.framework.aspect.annotation.Shib;
import edu.tamu.framework.model.ApiResponse;
import edu.tamu.framework.model.Credentials;
import edu.tamu.framework.util.EmailUtility;

@Controller
@ApiMapping("/user")
public class UserController {

    @Autowired
    private UserRepo userRepo;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired 
    private SimpMessagingTemplate simpMessagingTemplate;
    
    @Autowired
    private EmailUtility emailUtility;
    
    //private static final Logger logger = Logger.getLogger(UserController.class);
    
    @ApiMapping(value = "/register")
    public ApiResponse registration() {
        
        System.out.println("\n\nHERE\n\n");
        
//        if(dataMap.get("action").equals("VERIFY_EMAIL")) {
//            
//            //TODO: use email template. create VireoEmail service to use template with parameters
//            
//            String subject = "Vireo 4 Registration";
//            String content = "Email verification\n\n";
//            try {
//                emailUtility.sendEmail(dataMap.get("email"), subject, content);
//            } catch (MessagingException e) {
//                return new ApiResponse(ERROR, "Could not send email! " + dataMap.get("email"));
//            }
//        }
        
        
        return new ApiResponse(SUCCESS);
    }
    
    @ApiMapping("/login")
    public ApiResponse login(@Data String data) {
        
        Map<String,String> map = new HashMap<String,String>();      
        try {
            map = objectMapper.readValue(data, new TypeReference<HashMap<String,String>>(){});
        } catch (Exception e) {
            e.printStackTrace();
        }  
        
        
        
        return new ApiResponse(SUCCESS);
    }
    
    @ApiMapping("/credentials")
    @Auth
    public ApiResponse credentials(@Shib Object credentials) {
        
        Credentials shib = (Credentials) credentials;
        
        User user = userRepo.findByEmail(shib.getEmail());
        
        if(user == null) {
            return new ApiResponse(ERROR, "user not registered");
        }

        shib.setRole(user.getRole());
        
        return shib != null ? new ApiResponse(SUCCESS, shib) : new ApiResponse(REFRESH, "EXPIRED_JWT");
    }
    
    @ApiMapping("/all")
    @Auth(role="ROLE_MANAGER")
    @Transactional
    public ApiResponse allUsers() {            
        Map<String,List<User>> map = new HashMap<String,List<User>>();        
        map.put("list", userRepo.findAll());
        return new ApiResponse(SUCCESS, map);
    }
    
    @ApiMapping("/update-role")
    @Auth(role="ROLE_MANAGER")
    @Transactional
    public ApiResponse updateRole(@Data String data) throws Exception {        
        
        Map<String,String> map = new HashMap<String,String>();      
        try {
            map = objectMapper.readValue(data, new TypeReference<HashMap<String,String>>(){});
        } catch (Exception e) {
            e.printStackTrace();
        }       
        
        User user = userRepo.findByEmail(map.get("email"));
        user.setRole(map.get("role"));      
        userRepo.save(user);
        
        Map<String, Object> userMap = new HashMap<String, Object>();
        userMap.put("list", userRepo.findAll());
        userMap.put("changedUserEmail", map.get("email"));
        
        this.simpMessagingTemplate.convertAndSend("/channel/users", new ApiResponse(SUCCESS, userMap));
        
        return new ApiResponse(SUCCESS);
    }
    
}
