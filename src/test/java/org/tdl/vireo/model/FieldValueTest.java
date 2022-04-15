package org.tdl.vireo.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.transaction.annotation.Transactional;

public class FieldValueTest extends AbstractEntityTest {

    @BeforeEach
    public void setup() {
        fieldPredicate = fieldPredicateRepo.create(TEST_FIELD_PREDICATE_VALUE, false);
    }

    @Transactional
    @Override
    public void testCreate() {
        FieldValue fieldValue = fieldValueRepo.create(fieldPredicate);
        fieldValue.setValue(TEST_FIELD_VALUE);
        fieldValue = fieldValueRepo.save(fieldValue);
        assertEquals(1, fieldValueRepo.count(), "The repository did not save the entity!");
        assertEquals(TEST_FIELD_VALUE, fieldValue.getValue(), "Saved entity did not contain the value!");
        assertEquals(TEST_FIELD_PREDICATE_VALUE, fieldValue.getFieldPredicate().getValue(), "Saved entity did not contain the predicate value!");
    }

    @Override
    public void testDelete() {
        FieldValue fieldValue = fieldValueRepo.create(fieldPredicate);
        fieldValueRepo.delete(fieldValue);
        assertEquals(0, fieldValueRepo.count(), "The entity was not deleted!");
    }

    @Override
    public void testDuplication() {

    }

    @Override
    public void testCascade() {
        FieldValue fieldValue = fieldValueRepo.create(fieldPredicate);
        fieldValueRepo.delete(fieldValue);
        assertEquals(0, fieldValueRepo.count(), "The entity was not deleted!");
        assertEquals(1, fieldPredicateRepo.count(), "The entity was deleted!");
    }

    @AfterEach
    public void cleanUp() {
        fieldValueRepo.deleteAll();
        fieldPredicateRepo.deleteAll();
    }

}
