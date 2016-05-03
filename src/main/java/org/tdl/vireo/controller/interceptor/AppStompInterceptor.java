package org.tdl.vireo.controller.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.tdl.vireo.config.constant.ConfigurationName;
import org.tdl.vireo.enums.AppRole;
import org.tdl.vireo.model.User;
import org.tdl.vireo.model.repo.ConfigurationRepo;
import org.tdl.vireo.model.repo.UserRepo;

import edu.tamu.framework.interceptor.CoreStompInterceptor;
import edu.tamu.framework.model.Credentials;

@Component
public class AppStompInterceptor extends CoreStompInterceptor {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UserRepo userRepo;
    
    @Autowired
    private ConfigurationRepo configurationRepo;

    @Value("${app.authority.admins}")
    private String[] admins;

    // @Autowired @Lazy
    // private SimpMessagingTemplate simpMessagingTemplate;

    @Override
    public Credentials confirmCreateUser(Credentials shib) {

        User user = userRepo.findByEmail(shib.getEmail());
        
        // get shib headers out of DB
        String netIdHeader = configurationRepo.getValue(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_NETID, "netid");
        String birthYearHeader = configurationRepo.getValue(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_BIRTH_YEAR, "birthYear");
        String middleNameHeader = configurationRepo.getValue(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_MIDDLE_NAME, "middleName");
        String orcidHeader = configurationRepo.getValue(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_ORCID, "orcid");
        String institutionalIdentifierHeader = configurationRepo.getValue(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_INSTITUTIONAL_IDENTIFIER, "uin");

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
            user.setNetid(shib.getAllCredentials().get(netIdHeader));
            if (shib.getAllCredentials().get(birthYearHeader) != null) {
                user.setBirthYear(Integer.parseInt(shib.getAllCredentials().get(birthYearHeader)));
            }
            user.setMiddleName(shib.getAllCredentials().get(middleNameHeader));
            user.setOrcid(shib.getAllCredentials().get(orcidHeader));
            if (shib.getAllCredentials().get(institutionalIdentifierHeader) != null) {
                user.setUin(Long.parseLong(shib.getAllCredentials().get(institutionalIdentifierHeader)));
            }
            
            userRepo.save(user);
            // Map<String, Object> userMap = new HashMap<String, Object>();
            //
            // userMap.put("list", userRepo.findAll());
            //
            // this.simpMessagingTemplate.convertAndSend("/channel/users", new ApiResponse(SUCCESS, userMap));

        } else {

            if (user.getNetid() != null && !user.getNetid().equals(shib.getAllCredentials().get(netIdHeader))) {
                user.setNetid(shib.getAllCredentials().get(netIdHeader));
            }
            if (user.getBirthYear() != null && !user.getBirthYear().equals(shib.getAllCredentials().get(birthYearHeader))) {
                user.setBirthYear(Integer.parseInt(shib.getAllCredentials().get(birthYearHeader)));
            }
            if (user.getMiddleName() != null && !user.getMiddleName().equals(shib.getAllCredentials().get(middleNameHeader))) {
                user.setMiddleName(shib.getAllCredentials().get(middleNameHeader));
            }
            if (user.getOrcid() != null && !user.getOrcid().equals(shib.getAllCredentials().get(orcidHeader))) {
                user.setOrcid(shib.getAllCredentials().get(orcidHeader));
            }
            if (user.getUin() != null && !user.getUin().equals(shib.getAllCredentials().get(institutionalIdentifierHeader))) {
                user.setUin(Long.parseLong(shib.getAllCredentials().get(institutionalIdentifierHeader)));
            }
            user = userRepo.save(user);

            shib.setRole(user.getRole().toString());

        }

        return shib;

    }

}
