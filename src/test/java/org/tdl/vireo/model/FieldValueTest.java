package org.tdl.vireo.model;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.tdl.vireo.Application;
import org.tdl.vireo.annotations.Order;
import org.tdl.vireo.model.repo.FieldPredicateRepo;
import org.tdl.vireo.model.repo.FieldValueRepo;
import org.tdl.vireo.runner.OrderedRunner;

@RunWith(OrderedRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class FieldValueTest {
	
	static final String TEST_FIELD_PREDICATE_VALUE = "test.predicate";
	static final String TEST_FIELD_VALUE = "Test Field Value";
	
	@Autowired
	private FieldValueRepo fieldValueRepo;
	
	@Autowired
	private FieldPredicateRepo fieldPredicateRepo;
	
	
	@Test
	@Order(value = 1)
	public void testCreate() {
		FieldPredicate fieldPredicate = fieldPredicateRepo.create(TEST_FIELD_PREDICATE_VALUE);
		FieldValue fieldValue = fieldValueRepo.create(fieldPredicate);
		fieldValue.setValue(TEST_FIELD_VALUE);
		fieldValue = fieldValueRepo.save(fieldValue);
		assertEquals("The repository did not save the entity!", 1, fieldValueRepo.count());
		assertEquals("Saved entity did not contain the value!", TEST_FIELD_VALUE, fieldValue.getValue());
		assertEquals("Saved entity did not contain the predicate value!", TEST_FIELD_PREDICATE_VALUE, fieldValue.getPredicate().getValue());
	}
		
	@Test
	@Order(value = 2)
	public void testDelete() {
		FieldPredicate fieldPredicate = fieldPredicateRepo.create(TEST_FIELD_PREDICATE_VALUE);
		FieldValue fieldValue = fieldValueRepo.create(fieldPredicate);
		fieldValueRepo.delete(fieldValue);
		assertEquals("The entity was not deleted!", 0, fieldValueRepo.count());
	}
	
	@Test
	@Order(value = 3)
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

