package org.tdl.vireo.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.tdl.vireo.model.repo.impl.ComponentNotPresentOnOrgException;
import org.tdl.vireo.model.repo.impl.HeritableModelNonOverrideableException;
import org.tdl.vireo.model.repo.impl.WorkflowStepNonOverrideableException;

public class NoteTest extends AbstractEntityTest {

    @Before
    public void setUp() {
        parentCategory = organizationCategoryRepo.create(TEST_CATEGORY_NAME);
        organization = organizationRepo.create(TEST_ORGANIZATION_NAME, parentCategory);
        parentCategory = organizationCategoryRepo.findOne(parentCategory.getId());
        workflowStep = workflowStepRepo.create(TEST_WORKFLOW_STEP_NAME, organization);
        organization = organizationRepo.findOne(organization.getId());
    }

    @Override
    public void testCreate() {
        Note note = noteRepo.create(workflowStep, TEST_NOTE_NAME, TEST_NOTE_TEXT);
        assertEquals("The entity was not created!", 1, noteRepo.count());
        assertEquals("The entity did not have the correct name!", TEST_NOTE_NAME, note.getName());
        assertEquals("The entity did not have the correct text!", TEST_NOTE_TEXT, note.getText());
    }

    @Override
    public void testDuplication() {
        noteRepo.create(workflowStep, TEST_NOTE_NAME, TEST_NOTE_TEXT);
        try {
            noteRepo.create(workflowStep, TEST_NOTE_NAME, TEST_NOTE_TEXT);
        } 
        catch (DataIntegrityViolationException e) { /* SUCCESS */ }
        assertEquals("The repository duplicated entity!", 1, noteRepo.count());
    }

    @Override
    public void testDelete() {
        Note note = noteRepo.create(workflowStep, TEST_NOTE_NAME, TEST_NOTE_TEXT);
        noteRepo.delete(note);
        assertEquals("The entity was not deleted!", 0, noteRepo.count());
    }

    @Override
    public void testCascade() {
        Note note = noteRepo.create(workflowStep, TEST_NOTE_NAME, TEST_NOTE_TEXT);
        assertEquals("The entity was not created!", 1, noteRepo.count());
        assertEquals("The entity did not have the correct name!", TEST_NOTE_NAME, note.getName());
        assertEquals("The entity did not have the correct text!", TEST_NOTE_TEXT, note.getText());
        
        // test delete note
        noteRepo.delete(note);
        assertEquals("An note was not deleted!", 0, noteRepo.count());
        assertEquals("The workflowstep was deleted!", 1, workflowStepRepo.count());
    }
    
    @Test
    public void testInheritNoteViaPointer() throws ComponentNotPresentOnOrgException {
        
        Organization parentOrganization = organizationRepo.create(TEST_PARENT_ORGANIZATION_NAME, parentCategory);
        parentCategory = organizationCategoryRepo.findOne(parentCategory.getId());
        
        Organization childOrganization = organizationRepo.create(TEST_CHILD_ORGANIZATION_NAME, parentCategory);
        parentCategory = organizationCategoryRepo.findOne(parentCategory.getId());
        
        parentOrganization = organizationRepo.findOne(parentOrganization.getId());
        
        parentOrganization.addChildOrganization(childOrganization);
        parentOrganization = organizationRepo.save(parentOrganization);
        
        parentOrganization = organizationRepo.findOne(parentOrganization.getId());
        childOrganization = organizationRepo.findOne(childOrganization.getId());
        
        
        Organization grandchildOrganization = organizationRepo.create(TEST_GRAND_CHILD_ORGANIZATION_NAME, parentCategory);
        parentCategory = organizationCategoryRepo.findOne(parentCategory.getId());
        
        grandchildOrganization = organizationRepo.findOne(grandchildOrganization.getId());
        
        childOrganization.addChildOrganization(grandchildOrganization);
        childOrganization = organizationRepo.save(childOrganization);
        
        
        parentOrganization = organizationRepo.findOne(parentOrganization.getId());
        
        WorkflowStep parentWorkflowStep = workflowStepRepo.create(TEST_PARENT_WORKFLOW_STEP_NAME, parentOrganization);
        
        Note note = noteRepo.create(parentWorkflowStep, TEST_NOTE_NAME, TEST_NOTE_TEXT);
        
        parentOrganization = organizationRepo.findOne(parentOrganization.getId());
        childOrganization = organizationRepo.findOne(childOrganization.getId());
        grandchildOrganization = organizationRepo.findOne(grandchildOrganization.getId());
        
        
        assertTrue("The parent organization's workflow did not contain the original note!", parentOrganization.getAggregateWorkflowSteps().get(0).getOriginalNotes().contains(note));
        assertTrue("The child organization's workflow did not contain the original note!", childOrganization.getAggregateWorkflowSteps().get(0).getOriginalNotes().contains(note));
        assertTrue("The grand child organization's workflow did not contain the original note!", grandchildOrganization.getAggregateWorkflowSteps().get(0).getOriginalNotes().contains(note));
        
        
        assertTrue("The parent organization's workflow did not contain the aggregate note!", parentOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().contains(note));
        assertTrue("The child organization's workflow did not contain the aggregate note!", childOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().contains(note));
        assertTrue("The grand child organization's workflow did not contain the aggregate note!", grandchildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().contains(note));
        

        String noteText = "Updated Text";
        note.setText(noteText);
        
        
        try {
            noteRepo.update(note, parentOrganization);
        } catch (HeritableModelNonOverrideableException e) {
            e.printStackTrace();
            assertTrue("The note did not update beacuase it is non overrideable!", false);
            
        } catch (WorkflowStepNonOverrideableException e) {
            e.printStackTrace();
            assertTrue("The note did not update beacuase it's owning workflow step is non overrideable!", false);
        }
        
        
        parentOrganization = organizationRepo.findOne(parentOrganization.getId());
        childOrganization = organizationRepo.findOne(childOrganization.getId());
        grandchildOrganization = organizationRepo.findOne(grandchildOrganization.getId());
        
        
        assertEquals("The parent's note's text did not update", noteText, parentOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(0).getText());
        assertEquals("The child's note's text did not update", noteText, childOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(0).getText());
        assertEquals("The grand child's note's text did not update", noteText, grandchildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(0).getText());
    
    }
    
    @Test(expected=HeritableModelNonOverrideableException.class)
    public void testCantOverrideNonOverrideableNote() throws HeritableModelNonOverrideableException, WorkflowStepNonOverrideableException, ComponentNotPresentOnOrgException {
        
        Organization childOrganization = organizationRepo.create(TEST_CHILD_ORGANIZATION_NAME, organization, parentCategory);
        parentCategory = organizationCategoryRepo.findOne(parentCategory.getId());
        
        workflowStep = workflowStepRepo.findOne(workflowStep.getId());
                
        Note note = noteRepo.create(workflowStep, TEST_NOTE_NAME, TEST_NOTE_TEXT);
                
        note.setOverrideable(false);
        
                
        organization = organizationRepo.findOne(organization.getId());
        
        // actually set the note to non overrideable on parent first
        note = noteRepo.update(note, organization);
        
        
        assertFalse("The note was not made non-overrideable!", note.getOverrideable());
        
        
        childOrganization = organizationRepo.findOne(childOrganization.getId());
        
        note.setName("Updated Name");
        
        noteRepo.update(note, childOrganization);
    }
    
