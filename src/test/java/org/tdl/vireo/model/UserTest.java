package org.tdl.vireo.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

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
import org.tdl.vireo.enums.Role;
import org.tdl.vireo.model.repo.AddressRepo;
import org.tdl.vireo.model.repo.ContactInfoRepo;
import org.tdl.vireo.model.repo.OrganizationCategoryRepo;
import org.tdl.vireo.model.repo.OrganizationRepo;
import org.tdl.vireo.model.repo.UserRepo;
import org.tdl.vireo.runner.OrderedRunner;

@RunWith(OrderedRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class UserTest {

    private static final String TEST_USER_EMAIL = "admin@tdl.org";
    private static final String TEST_USER_FIRSTNAME = "TDL";
    private static final String TEST_USER_LASTNAME = "Admin";
    private static final Role TEST_USER_ROLE = Role.ADMINISTRATOR;

    private static final String TEST_SHIBBOLETH_AFFILIATION = "shib_affiliation";

    private static final String TEST_PREFERENCE_KEY = "key";
    private static final String TEST_PREFERENCE_VALUE = "value";

    private static final String TEST_CURRENT_ADDRESS1 = "101. E. 21st St.";
    private static final String TEST_CURRENT_ADDRESS2 = "PCL 1.333";
    private static final String TEST_CURRENT_CITY = "Austin";
    private static final String TEST_CURRENT_STATE = "Texas";
    private static final String TEST_CURRENT_POSTAL_CODE = "78759";
    private static final String TEST_CURRENT_COUNTRY = "USA";
    private static final String TEST_CURRENT_PHONE = "512-495-4418";
    private static final String TEST_CURRENT_EMAIL = "admin@tdl.org";

    private static final String TEST_PERMANENT_ADDRESS1 = "101. E. 21st St. <p>";
    private static final String TEST_PERMANENT_ADDRESS2 = "PCL 1.333 <p>";
    private static final String TEST_PERMANENT_CITY = "Austin <p>";
    private static final String TEST_PERMANENT_STATE = "Texas <p>";
    private static final String TEST_PERMANENT_POSTAL_CODE = "78759 <p>";
    private static final String TEST_PERMANENT_COUNTRY = "USA <p>";
    private static final String TEST_PERMANENT_PHONE = "512-495-4418 <p>";
    private static final String TEST_PERMANENT_EMAIL = "admin@tdl.org <p>";

    private static final String TEST_PARENT_CATEGORY_NAME = "Test Parent Category";
    private static final int TEST_PARENT_CATEGORY_LEVEL = 0;

    private static final String TEST_PARENT_ORGANIZATION_NAME = "Test Parent Organization";

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private AddressRepo addressRepo;

    @Autowired
    private ContactInfoRepo contactInfoRepo;

    @Autowired
    private OrganizationCategoryRepo organizationCategoryRepo;

    @Autowired
    private OrganizationRepo organizationRepo;

    @Before
    public void setUp() {
        assertEquals("The user repository was not empty!", 0, userRepo.count());
    }

    @Test
    @Order(value = 1)
    @Transactional
    public void testCreate() {
        User testUser = userRepo.create(TEST_USER_EMAIL, TEST_USER_FIRSTNAME, TEST_USER_LASTNAME, TEST_USER_ROLE);
        assertEquals("The user repository did not save the user!", 1, userRepo.count());
        assertEquals("Saved user did not contain the correct email!", TEST_USER_EMAIL, testUser.getEmail());
        assertEquals("Saved user did not contain the correct first name!", TEST_USER_FIRSTNAME, testUser.getFirstName());
        assertEquals("Saved user did not contain the correct last name!", TEST_USER_LASTNAME, testUser.getLastName());
        assertEquals("Saved user did not contain the correct role!", TEST_USER_ROLE, testUser.getRole());
    }

    @Test
    @Order(value = 2)
    public void testDuplication() {
        userRepo.create(TEST_USER_EMAIL, TEST_USER_FIRSTNAME, TEST_USER_LASTNAME, TEST_USER_ROLE);
        try {
            userRepo.create(TEST_USER_EMAIL, TEST_USER_FIRSTNAME, TEST_USER_LASTNAME, TEST_USER_ROLE);
        } catch (DataIntegrityViolationException e) {
            /* SUCCESS */ }
        assertEquals("The user repository duplicated the user!", 1, userRepo.count());
    }

    @Test
    @Order(value = 3)
    public void testFind() {
        userRepo.create(TEST_USER_EMAIL, TEST_USER_FIRSTNAME, TEST_USER_LASTNAME, TEST_USER_ROLE);
        User testUser = userRepo.findByEmail(TEST_USER_EMAIL);
        assertNotEquals("Did not find user!", null, testUser);
        assertEquals("Found user did not contain the correct email!", TEST_USER_EMAIL, testUser.getEmail());
    }

    @Test
    @Order(value = 4)
    public void testDelete() {
        User testUser = userRepo.create(TEST_USER_EMAIL, TEST_USER_FIRSTNAME, TEST_USER_LASTNAME, TEST_USER_ROLE);
        userRepo.delete(testUser);
        assertEquals("User did not delete!", 0, userRepo.count());
    }

    @Test
    @Order(value = 5)
    @Transactional
    public void testCascade() {
        Address currentAddress = addressRepo.create(TEST_CURRENT_ADDRESS1, TEST_CURRENT_ADDRESS2, TEST_CURRENT_CITY, TEST_CURRENT_STATE, TEST_CURRENT_POSTAL_CODE, TEST_CURRENT_COUNTRY);
        assertEquals("The address does not exist!", 1, addressRepo.count());
        ContactInfo currentContactInfo = contactInfoRepo.create(currentAddress, TEST_CURRENT_PHONE, TEST_CURRENT_EMAIL);
        assertEquals("The contact info does not exist!", 1, contactInfoRepo.count());

        Address permanentAddress = addressRepo.create(TEST_PERMANENT_ADDRESS1, TEST_PERMANENT_ADDRESS2, TEST_PERMANENT_CITY, TEST_PERMANENT_STATE, TEST_PERMANENT_POSTAL_CODE, TEST_PERMANENT_COUNTRY);
        assertEquals("The address does not exist!", 2, addressRepo.count());
        ContactInfo permanentContactInfo = contactInfoRepo.create(permanentAddress, TEST_PERMANENT_PHONE, TEST_PERMANENT_EMAIL);
        assertEquals("The contact info does not exist!", 2, contactInfoRepo.count());

        OrganizationCategory parentCategory = organizationCategoryRepo.create(TEST_PARENT_CATEGORY_NAME, TEST_PARENT_CATEGORY_LEVEL);
        assertEquals("The category does not exist!", 1, organizationCategoryRepo.count());

        Organization organization = organizationRepo.create(TEST_PARENT_ORGANIZATION_NAME, parentCategory);
        assertEquals("The organization does not exist!", 1, organizationRepo.count());

        User testUser = userRepo.create(TEST_USER_EMAIL, TEST_USER_FIRSTNAME, TEST_USER_LASTNAME, TEST_USER_ROLE);
        testUser.addOrganization(organization);
        testUser.addPreference(TEST_PREFERENCE_KEY, TEST_PREFERENCE_VALUE);
        testUser.addShibbolethAffiliation(TEST_SHIBBOLETH_AFFILIATION);
        testUser.setPermanentContactInfo(permanentContactInfo);
        testUser.setCurrentContactInfo(currentContactInfo);
        testUser = userRepo.save(testUser);

        //TODO test detach organization
        
        //TODO test delete user
    }

    @After
    public void cleanUp() {
        userRepo.deleteAll();
        organizationRepo.deleteAll();
        organizationCategoryRepo.deleteAll();
    }
}
