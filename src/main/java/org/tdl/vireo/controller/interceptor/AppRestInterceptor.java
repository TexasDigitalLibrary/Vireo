package org.tdl.vireo.controller.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.tdl.vireo.enums.AppRole;
import org.tdl.vireo.model.User;
import org.tdl.vireo.model.repo.UserRepo;

import edu.tamu.framework.interceptor.CoreRestInterceptor;
import edu.tamu.framework.model.Credentials;

public class AppRestInterceptor extends CoreRestInterceptor {
    
    private Logger logger = LoggerFactory.getLogger(this.getClass()); 

    @Autowired
    private UserRepo userRepo;
    
    @Value("${app.authority.admins}")
    private String[] admins;
    
//    @Autowired @Lazy
//    private SimpMessagingTemplate simpMessagingTemplate;
    
    @Override
    public Credentials confirmCreateUser(Credentials shib) {
        
        User user = userRepo.findByEmail(shib.getEmail());
        
        if(user == null) {
            
            AppRole role = AppRole.STUDENT;
            
            if(shib.getRole() == null) {
                shib.setRole("ROLE_USER");
            }
            String shibEmail = shib.getEmail();
            for(String email : admins) {
                if(email.equals(shibEmail)) {
                    shib.setRole("ROLE_ADMIN");
                    role = AppRole.ADMINISTRATOR;
                }
            }
            
            userRepo.create(shib.getEmail(), shib.getFirstName(), shib.getLastName(), role);
            
//            Map<String, Object> userMap = new HashMap<String, Object>();
//            
//            userMap.put("list", userRepo.findAll());
//            
//            this.simpMessagingTemplate.convertAndSend("/channel/users", new ApiResponse(SUCCESS, userMap));
        }
        else {  
            shib.setRole(user.getRole().toString());
        }
        
        return shib;
    }

}