package org.tdl.vireo.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.tdl.vireo.config.constant.ConfigurationName;
import org.tdl.vireo.enums.AppRole;
import org.tdl.vireo.model.User;
import org.tdl.vireo.model.repo.ConfigurationRepo;
import org.tdl.vireo.model.repo.UserRepo;

import edu.tamu.framework.model.Credentials;

@Service
public class UserCredentialsService {
    @Autowired
    UserRepo userRepo;

    @Autowired
    ConfigurationRepo configurationRepo;

    @Autowired
    private DefaultSubmissionListColumnService defaultSubmissionViewColumnService;

    @Value("${app.authority.admins}")
    private String[] admins;

    public Credentials buildAnonymousCredentials() {
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

    public User updateUserByCredentials(Credentials credentials) {
        User user = userRepo.findByEmail(credentials.getEmail());

        Map<String, String> shibSettings = new HashMap<String, String>();
        Map<String, String> shibValues = new HashMap<String, String>();

        shibSettings.put(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_NETID, "netid");
        shibSettings.put(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_BIRTH_YEAR, "birthYear");
        shibSettings.put(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_MIDDLE_NAME, "middleName");
        shibSettings.put(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_ORCID, "orcid");
        shibSettings.put(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_INSTITUTIONAL_IDENTIFIER, "uin");

        shibSettings.forEach((k, v) -> {
            shibValues.put(k, configurationRepo.getValueByNameAndType(k, "shibboleth") != null ? configurationRepo.getValueByNameAndType(k, "shibboleth") : v);
        });

        String uin = credentials.getAllCredentials().get(shibValues.get(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_INSTITUTIONAL_IDENTIFIER));
        if (uin == null) {
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

            user.setNetid(credentials.getAllCredentials().get(shibValues.get(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_NETID)));
            if (credentials.getAllCredentials().get(shibValues.get(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_BIRTH_YEAR)) != null) {
                user.setBirthYear(Integer.parseInt(credentials.getAllCredentials().get(shibValues.get(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_BIRTH_YEAR))));
            }
            user.setMiddleName(credentials.getAllCredentials().get(shibValues.get(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_MIDDLE_NAME)));
            user.setOrcid(credentials.getAllCredentials().get(shibValues.get(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_ORCID)));
            user.setUin(uin);

            user.setSubmissionViewColumns(defaultSubmissionViewColumnService.getDefaultSubmissionListColumns());

            user = userRepo.save(user);
        } else {

            // TODO: update only if user properties does not match current credentials

            user.setNetid(credentials.getAllCredentials().get(shibValues.get(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_INSTITUTIONAL_IDENTIFIER)));
            if (credentials.getAllCredentials().get(shibValues.get(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_BIRTH_YEAR)) != null) {
                user.setBirthYear(Integer.parseInt(credentials.getAllCredentials().get(shibValues.get(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_BIRTH_YEAR))));
            }
            user.setMiddleName(credentials.getAllCredentials().get(shibValues.get(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_MIDDLE_NAME)));
            user.setOrcid(credentials.getAllCredentials().get(shibValues.get(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_ORCID)));
            user.setUin(uin);

            user = userRepo.save(user);
        }

        credentials.setRole(user.getRole().toString());
        credentials.setUin(user.getUin());

        return user;
    }
}
