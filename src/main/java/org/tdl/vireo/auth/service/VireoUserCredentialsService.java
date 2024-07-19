package org.tdl.vireo.auth.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    private static final String NETID = ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_NETID;
    private static final String EMAIL = ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_EMAIL;
    private static final String BIRTH_YEAR = ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_BIRTH_YEAR;
    private static final String FIRST_NAME = ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_FIRST_NAME;
    private static final String MIDDLE_NAME = ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_MIDDLE_NAME;
    private static final String LAST_NAME = ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_LAST_NAME;
    private static final String ORCID = ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_ORCID;
    private static final String INSTITUTIONAL_IDENTIFIER = ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_INSTITUTIONAL_IDENTIFIER;

    private static final String SHIBBOLETH = "shibboleth";

    private static final Map<String, String> shibSettings = new HashMap<>();

    static {
        shibSettings.put(NETID, "netid");
        shibSettings.put(EMAIL, "email");
        shibSettings.put(BIRTH_YEAR, "birthYear");
        shibSettings.put(FIRST_NAME, "firstName");
        shibSettings.put(MIDDLE_NAME, "middleName");
        shibSettings.put(LAST_NAME, "lastName");
        shibSettings.put(ORCID, "orcid");
        shibSettings.put(INSTITUTIONAL_IDENTIFIER, "uin");
    }

    @Autowired
    private ConfigurationRepo configurationRepo;

    @Value("${app.useNetidAsIdentifier:false}")
    private boolean useNetidAsIdentifier;

    @Override
    public synchronized User updateUserByCredentials(Credentials credentials) {
        Map<String, String> shibValues = new HashMap<>();

        shibSettings.forEach((k, v) ->
            shibValues.put(k, configurationRepo.getValueByNameAndType(k, SHIBBOLETH) != null ? configurationRepo.getValueByNameAndType(k, SHIBBOLETH) : v)
        );

        String shibNetid = credentials.getAllCredentials().get(shibValues.get(NETID));
        String shibEmail = credentials.getAllCredentials().get(shibValues.get(EMAIL));
        String shibFirstName = credentials.getAllCredentials().get(shibValues.get(FIRST_NAME));
        String shibMiddleName = credentials.getAllCredentials().get(shibValues.get(MIDDLE_NAME));
        String shibLastName = credentials.getAllCredentials().get(shibValues.get(LAST_NAME));
        String shibOrcid = credentials.getAllCredentials().get(shibValues.get(ORCID));

        User user = useNetidAsIdentifier
            ? userRepo.findByNetid(shibNetid)
            : userRepo.findByEmail(shibEmail);

        if (user == null) {
            Role role = Role.ROLE_STUDENT;

            for (String email : admins) {
                if (email.equals(shibEmail)) {
                    role = Role.ROLE_ADMIN;
                }
            }

            user = userRepo.create(shibEmail, shibFirstName, shibLastName, role);

            user.setNetid(shibNetid);
            user.setMiddleName(shibMiddleName);
            user.setOrcid(shibOrcid);

            if (credentials.getAllCredentials().containsKey(shibValues.get(BIRTH_YEAR))) {
                String shibBirthYearValue = credentials.getAllCredentials().get(shibValues.get(BIRTH_YEAR));
                if (StringUtils.isNotEmpty(shibBirthYearValue)) {
                    user.setBirthYear(Integer.parseInt(shibBirthYearValue));
                }
            }

            user = userRepo.save(user);
        } else {
            boolean isUserUpdated = false;

            if (StringUtils.isNotEmpty(shibNetid) && !user.getNetid().equals(shibNetid)) {
                user.setNetid(shibNetid);
                isUserUpdated = true;
            }

            if (credentials.getAllCredentials().containsKey(shibValues.get(BIRTH_YEAR))) {
                String shibBirthYearValue = credentials.getAllCredentials().get(shibValues.get(BIRTH_YEAR));

                if (StringUtils.isNotEmpty(shibBirthYearValue)) {
                    int shibBirthYear = Integer.parseInt(shibBirthYearValue);
                    if (shibBirthYear != user.getBirthYear()) {
                        user.setBirthYear(shibBirthYear);
                        isUserUpdated = true;
                    }
                }
            }

            if (StringUtils.isNotEmpty(shibFirstName) && !user.getFirstName().equals(shibFirstName)) {
                user.setFirstName(shibFirstName);
                isUserUpdated = true;
            }

            if (StringUtils.isNotEmpty(shibMiddleName) && !user.getMiddleName().equals(shibMiddleName)) {
                user.setMiddleName(shibMiddleName);
                isUserUpdated = true;
            }

            if (StringUtils.isNotEmpty(shibLastName) && !user.getLastName().equals(shibLastName)) {
                user.setLastName(shibLastName);
                isUserUpdated = true;
            }

            if (StringUtils.isNotEmpty(shibOrcid) && !user.getOrcid().equals(shibOrcid)) {
                user.setOrcid(shibOrcid);
                isUserUpdated = true;
            }

            if (StringUtils.isNotEmpty(shibEmail) && !user.getUsername().equals(shibEmail)) {
                user.setUsername(shibEmail);
                isUserUpdated = true;
            }

            if (isUserUpdated) {
                user = userRepo.save(user);
            }
        }

        credentials.setRole(user.getRole().toString());

        credentials.setNetid(user.getNetid());
        credentials.setFirstName(user.getFirstName());
        credentials.setLastName(user.getLastName());

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
