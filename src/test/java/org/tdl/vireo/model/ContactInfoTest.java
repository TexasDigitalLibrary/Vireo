package org.tdl.vireo.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public class ContactInfoTest extends AbstractEntityTest {

    @BeforeEach
    public void setUp() {
        assertEquals(0, contactInfoRepo.count(), "The contactInfo repository is not empty!");
        testAddress = addressRepo.create(TEST_ADDRESS1, TEST_ADDRESS2, TEST_CITY, TEST_STATE, TEST_POSTAL_CODE, TEST_COUNTRY);
        assertEquals(1, addressRepo.count(), "The address dependency was not created successfully!");
    }

    @Override
    public void testCreate() {
        ContactInfo testContactInfo = contactInfoRepo.create(testAddress, TEST_PHONE, TEST_EMAIL);
        assertEquals(1, contactInfoRepo.count(), "The contact info is not created");
        assertEquals(testAddress, testContactInfo.getAddress(), "Created testContactInfo does not contain the correct address");
        assertEquals(TEST_PHONE, testContactInfo.getPhone(), "Created testContactInfo does not contain the correct phone ");
        assertEquals(TEST_EMAIL, testContactInfo.getEmail(), "Created testContactInfo does not contain the correct email ");
    }

    @Override
    public void testDuplication() {
        contactInfoRepo.create(testAddress, TEST_PHONE, TEST_EMAIL);
        contactInfoRepo.create(testAddress, TEST_PHONE, TEST_EMAIL);
        assertEquals(2, contactInfoRepo.count(), "Duplicate contact info entry is not saved");
    }

    @Override
    public void testDelete() {
        ContactInfo testContactInfo = contactInfoRepo.create(testAddress, TEST_PHONE, TEST_EMAIL);
        contactInfoRepo.delete(testContactInfo);
        assertEquals(0, contactInfoRepo.count(), "The contact info was not deleted");
    }

    @Override
    public void testCascade() {
        ContactInfo testContactInfo = contactInfoRepo.create(testAddress, TEST_PHONE, TEST_EMAIL);
        contactInfoRepo.delete(testContactInfo);
        assertEquals(0, addressRepo.count(), "Cascade delete did not happen for address");
    }

    @AfterEach
    public void cleanUp() {
        contactInfoRepo.deleteAll();
        addressRepo.deleteAll();
    }

}
