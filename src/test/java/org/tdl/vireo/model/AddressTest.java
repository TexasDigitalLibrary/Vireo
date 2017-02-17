package org.tdl.vireo.model;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;

public class AddressTest extends AbstractEntityTest {

    @Before
    public void setUp() {
        assertEquals("The address repository is not empty!", 0, addressRepo.count());
    }

    @Override
    public void testCreate() {
        Address testAddress = addressRepo.create(TEST_ADDRESS1, TEST_ADDRESS2, TEST_CITY, TEST_STATE, TEST_POSTAL_CODE, TEST_COUNTRY);
        assertEquals("The testAddress was not created", 1, addressRepo.count());
        assertEquals("Created address does not contain the correct address1", TEST_ADDRESS1, testAddress.getAddress1());
        assertEquals("Created address does not contain the correct address2 ", TEST_ADDRESS2, testAddress.getAddress2());
        assertEquals("Created address does not contain the correct city ", TEST_CITY, testAddress.getCity());
        assertEquals("Created address does not contain the correct state ", TEST_STATE, testAddress.getState());
        assertEquals("Created address does not contain the correct postalCode ", TEST_POSTAL_CODE, testAddress.getPostalCode());
        assertEquals("Created address does not contain the correct country ", TEST_COUNTRY, testAddress.getCountry());
    }

    @Override
    public void testDuplication() {
        addressRepo.create(TEST_ADDRESS1, TEST_ADDRESS2, TEST_CITY, TEST_STATE, TEST_POSTAL_CODE, TEST_COUNTRY);
        addressRepo.create(TEST_ADDRESS1, TEST_ADDRESS2, TEST_CITY, TEST_STATE, TEST_POSTAL_CODE, TEST_COUNTRY);
        assertEquals("Duplicate address entry is not saved", 2, addressRepo.count());
    }

    @Override
    public void testDelete() {
        Address testAddress = addressRepo.create(TEST_ADDRESS1, TEST_ADDRESS2, TEST_CITY, TEST_STATE, TEST_POSTAL_CODE, TEST_COUNTRY);
        addressRepo.delete(testAddress);
        assertEquals("The contact info was not deleted", 0, addressRepo.count());
    }

    @Override
    public void testCascade() {

    }

    @After
    public void cleanUp() {
        addressRepo.deleteAll();
    }

}
