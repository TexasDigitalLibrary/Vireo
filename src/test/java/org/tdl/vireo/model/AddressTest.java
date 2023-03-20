package org.tdl.vireo.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AddressTest extends AbstractEntityTest {

    @BeforeEach
    public void setUp() {
        assertEquals(0, addressRepo.count(), "The address repository is not empty!");
    }

    @Override
    @Test
    public void testCreate() {
        Address testAddress = addressRepo.create(TEST_ADDRESS1, TEST_ADDRESS2, TEST_CITY, TEST_STATE, TEST_POSTAL_CODE, TEST_COUNTRY);
        assertEquals(1, addressRepo.count(), "The testAddress was not created");
        assertEquals(TEST_ADDRESS1, testAddress.getAddress1(), "Created address does not contain the correct address1");
        assertEquals(TEST_ADDRESS2, testAddress.getAddress2(), "Created address does not contain the correct address2 ");
        assertEquals(TEST_CITY, testAddress.getCity(), "Created address does not contain the correct city ");
        assertEquals(TEST_STATE, testAddress.getState(), "Created address does not contain the correct state ");
        assertEquals(TEST_POSTAL_CODE, testAddress.getPostalCode(), "Created address does not contain the correct postalCode ");
        assertEquals(TEST_COUNTRY, testAddress.getCountry(), "Created address does not contain the correct country ");
    }

    @Override
    @Test
    public void testDuplication() {
        addressRepo.create(TEST_ADDRESS1, TEST_ADDRESS2, TEST_CITY, TEST_STATE, TEST_POSTAL_CODE, TEST_COUNTRY);
        addressRepo.create(TEST_ADDRESS1, TEST_ADDRESS2, TEST_CITY, TEST_STATE, TEST_POSTAL_CODE, TEST_COUNTRY);
        assertEquals(2, addressRepo.count(), "Duplicate address entry is not saved");
    }

    @Override
    @Test
    public void testDelete() {
        Address testAddress = addressRepo.create(TEST_ADDRESS1, TEST_ADDRESS2, TEST_CITY, TEST_STATE, TEST_POSTAL_CODE, TEST_COUNTRY);
        addressRepo.delete(testAddress);
        assertEquals(0, addressRepo.count(), "The contact info was not deleted");
    }

    @Override
    @Test
    public void testCascade() {

    }

    @AfterEach
    public void cleanUp() {
        addressRepo.deleteAll();
    }

}
