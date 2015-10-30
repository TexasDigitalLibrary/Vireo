package org.tdl.vireo.model;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.tdl.vireo.service.EntityControlledVocabularyService;

public class UserTest extends AbstractEntityTest {
    
    @Autowired
    EntityControlledVocabularyService entityControlledVocabularyRepo;

    @Before
    public void setUp() {
        assertEquals("The user repository was not empty!", 0, userRepo.count());
    }

    @Override
    public void testCreate() {
        
        
        entityControlledVocabularyRepo.getEntityNames().forEach(entityName -> {
            System.out.println("\n" + entityName + "\n");
        });
        
        
        Map<String, List<String>> entityPropertyMap = entityControlledVocabularyRepo.getAllEntityPropertyNames();
        
        entityPropertyMap.keySet().forEach(key -> {
            System.out.println("\nENTITY " + key + ":\n");
            entityPropertyMap.get(key).forEach(property -> {
                System.out.println("   " + property + "\n");
            });
        });
        
        System.out.println("\nGET BY ENTITY\n*************************************\nEmbargoType properties:\n");
        entityControlledVocabularyRepo.getPropertyNames(EmbargoType.class).forEach(property -> {
            System.out.println("    " + property + "\n");
        });
        System.out.println("\n*************************************\n");
        
        
        System.out.println("\nGET BY ENTITY NAME\n*************************************\nEmbargoType properties:\n");
        try {
            entityControlledVocabularyRepo.getPropertyNames("EmbargoType").forEach(property -> {
                System.out.println("    " + property + "\n");
            });
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println("\n*************************************\n");
        
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
        } 
        catch (DataIntegrityViolationException e) { /* SUCCESS */ }
        
        
        entityControlledVocabularyRepo.getControlledVocabulary(User.class, "email").forEach(property -> {
            System.out.println("\n" + property + "\n");
        });
        
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

        
        entityControlledVocabularyRepo.getControlledVocabulary(Address.class, "address1").forEach(property -> {
            System.out.println("\n" + property + "\n");
        });
        
        try {
            entityControlledVocabularyRepo.getControlledVocabulary("Address", "address1").forEach(property -> {
                System.out.println("\n" + property + "\n");
            });
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        
        
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

        // test detach organization
        testUser.removeOrganization(organization);
        testUser = userRepo.save(testUser);
        assertEquals("The organization was not detached from the user", 0, testUser.getOrganizations().size());

        // test delete user
        userRepo.delete(testUser);
        assertEquals("The testUser was not deleted!", 0, userRepo.count());
    }

    @After
    public void cleanUp() {
        userRepo.deleteAll();
        organizationRepo.deleteAll();
        organizationCategoryRepo.deleteAll();
    }
}
