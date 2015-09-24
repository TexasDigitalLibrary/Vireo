package org.tdl.vireo.model;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.tdl.vireo.Application;
import org.tdl.vireo.annotations.Order;
import org.tdl.vireo.model.repo.WorkflowRepo;
import org.tdl.vireo.model.repo.WorkflowStepRepo;
import org.tdl.vireo.runner.OrderedRunner;

@RunWith(OrderedRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class WorkflowTest {
	
    static final String TEST_WORKFLOW_NAME = "Test Workflow";
    
    static final boolean TEST_WORKFLOW_INHERITABILITY = true;
    
    static final String TEST_WORKFLOW_STEP_NAME = "Test Workflow Step";
    static final String TEST_DETACHABLE_WORKFLOW_STEP_NAME = "Test Detachable Workflow Step";

	@Autowired
	private WorkflowRepo workflowRepo;
	
	@Autowired
	private WorkflowStepRepo workflowStepRepo;
	
	@BeforeClass
    public static void init() {
		
    }
	
	@Before
	public void setUp() {
		
	}
	
	@Test
	@Order(value = 1)
	public void testCreate() {
		Workflow workflow = workflowRepo.create(TEST_WORKFLOW_NAME, TEST_WORKFLOW_INHERITABILITY);
		WorkflowStep workflowStep = workflowStepRepo.create(TEST_WORKFLOW_STEP_NAME);
		workflow.addWorkflowStep(workflowStep);
		workflow = workflowRepo.save(workflow);
		assertEquals("The repository did not save the entity!", 1, workflowRepo.count());
		assertEquals("Saved entity did not contain the correct name!", TEST_WORKFLOW_NAME, workflow.getName());
		assertEquals("Saved entity did not contain the correct number of workflow steps!", 1, workflow.getWorkflowSteps().size());
		assertEquals("Saved entity did not contain the correct workflow step name!", TEST_WORKFLOW_STEP_NAME, ((WorkflowStep) workflow.getWorkflowSteps().toArray()[0]).getName());
	}
	
	@Test
	@Order(value = 2)
	public void testDelete() {
		Workflow workflow = workflowRepo.create(TEST_WORKFLOW_NAME, TEST_WORKFLOW_INHERITABILITY);
		workflowRepo.delete(workflow);
		assertEquals("Entity did not delete!", 0, workflowRepo.count());
	}
	
	@Test
	@Order(value = 3)
	public void testCascade() {
		Workflow workflow = workflowRepo.create(TEST_WORKFLOW_NAME, TEST_WORKFLOW_INHERITABILITY);
		WorkflowStep workflowStep = workflowStepRepo.create(TEST_WORKFLOW_STEP_NAME);
		WorkflowStep detachableWorkflowStep = workflowStepRepo.create(TEST_DETACHABLE_WORKFLOW_STEP_NAME);
		workflow.addWorkflowStep(workflowStep);
		workflow.addWorkflowStep(detachableWorkflowStep);
		workflow = workflowRepo.save(workflow);
		
		// check number of workflow steps
		assertEquals("Saved entity did not contain the correct number of workflow steps!", 2, workflow.getWorkflowSteps().size());
		assertEquals("WorkflowStep repo does not have the correct number of workflow steps", 2, workflowStepRepo.count());
		
		// duck typing of workflow steps
		assertEquals("Saved entity did not contain the correct workflow step name!", TEST_WORKFLOW_STEP_NAME, workflow.getWorkflowStepById(workflowStep.getId()).getName());
		assertEquals("Saved entity did not contain the correct workflow detachable step name!", TEST_DETACHABLE_WORKFLOW_STEP_NAME, workflow.getWorkflowStepById(detachableWorkflowStep.getId()).getName());
		
		// test detach detachable workflow step
		workflow.removeWorkflowStep(detachableWorkflowStep);
		workflow = workflowRepo.update(workflow);
		assertEquals("The workflow step was not detached!", 1, workflow.getWorkflowSteps().size());	
		assertEquals("The workflow step was orphaned!", 1, workflowStepRepo.count());
		
		// test delete workflow
		workflowRepo.delete(workflow);
		assertEquals("The workflow was not deleted!", 0, workflowRepo.count());
		assertEquals("The workflow step was orphaned!", 0, workflowStepRepo.count());		
	}
	
	@After
	public void cleanUp() {
		workflowRepo.deleteAll();
		workflowStepRepo.deleteAll();
	}

}
