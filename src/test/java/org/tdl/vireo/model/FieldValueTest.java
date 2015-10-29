package org.tdl.vireo.model;

import static org.junit.Assert.assertEquals;

import org.junit.After;

public class FieldValueTest extends AbstractEntityTest {

    @Override
    public void testCreate() {
        FieldPredicate fieldPredicate = fieldPredicateRepo.create(TEST_FIELD_PREDICATE_VALUE);
        FieldValue fieldValue = fieldValueRepo.create(fieldPredicate);
        fieldValue.setValue(TEST_FIELD_VALUE);
        fieldValue = fieldValueRepo.save(fieldValue);
        assertEquals("The repository did not save the entity!", 1, fieldValueRepo.count());
        assertEquals("Saved entity did not contain the value!", TEST_FIELD_VALUE, fieldValue.getValue());
        assertEquals("Saved entity did not contain the predicate value!", TEST_FIELD_PREDICATE_VALUE, fieldValue.getPredicate().getValue());
    }

    @Override
    public void testDelete() {
        FieldPredicate fieldPredicate = fieldPredicateRepo.create(TEST_FIELD_PREDICATE_VALUE);
        FieldValue fieldValue = fieldValueRepo.create(fieldPredicate);
        fieldValueRepo.delete(fieldValue);
        assertEquals("The entity was not deleted!", 0, fieldValueRepo.count());
    }

    @Override
    public void testDuplication() {
        
    }

    @Override
    public void testCascade() {
        FieldPredicate fieldPredicate = fieldPredicateRepo.create(TEST_FIELD_PREDICATE_VALUE);
        FieldValue fieldValue = fieldValueRepo.create(fieldPredicate);
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
