package org.tdl.vireo.model;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.tdl.vireo.Application;
import org.tdl.vireo.annotations.Order;
import org.tdl.vireo.model.repo.AddressRepo;
import org.tdl.vireo.model.repo.ContactInfoRepo;
import org.tdl.vireo.runner.OrderedRunner;

@RunWith(OrderedRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class ContactInfoTest {
	
	private static final String TEST_ADDRESS1 = "101. E. 21st St.";
    private static final String TEST_ADDRESS2 = "PCL 1.333";
    private static final String TEST_CITY = "Austin";
    private static final String TEST_STATE = "Texas";
    private static final String TEST_POSTAL_CODE = "78759";
    private static final String TEST_COUNTRY = "USA";
    private static final String TEST_PHONE = "512-495-4418";
    private static final String TEST_EMAIL = "admin@tdl.org";
	
	@Autowired
    private ContactInfoRepo contactInfoRepo;
	
	@Autowired
    private AddressRepo addressRepo;
		
	@Before
    public void setUp() {
        assertEquals("The contactInfo repository is not empty!", 0, contactInfoRepo.count());
        
    }
	
	@Test
    @Order(value = 1)
    @Transactional
    public void testCreate() {
		Address testAddress = addressRepo.create(TEST_ADDRESS1, TEST_ADDRESS2, TEST_CITY, TEST_STATE, TEST_POSTAL_CODE, TEST_COUNTRY);
        ContactInfo testContactInfo = contactInfoRepo.create(testAddress, TEST_PHONE, TEST_EMAIL);
        assertEquals("The contact info is not created", 1, contactInfoRepo.count());
        assertEquals("Created testContactInfo does not contain the correct address1",TEST_ADDRESS1,  testContactInfo.getAddress().getAddress1());
        assertEquals("Created testContactInfo does not contain the correct address2 ",TEST_ADDRESS2, testContactInfo.getAddress().getAddress2());
        assertEquals("Created testContactInfo does not contain the correct city ",TEST_CITY, testContactInfo.getAddress().getCity());
        assertEquals("Created testContactInfo does not contain the correct state ",TEST_STATE, testContactInfo.getAddress().getState());
        assertEquals("Created testContactInfo does not contain the correct postalCode ",TEST_POSTAL_CODE, testContactInfo.getAddress().getPostalCode());
        assertEquals("Created testContactInfo does not contain the correct country ",TEST_COUNTRY, testContactInfo.getAddress().getCountry());
        assertEquals("Created testContactInfo does not contain the correct phone ",TEST_PHONE, testContactInfo.getPhone());
        assertEquals("Created testContactInfo does not contain the correct email ",TEST_EMAIL, testContactInfo.getEmail());
	}
	
	@Test
    @Order(value = 2)
    @Transactional
    public void testDuplication() {
		Address testAddress = addressRepo.create(TEST_ADDRESS1, TEST_ADDRESS2, TEST_CITY, TEST_STATE, TEST_POSTAL_CODE, TEST_COUNTRY);
		contactInfoRepo.create(testAddress, TEST_PHONE, TEST_EMAIL);
        contactInfoRepo.create(testAddress, TEST_PHONE, TEST_EMAIL);
        assertEquals("Duplicate contact info entry is not saved", 2, contactInfoRepo.count());
	}
  
	/*@Test
    @Order(value = 3)
    @Transactional
    public void testFind() { 
		We currently do not have find implemented for contact info
	}*/
	
	@Test
    @Order(value = 3)
    @Transactional
    public void testDelete() {
		Address testAddress = addressRepo.create(TEST_ADDRESS1, TEST_ADDRESS2, TEST_CITY, TEST_STATE, TEST_POSTAL_CODE, TEST_COUNTRY);
		ContactInfo testContactInfo = contactInfoRepo.create(testAddress, TEST_PHONE, TEST_EMAIL);
		contactInfoRepo.delete(testContactInfo);
		assertEquals("The contact info was not deleted", 0, contactInfoRepo.count());
	}
	
	@Test
    @Order(value = 4)
    @Transactional
    public void testCascade() {
		Address testAddress = addressRepo.create(TEST_ADDRESS1, TEST_ADDRESS2, TEST_CITY, TEST_STATE, TEST_POSTAL_CODE, TEST_COUNTRY);
		ContactInfo testContactInfo = contactInfoRepo.create(testAddress, TEST_PHONE, TEST_EMAIL);
		contactInfoRepo.delete(testContactInfo);
		assertEquals("Cascade delete did not happen for address" , 0, addressRepo.count());		
	}
	
	@After
    public void cleanUp() {
		addressRepo.deleteAll();
       contactInfoRepo.deleteAll();
    }

}
