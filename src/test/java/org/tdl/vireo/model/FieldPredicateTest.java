package org.tdl.vireo.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.After;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.dao.DataIntegrityViolationException;
import org.tdl.vireo.Application;
import org.tdl.vireo.runner.OrderedRunner;

@RunWith(OrderedRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class FieldPredicateTest extends AbstractEntityTest {
		
	@Override
	public void testCreate() {
		FieldPredicate fieldPredicate = fieldPredicateRepo.create(TEST_FIELD_PREDICATE_VALUE);
		assertEquals("The repository did not save the entity!", 1, fieldPredicateRepo.count());
        assertEquals("Saved entity did not contain the value!", TEST_FIELD_PREDICATE_VALUE, fieldPredicate.getValue());
	}
	
	@Override
	public void testDuplication() {
	    fieldPredicateRepo.create(TEST_FIELD_PREDICATE_VALUE);
        try {
            fieldPredicateRepo.create(TEST_FIELD_PREDICATE_VALUE);
        } catch (DataIntegrityViolationException e) { /* SUCCESS */ }
        assertEquals("The repository duplicated entity!", 1, fieldPredicateRepo.count());
	}
	
	@Override
	public void testFind() {
	    fieldPredicateRepo.create(TEST_FIELD_PREDICATE_VALUE);
	    FieldPredicate fieldPredicate = fieldPredicateRepo.findByValue(TEST_FIELD_PREDICATE_VALUE);
	    assertNotEquals("Did not find entity!", null, fieldPredicate);
        assertEquals("Found entity did not contain the correct value!", TEST_FIELD_PREDICATE_VALUE, fieldPredicate.getValue());
	}
	
	@Override
	public void testDelete() {
	    FieldPredicate fieldPredicate = fieldPredicateRepo.create(TEST_FIELD_PREDICATE_VALUE);
	    fieldPredicateRepo.delete(fieldPredicate);
	    assertEquals("The entity was not deleted!", 0, fieldPredicateRepo.count());
	}
	
	@Override
	public void testCascade() { }
	
	@After
	public void cleanUp() {
		fieldPredicateRepo.deleteAll();
	}

}

