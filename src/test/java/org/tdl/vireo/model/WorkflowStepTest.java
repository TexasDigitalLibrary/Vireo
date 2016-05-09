package org.tdl.vireo.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;
import org.tdl.vireo.annotations.Order;
import org.tdl.vireo.model.repo.impl.exception.WorkflowStepNonOverrideableException;

public class WorkflowStepTest extends AbstractEntityTest {

    @PersistenceContext
    EntityManager em;
    
    @Before
    public void setup() {
        parentCategory = organizationCategoryRepo.create(TEST_CATEGORY_NAME);
        organization = organizationRepo.create(TEST_ORGANIZATION_NAME, parentCategory);
    }
    
    @Override
    public void testCreate() {
        WorkflowStep workflowStep = workflowStepRepo.create(TEST_WORKFLOW_STEP_NAME, organization);
        FieldPredicate fieldPredicate = fieldPredicateRepo.create(TEST_FIELD_PREDICATE_VALUE);
        FieldProfile fieldProfile = fieldProfileRepo.create(workflowStep, fieldPredicate, TEST_FIELD_PROFILE_INPUT_TYPE, TEST_FIELD_PROFILE_USAGE, TEST_FIELD_PROFILE_REPEATABLE, TEST_FIELD_PROFILE_OVERRIDEABLE, TEST_FIELD_PROFILE_ENABLED, TEST_FIELD_PROFILE_OPTIONAL);
        assertEquals("The repository did not save the entity!", 1, workflowStepRepo.count());
        assertEquals("Saved entity did not contain the name!", TEST_WORKFLOW_STEP_NAME, workflowStep.getName());
        assertEquals("The field profile did not contain the correct value!", TEST_FIELD_PROFILE_INPUT_TYPE, fieldProfile.getInputType());
        assertEquals("The field profile did not contain the correct value!", TEST_FIELD_PROFILE_USAGE, fieldProfile.getUsage());
        assertEquals("The field profile did not contain the correct value!", TEST_FIELD_PROFILE_REPEATABLE, fieldProfile.getRepeatable());
        assertEquals("The field profile did not contain the correct value!", TEST_FIELD_PROFILE_OVERRIDEABLE, fieldProfile.getOverrideable());
        assertEquals("The field profile did not contain the correct value!", TEST_FIELD_PROFILE_ENABLED, fieldProfile.getEnabled());
        assertEquals("The field profile did not contain the correct value!", TEST_FIELD_PROFILE_OPTIONAL, fieldProfile.getOptional());
        assertEquals("Saved entity did not contain the field profile field predicate value!", fieldPredicate, workflowStep.getFieldProfileByPredicate(fieldPredicate).getPredicate());
    }
    
    @Test
    public void testWorkFlowStepDefaultEmptyInit() {
        Organization org = organizationRepo.create("testOrg", parentCategory);
        assertEquals("A newly created organization should have no workflow steps", 0, org.getWorkflowSteps().size());
        assertEquals("A newly created organization should have empty workflow step order", 0, org.getWorkflowStepOrder().size());
    }
    
    @Test
    public void testWorkFlowStepAppend() {
        Organization org = organizationRepo.create("testOrg", parentCategory);
        org.addWorkflowStep(workflowStepRepo.create("first step", org));
        assertEquals("The organization should have one step", 1, org.getWorkflowSteps().size());
        assertEquals("The organization should have one step", 1, org.getWorkflowStepOrder().size());
        org.addWorkflowStep(workflowStepRepo.create("second step", org));
        assertEquals("The organization should have one step", 2, org.getWorkflowSteps().size());
        assertEquals("The organization should have one step", 2, org.getWorkflowStepOrder().size());
    }

    @Override
    @Transactional
    public void testDelete() {
        WorkflowStep workflowStep = workflowStepRepo.create(TEST_WORKFLOW_STEP_NAME, organization);
        workflowStepRepo.delete(workflowStep);
        assertEquals("Entity did not delete!", 0, workflowStepRepo.count());
    }
    
    @Override
    public void testDuplication() {
        
    }

