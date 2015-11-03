package org.tdl.vireo.controller;

import static edu.tamu.framework.enums.ApiResponseType.ERROR;
import static edu.tamu.framework.enums.ApiResponseType.REFRESH;
import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.tdl.vireo.model.User;
import org.tdl.vireo.model.repo.UserRepo;

import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.aspect.annotation.Auth;
import edu.tamu.framework.aspect.annotation.Shib;
import edu.tamu.framework.model.ApiResponse;
import edu.tamu.framework.model.Credentials;

@Controller
@ApiMapping("/user")
public class UserController {

    @Autowired
    private UserRepo userRepo;
    
    //private static final Logger logger = Logger.getLogger(UserController.class);
    
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
    
}
