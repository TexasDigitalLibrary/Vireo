package org.tdl.vireo.model;

import static org.junit.Assert.assertEquals;

import org.junit.After;

public class WorkflowTest extends AbstractEntityTest {

    @Override
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

    @Override
    public void testDelete() {
        Workflow workflow = workflowRepo.create(TEST_WORKFLOW_NAME, TEST_WORKFLOW_INHERITABILITY);
        workflowRepo.delete(workflow);
        assertEquals("Entity did not delete!", 0, workflowRepo.count());
    }
    
    @Override
    public void testDuplication() {
        
    }

    @Override
    public void testCascade() {
        Workflow workflow = workflowRepo.create(TEST_WORKFLOW_NAME, TEST_WORKFLOW_INHERITABILITY);
        WorkflowStep workflowStep = workflowStepRepo.create(TEST_WORKFLOW_STEP_NAME);
        WorkflowStep severableWorkflowStep = workflowStepRepo.create(TEST_SEVERABLE_WORKFLOW_STEP_NAME);
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

        // reattach severable workflow step
        workflow.addWorkflowStep(severableWorkflowStep);
        workflow = workflowRepo.save(workflow);
        assertEquals("The workflow step was not reattached!", 2, workflowStepRepo.count());

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
