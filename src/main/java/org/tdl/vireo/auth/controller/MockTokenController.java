package org.tdl.vireo.auth.controller;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tdl.vireo.config.constant.ConfigurationName;
import org.tdl.vireo.model.repo.ConfigurationRepo;

import edu.tamu.weaver.token.provider.controller.WeaverMockTokenController;

@RestController
@RequestMapping("/mock/auth")
@Profile(value = { "development" })
public class MockTokenController extends WeaverMockTokenController {

    @Autowired
    private ConfigurationRepo configurationRepo;

    @PostConstruct
    public void setMockClaims() {

        Map<String, String> shibSettings = new HashMap<String, String>();
        Map<String, String> shibValues = new HashMap<String, String>();

        shibSettings.put(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_NETID, "netid");
        shibSettings.put(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_BIRTH_YEAR, "birthYear");
        shibSettings.put(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_MIDDLE_NAME, "middleName");
        shibSettings.put(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_FIRST_NAME, "firstName");
        shibSettings.put(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_LAST_NAME, "lastName");
        shibSettings.put(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_EMAIL, "email");
        shibSettings.put(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_ORCID, "orcid");
        shibSettings.put(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_INSTITUTION_IDENTIFIER, "institutionid");
        shibSettings.put(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_INSTITUTIONAL_IDENTIFIER, "uin");
        shibSettings.put(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_PERMANENT_EMAIL_ADDRESS, "permanentEmailAddress");
        shibSettings.put(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_PERMANENT_PHONE_NUMBER, "permanentPhoneNumber");
        shibSettings.put(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_PERMANENT_POSTAL_ADDRESS, "permanentPostalAddress");

        shibSettings.forEach((k, v) -> {
            String overrideValue = configurationRepo.getValueByNameAndType(k, "shibboleth");
            shibValues.put(k, overrideValue != null ? overrideValue : v);
        });

        Map<String, String> mockAdminClaims = new HashMap<String, String>();
        mockAdminClaims.put(shibValues.get(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_NETID), "aggieJack");
        mockAdminClaims.put(shibValues.get(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_INSTITUTION_IDENTIFIER), "inst-id-123");
        mockAdminClaims.put(shibValues.get(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_INSTITUTIONAL_IDENTIFIER), "123456789");
        mockAdminClaims.put(shibValues.get(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_LAST_NAME), "Daniels");
        mockAdminClaims.put(shibValues.get(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_FIRST_NAME), "Jack");
        mockAdminClaims.put(shibValues.get(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_EMAIL), "aggieJack@tamu.edu");

        mockAdminClaims.put(shibValues.get(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_BIRTH_YEAR), "1977");
        mockAdminClaims.put(shibValues.get(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_MIDDLE_NAME), "Jay");
        mockAdminClaims.put(shibValues.get(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_ORCID), "0000-0000-0000-0000");
        mockAdminClaims.put(shibValues.get(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_PERMANENT_EMAIL_ADDRESS), "aggieJack@tamu.edu");
        mockAdminClaims.put(shibValues.get(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_PERMANENT_PHONE_NUMBER), "800-555-1234");
        mockAdminClaims.put(shibValues.get(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_PERMANENT_POSTAL_ADDRESS), "5000 TAMU");
        mockAdminClaims.put("role", "ROLE_ADMIN");

        setMockClaims("admin", mockAdminClaims);

        Map<String, String> mockUserClaims = new HashMap<String, String>();
        mockUserClaims.put(shibValues.get(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_NETID), "bobBoring");
        mockUserClaims.put(shibValues.get(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_INSTITUTION_IDENTIFIER), "inst-id-123");
        mockUserClaims.put(shibValues.get(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_INSTITUTIONAL_IDENTIFIER), "987654321");
        mockUserClaims.put(shibValues.get(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_LAST_NAME), "Boring");
        mockUserClaims.put(shibValues.get(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_FIRST_NAME), "Bob");
        mockUserClaims.put(shibValues.get(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_EMAIL), "bobBoring@tamu.edu");

        mockUserClaims.put(shibValues.get(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_BIRTH_YEAR), "1978");
        mockUserClaims.put(shibValues.get(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_MIDDLE_NAME), "Be");
        mockUserClaims.put(shibValues.get(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_ORCID), "0000-0000-0000-0001");
        mockUserClaims.put(shibValues.get(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_PERMANENT_EMAIL_ADDRESS), "bobBoring@tamu.edu");
        mockUserClaims.put(shibValues.get(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_PERMANENT_PHONE_NUMBER), "800-555-4321");
        mockUserClaims.put(shibValues.get(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_PERMANENT_POSTAL_ADDRESS), "5000 TAMU");
        mockUserClaims.put("role", "ROLE_STUDENT");

        setMockClaims("user", mockUserClaims);
    }

}
