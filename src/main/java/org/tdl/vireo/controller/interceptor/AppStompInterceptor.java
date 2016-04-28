package org.tdl.vireo.controller.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.tdl.vireo.config.constant.ConfigurationName;
import org.tdl.vireo.enums.AppRole;
import org.tdl.vireo.model.User;
import org.tdl.vireo.model.repo.UserRepo;

import edu.tamu.framework.interceptor.CoreStompInterceptor;
import edu.tamu.framework.model.Credentials;

@Component
public class AppStompInterceptor extends CoreStompInterceptor {
    
    private Logger logger = LoggerFactory.getLogger(this.getClass()); 

    @Autowired
    private UserRepo userRepo;

    @Value("${app.authority.admins}")
    private String[] admins;

    // @Autowired @Lazy
    // private SimpMessagingTemplate simpMessagingTemplate;

    @Override
    public Credentials confirmCreateUser(Credentials shib) {

        User user = userRepo.findByEmail(shib.getEmail());

        if (user == null) {

            AppRole role = AppRole.STUDENT;

            if (shib.getRole() == null) {
                shib.setRole(role.toString());
            }
            String shibEmail = shib.getEmail();
            for (String email : admins) {
                if (email.equals(shibEmail)) {
                    role = AppRole.ADMINISTRATOR;
                    shib.setRole(role.toString());
                }
            }

            // User newUser =
            user = userRepo.create(shib.getEmail(), shib.getFirstName(), shib.getLastName(), role);
            user.setNetid(shib.getAllCredentials().get(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_NETID));
            if (shib.getAllCredentials().get(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_BIRTH_YEAR) != null) {
                user.setBirthYear(Integer.parseInt(shib.getAllCredentials().get(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_BIRTH_YEAR)));
            }
            user.setMiddleName(shib.getAllCredentials().get(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_MIDDLE_NAME));
            user.setOrcid(shib.getAllCredentials().get(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_ORCID));
            if (shib.getAllCredentials().get(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_BIRTH_YEAR) != null) {
                user.setUin(Long.parseLong(shib.getAllCredentials().get(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_INSTITUTIONAL_IDENTIFIER)));
            }
            userRepo.save(user);
            // Map<String, Object> userMap = new HashMap<String, Object>();
            //
            // userMap.put("list", userRepo.findAll());
            //
            // this.simpMessagingTemplate.convertAndSend("/channel/users", new ApiResponse(SUCCESS, userMap));
            
        } else {
            
            if (user.getNetid() != null && !user.getNetid().equals(shib.getAllCredentials().get(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_NETID))) {
                user.setNetid(shib.getAllCredentials().get(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_NETID));
            }
            if (user.getBirthYear() != null && !user.getBirthYear().equals(shib.getAllCredentials().get(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_BIRTH_YEAR))) {
                user.setBirthYear(Integer.parseInt(shib.getAllCredentials().get(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_BIRTH_YEAR)));
            }
            if (user.getMiddleName() != null && !user.getMiddleName().equals(shib.getAllCredentials().get(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_MIDDLE_NAME))) {
                user.setMiddleName(shib.getAllCredentials().get(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_MIDDLE_NAME));
            }
            if (user.getOrcid() != null && !user.getOrcid().equals(shib.getAllCredentials().get(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_ORCID))) {
                user.setOrcid(shib.getAllCredentials().get(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_ORCID));
            }           
            if (user.getUin() != null && !user.getUin().equals(shib.getAllCredentials().get(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_INSTITUTIONAL_IDENTIFIER))) {
                user.setUin(Long.parseLong(shib.getAllCredentials().get(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_INSTITUTIONAL_IDENTIFIER)));
            }            
            user = userRepo.save(user);

            shib.setRole(user.getRole().toString());

        }
        
        return shib;

    }

}
