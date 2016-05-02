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
import org.tdl.vireo.model.repo.impl.WorkflowStepNonOverrideableException;

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
        Note severableNote = noteRepo.create(TEST_SEVERABLE_NOTE_NAME, TEST_SEVERABLE_NOTE_TEXT);
        
        FieldPredicate fieldPredicate = fieldPredicateRepo.create(TEST_FIELD_PREDICATE_VALUE);
        FieldPredicate severableFieldPredicate = fieldPredicateRepo.create(TEST_SEVERABLE_FIELD_PREDICATE_VALUE);
        
        FieldProfile fieldProfile = fieldProfileRepo.create(workflowStep, fieldPredicate, TEST_FIELD_PROFILE_INPUT_TYPE, TEST_FIELD_PROFILE_USAGE, TEST_FIELD_PROFILE_REPEATABLE,  TEST_FIELD_PROFILE_OVERRIDEABLE, TEST_FIELD_PROFILE_ENABLED, TEST_FIELD_PROFILE_OPTIONAL);
        FieldProfile severableFieldProfile = fieldProfileRepo.create(workflowStep, severableFieldPredicate, TEST_SEVERABLE_FIELD_PROFILE_INPUT_TYPE, TEST_SEVERABLE_FIELD_PROFILE_USAGE, TEST_SEVERABLE_FIELD_PROFILE_REPEATABLE,  TEST_FIELD_PROFILE_OVERRIDEABLE, TEST_SEVERABLE_FIELD_PROFILE_ENABLED, TEST_SEVERABLE_FIELD_PROFILE_OPTIONAL);
        workflowStep.addNote(note);
        workflowStep.addNote(severableNote);
        
        
        
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
        assertEquals("Saved entity did not contain the field profile repeatable value!", TEST_SEVERABLE_FIELD_PROFILE_USAGE, workflowStep.getFieldProfileByPredicate(severableFieldPredicate).getUsage());
        assertEquals("Saved entity did not contain the field profile repeatable value!", TEST_SEVERABLE_FIELD_PROFILE_REPEATABLE, workflowStep.getFieldProfileByPredicate(severableFieldPredicate).getRepeatable());
        assertEquals("Saved entity did not contain the field profile required value!", TEST_SEVERABLE_FIELD_PROFILE_ENABLED, workflowStep.getFieldProfileByPredicate(severableFieldPredicate).getEnabled());
        assertEquals("Saved entity did not contain the field profile required value!", TEST_SEVERABLE_FIELD_PROFILE_OPTIONAL, workflowStep.getFieldProfileByPredicate(severableFieldPredicate).getOptional());
        assertEquals("Saved entity did not contain the field profile input type!", TEST_SEVERABLE_FIELD_PROFILE_INPUT_TYPE, workflowStep.getFieldProfileByPredicate(severableFieldPredicate).getInputType());

        // verify field predicates
        assertEquals("Saved entity did not contain the field profile field predicate value!", fieldPredicate, workflowStep.getFieldProfileByPredicate(fieldPredicate).getPredicate());
        assertEquals("Saved entity did not contain the field profile field predicate value!", severableFieldPredicate, workflowStep.getFieldProfileByPredicate(severableFieldPredicate).getPredicate());

        
       // detachedWorkflowStepForUpdate = clone(workflowStep);
        
        
        // test remove severable field profile
        workflowStep.removeFieldProfile(severableFieldProfile);
         
        try {
            workflowStep = workflowStepRepo.update(workflowStep, organization);
        } catch (WorkflowStepNonOverrideableException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        assertEquals("The field profile was not removed!", 1, workflowStep.getFieldProfiles().size());
        assertEquals("The field profile was deleted!", 2, fieldProfileRepo.count());
        
        
        //detachedWorkflowStepForUpdate = clone(workflowStep);
        
        
        // test remove severable note
        //detachedWorkflowStepForUpdate.removeNote(severableNote);
        workflowStep.removeNote(severableNote);
        
        try {
            workflowStep = workflowStepRepo.update(workflowStep, organization);
        } catch (WorkflowStepNonOverrideableException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        assertEquals("The note was not removed!", 1, workflowStep.getNotes().size());
        assertEquals("The note was deleted!", 2, noteRepo.count());
        
        // test delete workflow step
        workflowStepRepo.delete(workflowStep);
        
        // assert workflow step was deleted
        assertEquals("The workflow step was not deleted!", 0, workflowStepRepo.count());
        
        // assert all properties of originating workflow step are deleted
        assertEquals("The field profiles were orphaned!", 0, fieldProfileRepo.count());
        assertEquals("The notes were deleted!", 2, noteRepo.count());
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
       
       
        // Check that removal of middle organization does not disturb the grandchild's and Parent's workflow.
        organizationRepo.delete(organization);
       
        parentOrganization = organizationRepo.findOne(parentOrganization.getId());
        grandChildOrganization = organizationRepo.findOne(grandChildOrganization.getId());
       
        assertEquals("The worflowstep repo was empty", 1, workflowStepRepo.count());
        assertEquals("The Parent Organization did not add workflow steps", 1, parentOrganization.getWorkflowSteps().size());
        assertEquals("The Grand Child Organization did not inherit workflow steps", 1, grandChildOrganization.getWorkflowSteps().size());
        
        // detach workflow step entity when updating to simulate behavior of endpoint
        em.detach(workflowStep);
        
        //Check that a change to the parent step cascades to the child step.
        String newName = "A Changed Name";
        workflowStep.setName(newName);
        
        try {
            workflowStep = workflowStepRepo.update(workflowStep, workflowStep.getOriginatingOrganization());
        } catch (WorkflowStepNonOverrideableException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        assertEquals("The parents organization's workflowStep's name was incorrect", newName, ((WorkflowStep)parentOrganization.getWorkflowSteps().toArray()[0]).getName());
        assertEquals("The grandChildOrganization workflowStep's name was incorrect", newName, ((WorkflowStep)grandChildOrganization.getWorkflowSteps().toArray()[0]).getName());
        
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
        System.out.println("workflowStep has originator: " + workflowStep.getOriginatingOrganization().getName());
        String updatedName = "Updated Name";
        
        WorkflowStep detachedWorkflowStepForUpdate = clone(workflowStep);
        
        detachedWorkflowStepForUpdate.setName(updatedName);
        detachedWorkflowStepForUpdate.setOriginatingOrganization(parentOrganization);
        System.out.println("detachedWorkflowStepForUpdate has name and originator: " + detachedWorkflowStepForUpdate.getName() + ", " + detachedWorkflowStepForUpdate.getOriginatingOrganization().getName());

        //update the workflow step at the child - should result in a new one being created
        WorkflowStep derivativeWorkflowStep = null;
        try {
            derivativeWorkflowStep = workflowStepRepo.update(detachedWorkflowStepForUpdate, organization);
        } catch (WorkflowStepNonOverrideableException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        System.out.println("derivativeWorkflowStep has originator: " + derivativeWorkflowStep.getOriginatingOrganization().getName());
        
        
        
        
        //when updating the workflow step at organization, test that
        // a new workflow step is made at the organization
        assertFalse("The child organization did not recieve a new workflowStep; steps had same IDs of " + workflowStep.getId(), derivativeWorkflowStep.getId().equals(workflowStep.getId()));
        assertEquals("The updated workflowStep's name did not change.", updatedName, derivativeWorkflowStep.getName());
        
        // refreshed (and reattached)
        workflowStep = workflowStepRepo.findOne(workflowStep.getId());
        
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
    public void testPermissionWorkflowStepChangeAtChildOrg() throws WorkflowStepNonOverrideableException
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
        
        //test that we can't override a non-overrideable workflow step at the child of its originating organization
        WorkflowStep workflowStep = workflowStepRepo.create(TEST_WORKFLOW_STEP_NAME, parentOrganization);
        workflowStep.setOverrideable(false);
        
        //test that we can override a non-overrideable workflow step (which will remain the same database row) if we're the originating organization
        Long originalWorkflowStepId = workflowStep.getId();
        WorkflowStep updatedWorkflowStep = workflowStepRepo.update(workflowStep, parentOrganization);
        assertEquals("The originating Organization of the WorkflowStep couldn't properly update it!", updatedWorkflowStep.getId(), originalWorkflowStepId);
    }
    
    @Test
    @Order(value=9)
    @Transactional
    public void testMakeWorkflwoStepWithDescendantsNonOverrideable() throws WorkflowStepNonOverrideableException
    {
        System.out.println("starting here");
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
        Long step1Id = s1.getId();
        
        String updatedName = "Updated Name";
        
        WorkflowStep detachedStepForUpdates = clone(s1);
        detachedStepForUpdates.setName(updatedName);
        
        WorkflowStep s2 = workflowStepRepo.update(detachedStepForUpdates, organization);
        Long step2Id = s2.getId();
        
        String anotherUpdatedName ="Yet another updated name";
        detachedStepForUpdates = clone(s2);
        detachedStepForUpdates.setName(anotherUpdatedName);
        
        WorkflowStep s3 = workflowStepRepo.update(detachedStepForUpdates, grandChildOrganization);
        Long step3Id = s3.getId();
        
        System.out.println("\n\n*** I, s2, of ID " + s2.getId() + " am named: "+ s2.getName() + " and am contained by " + s2.getContainedByOrganizations().size() + " orgs but my originating org is " + s2.getOriginatingOrganization().getName() + "\n\n***");
        
        assertEquals("s1 had the wrong name!", TEST_WORKFLOW_STEP_NAME, s1.getName());
        assertEquals("s2 had the wrong name!", updatedName, s2.getName());
        assertEquals("s2 had the wrong originating Organization!", organization.getId(), s2.getOriginatingOrganization().getId());
        assertEquals("s2 was contained in the wrong Organization!", organization.getId(), ((Organization) s2.getContainedByOrganizations().toArray()[0]).getId());
        assertEquals("s3 had the wrong name!", anotherUpdatedName, s3.getName());
        System.out.println("s3 has " + s3.getContainedByOrganizations().size() );
        
        for(Organization o : s3.getContainedByOrganizations())
        {
            System.out.println("s3 " + s3.getName() + " contained by " + o.getName() );
        }
        
        assertTrue("s3 wasn't on a great grandchild organization who should have inherited it!", s3.getContainedByOrganizations().contains(anotherGreatGrandChildOrganization));
                
        
        
    }

    @After
    public void cleanUp() {
        //TODO:  need to be able to delete all the workflow steps
        //before deleting all the field profiles
        fieldProfileRepo.findAll().forEach(fieldProfile -> {
            fieldProfileRepo.delete(fieldProfile);
        });
        
        workflowStepRepo.findAll().forEach(workflowStep -> {
            workflowStepRepo.delete(workflowStep);
        });
        
//        for(Organization org : organizationRepo.findAll())
//        {
//            organizationRepo.delete(org);
//        }

        
        //organizationRepo.flush();
        //organizationRepo.deleteAll();
        organizationCategoryRepo.deleteAll();
        noteRepo.deleteAll();
        //fieldProfileRepo.deleteAll();
        fieldPredicateRepo.deleteAll();
    }
    
    private WorkflowStep clone(WorkflowStep ws)
    {
        WorkflowStep myDetachedWorkflowStep = new WorkflowStep(ws.getName(), ws.getOriginatingOrganization());
        myDetachedWorkflowStep.setId(ws.getId());
        myDetachedWorkflowStep.setOriginatingOrganization(null);
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