    @Test(expected=WorkflowStepNonOverrideableException.class)
    public void testCantOverrideNonOverrideableWorkflowStep() throws HeritableModelNonOverrideableException, WorkflowStepNonOverrideableException, ComponentNotPresentOnOrgException {
        
        Organization childOrganization = organizationRepo.create(TEST_CHILD_ORGANIZATION_NAME, organization, parentCategory);
        parentCategory = organizationCategoryRepo.findOne(parentCategory.getId());

        workflowStep = workflowStepRepo.findOne(workflowStep.getId());
                
        Note note = noteRepo.create(workflowStep, TEST_NOTE_NAME, TEST_NOTE_TEXT);
        
        
        organization = organizationRepo.findOne(organization.getId());
        workflowStep = workflowStepRepo.findOne(workflowStep.getId());
        
        workflowStep.setOverrideable(false);
        
        workflowStep = workflowStepRepo.update(workflowStep, organization);
        
                
        note = noteRepo.findOne(note.getId());
        
        childOrganization = organizationRepo.findOne(childOrganization.getId());
        
        workflowStep = workflowStepRepo.findOne(workflowStep.getId());
        
        assertEquals("The note's originating workflow step is not the intended workflow step!", workflowStep, note.getOriginatingWorkflowStep());
        
        assertFalse("The note's originating workflow step was not made non-overrideable!", note.getOriginatingWorkflowStep().getOverrideable());

        assertFalse("The workflowstep was not made non-overrideable!", workflowStep.getOverrideable());
        
        note.setName("Updated Name");
        
        noteRepo.update(note, childOrganization);
    }
    
    @Test
    public void testCanOverrideNonOverrideableAtOriginatingOrg() throws HeritableModelNonOverrideableException, WorkflowStepNonOverrideableException, ComponentNotPresentOnOrgException {
        
        organizationRepo.create(TEST_CHILD_ORGANIZATION_NAME, organization, parentCategory);
        parentCategory = organizationCategoryRepo.findOne(parentCategory.getId());
        
        workflowStep = workflowStepRepo.findOne(workflowStep.getId());
                
        Note note = noteRepo.create(workflowStep, TEST_NOTE_NAME, TEST_NOTE_TEXT);
                
        note.setOverrideable(false);
        
                
        organization = organizationRepo.findOne(organization.getId());
        
        // actually set the note to non overrideable on parent first
        note = noteRepo.update(note, organization);
        
        
        assertFalse("The note was not made non-overrideable!", note.getOverrideable());
        
        organization = organizationRepo.findOne(organization.getId());
        
        String updatedName = "Updated Name",
               updatedText = "Updated Text";
        
        note.setName(updatedName);
        note.setText(updatedText);
        
        note = noteRepo.update(note, organization);
        
        
        assertEquals("The overrideable note's name was not updated even by originating organization!", note.getName(), updatedName);
        assertEquals("The overrideable note's text was not updated even by originating organization!", note.getText(), updatedText);
        
    }
    
