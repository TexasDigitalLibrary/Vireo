package org.tdl.vireo.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;

public class FieldPredicateTest extends AbstractEntityTest {

    @Override
    @Test
    public void testCreate() {
        FieldPredicate fieldPredicate = fieldPredicateRepo.create(TEST_FIELD_PREDICATE_VALUE, Boolean.valueOf(false));
        assertEquals(1, fieldPredicateRepo.count(), "The repository did not save the entity!");
        assertEquals(TEST_FIELD_PREDICATE_VALUE, fieldPredicate.getValue(), "Saved entity did not contain the value!");
    }

    @Override
    @Test
    public void testDuplication() {
        fieldPredicateRepo.create(TEST_FIELD_PREDICATE_VALUE, Boolean.valueOf(false));
        try {
            fieldPredicateRepo.create(TEST_FIELD_PREDICATE_VALUE, Boolean.valueOf(false));
        } catch (DataIntegrityViolationException e) {
            /* SUCCESS */ }
        assertEquals(1, fieldPredicateRepo.count(), "The repository duplicated entity!");
    }

    @Override
    @Test
    public void testDelete() {
        FieldPredicate fieldPredicate = fieldPredicateRepo.create(TEST_FIELD_PREDICATE_VALUE, Boolean.valueOf(false));
        fieldPredicateRepo.delete(fieldPredicate);
        assertEquals(0, fieldPredicateRepo.count(), "The entity was not deleted!");
    }

    @Override
    @Test
    public void testCascade() {

    }

    @AfterEach
    public void cleanUp() {
        fieldPredicateRepo.deleteAll();
    }

}
