package org.tdl.vireo.auth.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tdl.vireo.config.constant.ConfigurationName;
import org.tdl.vireo.model.Role;
import org.tdl.vireo.model.User;
import org.tdl.vireo.model.repo.ConfigurationRepo;
import org.tdl.vireo.model.repo.UserRepo;

import edu.tamu.weaver.auth.model.Credentials;
import edu.tamu.weaver.auth.service.UserCredentialsService;

@Service
public class VireoUserCredentialsService extends UserCredentialsService<User, UserRepo> {

    @Autowired
    private ConfigurationRepo configurationRepo;

    @Override
    public synchronized User updateUserByCredentials(Credentials credentials) {
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

            Role role = Role.ROLE_STUDENT;

            if (credentials.getRole() == null) {
                credentials.setRole(role.toString());
            }

            String shibEmail = credentials.getEmail();

            for (String email : admins) {
                if (email.equals(shibEmail)) {
                    role = Role.ROLE_ADMIN;
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
            user.setUsername(credentials.getEmail());

            user = userRepo.save(user);
        } else {

            // TODO: update only if user properties does not match current credentials

            user.setNetid(credentials.getAllCredentials().get(shibValues.get(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_INSTITUTIONAL_IDENTIFIER)));
            if (credentials.getAllCredentials().get(shibValues.get(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_BIRTH_YEAR)) != null) {
                user.setBirthYear(Integer.parseInt(credentials.getAllCredentials().get(shibValues.get(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_BIRTH_YEAR))));
            }
            user.setMiddleName(credentials.getAllCredentials().get(shibValues.get(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_MIDDLE_NAME)));
            user.setOrcid(credentials.getAllCredentials().get(shibValues.get(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_ORCID)));
            user.setUsername(credentials.getEmail());

            user = userRepo.save(user);
        }

        credentials.setRole(user.getRole().toString());
        credentials.setUin(user.getUsername());

        return user;
    }

    public User createUserFromRegistration(String email, String firstName, String lastName, String password) {
        Role role = Role.ROLE_STUDENT;
        for (String adminEmail : admins) {
            if (adminEmail.equals(email)) {
                role = Role.ROLE_ADMIN;
                break;
            }
        }
        return userRepo.create(email, firstName, lastName, password, role);
    }

    @Override
    public String getAnonymousRole() {
        return Role.ROLE_ANONYMOUS.toString();
    }

}
