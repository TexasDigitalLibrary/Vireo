package org.tdl.vireo.model.repo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.tdl.vireo.model.Address;
import org.tdl.vireo.model.ContactInfo;
import org.tdl.vireo.model.User;

@Transactional(propagation = Propagation.REQUIRES_NEW)
public class UserRepoTest extends AbstractRepoTest {

    @Override
    @Test
    @Transactional(propagation = Propagation.REQUIRED)
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
    @Transactional(propagation = Propagation.NESTED)
    public void testDuplication() {
        userRepo.create(TEST_USER_EMAIL, TEST_USER_FIRSTNAME, TEST_USER_LASTNAME, TEST_USER_ROLE);

        Exception exception = assertThrows(DataIntegrityViolationException.class, () -> {
            userRepo.create(TEST_USER_EMAIL, TEST_USER_FIRSTNAME, TEST_USER_LASTNAME, TEST_USER_ROLE);
        });

        assertTrue(exception instanceof DataIntegrityViolationException);
    }

    @Override
    @Test
    @Transactional(propagation = Propagation.REQUIRED)
    public void testDelete() {
        Long originalCount = userRepo.count();
        User testUser = userRepo.create(TEST_USER_EMAIL, TEST_USER_FIRSTNAME, TEST_USER_LASTNAME, TEST_USER_ROLE);
        userRepo.delete(testUser);
        assertEquals(originalCount, userRepo.count(), "User did not delete!");
    }

    @Override
    @Test
    @Transactional(propagation = Propagation.REQUIRED)
    public void testCascade() {
        Long originalCount = userRepo.count();
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
        assertEquals(originalCount, userRepo.count(), "The testUser was not deleted!");
    }
}
