package org.tdl.vireo.model;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.springframework.dao.DataIntegrityViolationException;

public class FieldProfileTest extends AbstractEntityTest {

    @Before
    public void setUp() {
        assertEquals("The repository was not empty!", 0, languageRepo.count());
        assertEquals("The repository was not empty!", 0, fieldProfileRepo.count());
        assertEquals("The repository was not empty!", 0, fieldPredicateRepo.count());
        language = languageRepo.create(TEST_LANGUAGE);
        fieldPredicate = fieldPredicateRepo.create(TEST_FIELD_PREDICATE_VALUE);
        parentCategory = organizationCategoryRepo.create(TEST_CATEGORY_NAME);
        organization = organizationRepo.create(TEST_ORGANIZATION_NAME, parentCategory);
        workflowStep = workflowStepRepo.create(TEST_WORKFLOW_STEP_NAME, organization);
    }

    @Override
    public void testCreate() {
        FieldProfile fieldProfile = fieldProfileRepo.create(workflowStep, fieldPredicate, TEST_FIELD_PROFILE_INPUT_TYPE, TEST_FIELD_PROFILE_USAGE, TEST_FIELD_PROFILE_REPEATABLE, TEST_FIELD_PROFILE_OVERRIDEABLE, TEST_FIELD_PROFILE_ENABLED, TEST_FIELD_PROFILE_OPTIONAL);
        assertEquals("The repository did not save the entity!", 1, fieldProfileRepo.count());
        assertEquals("The field profile did not contain the correct perdicate value!", fieldPredicate, fieldProfile.getPredicate());
        assertEquals("The field predicate did not contain the correct value!", TEST_FIELD_PROFILE_INPUT_TYPE, fieldProfile.getInputType());
        assertEquals("The field predicate did not contain the correct value!", TEST_FIELD_PROFILE_USAGE, fieldProfile.getUsage());
        assertEquals("The field predicate did not contain the correct value!", TEST_FIELD_PROFILE_REPEATABLE, fieldProfile.getRepeatable());
        assertEquals("The field predicate did not contain the correct value!", TEST_FIELD_PROFILE_ENABLED, fieldProfile.getEnabled());
        assertEquals("The field predicate did not contain the correct value!", TEST_FIELD_PROFILE_OPTIONAL, fieldProfile.getOptional());
    }

    @Override
    public void testDuplication() {
        fieldProfileRepo.create(workflowStep, fieldPredicate, TEST_FIELD_PROFILE_INPUT_TYPE, TEST_FIELD_PROFILE_USAGE, TEST_FIELD_PROFILE_REPEATABLE, TEST_FIELD_PROFILE_OVERRIDEABLE, TEST_FIELD_PROFILE_ENABLED, TEST_FIELD_PROFILE_OPTIONAL);
        try {
        	fieldProfileRepo.create(workflowStep, fieldPredicate, TEST_FIELD_PROFILE_INPUT_TYPE, TEST_FIELD_PROFILE_USAGE, TEST_FIELD_PROFILE_REPEATABLE, TEST_FIELD_PROFILE_OVERRIDEABLE, TEST_FIELD_PROFILE_ENABLED, TEST_FIELD_PROFILE_OPTIONAL);
        } 
        catch (DataIntegrityViolationException e) { /* SUCCESS */ }
        assertEquals("The repository duplicated entity!", 1, fieldProfileRepo.count());
    }

    @Override
    public void testDelete() {
        FieldProfile fieldProfile = fieldProfileRepo.create(workflowStep, fieldPredicate, TEST_FIELD_PROFILE_INPUT_TYPE, TEST_FIELD_PROFILE_USAGE, TEST_FIELD_PROFILE_REPEATABLE, TEST_FIELD_PROFILE_OVERRIDEABLE, TEST_FIELD_PROFILE_ENABLED, TEST_FIELD_PROFILE_OPTIONAL);
        fieldProfileRepo.delete(fieldProfile);
        assertEquals("Entity did not delete!", 0, fieldProfileRepo.count());
    }

