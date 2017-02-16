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

import edu.tamu.framework.interceptor.CoreRestInterceptor;
import edu.tamu.framework.model.Credentials;

@Component
public class AppRestInterceptor extends CoreRestInterceptor {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ConfigurationRepo configurationRepo;

    @Autowired
    private DefaultSubmissionListColumnService defaultSubmissionViewColumnService;

    @Value("${app.authority.admins}")
    private String[] admins;

    // TODO: move static values into config
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
    public Credentials confirmCreateUser(Credentials credentials) {

        User user = userRepo.findByEmail(credentials.getEmail());

        // get shib headers out of DB
        String netIdHeader = configurationRepo.getValue(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_NETID, "netid");
        String birthYearHeader = configurationRepo.getValue(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_BIRTH_YEAR, "birthYear");
        String middleNameHeader = configurationRepo.getValue(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_MIDDLE_NAME, "middleName");
        String orcidHeader = configurationRepo.getValue(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_ORCID, "orcid");
        String institutionalIdentifierHeader = configurationRepo.getValue(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_INSTITUTIONAL_IDENTIFIER, "uin");

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

            // User newUser =
            user = userRepo.create(credentials.getEmail(), credentials.getFirstName(), credentials.getLastName(), role);
            user.setNetid(credentials.getAllCredentials().get(netIdHeader));
            if (credentials.getAllCredentials().get(birthYearHeader) != null) {
                user.setBirthYear(Integer.parseInt(credentials.getAllCredentials().get(birthYearHeader)));
            }
            user.setMiddleName(credentials.getAllCredentials().get(middleNameHeader));
            user.setOrcid(credentials.getAllCredentials().get(orcidHeader));
            if (credentials.getAllCredentials().get(institutionalIdentifierHeader) != null) {
                user.setUin(Long.parseLong(credentials.getAllCredentials().get(institutionalIdentifierHeader)));
            }

            user.setSubmissionViewColumns(defaultSubmissionViewColumnService.getDefaultSubmissionListColumns());

            user = userRepo.save(user);
        } else {

            if (user.getNetid() != null && !user.getNetid().equals(credentials.getAllCredentials().get(netIdHeader))) {
                user.setNetid(credentials.getAllCredentials().get(netIdHeader));
            }
            if (user.getBirthYear() != null && !user.getBirthYear().equals(credentials.getAllCredentials().get(birthYearHeader))) {
                user.setBirthYear(Integer.parseInt(credentials.getAllCredentials().get(birthYearHeader)));
            }
            if (user.getMiddleName() != null && !user.getMiddleName().equals(credentials.getAllCredentials().get(middleNameHeader))) {
                user.setMiddleName(credentials.getAllCredentials().get(middleNameHeader));
            }
            if (user.getOrcid() != null && !user.getOrcid().equals(credentials.getAllCredentials().get(orcidHeader))) {
                user.setOrcid(credentials.getAllCredentials().get(orcidHeader));
            }
            if (user.getUin() != null && !user.getUin().equals(credentials.getAllCredentials().get(institutionalIdentifierHeader))) {
                user.setUin(Long.parseLong(credentials.getAllCredentials().get(institutionalIdentifierHeader)));
            }

            user = userRepo.save(user);

            credentials.setRole(user.getRole().toString());
        }

        return credentials;
    }

}