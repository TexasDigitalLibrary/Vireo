package org.tdl.vireo.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
public class UserTest extends AbstractEntityTest {

    @BeforeEach
    public void setUp() {
        assertEquals(0, userRepo.count(), "The user repository was not empty!");
    }

    @Override
    @Test
    public void testCreate() {
        User testUser = userRepo.create(TEST_USER_EMAIL, TEST_USER_FIRSTNAME, TEST_USER_LASTNAME, TEST_USER_ROLE);
        assertEquals(1, userRepo.count(), "The user repository did not save the user!");
        assertEquals(TEST_USER_EMAIL, testUser.getEmail(), "Saved user did not contain the correct email!");
        assertEquals(TEST_USER_FIRSTNAME, testUser.getFirstName(), "Saved user did not contain the correct first name!");
        assertEquals(TEST_USER_LASTNAME, testUser.getLastName(), "Saved user did not contain the correct last name!");
        assertEquals(TEST_USER_ROLE, testUser.getRole(), "Saved user did not contain the correct role!");
    }

    @Override
    @Test
    public void testDuplication() {
        userRepo.create(TEST_USER_EMAIL, TEST_USER_FIRSTNAME, TEST_USER_LASTNAME, TEST_USER_ROLE);
        try {
            userRepo.create(TEST_USER_EMAIL, TEST_USER_FIRSTNAME, TEST_USER_LASTNAME, TEST_USER_ROLE);
        } catch (DataIntegrityViolationException e) {
            /* SUCCESS */
        }

        assertEquals(1, userRepo.count(), "The user repository duplicated the user!");
    }

    @Override
    @Test
    public void testDelete() {
        User testUser = userRepo.create(TEST_USER_EMAIL, TEST_USER_FIRSTNAME, TEST_USER_LASTNAME, TEST_USER_ROLE);
        userRepo.delete(testUser);
        assertEquals(0, userRepo.count(), "User did not delete!");
    }

    @Override
    @Test
    public void testCascade() {
        Address currentAddress = addressRepo.create(TEST_CURRENT_ADDRESS1, TEST_CURRENT_ADDRESS2, TEST_CURRENT_CITY, TEST_CURRENT_STATE, TEST_CURRENT_POSTAL_CODE, TEST_CURRENT_COUNTRY);
        assertEquals(1, addressRepo.count(), "The address does not exist!");
        ContactInfo currentContactInfo = contactInfoRepo.create(currentAddress, TEST_CURRENT_PHONE, TEST_CURRENT_EMAIL);
        assertEquals(1, contactInfoRepo.count(), "The contact info does not exist!");

        Address permanentAddress = addressRepo.create(TEST_PERMANENT_ADDRESS1, TEST_PERMANENT_ADDRESS2, TEST_PERMANENT_CITY, TEST_PERMANENT_STATE, TEST_PERMANENT_POSTAL_CODE, TEST_PERMANENT_COUNTRY);
        assertEquals(2, addressRepo.count(), "The address does not exist!");
        ContactInfo permanentContactInfo = contactInfoRepo.create(permanentAddress, TEST_PERMANENT_PHONE, TEST_PERMANENT_EMAIL);
        assertEquals(2, contactInfoRepo.count(), "The contact info does not exist!");

        User testUser = userRepo.create(TEST_USER_EMAIL, TEST_USER_FIRSTNAME, TEST_USER_LASTNAME, TEST_USER_ROLE);
        testUser.putSetting(TEST_SETTING_KEY, TEST_SETTING_VALUE);
        testUser.addShibbolethAffiliation(TEST_SHIBBOLETH_AFFILIATION);
        testUser.setPermanentContactInfo(permanentContactInfo);
        testUser.setCurrentContactInfo(currentContactInfo);
        testUser = userRepo.save(testUser);

        // test delete user
        userRepo.delete(testUser);
        assertEquals(0, userRepo.count(), "The testUser was not deleted!");
    }

    @AfterEach
    public void cleanUp() {
        namedSearchFilterGroupRepo.findAll().forEach(nsf -> {
            namedSearchFilterGroupRepo.delete(nsf);
        });
        namedSearchFilterRepo.findAll().forEach(nsf -> {
            namedSearchFilterRepo.delete(nsf);
        });
        filterCriterionRepo.deleteAll();
        userRepo.deleteAll();
        organizationRepo.findAll().forEach(organization -> {
            organizationRepo.delete(organization);
        });
        organizationCategoryRepo.deleteAll();
    }
}