    @Override
    public void testCascade() {
        // create field profile
        FieldProfile fieldProfile = fieldProfileRepo.create(workflowStep, fieldPredicate, TEST_FIELD_PROFILE_INPUT_TYPE, TEST_FIELD_PROFILE_USAGE, TEST_FIELD_PROFILE_REPEATABLE, TEST_FIELD_PROFILE_OVERRIDEABLE, TEST_FIELD_PROFILE_ENABLED, TEST_FIELD_PROFILE_OPTIONAL);

        // add glosses and controlled vocabularies
        FieldGloss fieldGloss = fieldGlossRepo.create(TEST_FIELD_GLOSS_VALUE, language);
        FieldGloss severableFieldGloss = fieldGlossRepo.create(TEST_SEVERABLE_FIELD_GLOSS_VALUE, language);
        ControlledVocabulary controlledVocabulary = controlledVocabularyRepo.create(TEST_CONTROLLED_VOCABULARY_NAME, language);
        ControlledVocabulary severablecontrolledVocabulary = controlledVocabularyRepo.create(TEST_SEVERABLE_CONTROLLED_VOCABULARY_NAME, language);
        fieldProfile.addFieldGloss(fieldGloss);
        fieldProfile.addControlledVocabulary(controlledVocabulary);
        fieldProfile.addFieldGloss(severableFieldGloss);
        fieldProfile.addControlledVocabulary(severablecontrolledVocabulary);
        fieldProfile = fieldProfileRepo.save(fieldProfile);

        // verify field glosses and controlled vocabularies
        assertEquals("The field profile did not contain the correct field gloss!", fieldGloss, (FieldGloss) fieldProfile.getFieldGlosses().toArray()[0]);
        assertEquals("The field profile did not contain the correct severable field gloss!", severableFieldGloss, (FieldGloss) fieldProfile.getFieldGlosses().toArray()[1]);
        assertEquals("The field profile did not contain the correct controlled vocabulary!", controlledVocabulary, fieldProfile.getControlledVocabularyByName(TEST_CONTROLLED_VOCABULARY_NAME));
        assertEquals("The field profile did not contain the correct severable controlled vocabulary!", severablecontrolledVocabulary, fieldProfile.getControlledVocabularyByName(TEST_SEVERABLE_CONTROLLED_VOCABULARY_NAME));

        // test remove severable gloss
        fieldProfile.removeFieldGloss(severableFieldGloss);
        fieldProfile = fieldProfileRepo.save(fieldProfile);
        assertEquals("The field profile had the incorrect number of glosses!", 1, fieldProfile.getFieldGlosses().size());

        // test remove severable controlled vocabularies
        fieldProfile.removeControlledVocabulary(severablecontrolledVocabulary);
        fieldProfile = fieldProfileRepo.save(fieldProfile);
        assertEquals("The field profile had the incorrect number of glosses!", 1, fieldProfile.getControlledVocabularies().size());

        // test delete profile
        fieldProfileRepo.delete(fieldProfile);
        assertEquals("An field profile was not deleted!", 0, fieldProfileRepo.count());
        assertEquals("The language was deleted!", 1, languageRepo.count());
        assertEquals("The field predicate was deleted!", 1, fieldPredicateRepo.count());
        assertEquals("The field glosses were deleted!", 2, fieldGlossRepo.count());
        assertEquals("The controlled vocabularies were deleted!", 2, controlledVocabularyRepo.count());

    }
    
//    @Test
//    @Transactional
//    public void testInheritFieldProfileViaPointer() {
//        Organization parentOrganization = organizationRepo.create(TEST_PARENT_ORGANIZATION_NAME, parentCategory);
//        Organization childOrganization = organizationRepo.create(TEST_CHILD_ORGANIZATION_NAME, parentCategory);
//        parentOrganization.addChildOrganization(childOrganization);
//        Organization grandchildOrganization = organizationRepo.create(TEST_GRAND_CHILD_ORGANIZATION_NAME, parentCategory);
//        childOrganization.addChildOrganization(grandchildOrganization);
//        
//        WorkflowStep parentWorkflowStep = workflowStepRepo.create(TEST_PARENT_WORKFLOW_STEP_NAME, parentOrganization);
//        FieldProfile fieldProfile = fieldProfileRepo.create(parentWorkflowStep, fieldPredicate, TEST_FIELD_PROFILE_INPUT_TYPE, TEST_FIELD_PROFILE_USAGE, TEST_FIELD_PROFILE_REPEATABLE, TEST_FIELD_PROFILE_OVERRIDEABLE, TEST_FIELD_PROFILE_ENABLED, TEST_FIELD_PROFILE_OPTIONAL);
//        
//        FieldProfile parentFieldProfile = parentOrganization.getWorkflowSteps().get(0).getFieldProfiles().get(0);
//        FieldProfile childFieldProfile = childOrganization.getWorkflowSteps().get(0).getFieldProfiles().get(0);
//        FieldProfile grandchildFieldProfile = grandchildOrganization.getWorkflowSteps().get(0).getFieldProfiles().get(0);
//
//        
//        assertEquals("The parent organization's workflow did not contain the fieldProfile", fieldProfile.getId(), parentFieldProfile.getId());
//        assertEquals("The parent organization's workflow did not contain the fieldProfile", fieldProfile.getId(), childFieldProfile.getId());
//        assertEquals("The parent organization's workflow did not contain the fieldProfile's predicate", fieldProfile.getPredicate().getId(), parentFieldProfile.getPredicate().getId());
//        assertEquals("The parent organization's workflow did not contain the fieldProfile's predicate", fieldProfile.getPredicate().getId(), childFieldProfile.getPredicate().getId());
//        
//        String updatedFieldPredicateValue = "Updated Value";
//        parentFieldProfile.getPredicate().setValue(updatedFieldPredicateValue);
//        fieldProfileRepo.save(parentFieldProfile);
//        
//        assertEquals("The child fieldProfile's value did not recieve updated value", updatedFieldPredicateValue, childFieldProfile.getPredicate().getValue());
//        assertEquals("The child fieldProfile's value did not recieve updated value", updatedFieldPredicateValue, grandchildFieldProfile.getPredicate().getValue());
//    }
//    
//    @Test
//    @Transactional
//    public void testCanOverrideNonOverrideableAtOriginatingOrg() throws FieldProfileNonOverrideableException
//    {
//        Organization parentOrganization = organizationRepo.create(TEST_PARENT_ORGANIZATION_NAME, parentCategory);
//        Organization childOrganization = organizationRepo.create(TEST_CHILD_ORGANIZATION_NAME, parentCategory);
//        parentOrganization.addChildOrganization(childOrganization);
//        
//        WorkflowStep parentWorkflowStep = workflowStepRepo.create(TEST_PARENT_WORKFLOW_STEP_NAME, parentOrganization);
//        FieldProfile fieldProfile = fieldProfileRepo.create(parentWorkflowStep, fieldPredicate, TEST_FIELD_PROFILE_INPUT_TYPE, TEST_FIELD_PROFILE_USAGE, TEST_FIELD_PROFILE_REPEATABLE, TEST_FIELD_PROFILE_NONOVERRIDEABLE, TEST_FIELD_PROFILE_ENABLED, TEST_FIELD_PROFILE_OPTIONAL);
//
//        fieldProfile.setOverrideable(false);
//        fieldProfileRepo.save(fieldProfile);
//        
//        FieldProfile copyForUpdate = clone(fieldProfile);
//        copyForUpdate.setHelp("Help!");
//        
//        assertTrue("The setter didn't work for help string on the FieldProfile!", copyForUpdate.getHelp().equals("Help!"));
//        
//        assertFalse("The field profile didn't record that it was made non-overrideable!", fieldProfile.getOverrideable());
//        
//        //expect not to throw exception as this field profile originates in a workflow step originating in the parentOrganization
//        fieldProfile = fieldProfileRepo.update(copyForUpdate, parentOrganization);
//        assertTrue("The field profile wasn't updated to include the changed help!", fieldProfile.getHelp().equals("Help!"));
//        
//    }
    
    
    @After
    public void cleanUp() {
    	fieldProfileRepo.findAll().forEach(fieldProfile -> {
    		fieldProfileRepo.delete(fieldProfile);
        });
    	workflowStepRepo.findAll().forEach(workflowStep -> {
        	workflowStepRepo.delete(workflowStep);
        });
        organizationCategoryRepo.deleteAll();
        organizationRepo.findAll().forEach(organization -> {
            organizationRepo.delete(organization);
        });
        fieldPredicateRepo.deleteAll();
        fieldGlossRepo.deleteAll();        
        controlledVocabularyRepo.deleteAll();
        languageRepo.deleteAll();
    }
    
}