    @Override
    @Transactional
    public void testCascade() {
        WorkflowStep workflowStep = workflowStepRepo.create(TEST_WORKFLOW_STEP_NAME, organization);
        
        Note note = noteRepo.create(TEST_NOTE_NAME, TEST_NOTE_TEXT);
        Note noteToDisassociate = noteRepo.create(TEST_SEVERABLE_NOTE_NAME, TEST_SEVERABLE_NOTE_TEXT);
        
        FieldPredicate fieldPredicate = fieldPredicateRepo.create(TEST_FIELD_PREDICATE_VALUE);
        FieldPredicate fieldPredicateToDisassociate = fieldPredicateRepo.create(TEST_SEVERABLE_FIELD_PREDICATE_VALUE);
        
        FieldProfile fieldProfile = fieldProfileRepo.create(workflowStep, fieldPredicate, TEST_FIELD_PROFILE_INPUT_TYPE, TEST_FIELD_PROFILE_USAGE, TEST_FIELD_PROFILE_REPEATABLE,  TEST_FIELD_PROFILE_OVERRIDEABLE, TEST_FIELD_PROFILE_ENABLED, TEST_FIELD_PROFILE_OPTIONAL);
        FieldProfile fieldProfileToDisassociate = fieldProfileRepo.create(workflowStep, fieldPredicateToDisassociate, TEST_SEVERABLE_FIELD_PROFILE_INPUT_TYPE, TEST_SEVERABLE_FIELD_PROFILE_USAGE, TEST_SEVERABLE_FIELD_PROFILE_REPEATABLE,  TEST_FIELD_PROFILE_OVERRIDEABLE, TEST_SEVERABLE_FIELD_PROFILE_ENABLED, TEST_SEVERABLE_FIELD_PROFILE_OPTIONAL);
        workflowStep.addNote(note);
        workflowStep.addNote(noteToDisassociate);
        
        
        
        //WorkflowStep detachedWorkflowStepForUpdate = clone(workflowStep);
        //detachedWorkflowStepForUpdate.setOriginatingOrganization(organization);
        
        try {
            workflowStep = workflowStepRepo.update(workflowStep, organization);
        } catch (WorkflowStepNonOverrideableException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        
        // check number of field profiles
        assertEquals("Saved entity did not contain the correct number of field profiles!", 2, workflowStep.getFieldProfiles().size());
        assertEquals("WorkflowStep repo does not have the correct number of field profiles", 2, fieldProfileRepo.count());
        
        // check number of notes
        assertEquals("WorkflowStep repo does not have the correct number of notes", 2, workflowStep.getNotes().size());

        // check number of field predicates
        assertEquals("WorkflowStep repo does not have the correct number of field profiles", 2, workflowStep.getFieldProfiles().size());

        // verify field profiles
        assertEquals("Saved entity did not contain the field profile repeatable value!", TEST_FIELD_PROFILE_REPEATABLE, workflowStep.getFieldProfileByPredicate(fieldPredicate).getRepeatable());
        assertEquals("Saved entity did not contain the field profile enabled value!", TEST_FIELD_PROFILE_ENABLED, fieldProfile.getEnabled());
        assertEquals("Saved entity did not contain the field profile optional value!", TEST_FIELD_PROFILE_OPTIONAL, fieldProfile.getOptional());
        assertEquals("Saved entity did not contain the field profile input type!", TEST_FIELD_PROFILE_INPUT_TYPE, workflowStep.getFieldProfileByPredicate(fieldPredicate).getInputType());
        assertEquals("Saved entity did not contain the field profile repeatable value!", TEST_SEVERABLE_FIELD_PROFILE_USAGE, workflowStep.getFieldProfileByPredicate(fieldPredicateToDisassociate).getUsage());
        assertEquals("Saved entity did not contain the field profile repeatable value!", TEST_SEVERABLE_FIELD_PROFILE_REPEATABLE, workflowStep.getFieldProfileByPredicate(fieldPredicateToDisassociate).getRepeatable());
        assertEquals("Saved entity did not contain the field profile required value!", TEST_SEVERABLE_FIELD_PROFILE_ENABLED, workflowStep.getFieldProfileByPredicate(fieldPredicateToDisassociate).getEnabled());
        assertEquals("Saved entity did not contain the field profile required value!", TEST_SEVERABLE_FIELD_PROFILE_OPTIONAL, workflowStep.getFieldProfileByPredicate(fieldPredicateToDisassociate).getOptional());
        assertEquals("Saved entity did not contain the field profile input type!", TEST_SEVERABLE_FIELD_PROFILE_INPUT_TYPE, workflowStep.getFieldProfileByPredicate(fieldPredicateToDisassociate).getInputType());

        // verify field predicates
        assertEquals("Saved entity did not contain the field profile field predicate value!", fieldPredicate, workflowStep.getFieldProfileByPredicate(fieldPredicate).getPredicate());
        assertEquals("Saved entity did not contain the field profile field predicate value!", fieldPredicateToDisassociate, workflowStep.getFieldProfileByPredicate(fieldPredicateToDisassociate).getPredicate());

        
       // detachedWorkflowStepForUpdate = clone(workflowStep);
        
        
        // test remove field profile from workflowStep
        workflowStep.removeFieldProfile(fieldProfileToDisassociate);
         
        try {
            workflowStep = workflowStepRepo.update(workflowStep, organization);
        } catch (WorkflowStepNonOverrideableException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        //the field profile should no longer be on the workflow step, and it should be deleted since it was orphaned
        assertEquals("The field profile was not removed!", 1, workflowStep.getFieldProfiles().size());
        assertEquals("The field profile was deleted!", 2, fieldProfileRepo.count());
        
        
        // test remove note from workflow step
        workflowStep.removeNote(noteToDisassociate);
        
        long noteCount = noteRepo.count();
        
        try {
            workflowStep = workflowStepRepo.update(workflowStep, organization);
        } catch (WorkflowStepNonOverrideableException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        //the note should no longer be on the workflow step, but it should not be deleted
        assertEquals("The note was not removed!", 1, workflowStep.getNotes().size());
        assertEquals("The note was deleted!", noteCount, noteRepo.count());
        
        // test delete workflow step
        workflowStepRepo.delete(workflowStep);
        
        // assert workflow step was deleted
        assertEquals("The workflow step was not deleted!", 0, workflowStepRepo.count());
        
        // assert all field profiles originating in the workflow step are deleted,
        // but the many-to-many associated properties are not
        assertEquals("The field profiles originating in this workflow step were orphaned!", 0, fieldProfileRepo.count());
        assertEquals("The notes were deleted!", noteCount, noteRepo.count());
        assertEquals("The field predicates were deleted!", 2, fieldPredicateRepo.count());
    }
    
    @Test
    @Order(value = 5)
    @Transactional
    public void testInheritWorkflowStepViaPointer() {
        
        Organization parentOrganization = organizationRepo.create(TEST_PARENT_ORGANIZATION_NAME, parentCategory);
        parentOrganization.addChildOrganization(organization);
        parentOrganization = organizationRepo.save(parentOrganization);
        Organization grandChildOrganization = organizationRepo.create(TEST_GRAND_CHILD_ORGANIZATION_NAME, organization, parentCategory);
       
        // Check that workflowstep is passed from parent through children
        assertEquals("The Parent Organization had workflow steps", 0, parentOrganization.getWorkflowSteps().size());
        assertEquals("The Organization had workflow steps", 0, organization.getWorkflowSteps().size());
        assertEquals("The Grand Child Organization had workflow steps", 0, grandChildOrganization.getWorkflowSteps().size());
        
        WorkflowStep workflowStep = workflowStepRepo.create(TEST_WORKFLOW_STEP_NAME, parentOrganization);
        
        parentOrganization = organizationRepo.findOne(parentOrganization.getId());
        organization = organizationRepo.findOne(organization.getId());
        grandChildOrganization = organizationRepo.findOne(grandChildOrganization.getId());
       
        assertEquals("The Parent Organization did not add workflow steps", 1, parentOrganization.getWorkflowSteps().size());
        assertEquals("The Organization did not inherit workflow steps", 1, organization.getWorkflowSteps().size());
        assertEquals("The Grand Child Organization did not inherit workflow steps", 1, grandChildOrganization.getWorkflowSteps().size());
        
        WorkflowStep detachedWSForUpdate = clone(workflowStep);
        
        //Check that a change to the parent step cascades to the child step.
        String newName = "A Changed Name";
        detachedWSForUpdate.setName(newName);
        try {
            workflowStep = workflowStepRepo.update(detachedWSForUpdate, workflowStep.getOriginatingOrganization());
        } catch (WorkflowStepNonOverrideableException e) {
            e.printStackTrace();
        }
        
        assertEquals("The workflow step didn't get the updated name!", newName, workflowStep.getName());
        assertEquals("The parents organization's workflowStep's name was not updated", newName, ((WorkflowStep)parentOrganization.getWorkflowSteps().toArray()[0]).getName());
        assertEquals("The grandChildOrganization workflowStep's name was not updated", newName, ((WorkflowStep)grandChildOrganization.getWorkflowSteps().toArray()[0]).getName());
    }   
     
    @Test
    @Transactional
    public void testMaintainHierarchyOnDeletionOfInteriorOrg()
    {
        
        Organization parentOrganization = organizationRepo.create(TEST_PARENT_ORGANIZATION_NAME, parentCategory);
        parentOrganization.addChildOrganization(organization);
        parentOrganization = organizationRepo.save(parentOrganization);
        Organization grandChildOrganization = organizationRepo.create(TEST_GRAND_CHILD_ORGANIZATION_NAME, organization, parentCategory);
       
        assertEquals("The Parent Organization had workflow steps", 0, parentOrganization.getWorkflowSteps().size());
        assertEquals("The Organization had workflow steps", 0, organization.getWorkflowSteps().size());
        assertEquals("The Grand Child Organization had workflow steps", 0, grandChildOrganization.getWorkflowSteps().size());
        
        WorkflowStep workflowStep = workflowStepRepo.create(TEST_WORKFLOW_STEP_NAME, parentOrganization);
      
        //Delete the interior organization
        organizationRepo.delete(organization);
        
        //Check that hierarchy is maintained and grandchild is moved to be child of the top
        assertEquals("The hierarchy was not maintained!" , parentOrganization.getChildrenOrganizations().toArray()[0], grandChildOrganization);
        assertEquals("The hierarchy was not maintained!" , parentOrganization, grandChildOrganization.getParentOrganizations().toArray()[0]);
       
        // Check that removal of middle organization does not disturb the grandchild's and Parent's workflow.
        assertEquals("The workflowstep repo didn't contain the single workflow step!", 1, workflowStepRepo.count());
        assertEquals("The Parent Organization didn't have the right workflowStep", workflowStep, parentOrganization.getWorkflowSteps().get(0));
        assertEquals("The Grand Child Organization didn't have the right workflowStep", workflowStep, grandChildOrganization.getWorkflowSteps().get(0));
        
        //Check that inheritance still works
        String newName = "A Changed Name";
        WorkflowStep detachedWSForUpdate = clone(workflowStep);
        detachedWSForUpdate.setName(newName);
        try {
            workflowStep = workflowStepRepo.update(detachedWSForUpdate, workflowStep.getOriginatingOrganization());
        } catch (WorkflowStepNonOverrideableException e) {
            e.printStackTrace();
        }
        
        assertEquals("The workflow step didn't get the updated name!", newName, workflowStep.getName());
        assertEquals("The parents organization's workflowStep's name was not updated", newName, parentOrganization.getWorkflowSteps().get(0).getName());
        assertEquals("The grandChildOrganization workflowStep's name was not updated", newName, grandChildOrganization.getWorkflowSteps().get(0).getName());
    }
    
    @Test
    @Order(value = 6)
    @Transactional
    public void testWorkflowStepChangeAtChildOrg()
    {
        Organization parentOrganization = organizationRepo.create(TEST_PARENT_ORGANIZATION_NAME, parentCategory);
        parentOrganization.addChildOrganization(organization);
        parentOrganization = organizationRepo.save(parentOrganization);
        
        Organization grandChildOrganization = organizationRepo.create(TEST_GRAND_CHILD_ORGANIZATION_NAME, parentCategory);
        organization.addChildOrganization(grandChildOrganization);
        
        Organization greatGrandChildOrganization = organizationRepo.create("TestGreatGrandchildOrganizationName", parentCategory);
        grandChildOrganization.addChildOrganization(greatGrandChildOrganization);
        
        Organization anotherGreatGrandChildOrganization = organizationRepo.create("AnotherTestGreatGrandchildOrganizationName", parentCategory);
        grandChildOrganization.addChildOrganization(anotherGreatGrandChildOrganization);
        
        WorkflowStep workflowStep = workflowStepRepo.create(TEST_WORKFLOW_STEP_NAME, parentOrganization);
        String updatedName = "Updated Name";
        
        WorkflowStep detachedWorkflowStepForUpdate = clone(workflowStep);
        
        detachedWorkflowStepForUpdate.setName(updatedName);
        detachedWorkflowStepForUpdate.setOriginatingOrganization(parentOrganization);
        detachedWorkflowStepForUpdate.setOriginatingWorkflowStep(workflowStep);
       
        //update the workflow step at the child - should result in a new one being created
        WorkflowStep derivativeWorkflowStep = null;
        try {
            derivativeWorkflowStep = workflowStepRepo.update(detachedWorkflowStepForUpdate, organization);
        } catch (WorkflowStepNonOverrideableException e) {
            e.printStackTrace();
        }
        
        //when updating the workflow step at organization, test that
        // a new workflow step is made at the organization
        assertFalse("The child organization did not recieve a new workflowStep; steps had same IDs of " + workflowStep.getId(), derivativeWorkflowStep.getId().equals(workflowStep.getId()));
        assertEquals("The updated workflowStep's name did not change.", updatedName, derivativeWorkflowStep.getName());
        assertEquals("The parent workflowStep's name did change.", TEST_WORKFLOW_STEP_NAME, workflowStep.getName());
        
        // the new workflow step remembers from whence it was derived (the parent's workflow step)
        assertEquals("The child's new workflow step knew not from whence it came", workflowStep.getId(), derivativeWorkflowStep.getOriginatingWorkflowStep().getId());
        
        //and furthermore, the organization's descendants point to the new WorkflowStep
        Long grandchildWorkflowStepId = grandChildOrganization.getWorkflowSteps().get(0).getId();
        assertEquals("The grandchild organization didn't start pointing at the new workflow step it was supposed to inherit!", grandchildWorkflowStepId, derivativeWorkflowStep.getId());
        Long greatGrandChildWorkflowStepId = greatGrandChildOrganization.getWorkflowSteps().get(0).getId();
        assertEquals("The great grandchild organization didn't start pointing at the new workflow step it was supposed to inherit!", greatGrandChildWorkflowStepId, derivativeWorkflowStep.getId());
        Long anotherGreatGrandChildWorkflowStepId = anotherGreatGrandChildOrganization.getWorkflowSteps().get(0).getId();
        assertEquals("Another great grandchild organization didn't start pointing at the new workflow step it was supposed to inherit!", anotherGreatGrandChildWorkflowStepId, derivativeWorkflowStep.getId());

    }
    
    @Test(expected=WorkflowStepNonOverrideableException.class)
    @Order(value = 7)
    @Transactional
    public void testCantOverrideNonOverrideable() throws WorkflowStepNonOverrideableException
    {
        Organization parentOrganization = organizationRepo.create(TEST_PARENT_ORGANIZATION_NAME, parentCategory);
        parentOrganization.addChildOrganization(organization);
        parentOrganization = organizationRepo.save(parentOrganization);
        
        //test that we can't override a non-overrideable workflow step at the child of its originating organization
        WorkflowStep workflowStep = workflowStepRepo.create(TEST_WORKFLOW_STEP_NAME, parentOrganization);
        workflowStep.setOverrideable(false);
        
        workflowStepRepo.update(workflowStep, organization);
    }
    
    @Test
    @Order(value=8)
    @Transactional
    public void testPermissionWorkflowChangeNonOverrideableAtOriginatingOrg() throws WorkflowStepNonOverrideableException
    {
        Organization parentOrganization = organizationRepo.create(TEST_PARENT_ORGANIZATION_NAME, parentCategory);
        parentOrganization.addChildOrganization(organization);
        parentOrganization = organizationRepo.save(parentOrganization);
        
        WorkflowStep workflowStep = workflowStepRepo.create(TEST_WORKFLOW_STEP_NAME, parentOrganization);
        assertEquals("the workflow step didn't start out overrideable as expected!", true, workflowStep.getOverrideable());
        workflowStep.setOverrideable(false);
        
        //test that we can override a non-overrideable workflow step (which will remain the same database row) if we're the originating organization
        Long originalWorkflowStepId = workflowStep.getId();
        WorkflowStep updatedWorkflowStep = workflowStepRepo.update(workflowStep, parentOrganization);
        assertEquals("The originating Organization of the WorkflowStep couldn't update it!", updatedWorkflowStep.getId(), originalWorkflowStepId);
        assertEquals("The originating Organization of the WorkflowStep couldn't make it non-overrideable!", false, updatedWorkflowStep.getOverrideable());
        assertEquals("The originating Organization of the WorkflowStep couldn't make it non-overrideable!", false, workflowStep.getOverrideable());
    }
    
    @Test
    @Order(value=9)
    @Transactional
    public void testMakeWorkflwoStepWithDescendantsNonOverrideable() throws WorkflowStepNonOverrideableException
    {
        //Step S1 has derivative step S2 which has derivative step S3
        //Test that making S1 non-overrideable will blow away S2 and S3 and replace pointer to them with pointers to S1
        
        Organization parentOrganization = organizationRepo.create(TEST_PARENT_ORGANIZATION_NAME, parentCategory);
        parentOrganization.addChildOrganization(organization);
        parentOrganization = organizationRepo.save(parentOrganization);
        
        Organization grandChildOrganization = organizationRepo.create(TEST_GRAND_CHILD_ORGANIZATION_NAME, parentCategory);
        organization.addChildOrganization(grandChildOrganization);
        
        Organization greatGrandChildOrganization = organizationRepo.create("TestGreatGrandchildOrganizationName", parentCategory);
        grandChildOrganization.addChildOrganization(greatGrandChildOrganization);
        
        Organization anotherGreatGrandChildOrganization = organizationRepo.create("AnotherTestGreatGrandchildOrganizationName", parentCategory);
        grandChildOrganization.addChildOrganization(anotherGreatGrandChildOrganization);
        
        WorkflowStep s1 = workflowStepRepo.create(TEST_WORKFLOW_STEP_NAME, parentOrganization);
        
        // add a couple of additional steps to test ordering
        WorkflowStep t1 = workflowStepRepo.create("Step T", parentOrganization);

        WorkflowStep u1 = workflowStepRepo.create("Step U", parentOrganization);

        String updatedName = "Updated Name";
        
        WorkflowStep detachedStepForUpdates = clone(s1);
        detachedStepForUpdates.setOriginatingWorkflowStep(s1);
        detachedStepForUpdates.setName(updatedName);
        
        WorkflowStep s2 = workflowStepRepo.update(detachedStepForUpdates, organization);
        
        String anotherUpdatedName ="Yet another updated name";
        detachedStepForUpdates = clone(s2);
        detachedStepForUpdates.setOriginatingWorkflowStep(s2);
        detachedStepForUpdates.setName(anotherUpdatedName);
         
        WorkflowStep s3 = workflowStepRepo.update(detachedStepForUpdates, grandChildOrganization);
        
        assertEquals("s1 had the wrong name!", TEST_WORKFLOW_STEP_NAME, s1.getName());
        assertEquals("s2 had the wrong name!", updatedName, s2.getName());
        assertEquals("s2 had the wrong originating Organization!", organization.getId(), s2.getOriginatingOrganization().getId());
        assertTrue("s2 was not contained in the right Organization!", s2.getContainedByOrganizations().contains(organization));
        assertEquals("s3 had the wrong name!", anotherUpdatedName, s3.getName());
        assertEquals("s3 had the wrong originating Organization!", grandChildOrganization.getId(), s3.getOriginatingOrganization().getId());
        assertTrue("s3 wasn't on a great grandchild organization who should have inherited it!", s3.getContainedByOrganizations().contains(anotherGreatGrandChildOrganization));
        
        long numWorkflowSteps = workflowStepRepo.count();
        
        //now we are ready to make step 1 non-overrideable and ensure that step 2 and 3 go away
        detachedStepForUpdates = clone(s1);
        detachedStepForUpdates.setOverrideable(false);
        
        s1.setOverrideable(false);
        s1 = workflowStepRepo.update(s1, parentOrganization);
        
        assertEquals("Workflow Step Repo didn't get the disallowed (no longer overrideable) steps deleted!", numWorkflowSteps-2, workflowStepRepo.count());
        
        assertTrue("Child org didn't get its workflow step replaced by the non-overrideable s1!", organization.getWorkflowSteps().contains(s1));
        assertTrue("Grandchild org didn't get its workflow step replaced by the non-overrideable s1!", grandChildOrganization.getWorkflowSteps().contains(s1));
        assertTrue("Great grandchild org didn't get its workflow step replaced by the non-overrideable s1!", greatGrandChildOrganization.getWorkflowSteps().contains(s1));
        assertTrue("Another Great grandchild org didn't get its workflow step replaced by the non-overrideable s1!", anotherGreatGrandChildOrganization.getWorkflowSteps().contains(s1));
        
        assertEquals("Great grandchild org didn't have s1 as the first step", s1.getId(), greatGrandChildOrganization.getWorkflowStepOrder().get(0));
        assertEquals("Great grandchild org didn't have t1 as the second step", t1.getId(), greatGrandChildOrganization.getWorkflowStepOrder().get(1));
        assertEquals("Great grandchild org didn't have u1 as the third step", u1.getId(), greatGrandChildOrganization.getWorkflowStepOrder().get(2));        
    }

    @After
    public void cleanUp() {
        //TODO:  need to be able to delete all the workflow steps
        //before deleting all the field profiles
        //have to delete the fieldProfiles first to avoid an org.hibernate.AssertionFailure after the testInheritWorkflowStepViaPointer test

        
        
        organizationCategoryRepo.deleteAll();
        assertEquals("Couldn't delete all organization categories!", 0, organizationCategoryRepo.count());
        
        noteRepo.deleteAll();
        assertEquals("Couldn't delete all notes!", 0, noteRepo.count());
        
        fieldPredicateRepo.deleteAll();
        assertEquals("Couldn't delete all predicates!", 0, fieldPredicateRepo.count());
        
        organizationRepo.deleteAll();
        assertEquals("Couldn't delete all organizations", 0, organizationRepo.count());
        
//        workflowStepRepo.findAll().forEach(workflowStep -> {
//            workflowStepRepo.delete(workflowStep);
//        });
        
        //We must call this after deleting all the organizations, categories, etc. so as to be able to call the default (non-custom)
        //deleteAll() method. WorkflowStepRepo's delete() is a custom override, and trying to call it on each item in findAll using a
        //foreach loop will cause an exception (we'd be modifying an array while we iterate over it). This is commented out immediately above.
        workflowStepRepo.deleteAll();
        assertEquals("Couldn't delete all workflow steps!", 0, workflowStepRepo.count());
        
        fieldProfileRepo.findAll().forEach(fieldProfile -> {
            fieldProfileRepo.delete(fieldProfile);
        });
        assertEquals("Couldn't delete all field profiles!", 0, fieldProfileRepo.count());
        
    }
    
    private WorkflowStep clone(WorkflowStep ws)
    {
        WorkflowStep myDetachedWorkflowStep = new WorkflowStep(ws.getName(), ws.getOriginatingOrganization());
        myDetachedWorkflowStep.setId(ws.getId());
        myDetachedWorkflowStep.setOriginatingOrganization(ws.getOriginatingOrganization());
        myDetachedWorkflowStep.setContainedByOrganizations(ws.getContainedByOrganizations());
        myDetachedWorkflowStep.setOverrideable(ws.getOverrideable());
        myDetachedWorkflowStep.setContainedByOrganizations(ws.getContainedByOrganizations());
        myDetachedWorkflowStep.setFieldProfiles(ws.getFieldProfiles());
        myDetachedWorkflowStep.setNotes(ws.getNotes());
        myDetachedWorkflowStep.setOriginalFieldProfiles(ws.getOriginalFieldProfiles());
        myDetachedWorkflowStep.setOriginatingWorkflowStep(ws.getOriginatingWorkflowStep());
        return myDetachedWorkflowStep;
    }

}
