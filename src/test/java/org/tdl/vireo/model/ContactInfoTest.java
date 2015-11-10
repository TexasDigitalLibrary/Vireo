package org.tdl.vireo.model;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;

public class ContactInfoTest extends AbstractEntityTest {

    @Before
    public void setUp() {
        assertEquals("The contactInfo repository is not empty!", 0, contactInfoRepo.count());
        testAddress = addressRepo.create(TEST_ADDRESS1, TEST_ADDRESS2, TEST_CITY, TEST_STATE, TEST_POSTAL_CODE, TEST_COUNTRY);
        assertEquals("The address dependency was not created successfully!", 1, addressRepo.count());
    }

    @Override
    public void testCreate() {
        ContactInfo testContactInfo = contactInfoRepo.create(testAddress, TEST_PHONE, TEST_EMAIL);
        assertEquals("The contact info is not created", 1, contactInfoRepo.count());
        assertEquals("Created testContactInfo does not contain the correct address", testAddress, testContactInfo.getAddress());
        assertEquals("Created testContactInfo does not contain the correct phone ", TEST_PHONE, testContactInfo.getPhone());
        assertEquals("Created testContactInfo does not contain the correct email ", TEST_EMAIL, testContactInfo.getEmail());
    }

    @Override
    public void testDuplication() {
        contactInfoRepo.create(testAddress, TEST_PHONE, TEST_EMAIL);
        contactInfoRepo.create(testAddress, TEST_PHONE, TEST_EMAIL);
        assertEquals("Duplicate contact info entry is not saved", 2, contactInfoRepo.count());
    }

    @Override
    public void testDelete() {
        ContactInfo testContactInfo = contactInfoRepo.create(testAddress, TEST_PHONE, TEST_EMAIL);
        contactInfoRepo.delete(testContactInfo);
        assertEquals("The contact info was not deleted", 0, contactInfoRepo.count());
    }

    @Override
    public void testCascade() {
        ContactInfo testContactInfo = contactInfoRepo.create(testAddress, TEST_PHONE, TEST_EMAIL);
        contactInfoRepo.delete(testContactInfo);
        assertEquals("Cascade delete did not happen for address", 0, addressRepo.count());
    }

    @After
    public void cleanUp() {
        contactInfoRepo.deleteAll();
        addressRepo.deleteAll();
    }

}
