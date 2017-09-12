package org.tdl.vireo.model;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.springframework.dao.DataIntegrityViolationException;

public class FieldPredicateTest extends AbstractEntityTest {

    @Override
    public void testCreate() {
        FieldPredicate fieldPredicate = fieldPredicateRepo.create(TEST_FIELD_PREDICATE_VALUE, new Boolean(false));
        assertEquals("The repository did not save the entity!", 1, fieldPredicateRepo.count());
        assertEquals("Saved entity did not contain the value!", TEST_FIELD_PREDICATE_VALUE, fieldPredicate.getValue());
    }

    @Override
    public void testDuplication() {
        fieldPredicateRepo.create(TEST_FIELD_PREDICATE_VALUE, new Boolean(false));
        try {
            fieldPredicateRepo.create(TEST_FIELD_PREDICATE_VALUE, new Boolean(false));
        } catch (DataIntegrityViolationException e) {
            /* SUCCESS */ }
        assertEquals("The repository duplicated entity!", 1, fieldPredicateRepo.count());
    }

    @Override
    public void testDelete() {
        FieldPredicate fieldPredicate = fieldPredicateRepo.create(TEST_FIELD_PREDICATE_VALUE, new Boolean(false));
        fieldPredicateRepo.delete(fieldPredicate);
        assertEquals("The entity was not deleted!", 0, fieldPredicateRepo.count());
    }

    @Override
    public void testCascade() {

    }

    @After
    public void cleanUp() {
        fieldPredicateRepo.deleteAll();
    }

}
