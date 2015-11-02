package org.tdl.vireo.controller.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.tdl.vireo.enums.Role;
import org.tdl.vireo.model.User;
import org.tdl.vireo.model.repo.UserRepo;

import edu.tamu.framework.interceptor.CoreStompInterceptor;
import edu.tamu.framework.model.Credentials;

@Component
public class AppStompInterceptor extends CoreStompInterceptor {
    
    @Autowired
    private UserRepo userRepo;
    
    @Value("${app.authority.admins}")
    private String[] admins;
    
    @Autowired @Lazy
    private SimpMessagingTemplate simpMessagingTemplate;
    
    @Override
    public Credentials confirmCreateUser(Credentials shib) {
        
        User user = userRepo.findByEmail(shib.getEmail());
        
        if(user == null) {
            
            Role role = Role.USER;
            
            if(shib.getRole() == null) {
                shib.setRole("ROLE_USER");
            }
            String shibEmail = shib.getEmail();            
            for(String email : admins) {
                if(email.equals(shibEmail)) {
                    shib.setRole("ROLE_ADMIN");                 
                }
            }
            
            userRepo.create(shib.getEmail(), shib.getFirstName(), shib.getLastName(), role);
            
            /* For broadcasting list of all users upon new user added
            Map<String, Object> userMap = new HashMap<String, Object>();
            
            userMap.put("list", userRepo.findAll());
            
            this.simpMessagingTemplate.convertAndSend("/channel/users", new ApiResponse(SUCCESS, userMap));
            */
    
        }
        else {
            shib.setRole(user.getRole());
        }
        
        return shib;
        
    }
    
}