    @Test
    public void testInheritAndRemoveNotes() {
        
        // this test calls for adding a single workflowstep to the parent organization
        workflowStepRepo.delete(workflowStep);
        
        organization = organizationRepo.findOne(organization.getId());
              
        Organization parentOrganization = organizationRepo.create(TEST_PARENT_ORGANIZATION_NAME, parentCategory);
        parentCategory = organizationCategoryRepo.findOne(parentCategory.getId());
        
        parentOrganization.addChildOrganization(organization);
        parentOrganization = organizationRepo.save(parentOrganization);
        
        organization = organizationRepo.findOne(organization.getId());
      
        
        Organization grandChildOrganization = organizationRepo.create(TEST_GRAND_CHILD_ORGANIZATION_NAME, organization, parentCategory);
        parentCategory = organizationCategoryRepo.findOne(parentCategory.getId());
        
        grandChildOrganization = organizationRepo.findOne(grandChildOrganization.getId());
        organization = organizationRepo.findOne(organization.getId());
      
        
        parentOrganization = organizationRepo.findOne(parentOrganization.getId());
        
        WorkflowStep workflowStep = workflowStepRepo.create(TEST_WORKFLOW_STEP_NAME, parentOrganization);
        
        parentOrganization = organizationRepo.findOne(parentOrganization.getId());
        organization = organizationRepo.findOne(organization.getId());
        grandChildOrganization = organizationRepo.findOne(grandChildOrganization.getId());
        
        
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                
        assertEquals("parentOrganization's workflow step has the incorrect number of notes!", 0, parentOrganization.getOriginalWorkflowSteps().get(0).getOriginalNotes().size());
        assertEquals("parentOrganization's aggregate workflow step has the incorrect number of aggregate notes!", 0, parentOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().size());
        
        assertEquals("organization's aggregate workflow step has the incorrect number of aggregate notes!", 0, organization.getAggregateWorkflowSteps().get(0).getAggregateNotes().size());
        
        assertEquals("grandChildOrganization's aggregate workflow step has the incorrect number of aggregate notes!", 0, grandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().size());
        
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        
        String note1Name = TEST_NOTE_NAME + " 1",
               note2Name = TEST_NOTE_NAME + " 2",
               note3Name = TEST_NOTE_NAME + " 3";
        
        Note n1 = noteRepo.create(workflowStep, note1Name, TEST_NOTE_TEXT);
        workflowStep = workflowStepRepo.findOne(workflowStep.getId());
        
        Note n2 = noteRepo.create(workflowStep, note2Name, TEST_NOTE_TEXT);
        workflowStep = workflowStepRepo.findOne(workflowStep.getId());
        
        Note n3 = noteRepo.create(workflowStep, note3Name, TEST_NOTE_TEXT);
        workflowStep = workflowStepRepo.findOne(workflowStep.getId());
        
        
        
        parentOrganization = organizationRepo.findOne(parentOrganization.getId());
        organization = organizationRepo.findOne(organization.getId());
        grandChildOrganization = organizationRepo.findOne(grandChildOrganization.getId());
        
        
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                
        assertEquals("parentOrganization's workflow step has the incorrect number of notes!", 3, parentOrganization.getOriginalWorkflowSteps().get(0).getOriginalNotes().size());
        assertTrue("parentOrganization's workflow step's did not contain note 1!", parentOrganization.getOriginalWorkflowSteps().get(0).getOriginalNotes().contains(n1));
        assertTrue("parentOrganization's workflow step's did not contain note 2!", parentOrganization.getOriginalWorkflowSteps().get(0).getOriginalNotes().contains(n2));
        assertTrue("parentOrganization's workflow step's did not contain note 3!", parentOrganization.getOriginalWorkflowSteps().get(0).getOriginalNotes().contains(n3));
        
        assertEquals("parentOrganization's aggregate workflow step has the incorrect number of aggregate notes!", 3, parentOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().size());
        assertTrue("parentOrganization's aggregate workflow step's did not contain note 1!", parentOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().contains(n1));
        assertTrue("parentOrganization's aggregate workflow step's did not contain note 2!", parentOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().contains(n2));
        assertTrue("parentOrganization's aggregate workflow step's did not contain note 3!", parentOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().contains(n3));
        
        assertEquals("organization's aggregate workflow step has the incorrect number of aggregate notes!", 3, organization.getAggregateWorkflowSteps().get(0).getAggregateNotes().size());
        assertTrue("organization's aggregate workflow step's did not contain note 1!", organization.getAggregateWorkflowSteps().get(0).getAggregateNotes().contains(n1));
        assertTrue("organization's aggregate workflow step's did not contain note 2!", organization.getAggregateWorkflowSteps().get(0).getAggregateNotes().contains(n2));
        assertTrue("organization's aggregate workflow step's did not contain note 3!", organization.getAggregateWorkflowSteps().get(0).getAggregateNotes().contains(n3));
        
        assertEquals("grandChildOrganization's aggregate workflow step has the incorrect number of aggregate notes!", 3, grandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().size());
        assertTrue("grandChildOrganization's aggregate workflow step's did not contain note 1!", grandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().contains(n1));
        assertTrue("grandChildOrganization's aggregate workflow step's did not contain note 2!", grandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().contains(n2));
        assertTrue("grandChildOrganization's aggregate workflow step's did not contain note 3!", grandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().contains(n3));
        
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        
        
        
        Organization greatGrandChildOrganization = organizationRepo.create("TestGreatGrandchildOrganizationName", grandChildOrganization, parentCategory);
        parentCategory = organizationCategoryRepo.findOne(parentCategory.getId());
        
        grandChildOrganization = organizationRepo.findOne(grandChildOrganization.getId());
        
        Organization anotherGreatGrandChildOrganization = organizationRepo.create("AnotherTestGreatGrandchildOrganizationName", grandChildOrganization, parentCategory);
        parentCategory = organizationCategoryRepo.findOne(parentCategory.getId());
        
        
        parentOrganization = organizationRepo.findOne(parentOrganization.getId());
        organization = organizationRepo.findOne(organization.getId());
        grandChildOrganization = organizationRepo.findOne(grandChildOrganization.getId());
        greatGrandChildOrganization = organizationRepo.findOne(greatGrandChildOrganization.getId());
        
        
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            
        assertEquals("greatGrandChildOrganization's aggregate workflow step has the incorrect number of aggregate notes!", 3, greatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().size());
        assertTrue("greatGrandChildOrganization's aggregate workflow step's did not contain note 1!", greatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().contains(n1));
        assertTrue("greatGrandChildOrganization's aggregate workflow step's did not contain note 2!", greatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().contains(n2));
        assertTrue("greatGrandChildOrganization's aggregate workflow step's did not contain note 3!", greatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().contains(n3));
        
        assertEquals("anotherGreatGrandChildOrganization's aggregate workflow step has the incorrect number of aggregate notes!", 3, anotherGreatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().size());
        assertTrue("anotherGreatGrandChildOrganization's aggregate workflow step's did not contain note 1!", anotherGreatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().contains(n1));
        assertTrue("anotherGreatGrandChildOrganization's aggregate workflow step's did not contain note 2!", anotherGreatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().contains(n2));
        assertTrue("anotherGreatGrandChildOrganization's aggregate workflow step's did not contain note 3!", anotherGreatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().contains(n3));
        
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        
        
        parentOrganization.getOriginalWorkflowSteps().get(0).removeOriginalNote(n1);
        parentOrganization = organizationRepo.save(parentOrganization);
        
        noteRepo.delete(n1);
        
        
        parentOrganization = organizationRepo.findOne(parentOrganization.getId());
        organization = organizationRepo.findOne(organization.getId());
        grandChildOrganization = organizationRepo.findOne(grandChildOrganization.getId());
        greatGrandChildOrganization = organizationRepo.findOne(greatGrandChildOrganization.getId());
        anotherGreatGrandChildOrganization = organizationRepo.findOne(anotherGreatGrandChildOrganization.getId());

        
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                
        assertEquals("parentOrganization's workflow step has the incorrect number of notes!", 2, parentOrganization.getOriginalWorkflowSteps().get(0).getOriginalNotes().size());
        assertFalse("parentOrganization's workflow step's still contains note 1!", parentOrganization.getOriginalWorkflowSteps().get(0).getOriginalNotes().contains(n1));
        assertTrue("parentOrganization's workflow step's did not contain note 2!", parentOrganization.getOriginalWorkflowSteps().get(0).getOriginalNotes().contains(n2));
        assertTrue("parentOrganization's workflow step's did not contain note 3!", parentOrganization.getOriginalWorkflowSteps().get(0).getOriginalNotes().contains(n3));
        
        assertEquals("parentOrganization's aggregate workflow step has the incorrect number of aggregate notes!", 2, parentOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().size());
        assertFalse("parentOrganization's aggregate workflow step's still contains note 1!", parentOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().contains(n1));
        assertTrue("parentOrganization's aggregate workflow step's did not contain note 2!", parentOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().contains(n2));
        assertTrue("parentOrganization's aggregate workflow step's did not contain note 3!", parentOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().contains(n3));
        
        assertEquals("organization's aggregate workflow step has the incorrect number of aggregate notes!", 2, organization.getAggregateWorkflowSteps().get(0).getAggregateNotes().size());
        assertFalse("organization's aggregate workflow step's still contains note 1!", organization.getAggregateWorkflowSteps().get(0).getAggregateNotes().contains(n1));
        assertTrue("organization's aggregate workflow step's did not contain note 2!", organization.getAggregateWorkflowSteps().get(0).getAggregateNotes().contains(n2));
        assertTrue("organization's aggregate workflow step's did not contain note 3!", organization.getAggregateWorkflowSteps().get(0).getAggregateNotes().contains(n3));
        
        assertEquals("grandChildOrganization's aggregate workflow step has the incorrect number of aggregate notes!", 2, grandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().size());
        assertFalse("grandChildOrganization's aggregate workflow step's still contains note 1!", grandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().contains(n1));
        assertTrue("grandChildOrganization's aggregate workflow step's did not contain note 2!", grandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().contains(n2));
        assertTrue("grandChildOrganization's aggregate workflow step's did not contain note 3!", grandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().contains(n3));
        
        assertEquals("greatGrandChildOrganization's aggregate workflow step has the incorrect number of aggregate notes!", 2, greatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().size());
        assertFalse("greatGrandChildOrganization's aggregate workflow step's still contains note 1!", greatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().contains(n1));
        assertTrue("greatGrandChildOrganization's aggregate workflow step's did not contain note 2!", greatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().contains(n2));
        assertTrue("greatGrandChildOrganization's aggregate workflow step's did not contain note 3!", greatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().contains(n3));
        
        assertEquals("anotherGreatGrandChildOrganization's aggregate workflow step has the incorrect number of aggregate notes!", 2, anotherGreatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().size());
        assertFalse("anotherGreatGrandChildOrganization's aggregate workflow step's still contains note 1!", anotherGreatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().contains(n1));
        assertTrue("anotherGreatGrandChildOrganization's aggregate workflow step's did not contain note 2!", anotherGreatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().contains(n2));
        assertTrue("anotherGreatGrandChildOrganization's aggregate workflow step's did not contain note 3!", anotherGreatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().contains(n3));
        
        
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                
    }
    
    
    @Test
    public void testReorderAggregateNotes() throws WorkflowStepNonOverrideableException, ComponentNotPresentOnOrgException {
        
        // this test calls for adding a single workflowstep to the parent organization
        workflowStepRepo.delete(workflowStep);
        
        organization = organizationRepo.findOne(organization.getId());
         
        Organization parentOrganization = organizationRepo.create(TEST_PARENT_ORGANIZATION_NAME, parentCategory);
        parentCategory = organizationCategoryRepo.findOne(parentCategory.getId());
        
        parentOrganization.addChildOrganization(organization);
        parentOrganization = organizationRepo.save(parentOrganization);
        
        organization = organizationRepo.findOne(organization.getId());
      
        
        Organization grandChildOrganization = organizationRepo.create(TEST_GRAND_CHILD_ORGANIZATION_NAME, organization, parentCategory);
        parentCategory = organizationCategoryRepo.findOne(parentCategory.getId());
        
        grandChildOrganization = organizationRepo.findOne(grandChildOrganization.getId());
        organization = organizationRepo.findOne(organization.getId());
        
        
        Organization greatGrandChildOrganization = organizationRepo.create("TestGreatGrandchildOrganizationName", grandChildOrganization, parentCategory);
        parentCategory = organizationCategoryRepo.findOne(parentCategory.getId());
        
        grandChildOrganization = organizationRepo.findOne(grandChildOrganization.getId());
        
        Organization anotherGreatGrandChildOrganization = organizationRepo.create("AnotherTestGreatGrandchildOrganizationName", grandChildOrganization, parentCategory);
        parentCategory = organizationCategoryRepo.findOne(parentCategory.getId());
      
        
        parentOrganization = organizationRepo.findOne(parentOrganization.getId());
        
        WorkflowStep workflowStep = workflowStepRepo.create(TEST_WORKFLOW_STEP_NAME, parentOrganization);
             
        
        String note1Name = TEST_NOTE_NAME + " 1",
               note2Name = TEST_NOTE_NAME + " 2",
               note3Name = TEST_NOTE_NAME + " 3";
         
        Note n1 = noteRepo.create(workflowStep, note1Name, TEST_NOTE_TEXT);
        workflowStep = workflowStepRepo.findOne(workflowStep.getId());
         
        Note n2 = noteRepo.create(workflowStep, note2Name, TEST_NOTE_TEXT);
        workflowStep = workflowStepRepo.findOne(workflowStep.getId());
         
        Note n3 = noteRepo.create(workflowStep, note3Name, TEST_NOTE_TEXT);
        workflowStep = workflowStepRepo.findOne(workflowStep.getId());
        
        
        Long n1Id = n1.getId();
        Long n2Id = n2.getId();
        Long n3Id = n3.getId();
        
        
        parentOrganization = organizationRepo.findOne(parentOrganization.getId());
        organization = organizationRepo.findOne(organization.getId());
        grandChildOrganization = organizationRepo.findOne(grandChildOrganization.getId());
        greatGrandChildOrganization = organizationRepo.findOne(greatGrandChildOrganization.getId());
        anotherGreatGrandChildOrganization = organizationRepo.findOne(anotherGreatGrandChildOrganization.getId());
        
        
        
        assertEquals("The parentOrganization's aggregate workflow step's first aggregate note was not as expected!", n1, parentOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(0));
        assertEquals("The parentOrganization's aggregate workflow step's second aggregate note was not as expected!", n2, parentOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(1));
        assertEquals("The parentOrganization's aggregate workflow step's third aggregate note was not as expected!", n3, parentOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(2));
        
        assertEquals("The organization's aggregate workflow step's first aggregate note was not as expected!", n1, organization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(0));
        assertEquals("The organization's aggregate workflow step's second aggregate note was not as expected!", n2, organization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(1));
        assertEquals("The organization's aggregate workflow step's third aggregate note was not as expected!", n3, organization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(2));
        
        assertEquals("The grandChildOrganization's aggregate workflow step's first aggregate note was not as expected!", n1, grandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(0));
        assertEquals("The grandChildOrganization's aggregate workflow step's second aggregate note was not as expected!", n2, grandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(1));
        assertEquals("The grandChildOrganization's aggregate workflow step's third aggregate note was not as expected!", n3, grandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(2));
        
        assertEquals("The greatGrandChildOrganization's aggregate workflow step's first aggregate note was not as expected!", n1, greatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(0));
        assertEquals("The greatGrandChildOrganization's aggregate workflow step's second aggregate note was not as expected!", n2, greatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(1));
        assertEquals("The greatGrandChildOrganization's aggregate workflow step's third aggregate note was not as expected!", n3, greatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(2));
        
        assertEquals("The anotherGreatGrandChildOrganization's aggregate workflow step's first aggregate note was not as expected!", n1, anotherGreatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(0));
        assertEquals("The anotherGreatGrandChildOrganization's aggregate workflow step's second aggregate note was not as expected!", n2, anotherGreatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(1));
        assertEquals("The anotherGreatGrandChildOrganization's aggregate workflow step's third aggregate note was not as expected!", n3, anotherGreatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(2));
        
        
        
        n1 = noteRepo.findOne(n1Id);
        n2 = noteRepo.findOne(n2Id);
        
        workflowStep = workflowStepRepo.swapNotes(parentOrganization, workflowStep, n1, n2);
        

        parentOrganization = organizationRepo.findOne(parentOrganization.getId());
        organization = organizationRepo.findOne(organization.getId());
        grandChildOrganization = organizationRepo.findOne(grandChildOrganization.getId());
        greatGrandChildOrganization = organizationRepo.findOne(greatGrandChildOrganization.getId());
        anotherGreatGrandChildOrganization = organizationRepo.findOne(anotherGreatGrandChildOrganization.getId());
       
        
       
        assertEquals("The parentOrganization's aggregate workflow step's first aggregate note was not as expected!", n2, parentOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(0));
        assertEquals("The parentOrganization's aggregate workflow step's second aggregate note was not as expected!", n1, parentOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(1));
        assertEquals("The parentOrganization's aggregate workflow step's third aggregate note was not as expected!", n3, parentOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(2));
        
        assertEquals("The organization's aggregate workflow step's first aggregate note was not as expected!", n2, organization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(0));
        assertEquals("The organization's aggregate workflow step's second aggregate note was not as expected!", n1, organization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(1));
        assertEquals("The organization's aggregate workflow step's third aggregate note was not as expected!", n3, organization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(2));
        
        assertEquals("The grandChildOrganization's aggregate workflow step's first aggregate note was not as expected!", n2, grandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(0));
        assertEquals("The grandChildOrganization's aggregate workflow step's second aggregate note was not as expected!", n1, grandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(1));
        assertEquals("The grandChildOrganization's aggregate workflow step's third aggregate note was not as expected!", n3, grandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(2));
        
        assertEquals("The greatGrandChildOrganization's aggregate workflow step's first aggregate note was not as expected!", n2, greatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(0));
        assertEquals("The greatGrandChildOrganization's aggregate workflow step's second aggregate note was not as expected!", n1, greatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(1));
        assertEquals("The greatGrandChildOrganization's aggregate workflow step's third aggregate note was not as expected!", n3, greatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(2));
        
        assertEquals("The anotherGreatGrandChildOrganization's aggregate workflow step's first aggregate note was not as expected!", n2, anotherGreatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(0));
        assertEquals("The anotherGreatGrandChildOrganization's aggregate workflow step's second aggregate note was not as expected!", n1, anotherGreatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(1));
        assertEquals("The anotherGreatGrandChildOrganization's aggregate workflow step's third aggregate note was not as expected!", n3, anotherGreatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(2));
        
        
        n2 = noteRepo.findOne(n2Id);
        n3 = noteRepo.findOne(n3Id);
        
        workflowStep = workflowStepRepo.swapNotes(parentOrganization, workflowStep, n2, n3);
        

        parentOrganization = organizationRepo.findOne(parentOrganization.getId());
        organization = organizationRepo.findOne(organization.getId());
        grandChildOrganization = organizationRepo.findOne(grandChildOrganization.getId());
        greatGrandChildOrganization = organizationRepo.findOne(greatGrandChildOrganization.getId());
        anotherGreatGrandChildOrganization = organizationRepo.findOne(anotherGreatGrandChildOrganization.getId());
       
        
       
        assertEquals("The parentOrganization's aggregate workflow step's first aggregate note was not as expected!", n3, parentOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(0));
        assertEquals("The parentOrganization's aggregate workflow step's second aggregate note was not as expected!", n1, parentOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(1));
        assertEquals("The parentOrganization's aggregate workflow step's third aggregate note was not as expected!", n2, parentOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(2));
        
        assertEquals("The organization's aggregate workflow step's first aggregate note was not as expected!", n3, organization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(0));
        assertEquals("The organization's aggregate workflow step's second aggregate note was not as expected!", n1, organization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(1));
        assertEquals("The organization's aggregate workflow step's third aggregate note was not as expected!", n2, organization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(2));
        
        assertEquals("The grandChildOrganization's aggregate workflow step's first aggregate note was not as expected!", n3, grandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(0));
        assertEquals("The grandChildOrganization's aggregate workflow step's second aggregate note was not as expected!", n1, grandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(1));
        assertEquals("The grandChildOrganization's aggregate workflow step's third aggregate note was not as expected!", n2, grandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(2));
        
        assertEquals("The greatGrandChildOrganization's aggregate workflow step's first aggregate note was not as expected!", n3, greatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(0));
        assertEquals("The greatGrandChildOrganization's aggregate workflow step's second aggregate note was not as expected!", n1, greatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(1));
        assertEquals("The greatGrandChildOrganization's aggregate workflow step's third aggregate note was not as expected!", n2, greatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(2));
        
        assertEquals("The anotherGreatGrandChildOrganization's aggregate workflow step's first aggregate note was not as expected!", n3, anotherGreatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(0));
        assertEquals("The anotherGreatGrandChildOrganization's aggregate workflow step's second aggregate note was not as expected!", n1, anotherGreatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(1));
        assertEquals("The anotherGreatGrandChildOrganization's aggregate workflow step's third aggregate note was not as expected!", n2, anotherGreatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(2));
        
        
        n1 = noteRepo.findOne(n1Id);
        n3 = noteRepo.findOne(n3Id);
        
        // creates a new workflow step
        workflowStepRepo.swapNotes(organization, workflowStep, n1, n3);
        

        parentOrganization = organizationRepo.findOne(parentOrganization.getId());
        organization = organizationRepo.findOne(organization.getId());
        grandChildOrganization = organizationRepo.findOne(grandChildOrganization.getId());
        greatGrandChildOrganization = organizationRepo.findOne(greatGrandChildOrganization.getId());
        anotherGreatGrandChildOrganization = organizationRepo.findOne(anotherGreatGrandChildOrganization.getId());
        
        
        
        WorkflowStep newWorkflowStep = organization.getOriginalWorkflowSteps().get(0);
         
        
        
        assertEquals("The parentOrganization's aggregate workflow step's first aggregate note was not as expected!", n3, parentOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(0));
        assertEquals("The parentOrganization's aggregate workflow step's second aggregate note was not as expected!", n1, parentOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(1));
        assertEquals("The parentOrganization's aggregate workflow step's third aggregate note was not as expected!", n2, parentOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(2));
        
        // make sure new workflow step contains all notes
        assertTrue("The organization's original workflow step's contains first note!", organization.getOriginalWorkflowSteps().get(0).getAggregateNotes().contains(n1));
        assertTrue("The organization's original workflow step's contains second note!!", organization.getOriginalWorkflowSteps().get(0).getAggregateNotes().contains(n2));
        assertTrue("The organization's original workflow step's contains third note!!", organization.getOriginalWorkflowSteps().get(0).getAggregateNotes().contains(n3));
        
        
        assertEquals("The organization aggregate workflow steps does not have new workflow step from reorder on non originating organization!", newWorkflowStep, organization.getAggregateWorkflowSteps().get(0));
        
        assertEquals("The organization's aggregate workflow step's first aggregate note was not as expected!", n1, organization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(0));
        assertEquals("The organization's aggregate workflow step's second aggregate note was not as expected!", n3, organization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(1));
        assertEquals("The organization's aggregate workflow step's third aggregate note was not as expected!", n2, organization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(2));
        
        
        assertEquals("The grandChildOrganization did not inherit new workflow step from reorder on non originating organization!", newWorkflowStep, grandChildOrganization.getAggregateWorkflowSteps().get(0));
        
        assertEquals("The grandChildOrganization's aggregate workflow step's first aggregate note was not as expected!", n1, grandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(0));
        assertEquals("The grandChildOrganization's aggregate workflow step's second aggregate note was not as expected!", n3, grandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(1));
        assertEquals("The grandChildOrganization's aggregate workflow step's third aggregate note was not as expected!", n2, grandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(2));
        
        
        assertEquals("The greatGrandChildOrganization did not inherit new workflow step from reorder on non originating organization!", newWorkflowStep, greatGrandChildOrganization.getAggregateWorkflowSteps().get(0));
        
        assertEquals("The greatGrandChildOrganization's aggregate workflow step's first aggregate note was not as expected!", n1, greatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(0));
        assertEquals("The greatGrandChildOrganization's aggregate workflow step's second aggregate note was not as expected!", n3, greatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(1));
        assertEquals("The greatGrandChildOrganization's aggregate workflow step's third aggregate note was not as expected!", n2, greatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(2));
        
        
        assertEquals("The anotherGreatGrandChildOrganization did not inherit new workflow step from reorder on non originating organization!", newWorkflowStep, anotherGreatGrandChildOrganization.getAggregateWorkflowSteps().get(0));
        
        assertEquals("The anotherGreatGrandChildOrganization's aggregate workflow step's first aggregate note was not as expected!", n1, anotherGreatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(0));
        assertEquals("The anotherGreatGrandChildOrganization's aggregate workflow step's second aggregate note was not as expected!", n3, anotherGreatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(1));
        assertEquals("The anotherGreatGrandChildOrganization's aggregate workflow step's third aggregate note was not as expected!", n2, anotherGreatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(2));
        
    }
    
