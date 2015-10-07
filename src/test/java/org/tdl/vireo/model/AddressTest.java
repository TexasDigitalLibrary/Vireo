package org.tdl.vireo.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;
import org.tdl.vireo.Application;
import org.tdl.vireo.annotations.Order;
import org.tdl.vireo.model.repo.AddressRepo;
import org.tdl.vireo.runner.OrderedRunner;

@RunWith(OrderedRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class AddressTest {
	private static final String TEST_ADDRESS1 = "101. E. 21st St.";
    private static final String TEST_ADDRESS2 = "PCL 1.333";
    private static final String TEST_CITY = "Austin";
    private static final String TEST_STATE = "Texas";
    private static final String TEST_POSTAL_CODE = "78759";
    private static final String TEST_COUNTRY = "USA";
	
	@Autowired
    private AddressRepo addressRepo;

	@Before
    public void setUp() {
        assertEquals("The address repository is not empty!", 0, addressRepo.count());
        
    }
	
	@Test
    @Order(value = 1)
    @Transactional
    public void testCreate() {
		Address testAddress = addressRepo.create(TEST_ADDRESS1, TEST_ADDRESS2, TEST_CITY, TEST_STATE, TEST_POSTAL_CODE, TEST_COUNTRY);
		assertEquals("The testAddress was not created", 1, addressRepo.count());
		
		assertEquals("Created address does not contain the correct address1",TEST_ADDRESS1,  testAddress.getAddress1());
        assertEquals("Created address does not contain the correct address2 ",TEST_ADDRESS2, testAddress.getAddress2());
        assertEquals("Created address does not contain the correct city ",TEST_CITY, testAddress.getCity());
        assertEquals("Created address does not contain the correct state ",TEST_STATE, testAddress.getState());
        assertEquals("Created address does not contain the correct postalCode ",TEST_POSTAL_CODE, testAddress.getPostalCode());
        assertEquals("Created address does not contain the correct country ",TEST_COUNTRY, testAddress.getCountry());
	}
	
	@Test
    @Order(value = 2)
    @Transactional
    public void testDuplication() {
		addressRepo.create(TEST_ADDRESS1, TEST_ADDRESS2, TEST_CITY, TEST_STATE, TEST_POSTAL_CODE, TEST_COUNTRY);
		addressRepo.create(TEST_ADDRESS1, TEST_ADDRESS2, TEST_CITY, TEST_STATE, TEST_POSTAL_CODE, TEST_COUNTRY);
		assertEquals("Duplicate address entry is not saved", 2, addressRepo.count());
	}
	
	@Test
    @Order(value = 3)
    @Transactional
    public void testDelete() {
		Address testAddress = addressRepo.create(TEST_ADDRESS1, TEST_ADDRESS2, TEST_CITY, TEST_STATE, TEST_POSTAL_CODE, TEST_COUNTRY);
		addressRepo.delete(testAddress);
		assertEquals("The contact info was not deleted", 0, addressRepo.count());
	}

	@After
    public void cleanUp() {
		addressRepo.deleteAll();
    }
}
