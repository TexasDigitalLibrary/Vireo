package org.tdl.vireo.model;

import static org.junit.Assert.assertEquals;

import org.junit.After;

public class FieldValueTest extends AbstractEntityTest {

    @Override
    public void testCreate() {
    	FieldProfile fieldProfile = fieldProfileRepo.create(workflowStep, fieldPredicate, inputType, TEST_FIELD_PROFILE_USAGE, TEST_FIELD_PROFILE_REPEATABLE, TEST_FIELD_PROFILE_OVERRIDEABLE, TEST_FIELD_PROFILE_ENABLED, TEST_FIELD_PROFILE_OPTIONAL);
    	SubmissionFieldProfile submissionfieldProfile = submissionFieldProfileRepo.create(fieldProfile);
        FieldValue fieldValue = fieldValueRepo.create(submissionfieldProfile);
        fieldValue.setValue(TEST_FIELD_VALUE);
        fieldValue = fieldValueRepo.save(fieldValue);
        assertEquals("The repository did not save the entity!", 1, fieldValueRepo.count());
        assertEquals("Saved entity did not contain the value!", TEST_FIELD_VALUE, fieldValue.getValue());
        assertEquals("Saved entity did not contain the predicate value!", TEST_FIELD_PREDICATE_VALUE, fieldValue.getFieldPredicate().getValue());
    }

    @Override
    public void testDelete() {
    	FieldProfile fieldProfile = fieldProfileRepo.create(workflowStep, fieldPredicate, inputType, TEST_FIELD_PROFILE_USAGE, TEST_FIELD_PROFILE_REPEATABLE, TEST_FIELD_PROFILE_OVERRIDEABLE, TEST_FIELD_PROFILE_ENABLED, TEST_FIELD_PROFILE_OPTIONAL);
    	SubmissionFieldProfile submissionfieldProfile = submissionFieldProfileRepo.create(fieldProfile);
        FieldValue fieldValue = fieldValueRepo.create(submissionfieldProfile);
        fieldValueRepo.delete(fieldValue);
        assertEquals("The entity was not deleted!", 0, fieldValueRepo.count());
    }

    @Override
    public void testDuplication() {
        
    }

    @Override
    public void testCascade() {
    	FieldProfile fieldProfile = fieldProfileRepo.create(workflowStep, fieldPredicate, inputType, TEST_FIELD_PROFILE_USAGE, TEST_FIELD_PROFILE_REPEATABLE, TEST_FIELD_PROFILE_OVERRIDEABLE, TEST_FIELD_PROFILE_ENABLED, TEST_FIELD_PROFILE_OPTIONAL);
    	SubmissionFieldProfile submissionfieldProfile = submissionFieldProfileRepo.create(fieldProfile);
        FieldValue fieldValue = fieldValueRepo.create(submissionfieldProfile);
        fieldValueRepo.delete(fieldValue);
        assertEquals("The entity was not deleted!", 0, fieldValueRepo.count());
        assertEquals("The entity was deleted!", 1, fieldPredicateRepo.count());
    }

    @After
    public void cleanUp() {
        fieldValueRepo.deleteAll();
        fieldPredicateRepo.deleteAll();
    }

}
