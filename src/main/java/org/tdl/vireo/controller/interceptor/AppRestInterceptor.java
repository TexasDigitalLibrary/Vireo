package org.tdl.vireo.controller.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.tdl.vireo.enums.Role;
import org.tdl.vireo.model.User;
import org.tdl.vireo.model.repo.UserRepo;

import edu.tamu.framework.interceptor.CoreRestInterceptor;
import edu.tamu.framework.model.Credentials;

public class AppRestInterceptor extends CoreRestInterceptor {

    @Autowired
    private UserRepo userRepo;
    
    @Value("${app.authority.admins}")
    private String[] admins;
    
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
                    role = Role.ADMINISTRATOR;
                }
            }
            
            userRepo.create(shib.getEmail(), shib.getFirstName(), shib.getLastName(), role);

        }
        else {  
            shib.setRole(user.getRole());
        }
        
        return shib;
    }

}