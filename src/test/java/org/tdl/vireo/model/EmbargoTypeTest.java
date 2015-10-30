package org.tdl.vireo.model;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.tdl.vireo.enums.EmbargoGuarantor;

public class EmbargoTypeTest extends AbstractEntityTest {

    @Before
    public void setUp() {
        assertEquals("Ebargo repo was not empty!", 0, embargoTypeRepo.count());
    }

    @Override
    public void testCreate() {
        EmbargoType testEmbargo = embargoTypeRepo.create(TEST_EMBARGO_NAME, TEST_EMBARGO_DESCRIPTION, TEST_EMBARGO_DURATION);
        assertEquals("Embargo Repo did not save the embargo!", 1, embargoTypeRepo.count());
        assertEquals("Embargo Repo did not save the correct embargo name!", TEST_EMBARGO_NAME, testEmbargo.getName());
        assertEquals("Embargo Repo did not save the correct embargo description!", TEST_EMBARGO_DESCRIPTION, testEmbargo.getDescription());
        assertEquals("Embargo Repo did not save the correct embargo duration!", TEST_EMBARGO_DURATION, testEmbargo.getDuration());
    
        
        embargoTypeRepo.create("Test", "Test", 1);
        
        EmbargoType testEmbargo2 = embargoTypeRepo.create("Proquest", "Proquest", 1);
        
        testEmbargo2.setGuarantor(EmbargoGuarantor.PROQUEST);

        embargoTypeRepo.save(testEmbargo2);
        
        entityControlledVocabularyRepo.getEntityNames().forEach(entityName -> {
            System.out.println("\n" + entityName + "\n");
        });
        
        try {
            entityControlledVocabularyRepo.getControlledVocabulary("EmbargoType", "guarantor").forEach(property -> {
                System.out.println("\n" + property + "\n");
            });
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        
        entityControlledVocabularyRepo.getControlledVocabulary(EmbargoType.class, "guarantor").forEach(property -> {
            System.out.println("\n" + property + "\n");
        });
    
    }
    
    @Override
    public void testDuplication() {
    }

    @Override
    public void testDelete() {
    }

    @Override
    public void testCascade() {
    }

    @After
    public void cleanUp() {
        embargoTypeRepo.deleteAll();
    }


}