    @Test
    public void testNoteChangeAtChildOrg() throws HeritableModelNonOverrideableException, WorkflowStepNonOverrideableException, ComponentNotPresentOnOrgException {
        
        // this test calls for adding a single workflowstep to the parent organization
        workflowStepRepo.delete(workflowStep);
        
        organization = organizationRepo.findOne(organization.getId());
        
        Organization parentOrganization = organizationRepo.create(TEST_PARENT_ORGANIZATION_NAME, parentCategory);
        parentCategory = organizationCategoryRepo.findOne(parentCategory.getId());
                
        parentOrganization.addChildOrganization(organization);
        parentOrganization = organizationRepo.save(parentOrganization);
        
        organization = organizationRepo.findOne(organization.getId());
      
        Organization grandChildOrganization = organizationRepo.create(TEST_GRAND_CHILD_ORGANIZATION_NAME, parentCategory);
        parentCategory = organizationCategoryRepo.findOne(parentCategory.getId());
        
        
        grandChildOrganization = organizationRepo.findOne(grandChildOrganization.getId());
        organization = organizationRepo.findOne(organization.getId());
        
        organization.addChildOrganization(grandChildOrganization);
        organization = organizationRepo.save(organization);
        
        
        Organization greatGrandChildOrganization = organizationRepo.create("TestGreatGrandchildOrganizationName", parentCategory);
        parentCategory = organizationCategoryRepo.findOne(parentCategory.getId());
        
        
        greatGrandChildOrganization = organizationRepo.findOne(greatGrandChildOrganization.getId());
        grandChildOrganization = organizationRepo.findOne(grandChildOrganization.getId());
        
        grandChildOrganization.addChildOrganization(greatGrandChildOrganization);
        grandChildOrganization = organizationRepo.save(grandChildOrganization);
        
        Organization anotherGreatGrandChildOrganization = organizationRepo.create("AnotherTestGreatGrandchildOrganizationName", parentCategory);
        parentCategory = organizationCategoryRepo.findOne(parentCategory.getId());
        
        
        anotherGreatGrandChildOrganization = organizationRepo.findOne(anotherGreatGrandChildOrganization.getId());
        grandChildOrganization = organizationRepo.findOne(grandChildOrganization.getId());
        
        grandChildOrganization.addChildOrganization(anotherGreatGrandChildOrganization);
        grandChildOrganization = organizationRepo.save(grandChildOrganization);
        
        
        
        parentOrganization = organizationRepo.findOne(parentOrganization.getId());
        
        WorkflowStep workflowStep = workflowStepRepo.create(TEST_WORKFLOW_STEP_NAME, parentOrganization);
        
        Note note = noteRepo.create(workflowStep, TEST_NOTE_NAME, TEST_NOTE_TEXT);
        
        workflowStep = workflowStepRepo.findOne(workflowStep.getId());
        
        Long originalNoteId = note.getId();
        
        parentOrganization = organizationRepo.findOne(parentOrganization.getId());
        organization = organizationRepo.findOne(organization.getId());
        grandChildOrganization = organizationRepo.findOne(grandChildOrganization.getId());
        greatGrandChildOrganization = organizationRepo.findOne(greatGrandChildOrganization.getId());
        anotherGreatGrandChildOrganization = organizationRepo.findOne(anotherGreatGrandChildOrganization.getId());
        
        
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        
                
        assertEquals("Parent organization has the incorrect number of workflow steps!", 1, parentOrganization.getOriginalWorkflowSteps().size());
        assertEquals("Parent organization has wrong size of workflow!", 1, parentOrganization.getAggregateWorkflowSteps().size());
        
        assertEquals("organization has the incorrect number of workflow steps!", 0, organization.getOriginalWorkflowSteps().size());
        assertEquals("organization has wrong size of workflow!", 1, organization.getAggregateWorkflowSteps().size());
        
        assertEquals("Grand child organization has the incorrect number of workflow steps!", 0, grandChildOrganization.getOriginalWorkflowSteps().size());
        assertEquals("Grand child organization has wrong size of workflow!", 1, grandChildOrganization.getAggregateWorkflowSteps().size());
        
        assertEquals("Great grand child organization has the incorrect number of workflow steps!", 0, greatGrandChildOrganization.getOriginalWorkflowSteps().size());
        assertEquals("Great grand child organization has wrong size of workflow!", 1, greatGrandChildOrganization.getAggregateWorkflowSteps().size());
        
        assertEquals("Another great grand child organization has the incorrect number of workflow steps!", 0, anotherGreatGrandChildOrganization.getOriginalWorkflowSteps().size());
        assertEquals("Another great grand child organization has wrong size of workflow!", 1, anotherGreatGrandChildOrganization.getAggregateWorkflowSteps().size());
        
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        
        

        note.setName("Updated Name");
      
        //request the change at the level of the child organization        
        Note updatedNote = noteRepo.update(note, organization);
        
        
        parentOrganization = organizationRepo.findOne(parentOrganization.getId());
        organization = organizationRepo.findOne(organization.getId());
        grandChildOrganization = organizationRepo.findOne(grandChildOrganization.getId());
        greatGrandChildOrganization = organizationRepo.findOne(greatGrandChildOrganization.getId());
        anotherGreatGrandChildOrganization = organizationRepo.findOne(anotherGreatGrandChildOrganization.getId());
        
        // pointer to note became updatedNote, must fetch it agains
        note = noteRepo.findOne(originalNoteId);
      
        //There should be a new workflow step on the child organization that is distinct from the original workflowStep
        WorkflowStep updatedWorkflowStep = organization.getAggregateWorkflowSteps().get(0);
        assertFalse("The updatedWorkflowStep was just the same as the original from which it was derived when its note was updated!", workflowStep.getId().equals(updatedWorkflowStep.getId()));
      
        //The new workflow step should contain the new updatedNote
        assertTrue("The updatedWorkflowStep didn't contain the new updatedNote", updatedWorkflowStep.getAggregateNotes().contains(updatedNote));
      
        //The updatedNote should be distinct from the original note
        assertFalse("The updatedNote was just the same as the original from which it was derived!", note.getId().equals(updatedNote.getId()));
      
        //the grandchild and great grandchildren should all be using the new workflow step and the updatedNote
        assertTrue("The grandchild org didn't have the updatedWorkflowStep!", grandChildOrganization.getAggregateWorkflowSteps().contains(updatedWorkflowStep));
        assertTrue("The grandchild org didn't have the updatedNote on the updatedWorkflowStep!", grandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().contains(updatedNote));
        assertTrue("The great grandchild org didn't have the updatedWorkflowStep!", greatGrandChildOrganization.getAggregateWorkflowSteps().contains(updatedWorkflowStep));
        assertTrue("The great grandchild org didn't have the updatedNote on the updatedWorkflowStep!", greatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().contains(updatedNote));
        assertTrue("Another great grandchild org didn't have the updatedWorkflowStep!", anotherGreatGrandChildOrganization.getAggregateWorkflowSteps().contains(updatedWorkflowStep));
        assertTrue("Another great grandchild org didn't have the updatedNote on the updatedWorkflowStep!", anotherGreatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().contains(updatedNote));
    }
 
