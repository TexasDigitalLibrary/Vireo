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
public class AppStompInterceptor extends CoreStompInterceptor {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ConfigurationRepo configurationRepo;

    @Value("${app.authority.admins}")
    private String[] admins;

    @Autowired
    private DefaultSubmissionListColumnService defaultSubmissionViewColumnService;

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
    public Credentials confirmCreateUser(Credentials creadentials) {
        User user = userRepo.findByEmail(creadentials.getEmail());

        // get shib headers out of DB
        String netIdHeader = configurationRepo.getValue(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_NETID, "netid");
        String birthYearHeader = configurationRepo.getValue(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_BIRTH_YEAR, "birthYear");
        String middleNameHeader = configurationRepo.getValue(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_MIDDLE_NAME, "middleName");
        String orcidHeader = configurationRepo.getValue(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_ORCID, "orcid");
        String institutionalIdentifierHeader = configurationRepo.getValue(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_INSTITUTIONAL_IDENTIFIER, "uin");

        // TODO: check to see if credentials is from basic login or shibboleth
        // do not create new user from basic login credentials that have no user!
        if (user == null) {

            AppRole role = AppRole.STUDENT;

            if (creadentials.getRole() == null) {
                creadentials.setRole(role.toString());
            }
            String shibEmail = creadentials.getEmail();
            for (String email : admins) {
                if (email.equals(shibEmail)) {
                    role = AppRole.ADMINISTRATOR;
                    creadentials.setRole(role.toString());
                }
            }

            user = userRepo.create(creadentials.getEmail(), creadentials.getFirstName(), creadentials.getLastName(), role);
            user.setNetid(creadentials.getAllCredentials().get(netIdHeader));
            if (creadentials.getAllCredentials().get(birthYearHeader) != null) {
                user.setBirthYear(Integer.parseInt(creadentials.getAllCredentials().get(birthYearHeader)));
            }
            user.setMiddleName(creadentials.getAllCredentials().get(middleNameHeader));
            user.setOrcid(creadentials.getAllCredentials().get(orcidHeader));
            if (creadentials.getAllCredentials().get(institutionalIdentifierHeader) != null) {
                user.setUin(Long.parseLong(creadentials.getAllCredentials().get(institutionalIdentifierHeader)));
            }

            user.setSubmissionViewColumns(defaultSubmissionViewColumnService.getDefaultSubmissionListColumns());

            userRepo.save(user);

        } else {

            if (user.getNetid() != null && !user.getNetid().equals(creadentials.getAllCredentials().get(netIdHeader))) {
                user.setNetid(creadentials.getAllCredentials().get(netIdHeader));
            }
            if (user.getBirthYear() != null && !user.getBirthYear().equals(creadentials.getAllCredentials().get(birthYearHeader))) {
                user.setBirthYear(Integer.parseInt(creadentials.getAllCredentials().get(birthYearHeader)));
            }
            if (user.getMiddleName() != null && !user.getMiddleName().equals(creadentials.getAllCredentials().get(middleNameHeader))) {
                user.setMiddleName(creadentials.getAllCredentials().get(middleNameHeader));
            }
            if (user.getOrcid() != null && !user.getOrcid().equals(creadentials.getAllCredentials().get(orcidHeader))) {
                user.setOrcid(creadentials.getAllCredentials().get(orcidHeader));
            }
            if (user.getUin() != null && !user.getUin().equals(creadentials.getAllCredentials().get(institutionalIdentifierHeader))) {
                user.setUin(Long.parseLong(creadentials.getAllCredentials().get(institutionalIdentifierHeader)));
            }
            user = userRepo.save(user);

            creadentials.setRole(user.getRole().toString());

        }

        return creadentials;

    }

}
