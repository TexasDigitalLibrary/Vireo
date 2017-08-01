package org.tdl.vireo.model;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.springframework.dao.DataIntegrityViolationException;

public class UserTest extends AbstractEntityTest {

    @Before
    public void setUp() {
        assertEquals("The user repository was not empty!", 0, userRepo.count());
    }

    @Override
    public void testCreate() {
        User testUser = userRepo.create(TEST_USER_EMAIL, TEST_USER_FIRSTNAME, TEST_USER_LASTNAME, TEST_USER_ROLE);
        assertEquals("The user repository did not save the user!", 1, userRepo.count());
        assertEquals("Saved user did not contain the correct email!", TEST_USER_EMAIL, testUser.getEmail());
        assertEquals("Saved user did not contain the correct first name!", TEST_USER_FIRSTNAME, testUser.getFirstName());
        assertEquals("Saved user did not contain the correct last name!", TEST_USER_LASTNAME, testUser.getLastName());
        assertEquals("Saved user did not contain the correct role!", TEST_USER_ROLE, testUser.getRole());
    }

    @Override
    public void testDuplication() {
        userRepo.create(TEST_USER_EMAIL, TEST_USER_FIRSTNAME, TEST_USER_LASTNAME, TEST_USER_ROLE);
        try {
            userRepo.create(TEST_USER_EMAIL, TEST_USER_FIRSTNAME, TEST_USER_LASTNAME, TEST_USER_ROLE);
        } catch (DataIntegrityViolationException e) {
            /* SUCCESS */ }
        assertEquals("The user repository duplicated the user!", 1, userRepo.count());
    }

    @Override
    public void testDelete() {
        User testUser = userRepo.create(TEST_USER_EMAIL, TEST_USER_FIRSTNAME, TEST_USER_LASTNAME, TEST_USER_ROLE);
        userRepo.delete(testUser);
        assertEquals("User did not delete!", 0, userRepo.count());
    }

    @Override
    public void testCascade() {
        Address currentAddress = addressRepo.create(TEST_CURRENT_ADDRESS1, TEST_CURRENT_ADDRESS2, TEST_CURRENT_CITY, TEST_CURRENT_STATE, TEST_CURRENT_POSTAL_CODE, TEST_CURRENT_COUNTRY);
        assertEquals("The address does not exist!", 1, addressRepo.count());
        ContactInfo currentContactInfo = contactInfoRepo.create(currentAddress, TEST_CURRENT_PHONE, TEST_CURRENT_EMAIL);
        assertEquals("The contact info does not exist!", 1, contactInfoRepo.count());

        Address permanentAddress = addressRepo.create(TEST_PERMANENT_ADDRESS1, TEST_PERMANENT_ADDRESS2, TEST_PERMANENT_CITY, TEST_PERMANENT_STATE, TEST_PERMANENT_POSTAL_CODE, TEST_PERMANENT_COUNTRY);
        assertEquals("The address does not exist!", 2, addressRepo.count());
        ContactInfo permanentContactInfo = contactInfoRepo.create(permanentAddress, TEST_PERMANENT_PHONE, TEST_PERMANENT_EMAIL);
        assertEquals("The contact info does not exist!", 2, contactInfoRepo.count());

        User testUser = userRepo.create(TEST_USER_EMAIL, TEST_USER_FIRSTNAME, TEST_USER_LASTNAME, TEST_USER_ROLE);
        testUser.putSetting(TEST_SETTING_KEY, TEST_SETTING_VALUE);
        testUser.addShibbolethAffiliation(TEST_SHIBBOLETH_AFFILIATION);
        testUser.setPermanentContactInfo(permanentContactInfo);
        testUser.setCurrentContactInfo(currentContactInfo);
        testUser = userRepo.save(testUser);

        // test delete user
        userRepo.delete(testUser);
        assertEquals("The testUser was not deleted!", 0, userRepo.count());
    }

    @After
    public void cleanUp() {
        namedSearchFilterRepo.findAll().forEach(nsf -> {
            namedSearchFilterRepo.delete(nsf);
        });
        userRepo.deleteAll();
        organizationRepo.findAll().forEach(organization -> {
            organizationRepo.delete(organization);
        });
        organizationCategoryRepo.deleteAll();
    }
}