    @Test
    public void testMaintainNoteOrderWhenOverriding() throws HeritableModelNonOverrideableException, WorkflowStepNonOverrideableException, ComponentNotPresentOnOrgException {
        
        // this test calls for adding a single workflowstep to the parent organization
        workflowStepRepo.delete(workflowStep);
        
        organization = organizationRepo.findOne(organization.getId());
      
        Organization parentOrganization = organizationRepo.create(TEST_PARENT_ORGANIZATION_NAME, parentCategory);
        parentCategory = organizationCategoryRepo.findOne(parentCategory.getId());
        
        parentOrganization.addChildOrganization(organization);
        parentOrganization = organizationRepo.save(parentOrganization);
        
        organization = organizationRepo.findOne(organization.getId());
      
        
        Organization grandChildOrganization = organizationRepo.create(TEST_GRAND_CHILD_ORGANIZATION_NAME, organization, parentCategory);
        parentCategory = organizationCategoryRepo.findOne(parentCategory.getId());
        
        grandChildOrganization = organizationRepo.findOne(grandChildOrganization.getId());
        organization = organizationRepo.findOne(organization.getId());
      
        
        Organization greatGrandChildOrganization = organizationRepo.create("TestGreatGrandchildOrganizationName", grandChildOrganization, parentCategory);
        parentCategory = organizationCategoryRepo.findOne(parentCategory.getId());
        
        greatGrandChildOrganization = organizationRepo.findOne(greatGrandChildOrganization.getId());
        grandChildOrganization = organizationRepo.findOne(grandChildOrganization.getId());
        
      
        Organization anotherGreatGrandChildOrganization = organizationRepo.create("AnotherTestGreatGrandchildOrganizationName", grandChildOrganization, parentCategory);
        parentCategory = organizationCategoryRepo.findOne(parentCategory.getId());
        
        anotherGreatGrandChildOrganization = organizationRepo.findOne(anotherGreatGrandChildOrganization.getId());
        grandChildOrganization = organizationRepo.findOne(grandChildOrganization.getId());
        
        
        parentOrganization = organizationRepo.findOne(parentOrganization.getId());
        
        WorkflowStep workflowStep = workflowStepRepo.create(TEST_WORKFLOW_STEP_NAME, parentOrganization);
        
        
        String note1Name = TEST_NOTE_NAME + " 1",
               note2Name = TEST_NOTE_NAME + " 2",
               note3Name = TEST_NOTE_NAME + " 3";
         
        Note n1 = noteRepo.create(workflowStep, note1Name, TEST_NOTE_TEXT);
        workflowStep = workflowStepRepo.findOne(workflowStep.getId());
         
        Note n2 = noteRepo.create(workflowStep, note2Name, TEST_NOTE_TEXT);
        workflowStep = workflowStepRepo.findOne(workflowStep.getId());
         
        Note n3 = noteRepo.create(workflowStep, note3Name, TEST_NOTE_TEXT);
        workflowStep = workflowStepRepo.findOne(workflowStep.getId());
        
        
        n2.setName("Updated Name!");
        
        grandChildOrganization = organizationRepo.findOne(grandChildOrganization.getId());
        
        Note n2Updated = noteRepo.update(n2, grandChildOrganization);
        
        grandChildOrganization = organizationRepo.findOne(grandChildOrganization.getId());
        
        WorkflowStep newWSWithNewNoteViaOriginals = grandChildOrganization.getOriginalWorkflowSteps().get(0);
        WorkflowStep newWSWithNewNoteViaAggregation = grandChildOrganization.getAggregateWorkflowSteps().get(0);
        
          
        assertEquals("The new aggregated workflow step on the grandchild org was not the one the grandchild org just originated!  Original had id " + newWSWithNewNoteViaOriginals.getId() + " while aggregate had id " + newWSWithNewNoteViaAggregation.getId(), newWSWithNewNoteViaOriginals, newWSWithNewNoteViaAggregation);
        
        
        assertEquals("Updated note was in the wrong order!", n2Updated.getId(), grandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(1).getId());
        
    }
    
    
    /**
     * Test that when a note is overridden at a child org, the override is removed and the original is reattached when 
     * the note is made non-overrideable at a parent org.
     * @throws WorkflowStepNonOverrideableException
     * @throws HeritableModelNonOverrideableException
     * @throws ComponentNotPresentOnOrgException
     */
    @Test
    public void testReInheritOverriddenNote() throws WorkflowStepNonOverrideableException, HeritableModelNonOverrideableException, ComponentNotPresentOnOrgException {
        // this test calls for adding a single workflowstep to the parent organization, so get rid of the one at the middle org
        workflowStepRepo.delete(workflowStep);
        
        organization = organizationRepo.findOne(organization.getId());
        
        Organization parentOrganization = organizationRepo.create(TEST_PARENT_ORGANIZATION_NAME, parentCategory);
        parentCategory = organizationCategoryRepo.findOne(parentCategory.getId());
        
        parentOrganization.addChildOrganization(organization);
        parentOrganization = organizationRepo.save(parentOrganization);
        
        organization = organizationRepo.findOne(organization.getId());
      
        
        Organization grandChildOrganization = organizationRepo.create(TEST_GRAND_CHILD_ORGANIZATION_NAME, organization, parentCategory);
        parentCategory = organizationCategoryRepo.findOne(parentCategory.getId());
        
        grandChildOrganization = organizationRepo.findOne(grandChildOrganization.getId());
        organization = organizationRepo.findOne(organization.getId());
      
        
        parentOrganization = organizationRepo.findOne(parentOrganization.getId());
        
        WorkflowStep workflowStep = workflowStepRepo.create(TEST_WORKFLOW_STEP_NAME, parentOrganization);
        
        parentOrganization = organizationRepo.findOne(parentOrganization.getId());
        organization = organizationRepo.findOne(organization.getId());
        grandChildOrganization = organizationRepo.findOne(grandChildOrganization.getId());
        
        //now we have an org hierarchy with a parent, child, and grandchild
        /////////////////////////////////////////////////////////////////////////
        String note1Name = TEST_NOTE_NAME + " 1",
               note2Name = TEST_NOTE_NAME + " 2",
               note3Name = TEST_NOTE_NAME + " 3";
         
         Note n1 = noteRepo.create(workflowStep, note1Name, TEST_NOTE_TEXT);
         workflowStep = workflowStepRepo.findOne(workflowStep.getId());
         
         Note n2 = noteRepo.create(workflowStep, note2Name, TEST_NOTE_TEXT);
         workflowStep = workflowStepRepo.findOne(workflowStep.getId());
         
         Note n3 = noteRepo.create(workflowStep, note3Name, TEST_NOTE_TEXT);
         workflowStep = workflowStepRepo.findOne(workflowStep.getId());
         
         
         
         grandChildOrganization = organizationRepo.findOne(grandChildOrganization.getId());
         
         // now we have three notes originating at the topmost org
         //////////////////////////////////////////////////////////////////////
         
         // make a note non-overrideable at the grandchild org
         long n2Id = n2.getId();
         
         n2.setOverrideable(false);
         
         Note n2updatedAtGrandchild = noteRepo.update(n2, grandChildOrganization);
         
         grandChildOrganization = organizationRepo.findOne(grandChildOrganization.getId());
         
         n2 = noteRepo.findOne(n2Id);
         
         // ensure that a new note got made at the grandchild after this override (to non-overrideable :) )
         // old n2 should still be overrideable
         assertTrue("Note updated at grandchild changed the note at the parent!" , n2.getOverrideable());
         
         // old n2 should be different from the new n2 updated at the grandchild
         assertFalse("Note updated at grandchild didn't get duplicated!", n2.getId().equals(n2updatedAtGrandchild.getId()));
         assertEquals("A new Note didn't get originated at an org that overrode it!", 1, grandChildOrganization.getAggregateWorkflowSteps().get(0).getOriginalNotes().size());
         assertTrue("A new Note didn't get originated at an org that overrode it!", grandChildOrganization.getAggregateWorkflowSteps().get(0).getOriginalNotes().contains(n2updatedAtGrandchild));
         
                 
         organization = organizationRepo.findOne(organization.getId());
                  
         // TODO: make the note non-overrideable at the child org
         n2.setOverrideable(false);
         Note n2updatedAtChild = noteRepo.update(n2, organization);

         
         n2 = noteRepo.findOne(n2Id);
         
         // ensure that a new note got made at the child after this override (to non-overrideable :) )
         // old n2 should still be overrideable
         assertTrue("Note updated at child didn't get duplicated!" , n2.getOverrideable());
         // old n2 should be different from the new n2 updated at the grandchild
         assertFalse("Note updated at child didn't get duplicated!", n2.getId().equals(n2updatedAtChild.getId()));
         
         grandChildOrganization = organizationRepo.findOne(grandChildOrganization.getId());
         
         
         
         assertFalse("Grand child inherited original note from child organization", grandChildOrganization.getAggregateWorkflowSteps().get(0).getOriginalNotes().contains(n2updatedAtChild));
         
         // ensure that the grandchild's new note is replaced by the child's non-overrideable one
         assertTrue("Note made non-overrideable didn't blow away an inferior override at descendant org!", grandChildOrganization.getAggregateWorkflowSteps().get(0).getOriginalNotes().isEmpty());
         assertFalse("Note made non-overrideable still stuck around at the grandchild who'd overridden it!", grandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().contains(n2updatedAtGrandchild));
         
         // TODO: make the note non-overrideable at the parent org
         // TOOO: ensure that the child's note is replaced by the original, non-overrideable at both the child and grandchild
         
         
         // now all is restored, except one of the notes is non-overrideable at the parent
         
         
         // TODO: remove a note at the grandchild
         // TODO: make the removed note non-overrideable at the parent
         // TODO: ensure that the removed note is reaggregated upon the grandchild who removed it
         
    }

    
    //TODO:  this test is not done, development of the full feature (indefinitely) deferred for now
    @Test
    public void testDeleteNoteAtDescendantOrganizationAndDuplicateWorkflowStepIsDeletedToo() throws HeritableModelNonOverrideableException, WorkflowStepNonOverrideableException, ComponentNotPresentOnOrgException
    {
        // this test calls for adding a single workflowstep to the parent organization
        workflowStepRepo.delete(workflowStep);
        
        organization = organizationRepo.findOne(organization.getId());
        
      
        Organization parentOrganization = organizationRepo.create(TEST_PARENT_ORGANIZATION_NAME, parentCategory);
        parentCategory = organizationCategoryRepo.findOne(parentCategory.getId());
        
        parentOrganization.addChildOrganization(organization);
        parentOrganization = organizationRepo.save(parentOrganization);
        
        organization = organizationRepo.findOne(organization.getId());
        
        Organization grandChildOrganization = organizationRepo.create(TEST_GRAND_CHILD_ORGANIZATION_NAME, organization, parentCategory);
        parentCategory = organizationCategoryRepo.findOne(parentCategory.getId());
        
        grandChildOrganization = organizationRepo.findOne(grandChildOrganization.getId());
        organization = organizationRepo.findOne(organization.getId());
        
        Organization greatGrandChildOrganization = organizationRepo.create("TestGreatGrandchildOrganizationName", grandChildOrganization, parentCategory);
        parentCategory = organizationCategoryRepo.findOne(parentCategory.getId());
        
        greatGrandChildOrganization = organizationRepo.findOne(greatGrandChildOrganization.getId());
        grandChildOrganization = organizationRepo.findOne(grandChildOrganization.getId());
        
      
        Organization anotherGreatGrandChildOrganization = organizationRepo.create("AnotherTestGreatGrandchildOrganizationName", grandChildOrganization, parentCategory);
        parentCategory = organizationCategoryRepo.findOne(parentCategory.getId());
        
        anotherGreatGrandChildOrganization = organizationRepo.findOne(anotherGreatGrandChildOrganization.getId());
        grandChildOrganization = organizationRepo.findOne(grandChildOrganization.getId());
        
        
        parentOrganization = organizationRepo.findOne(parentOrganization.getId());
        
        WorkflowStep workflowStep = workflowStepRepo.create(TEST_WORKFLOW_STEP_NAME, parentOrganization);
        
        String note1Name = TEST_NOTE_NAME + " 1",
               note2Name = TEST_NOTE_NAME + " 2",
               note3Name = TEST_NOTE_NAME + " 3";
         
        Note n1 = noteRepo.create(workflowStep, note1Name, TEST_NOTE_TEXT);
        workflowStep = workflowStepRepo.findOne(workflowStep.getId());
         
        Note n2 = noteRepo.create(workflowStep, note2Name, TEST_NOTE_TEXT);
        workflowStep = workflowStepRepo.findOne(workflowStep.getId());
         
        Note n3 = noteRepo.create(workflowStep, note3Name, TEST_NOTE_TEXT);
        workflowStep = workflowStepRepo.findOne(workflowStep.getId());
        
        
        
        organization = organizationRepo.findOne(organization.getId());
        
        //override the note at the child
        n1.setName("Updated Name!");
        Note nPrime = noteRepo.update(n1, organization);
        
        //TODO:  make note non-overrideable, check that fpPrime goes away and the new derivative step goes away
        
    }
    

    @After
    public void cleanUp() {
        noteRepo.findAll().forEach(note -> {
            noteRepo.delete(note);
        });

        workflowStepRepo.findAll().forEach(workflowStep -> {
            workflowStepRepo.delete(workflowStep);
        });

        organizationCategoryRepo.deleteAll();

        organizationRepo.findAll().forEach(organization -> {
            organizationRepo.delete(organization);
        });
    }

}
