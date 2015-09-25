package org.tdl.vireo.model;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.tdl.vireo.Application;
import org.tdl.vireo.annotations.Order;
import org.tdl.vireo.enums.InputType;
import org.tdl.vireo.model.repo.FieldPredicateRepo;
import org.tdl.vireo.model.repo.FieldProfileRepo;
import org.tdl.vireo.model.repo.WorkflowStepRepo;
import org.tdl.vireo.runner.OrderedRunner;

@RunWith(OrderedRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class WorkflowStepTest {
	
	static final String TEST_WORKFLOW_STEP_NAME = "Test Workflow Step";
	
	static final String TEST_FIELD_PREDICATE_VALUE = "test.predicate";	
	static final Boolean TEST_FIELD_PROFILE_REPEATABLE = true;
	static final Boolean TEST_FIELD_PROFILE_REQUIRED = true;
	static final InputType TEST_FIELD_PROFILE_INPUT_TYPE = InputType.INPUT_TEXT;
	
	static final String TEST_DETACHABLE_FIELD_PREDICATE_VALUE = "test.detachable.predicate";
	static final Boolean TEST_DETACHABLE_FIELD_PROFILE_REPEATABLE = false;
	static final Boolean TEST_DETACHABLE_FIELD_PROFILE_REQUIRED = false;
	static final InputType TEST_DETACHABLE_FIELD_PROFILE_INPUT_TYPE = InputType.INPUT_TEXT;

	@Autowired
	private WorkflowStepRepo workflowStepRepo;
	
	@Autowired
	private FieldProfileRepo fieldProfileRepo;
	
	@Autowired
	private FieldPredicateRepo fieldPredicateRepo;
	
	@Test
	@Order(value = 1)
	public void testCreate() {
		WorkflowStep workflowStep = workflowStepRepo.create(TEST_WORKFLOW_STEP_NAME);
		FieldPredicate fieldPredicate = fieldPredicateRepo.create(TEST_FIELD_PREDICATE_VALUE);
		FieldProfile fieldProfile = fieldProfileRepo.create(fieldPredicate, TEST_FIELD_PROFILE_INPUT_TYPE, TEST_FIELD_PROFILE_REPEATABLE, TEST_FIELD_PROFILE_REQUIRED);
		workflowStep.addFieldProfile(fieldProfile);
		workflowStep = workflowStepRepo.save(workflowStep);
		assertEquals("The repository did not save the entity!", 1, workflowStepRepo.count());
		assertEquals("Saved entity did not contain the name!", TEST_WORKFLOW_STEP_NAME, workflowStep.getName());
		assertEquals("Saved entity did not contain the field profile repeatable value!", TEST_FIELD_PROFILE_REPEATABLE, workflowStep.getFieldProfileById(fieldProfile.getId()).getRepeatable());
		assertEquals("Saved entity did not contain the field profile required value!", TEST_FIELD_PROFILE_REQUIRED, workflowStep.getFieldProfileById(fieldProfile.getId()).getRequired());
		assertEquals("Saved entity did not contain the field profile input type!", TEST_FIELD_PROFILE_INPUT_TYPE, workflowStep.getFieldProfileById(fieldProfile.getId()).getInputType());
		assertEquals("Saved entity did not contain the field profile field predicate value!", TEST_FIELD_PREDICATE_VALUE, workflowStep.getFieldProfileById(fieldProfile.getId()).getFieldPredicate().getValue());
	}
	
	@Test
	@Order(value = 2)
	public void testDelete() {
		WorkflowStep workflowStep = workflowStepRepo.create(TEST_WORKFLOW_STEP_NAME);
		workflowStepRepo.delete(workflowStep);
		assertEquals("Entity did not delete!", 0, workflowStepRepo.count());
	}
	
	@Test
	@Order(value = 3)
	public void testCascade() {
		WorkflowStep workflowStep = workflowStepRepo.create(TEST_WORKFLOW_STEP_NAME);
		FieldPredicate fieldPredicate = fieldPredicateRepo.create(TEST_FIELD_PREDICATE_VALUE);
		FieldPredicate detachableFieldPredicate = fieldPredicateRepo.create(TEST_DETACHABLE_FIELD_PREDICATE_VALUE);
		FieldProfile fieldProfile = fieldProfileRepo.create(fieldPredicate, TEST_FIELD_PROFILE_INPUT_TYPE, TEST_FIELD_PROFILE_REPEATABLE, TEST_FIELD_PROFILE_REQUIRED);
		FieldProfile detachableFieldProfile = fieldProfileRepo.create(detachableFieldPredicate, TEST_DETACHABLE_FIELD_PROFILE_INPUT_TYPE, TEST_DETACHABLE_FIELD_PROFILE_REPEATABLE, TEST_DETACHABLE_FIELD_PROFILE_REQUIRED);
		workflowStep.addFieldProfile(fieldProfile);
		workflowStep.addFieldProfile(detachableFieldProfile);
		workflowStep = workflowStepRepo.save(workflowStep);
		
		// check number of field profiles
		assertEquals("Saved entity did not contain the correct number of field profiles!", 2, workflowStep.getFieldProfiles().size());
		assertEquals("WorkflowStep repo does not have the correct number of field profiles", 2, fieldProfileRepo.count());
		
		// check number of field predicates
		assertEquals("WorkflowStep repo does not have the correct number of field profiles", 2, fieldPredicateRepo.count());
		
		// verify field profiles
		assertEquals("Saved entity did not contain the field profile repeatable value!", TEST_FIELD_PROFILE_REPEATABLE, workflowStep.getFieldProfileById(fieldProfile.getId()).getRepeatable());
		assertEquals("Saved entity did not contain the field profile required value!", TEST_FIELD_PROFILE_REQUIRED, workflowStep.getFieldProfileById(fieldProfile.getId()).getRequired());
		assertEquals("Saved entity did not contain the field profile input type!", TEST_FIELD_PROFILE_INPUT_TYPE, workflowStep.getFieldProfileById(fieldProfile.getId()).getInputType());
		assertEquals("Saved entity did not contain the field profile repeatable value!", TEST_DETACHABLE_FIELD_PROFILE_REPEATABLE, workflowStep.getFieldProfileById(detachableFieldProfile.getId()).getRepeatable());
		assertEquals("Saved entity did not contain the field profile required value!", TEST_DETACHABLE_FIELD_PROFILE_REQUIRED, workflowStep.getFieldProfileById(detachableFieldProfile.getId()).getRequired());
		assertEquals("Saved entity did not contain the field profile input type!", TEST_DETACHABLE_FIELD_PROFILE_INPUT_TYPE, workflowStep.getFieldProfileById(detachableFieldProfile.getId()).getInputType());
		
		// verify field predicates
		assertEquals("Saved entity did not contain the field profile field predicate value!", TEST_FIELD_PREDICATE_VALUE, workflowStep.getFieldProfileById(fieldProfile.getId()).getFieldPredicate().getValue());
		assertEquals("Saved entity did not contain the field profile field predicate value!", TEST_DETACHABLE_FIELD_PREDICATE_VALUE, workflowStep.getFieldProfileById(detachableFieldProfile.getId()).getFieldPredicate().getValue());
				
		// test detach detachable workflow step
		workflowStep.removeFieldProfile(detachableFieldProfile);
		workflowStep = workflowStepRepo.save(workflowStep);
		assertEquals("The field profile was not detached!", 1, workflowStep.getFieldProfiles().size());	
		assertEquals("The field profile was deleted!", 2, fieldProfileRepo.count());
		
		// test delete workflow step
		workflowStepRepo.delete(workflowStep);
		assertEquals("The workflow step was not deleted!", 0, workflowStepRepo.count());
		assertEquals("The field profiles were deleted!", 2, fieldProfileRepo.count());
		assertEquals("The field predicate was deleted!", 2, fieldPredicateRepo.count());
	}
	
	@After
	public void cleanUp() {
		workflowStepRepo.deleteAll();
		fieldProfileRepo.deleteAll();
		fieldPredicateRepo.deleteAll();		
	}

}
