package org.tdl.vireo.model;

import static org.junit.Assert.assertEquals;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;
import org.tdl.vireo.annotations.Order;

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
        
        
        // detach workflow step entity when updating to simulate behavior of endpoint
        em.detach(workflowStep);
        
        workflowStep.addNote(note);
        workflowStep.addNote(severableNote);
        
        
        workflowStep = workflowStepRepo.update(workflowStep, organization);

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

        
        // detach workflow step entity when updating to simulate behavior of endpoint
        em.detach(workflowStep);
        
        
        // test remove severable field profile
        workflowStep.removeFieldProfile(severableFieldProfile);
         
        // probably should be an update
        workflowStep = workflowStepRepo.update(workflowStep, organization);
        
        assertEquals("The field profile was not removed!", 1, workflowStep.getFieldProfiles().size());
        assertEquals("The field profile was deleted!", 2, fieldProfileRepo.count());
        
        
        // detach workflow step entity when updating to simulate behavior of endpoint
        em.detach(workflowStep);
        
        
        // test remove severable note
        workflowStep.removeNote(severableNote);
        
        // probably should be an update
        workflowStep = workflowStepRepo.update(workflowStep, organization);
        
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
    public void testWorkflowStepChangeAtChild()
    {
        Organization parentOrganization = organizationRepo.create(TEST_PARENT_ORGANIZATION_NAME, parentCategory);
        parentOrganization.addChildOrganization(organization);
        parentOrganization = organizationRepo.save(parentOrganization);
        
        WorkflowStep workflowStep = workflowStepRepo.create(TEST_WORKFLOW_STEP_NAME, parentOrganization);
        
        String updatedName = "Updated Name";
        
        //detach this workflowStep as it is now just a means of requesting updates at the organization's level,
        //and we don't want transactional changes to it persisted
        em.detach(workflowStep);
        
        workflowStep.setName(updatedName);
        
        WorkflowStep derivativeWorkflowStep = workflowStepRepo.update(workflowStep, organization);
        
        
        //when updating the workflow step at organization, test that
        // a new workflow step is made at the organization
        assertEquals("The child organization did not recieve a new workflowStep", false, derivativeWorkflowStep.getId().equals(workflowStep.getId()));
        assertEquals("The updated workflowStep's name did not change.", updatedName, derivativeWorkflowStep.getName());
        
        // refreshed (and reattached)
        workflowStep = workflowStepRepo.findOne(workflowStep.getId());
        
        assertEquals("The parent workflowStep's name did change.", TEST_WORKFLOW_STEP_NAME, workflowStep.getName());
        
        // the new workflow step remembers from whence it was derived (the parent's workflow step)
        assertEquals("The child's new workflow step knew not from whence it came", workflowStep.getId(), derivativeWorkflowStep.getOriginatingWorkflowStep().getId()); 
        
    }
    
    @Test
    @Order(value = 6)
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
        
        workflowStep = workflowStepRepo.update(workflowStep, workflowStep.getOriginatingOrganization());
        
        assertEquals("The parents organization's workflowStep's name was incorrect", newName, ((WorkflowStep)parentOrganization.getWorkflowSteps().toArray()[0]).getName());
        assertEquals("The grandChildOrganization workflowStep's name was incorrect", newName, ((WorkflowStep)grandChildOrganization.getWorkflowSteps().toArray()[0]).getName());
        
    }

    @After
    public void cleanUp() {
        workflowStepRepo.findAll().forEach(workflowStep -> {
            workflowStepRepo.delete(workflowStep);
        });
        organizationRepo.deleteAll();
        organizationCategoryRepo.deleteAll();
        noteRepo.deleteAll();
        fieldProfileRepo.deleteAll();
        fieldPredicateRepo.deleteAll();
    }

}
