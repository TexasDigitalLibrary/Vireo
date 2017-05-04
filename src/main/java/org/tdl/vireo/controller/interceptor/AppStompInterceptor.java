package org.tdl.vireo.controller.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.tdl.vireo.config.constant.ConfigurationName;
import org.tdl.vireo.enums.AppRole;
import org.tdl.vireo.model.User;
import org.tdl.vireo.model.repo.ConfigurationRepo;
import org.tdl.vireo.model.repo.UserRepo;
import org.tdl.vireo.service.DefaultSubmissionListColumnService;

import edu.tamu.framework.interceptor.CoreStompInterceptor;
import edu.tamu.framework.model.Credentials;

@Component
public class AppStompInterceptor extends CoreStompInterceptor<User> {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ConfigurationRepo configurationRepo;

    @Value("${app.authority.admins}")
    private String[] admins;

    @Autowired
    private DefaultSubmissionListColumnService defaultSubmissionViewColumnService;

    @Override
    public Credentials getAnonymousCredentials() {
        Credentials anonymousCredentials = new Credentials();
        anonymousCredentials.setAffiliation("NA");
        anonymousCredentials.setLastName("Anonymous");
        anonymousCredentials.setFirstName("Role");
        anonymousCredentials.setNetid("anonymous-" + Math.round(Math.random() * 100000));
        anonymousCredentials.setUin("000000000");
        anonymousCredentials.setExp("1436982214754");
        anonymousCredentials.setEmail("helpdesk@library.tamu.edu");
        anonymousCredentials.setRole("NONE");
        return anonymousCredentials;
    }

    @Override
    public User confirmCreateUser(Credentials credentials) {
        User user = userRepo.findByEmail(credentials.getEmail());

        // get shib headers out of DB
        String netIdHeader = configurationRepo.getValue(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_NETID, "netid");
        String birthYearHeader = configurationRepo.getValue(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_BIRTH_YEAR, "birthYear");
        String middleNameHeader = configurationRepo.getValue(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_MIDDLE_NAME, "middleName");
        String orcidHeader = configurationRepo.getValue(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_ORCID, "orcid");
        String institutionalIdentifierHeader = configurationRepo.getValue(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_INSTITUTIONAL_IDENTIFIER, "uin");

        
        String uin = credentials.getAllCredentials().get(institutionalIdentifierHeader);
        
        if(uin == null) {
            uin = credentials.getEmail();
        }
        
        // TODO: check to see if credentials is from basic login or shibboleth
        // do not create new user from basic login credentials that have no user!
        if (user == null) {

            AppRole role = AppRole.STUDENT;

            if (credentials.getRole() == null) {
                credentials.setRole(role.toString());
            }

            String shibEmail = credentials.getEmail();

            for (String email : admins) {
                if (email.equals(shibEmail)) {
                    role = AppRole.ADMINISTRATOR;
                    credentials.setRole(role.toString());
                }
            }

            user = userRepo.create(credentials.getEmail(), credentials.getFirstName(), credentials.getLastName(), role);

            user.setNetid(credentials.getAllCredentials().get(netIdHeader));
            if (credentials.getAllCredentials().get(birthYearHeader) != null) {
                user.setBirthYear(Integer.parseInt(credentials.getAllCredentials().get(birthYearHeader)));
            }
            user.setMiddleName(credentials.getAllCredentials().get(middleNameHeader));
            user.setOrcid(credentials.getAllCredentials().get(orcidHeader));
            user.setUin(uin);

            user.setSubmissionViewColumns(defaultSubmissionViewColumnService.getDefaultSubmissionListColumns());

            user = userRepo.save(user);
        } else {

            // TODO: update only if user properties does not match current credentials

            user.setNetid(credentials.getAllCredentials().get(netIdHeader));
            if (credentials.getAllCredentials().get(birthYearHeader) != null) {
                user.setBirthYear(Integer.parseInt(credentials.getAllCredentials().get(birthYearHeader)));
            }
            user.setMiddleName(credentials.getAllCredentials().get(middleNameHeader));
            user.setOrcid(credentials.getAllCredentials().get(orcidHeader));
            user.setUin(uin);

            user = userRepo.save(user);
        }

        credentials.setRole(user.getRole().toString());
        credentials.setUin(user.getUin());

        return user;

    }

}
