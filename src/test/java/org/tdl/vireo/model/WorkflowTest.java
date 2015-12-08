package org.tdl.vireo.model;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;

public class WorkflowTest extends AbstractEntityTest {

    @Before
    public void setup() {
        parentCategory = organizationCategoryRepo.create(TEST_CATEGORY_NAME, TEST_CATEGORY_LEVEL);
        organization = organizationRepo.create(TEST_ORGANIZATION_NAME, parentCategory);
    }
    
    @Override
    public void testCreate() {
        Workflow workflow = workflowRepo.create(TEST_WORKFLOW_NAME, TEST_WORKFLOW_INHERITABILITY, organization);
        WorkflowStep workflowStep = workflowStepRepo.create(TEST_WORKFLOW_STEP_NAME, workflow);
        workflow.addWorkflowStep(workflowStep);
        workflow = workflowRepo.save(workflow);
        assertEquals("The repository did not save the entity!", 1, workflowRepo.count());
        assertEquals("Saved entity did not contain the correct name!", TEST_WORKFLOW_NAME, workflow.getName());
        assertEquals("Saved entity did not contain the correct number of workflow steps!", 1, workflow.getWorkflowSteps().size());
        assertEquals("Saved entity did not contain the correct workflow step name!", TEST_WORKFLOW_STEP_NAME, ((WorkflowStep) workflow.getWorkflowSteps().toArray()[0]).getName());
    }

    @Override
    public void testDelete() {
        Workflow workflow = workflowRepo.create(TEST_WORKFLOW_NAME, TEST_WORKFLOW_INHERITABILITY, organization);
        workflowRepo.delete(workflow);
        assertEquals("Entity did not delete!", 0, workflowRepo.count());
    }
    
    @Override
    public void testDuplication() {
        
    }

    @Override
    public void testCascade() {
        Workflow workflow = workflowRepo.create(TEST_WORKFLOW_NAME, TEST_WORKFLOW_INHERITABILITY, organization);
        WorkflowStep workflowStep = workflowStepRepo.create(TEST_WORKFLOW_STEP_NAME, workflow);
        WorkflowStep severableWorkflowStep = workflowStepRepo.create(TEST_SEVERABLE_WORKFLOW_STEP_NAME, workflow);
        workflow.addWorkflowStep(workflowStep);
        workflow.addWorkflowStep(severableWorkflowStep);
        workflow = workflowRepo.save(workflow);

        // check number of workflow steps
        assertEquals("Saved entity did not contain the correct number of workflow steps!", 2, workflow.getWorkflowSteps().size());
        assertEquals("WorkflowStep repo does not have the correct number of workflow steps", 2, workflowStepRepo.count());

        // verify workflow steps
        assertEquals("Saved entity did not contain the correct workflow step name!", workflowStep, (WorkflowStep) workflow.getWorkflowSteps().toArray()[0]);
        assertEquals("Saved entity did not contain the correct workflow severable step name!", severableWorkflowStep, (WorkflowStep) workflow.getWorkflowSteps().toArray()[1]);

        // test remove severable workflow step
        workflow.removeWorkflowStep(severableWorkflowStep);
        workflow = workflowRepo.save(workflow);
        assertEquals("The workflow step was not removed!", 1, workflow.getWorkflowSteps().size());

        assertEquals("The workflow step was orphaned!", 1, workflowStepRepo.count());

        // create and reattach severable workflow step
        severableWorkflowStep = workflowStepRepo.create(TEST_SEVERABLE_WORKFLOW_STEP_NAME, workflow);
        workflow.addWorkflowStep(severableWorkflowStep);
        workflow = workflowRepo.save(workflow);
        assertEquals("The workflow step was not reattached!", 2, workflow.getWorkflowSteps().size());

        // test delete workflow
        workflowRepo.delete(workflow);
        assertEquals("The workflow was not deleted!", 0, workflowRepo.count());
        assertEquals("The workflow step was orphaned!", 0, workflowStepRepo.count());
    }

    @After
    public void cleanUp() {
        workflowStepRepo.deleteAll();
        workflowRepo.deleteAll();        
        organizationRepo.deleteAll();
        organizationCategoryRepo.deleteAll();
    }

}
