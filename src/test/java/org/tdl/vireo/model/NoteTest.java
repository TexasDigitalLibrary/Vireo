package org.tdl.vireo.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.tdl.vireo.exception.ComponentNotPresentOnOrgException;
import org.tdl.vireo.exception.HeritableModelNonOverrideableException;
import org.tdl.vireo.exception.WorkflowStepNonOverrideableException;

public class NoteTest extends AbstractEntityTest {

    @BeforeEach
    public void setUp() {
        parentCategory = organizationCategoryRepo.create(TEST_CATEGORY_NAME);
        organization = organizationRepo.create(TEST_ORGANIZATION_NAME, parentCategory);
        parentCategory = organizationCategoryRepo.getById(parentCategory.getId());
        workflowStep = workflowStepRepo.create(TEST_WORKFLOW_STEP_NAME, organization);
        organization = organizationRepo.getById(organization.getId());
    }

    @Override
    public void testCreate() {
        Note note = noteRepo.create(workflowStep, TEST_NOTE_NAME, TEST_NOTE_TEXT);
        assertEquals(1, noteRepo.count(), "The entity was not created!");
        assertEquals(TEST_NOTE_NAME, note.getName(), "The entity did not have the correct name!");
        assertEquals(TEST_NOTE_TEXT, note.getText(), "The entity did not have the correct text!");
    }

    @Override
    public void testDuplication() {
        noteRepo.create(workflowStep, TEST_NOTE_NAME, TEST_NOTE_TEXT);
        try {
            noteRepo.create(workflowStep, TEST_NOTE_NAME, TEST_NOTE_TEXT);
        } catch (DataIntegrityViolationException e) {
            /* SUCCESS */ }
        assertEquals(1, noteRepo.count(), "The repository duplicated entity!");
    }

    @Override
    public void testDelete() {
        Note note = noteRepo.create(workflowStep, TEST_NOTE_NAME, TEST_NOTE_TEXT);
        noteRepo.delete(note);
        assertEquals(0, noteRepo.count(), "The entity was not deleted!");
    }

    @Override
    public void testCascade() {
        Note note = noteRepo.create(workflowStep, TEST_NOTE_NAME, TEST_NOTE_TEXT);
        assertEquals(1, noteRepo.count(), "The entity was not created!");
        assertEquals(TEST_NOTE_NAME, note.getName(), "The entity did not have the correct name!");
        assertEquals(TEST_NOTE_TEXT, note.getText(), "The entity did not have the correct text!");

        // test delete note
        noteRepo.delete(note);
        assertEquals(0, noteRepo.count(), "An note was not deleted!");
        assertEquals(1, workflowStepRepo.count(), "The workflowstep was deleted!");
    }

    @Test
    public void testInheritNoteViaPointer() throws ComponentNotPresentOnOrgException {

        Organization parentOrganization = organizationRepo.create(TEST_PARENT_ORGANIZATION_NAME, parentCategory);
        parentCategory = organizationCategoryRepo.getById(parentCategory.getId());

        Organization childOrganization = organizationRepo.create(TEST_CHILD_ORGANIZATION_NAME, parentCategory);
        parentCategory = organizationCategoryRepo.getById(parentCategory.getId());

        parentOrganization = organizationRepo.getById(parentOrganization.getId());

        parentOrganization.addChildOrganization(childOrganization);
        parentOrganization = organizationRepo.save(parentOrganization);

        parentOrganization = organizationRepo.getById(parentOrganization.getId());
        childOrganization = organizationRepo.getById(childOrganization.getId());

        Organization grandchildOrganization = organizationRepo.create(TEST_GRAND_CHILD_ORGANIZATION_NAME, parentCategory);
        parentCategory = organizationCategoryRepo.getById(parentCategory.getId());

        grandchildOrganization = organizationRepo.getById(grandchildOrganization.getId());

        childOrganization.addChildOrganization(grandchildOrganization);
        childOrganization = organizationRepo.save(childOrganization);

        parentOrganization = organizationRepo.getById(parentOrganization.getId());

        WorkflowStep parentWorkflowStep = workflowStepRepo.create(TEST_PARENT_WORKFLOW_STEP_NAME, parentOrganization);

        Note note = noteRepo.create(parentWorkflowStep, TEST_NOTE_NAME, TEST_NOTE_TEXT);

        parentOrganization = organizationRepo.getById(parentOrganization.getId());
        childOrganization = organizationRepo.getById(childOrganization.getId());
        grandchildOrganization = organizationRepo.getById(grandchildOrganization.getId());

        assertTrue(parentOrganization.getAggregateWorkflowSteps().get(0).getOriginalNotes().contains(note), "The parent organization's workflow did not contain the original note!");
        assertTrue(childOrganization.getAggregateWorkflowSteps().get(0).getOriginalNotes().contains(note), "The child organization's workflow did not contain the original note!");
        assertTrue(grandchildOrganization.getAggregateWorkflowSteps().get(0).getOriginalNotes().contains(note), "The grand child organization's workflow did not contain the original note!");

        assertTrue(parentOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().contains(note), "The parent organization's workflow did not contain the aggregate note!");
        assertTrue(childOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().contains(note), "The child organization's workflow did not contain the aggregate note!");
        assertTrue(grandchildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().contains(note), "The grand child organization's workflow did not contain the aggregate note!");

        String noteText = "Updated Text";
        note.setText(noteText);

        try {
            noteRepo.update(note, parentOrganization);
        } catch (HeritableModelNonOverrideableException e) {
            e.printStackTrace();
            assertTrue(false, "The note did not update beacuase it is non overrideable!");

        } catch (WorkflowStepNonOverrideableException e) {
            e.printStackTrace();
            assertTrue(false, "The note did not update beacuase it's owning workflow step is non overrideable!");
        }

        parentOrganization = organizationRepo.getById(parentOrganization.getId());
        childOrganization = organizationRepo.getById(childOrganization.getId());
        grandchildOrganization = organizationRepo.getById(grandchildOrganization.getId());

        assertEquals(noteText, parentOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(0).getText(), "The parent's note's text did not update");
        assertEquals(noteText, childOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(0).getText(), "The child's note's text did not update");
        assertEquals(noteText, grandchildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(0).getText(), "The grand child's note's text did not update");

    }

    @Test
    public void testCantOverrideNonOverrideableNote() throws HeritableModelNonOverrideableException, WorkflowStepNonOverrideableException, ComponentNotPresentOnOrgException {

        Assertions.assertThrows(HeritableModelNonOverrideableException.class, () -> {

            Organization childOrganization = organizationRepo.create(TEST_CHILD_ORGANIZATION_NAME, organization, parentCategory);
            parentCategory = organizationCategoryRepo.getById(parentCategory.getId());

            workflowStep = workflowStepRepo.getById(workflowStep.getId());

            Note note = noteRepo.create(workflowStep, TEST_NOTE_NAME, TEST_NOTE_TEXT);

            note.setOverrideable(false);

            organization = organizationRepo.getById(organization.getId());

            // actually set the note to non overrideable on parent first
            note = noteRepo.update(note, organization);

            assertFalse(note.getOverrideable(), "The note was not made non-overrideable!");

            childOrganization = organizationRepo.getById(childOrganization.getId());

            note.setName("Updated Name");

            noteRepo.update(note, childOrganization);
        });
    }

    @Test
    public void testCantOverrideNonOverrideableWorkflowStep() throws HeritableModelNonOverrideableException, WorkflowStepNonOverrideableException, ComponentNotPresentOnOrgException {

        Assertions.assertThrows(WorkflowStepNonOverrideableException.class, () -> {
            Organization childOrganization = organizationRepo.create(TEST_CHILD_ORGANIZATION_NAME, organization, parentCategory);
            parentCategory = organizationCategoryRepo.getById(parentCategory.getId());

            workflowStep = workflowStepRepo.getById(workflowStep.getId());

            Note note = noteRepo.create(workflowStep, TEST_NOTE_NAME, TEST_NOTE_TEXT);

            organization = organizationRepo.getById(organization.getId());
            workflowStep = workflowStepRepo.getById(workflowStep.getId());

            workflowStep.setOverrideable(false);

            workflowStep = workflowStepRepo.update(workflowStep, organization);

            note = noteRepo.getById(note.getId());

            childOrganization = organizationRepo.getById(childOrganization.getId());

            workflowStep = workflowStepRepo.getById(workflowStep.getId());

            assertEquals(workflowStep, note.getOriginatingWorkflowStep(), "The note's originating workflow step is not the intended workflow step!");

            assertFalse(note.getOriginatingWorkflowStep().getOverrideable(), "The note's originating workflow step was not made non-overrideable!");

            assertFalse(workflowStep.getOverrideable(), "The workflowstep was not made non-overrideable!");

            note.setName("Updated Name");

            noteRepo.update(note, childOrganization);
        });
    }

    @Test
    public void testCanOverrideNonOverrideableAtOriginatingOrg() throws HeritableModelNonOverrideableException, WorkflowStepNonOverrideableException, ComponentNotPresentOnOrgException {

        organizationRepo.create(TEST_CHILD_ORGANIZATION_NAME, organization, parentCategory);
        parentCategory = organizationCategoryRepo.getById(parentCategory.getId());

        workflowStep = workflowStepRepo.getById(workflowStep.getId());

        Note note = noteRepo.create(workflowStep, TEST_NOTE_NAME, TEST_NOTE_TEXT);

        note.setOverrideable(false);

        organization = organizationRepo.getById(organization.getId());

        // actually set the note to non overrideable on parent first
        note = noteRepo.update(note, organization);

        assertFalse(note.getOverrideable(), "The note was not made non-overrideable!");

        organization = organizationRepo.getById(organization.getId());

        String updatedName = "Updated Name", updatedText = "Updated Text";

        note.setName(updatedName);
        note.setText(updatedText);

        note = noteRepo.update(note, organization);

        assertEquals(note.getName(), updatedName, "The overrideable note's name was not updated even by originating organization!");
        assertEquals(note.getText(), updatedText, "The overrideable note's text was not updated even by originating organization!");

    }

    @Test
    public void testInheritAndRemoveNotes() {

        // this test calls for adding a single workflowstep to the parent organization
        workflowStepRepo.delete(workflowStep);

        organization = organizationRepo.getById(organization.getId());

        Organization parentOrganization = organizationRepo.create(TEST_PARENT_ORGANIZATION_NAME, parentCategory);
        parentCategory = organizationCategoryRepo.getById(parentCategory.getId());

        parentOrganization.addChildOrganization(organization);
        parentOrganization = organizationRepo.save(parentOrganization);

        organization = organizationRepo.getById(organization.getId());

        Organization grandChildOrganization = organizationRepo.create(TEST_GRAND_CHILD_ORGANIZATION_NAME, organization, parentCategory);
        parentCategory = organizationCategoryRepo.getById(parentCategory.getId());

        grandChildOrganization = organizationRepo.getById(grandChildOrganization.getId());
        organization = organizationRepo.getById(organization.getId());

        parentOrganization = organizationRepo.getById(parentOrganization.getId());

        WorkflowStep workflowStep = workflowStepRepo.create(TEST_WORKFLOW_STEP_NAME, parentOrganization);

        parentOrganization = organizationRepo.getById(parentOrganization.getId());
        organization = organizationRepo.getById(organization.getId());
        grandChildOrganization = organizationRepo.getById(grandChildOrganization.getId());

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        assertEquals(0, parentOrganization.getOriginalWorkflowSteps().get(0).getOriginalNotes().size(), "parentOrganization's workflow step has the incorrect number of notes!");
        assertEquals(0, parentOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().size(), "parentOrganization's aggregate workflow step has the incorrect number of aggregate notes!");

        assertEquals(0, organization.getAggregateWorkflowSteps().get(0).getAggregateNotes().size(), "organization's aggregate workflow step has the incorrect number of aggregate notes!");

        assertEquals(0, grandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().size(), "grandChildOrganization's aggregate workflow step has the incorrect number of aggregate notes!");

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        String note1Name = TEST_NOTE_NAME + " 1", note2Name = TEST_NOTE_NAME + " 2", note3Name = TEST_NOTE_NAME + " 3";

        Note n1 = noteRepo.create(workflowStep, note1Name, TEST_NOTE_TEXT);
        workflowStep = workflowStepRepo.getById(workflowStep.getId());

        Note n2 = noteRepo.create(workflowStep, note2Name, TEST_NOTE_TEXT);
        workflowStep = workflowStepRepo.getById(workflowStep.getId());

        Note n3 = noteRepo.create(workflowStep, note3Name, TEST_NOTE_TEXT);
        workflowStep = workflowStepRepo.getById(workflowStep.getId());

        parentOrganization = organizationRepo.getById(parentOrganization.getId());
        organization = organizationRepo.getById(organization.getId());
        grandChildOrganization = organizationRepo.getById(grandChildOrganization.getId());

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        assertEquals(3, parentOrganization.getOriginalWorkflowSteps().get(0).getOriginalNotes().size(), "parentOrganization's workflow step has the incorrect number of notes!");
        assertTrue(parentOrganization.getOriginalWorkflowSteps().get(0).getOriginalNotes().contains(n1), "parentOrganization's workflow step's did not contain note 1!");
        assertTrue(parentOrganization.getOriginalWorkflowSteps().get(0).getOriginalNotes().contains(n2), "parentOrganization's workflow step's did not contain note 2!");
        assertTrue(parentOrganization.getOriginalWorkflowSteps().get(0).getOriginalNotes().contains(n3), "parentOrganization's workflow step's did not contain note 3!");

        assertEquals(3, parentOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().size(), "parentOrganization's aggregate workflow step has the incorrect number of aggregate notes!");
        assertTrue(parentOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().contains(n1), "parentOrganization's aggregate workflow step's did not contain note 1!");
        assertTrue(parentOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().contains(n2), "parentOrganization's aggregate workflow step's did not contain note 2!");
        assertTrue(parentOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().contains(n3), "parentOrganization's aggregate workflow step's did not contain note 3!");

        assertEquals(3, organization.getAggregateWorkflowSteps().get(0).getAggregateNotes().size(), "organization's aggregate workflow step has the incorrect number of aggregate notes!");
        assertTrue(organization.getAggregateWorkflowSteps().get(0).getAggregateNotes().contains(n1), "organization's aggregate workflow step's did not contain note 1!");
        assertTrue(organization.getAggregateWorkflowSteps().get(0).getAggregateNotes().contains(n2), "organization's aggregate workflow step's did not contain note 2!");
        assertTrue(organization.getAggregateWorkflowSteps().get(0).getAggregateNotes().contains(n3), "organization's aggregate workflow step's did not contain note 3!");

        assertEquals(3, grandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().size(), "grandChildOrganization's aggregate workflow step has the incorrect number of aggregate notes!");
        assertTrue(grandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().contains(n1), "grandChildOrganization's aggregate workflow step's did not contain note 1!");
        assertTrue(grandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().contains(n2), "grandChildOrganization's aggregate workflow step's did not contain note 2!");
        assertTrue(grandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().contains(n3), "grandChildOrganization's aggregate workflow step's did not contain note 3!");

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        Organization greatGrandChildOrganization = organizationRepo.create("TestGreatGrandchildOrganizationName", grandChildOrganization, parentCategory);
        parentCategory = organizationCategoryRepo.getById(parentCategory.getId());

        grandChildOrganization = organizationRepo.getById(grandChildOrganization.getId());

        Organization anotherGreatGrandChildOrganization = organizationRepo.create("AnotherTestGreatGrandchildOrganizationName", grandChildOrganization, parentCategory);
        parentCategory = organizationCategoryRepo.getById(parentCategory.getId());

        parentOrganization = organizationRepo.getById(parentOrganization.getId());
        organization = organizationRepo.getById(organization.getId());
        grandChildOrganization = organizationRepo.getById(grandChildOrganization.getId());
        greatGrandChildOrganization = organizationRepo.getById(greatGrandChildOrganization.getId());

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        assertEquals(3, greatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().size(), "greatGrandChildOrganization's aggregate workflow step has the incorrect number of aggregate notes!");
        assertTrue(greatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().contains(n1), "greatGrandChildOrganization's aggregate workflow step's did not contain note 1!");
        assertTrue(greatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().contains(n2), "greatGrandChildOrganization's aggregate workflow step's did not contain note 2!");
        assertTrue(greatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().contains(n3), "greatGrandChildOrganization's aggregate workflow step's did not contain note 3!");

        assertEquals(3, anotherGreatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().size(), "anotherGreatGrandChildOrganization's aggregate workflow step has the incorrect number of aggregate notes!");
        assertTrue(anotherGreatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().contains(n1), "anotherGreatGrandChildOrganization's aggregate workflow step's did not contain note 1!");
        assertTrue(anotherGreatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().contains(n2), "anotherGreatGrandChildOrganization's aggregate workflow step's did not contain note 2!");
        assertTrue(anotherGreatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().contains(n3), "anotherGreatGrandChildOrganization's aggregate workflow step's did not contain note 3!");

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        parentOrganization.getOriginalWorkflowSteps().get(0).removeOriginalNote(n1);
        parentOrganization = organizationRepo.save(parentOrganization);

        noteRepo.delete(n1);

        parentOrganization = organizationRepo.getById(parentOrganization.getId());
        organization = organizationRepo.getById(organization.getId());
        grandChildOrganization = organizationRepo.getById(grandChildOrganization.getId());
        greatGrandChildOrganization = organizationRepo.getById(greatGrandChildOrganization.getId());
        anotherGreatGrandChildOrganization = organizationRepo.getById(anotherGreatGrandChildOrganization.getId());

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        assertEquals(2, parentOrganization.getOriginalWorkflowSteps().get(0).getOriginalNotes().size(), "parentOrganization's workflow step has the incorrect number of notes!");
        assertFalse(parentOrganization.getOriginalWorkflowSteps().get(0).getOriginalNotes().contains(n1), "parentOrganization's workflow step's still contains note 1!");
        assertTrue(parentOrganization.getOriginalWorkflowSteps().get(0).getOriginalNotes().contains(n2), "parentOrganization's workflow step's did not contain note 2!");
        assertTrue(parentOrganization.getOriginalWorkflowSteps().get(0).getOriginalNotes().contains(n3), "parentOrganization's workflow step's did not contain note 3!");

        assertEquals(2, parentOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().size(), "parentOrganization's aggregate workflow step has the incorrect number of aggregate notes!");
        assertFalse(parentOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().contains(n1), "parentOrganization's aggregate workflow step's still contains note 1!");
        assertTrue(parentOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().contains(n2), "parentOrganization's aggregate workflow step's did not contain note 2!");
        assertTrue(parentOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().contains(n3), "parentOrganization's aggregate workflow step's did not contain note 3!");

        assertEquals(2, organization.getAggregateWorkflowSteps().get(0).getAggregateNotes().size(), "organization's aggregate workflow step has the incorrect number of aggregate notes!");
        assertFalse(organization.getAggregateWorkflowSteps().get(0).getAggregateNotes().contains(n1), "organization's aggregate workflow step's still contains note 1!");
        assertTrue(organization.getAggregateWorkflowSteps().get(0).getAggregateNotes().contains(n2), "organization's aggregate workflow step's did not contain note 2!");
        assertTrue(organization.getAggregateWorkflowSteps().get(0).getAggregateNotes().contains(n3), "organization's aggregate workflow step's did not contain note 3!");

        assertEquals(2, grandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().size(), "grandChildOrganization's aggregate workflow step has the incorrect number of aggregate notes!");
        assertFalse(grandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().contains(n1), "grandChildOrganization's aggregate workflow step's still contains note 1!");
        assertTrue(grandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().contains(n2), "grandChildOrganization's aggregate workflow step's did not contain note 2!");
        assertTrue(grandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().contains(n3), "grandChildOrganization's aggregate workflow step's did not contain note 3!");

        assertEquals(2, greatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().size(), "greatGrandChildOrganization's aggregate workflow step has the incorrect number of aggregate notes!");
        assertFalse(greatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().contains(n1), "greatGrandChildOrganization's aggregate workflow step's still contains note 1!");
        assertTrue(greatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().contains(n2), "greatGrandChildOrganization's aggregate workflow step's did not contain note 2!");
        assertTrue(greatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().contains(n3), "greatGrandChildOrganization's aggregate workflow step's did not contain note 3!");

        assertEquals(2, anotherGreatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().size(), "anotherGreatGrandChildOrganization's aggregate workflow step has the incorrect number of aggregate notes!");
        assertFalse(anotherGreatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().contains(n1), "anotherGreatGrandChildOrganization's aggregate workflow step's still contains note 1!");
        assertTrue(anotherGreatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().contains(n2), "anotherGreatGrandChildOrganization's aggregate workflow step's did not contain note 2!");
        assertTrue(anotherGreatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().contains(n3), "anotherGreatGrandChildOrganization's aggregate workflow step's did not contain note 3!");

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    }

    @Test
    public void testReorderAggregateNotes() throws WorkflowStepNonOverrideableException, ComponentNotPresentOnOrgException {

        // this test calls for adding a single workflowstep to the parent organization
        workflowStepRepo.delete(workflowStep);

        organization = organizationRepo.getById(organization.getId());

        Organization parentOrganization = organizationRepo.create(TEST_PARENT_ORGANIZATION_NAME, parentCategory);
        parentCategory = organizationCategoryRepo.getById(parentCategory.getId());

        parentOrganization.addChildOrganization(organization);
        parentOrganization = organizationRepo.save(parentOrganization);

        organization = organizationRepo.getById(organization.getId());

        Organization grandChildOrganization = organizationRepo.create(TEST_GRAND_CHILD_ORGANIZATION_NAME, organization, parentCategory);
        parentCategory = organizationCategoryRepo.getById(parentCategory.getId());

        grandChildOrganization = organizationRepo.getById(grandChildOrganization.getId());
        organization = organizationRepo.getById(organization.getId());

        Organization greatGrandChildOrganization = organizationRepo.create("TestGreatGrandchildOrganizationName", grandChildOrganization, parentCategory);
        parentCategory = organizationCategoryRepo.getById(parentCategory.getId());

        grandChildOrganization = organizationRepo.getById(grandChildOrganization.getId());

        Organization anotherGreatGrandChildOrganization = organizationRepo.create("AnotherTestGreatGrandchildOrganizationName", grandChildOrganization, parentCategory);
        parentCategory = organizationCategoryRepo.getById(parentCategory.getId());

        parentOrganization = organizationRepo.getById(parentOrganization.getId());

        WorkflowStep workflowStep = workflowStepRepo.create(TEST_WORKFLOW_STEP_NAME, parentOrganization);

        String note1Name = TEST_NOTE_NAME + " 1", note2Name = TEST_NOTE_NAME + " 2", note3Name = TEST_NOTE_NAME + " 3";

        Note n1 = noteRepo.create(workflowStep, note1Name, TEST_NOTE_TEXT);
        workflowStep = workflowStepRepo.getById(workflowStep.getId());

        Note n2 = noteRepo.create(workflowStep, note2Name, TEST_NOTE_TEXT);
        workflowStep = workflowStepRepo.getById(workflowStep.getId());

        Note n3 = noteRepo.create(workflowStep, note3Name, TEST_NOTE_TEXT);
        workflowStep = workflowStepRepo.getById(workflowStep.getId());

        Long n1Id = n1.getId();
        Long n2Id = n2.getId();
        Long n3Id = n3.getId();

        parentOrganization = organizationRepo.getById(parentOrganization.getId());
        organization = organizationRepo.getById(organization.getId());
        grandChildOrganization = organizationRepo.getById(grandChildOrganization.getId());
        greatGrandChildOrganization = organizationRepo.getById(greatGrandChildOrganization.getId());
        anotherGreatGrandChildOrganization = organizationRepo.getById(anotherGreatGrandChildOrganization.getId());

        assertEquals(n1, parentOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(0), "The parentOrganization's aggregate workflow step's first aggregate note was not as expected!");
        assertEquals(n2, parentOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(1), "The parentOrganization's aggregate workflow step's second aggregate note was not as expected!");
        assertEquals(n3, parentOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(2), "The parentOrganization's aggregate workflow step's third aggregate note was not as expected!");

        assertEquals(n1, organization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(0), "The organization's aggregate workflow step's first aggregate note was not as expected!");
        assertEquals(n2, organization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(1), "The organization's aggregate workflow step's second aggregate note was not as expected!");
        assertEquals(n3, organization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(2), "The organization's aggregate workflow step's third aggregate note was not as expected!");

        assertEquals(n1, grandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(0), "The grandChildOrganization's aggregate workflow step's first aggregate note was not as expected!");
        assertEquals(n2, grandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(1), "The grandChildOrganization's aggregate workflow step's second aggregate note was not as expected!");
        assertEquals(n3, grandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(2), "The grandChildOrganization's aggregate workflow step's third aggregate note was not as expected!");

        assertEquals(n1, greatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(0), "The greatGrandChildOrganization's aggregate workflow step's first aggregate note was not as expected!");
        assertEquals(n2, greatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(1), "The greatGrandChildOrganization's aggregate workflow step's second aggregate note was not as expected!");
        assertEquals(n3, greatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(2), "The greatGrandChildOrganization's aggregate workflow step's third aggregate note was not as expected!");

        assertEquals(n1, anotherGreatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(0), "The anotherGreatGrandChildOrganization's aggregate workflow step's first aggregate note was not as expected!");
        assertEquals(n2, anotherGreatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(1), "The anotherGreatGrandChildOrganization's aggregate workflow step's second aggregate note was not as expected!");
        assertEquals(n3, anotherGreatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(2), "The anotherGreatGrandChildOrganization's aggregate workflow step's third aggregate note was not as expected!");

        n1 = noteRepo.getById(n1Id);
        n2 = noteRepo.getById(n2Id);

        workflowStep = workflowStepRepo.swapNotes(parentOrganization, workflowStep, n1, n2);

        parentOrganization = organizationRepo.getById(parentOrganization.getId());
        organization = organizationRepo.getById(organization.getId());
        grandChildOrganization = organizationRepo.getById(grandChildOrganization.getId());
        greatGrandChildOrganization = organizationRepo.getById(greatGrandChildOrganization.getId());
        anotherGreatGrandChildOrganization = organizationRepo.getById(anotherGreatGrandChildOrganization.getId());

        assertEquals(n2, parentOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(0), "The parentOrganization's aggregate workflow step's first aggregate note was not as expected!");
        assertEquals(n1, parentOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(1), "The parentOrganization's aggregate workflow step's second aggregate note was not as expected!");
        assertEquals(n3, parentOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(2), "The parentOrganization's aggregate workflow step's third aggregate note was not as expected!");

        assertEquals(n2, organization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(0), "The organization's aggregate workflow step's first aggregate note was not as expected!");
        assertEquals(n1, organization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(1), "The organization's aggregate workflow step's second aggregate note was not as expected!");
        assertEquals(n3, organization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(2), "The organization's aggregate workflow step's third aggregate note was not as expected!");

        assertEquals(n2, grandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(0), "The grandChildOrganization's aggregate workflow step's first aggregate note was not as expected!");
        assertEquals(n1, grandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(1), "The grandChildOrganization's aggregate workflow step's second aggregate note was not as expected!");
        assertEquals(n3, grandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(2), "The grandChildOrganization's aggregate workflow step's third aggregate note was not as expected!");

        assertEquals(n2, greatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(0), "The greatGrandChildOrganization's aggregate workflow step's first aggregate note was not as expected!");
        assertEquals(n1, greatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(1), "The greatGrandChildOrganization's aggregate workflow step's second aggregate note was not as expected!");
        assertEquals(n3, greatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(2), "The greatGrandChildOrganization's aggregate workflow step's third aggregate note was not as expected!");

        assertEquals(n2, anotherGreatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(0), "The anotherGreatGrandChildOrganization's aggregate workflow step's first aggregate note was not as expected!");
        assertEquals(n1, anotherGreatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(1), "The anotherGreatGrandChildOrganization's aggregate workflow step's second aggregate note was not as expected!");
        assertEquals(n3, anotherGreatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(2), "The anotherGreatGrandChildOrganization's aggregate workflow step's third aggregate note was not as expected!");

        n2 = noteRepo.getById(n2Id);
        n3 = noteRepo.getById(n3Id);

        workflowStep = workflowStepRepo.swapNotes(parentOrganization, workflowStep, n2, n3);

        parentOrganization = organizationRepo.getById(parentOrganization.getId());
        organization = organizationRepo.getById(organization.getId());
        grandChildOrganization = organizationRepo.getById(grandChildOrganization.getId());
        greatGrandChildOrganization = organizationRepo.getById(greatGrandChildOrganization.getId());
        anotherGreatGrandChildOrganization = organizationRepo.getById(anotherGreatGrandChildOrganization.getId());

        assertEquals(n3, parentOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(0), "The parentOrganization's aggregate workflow step's first aggregate note was not as expected!");
        assertEquals(n1, parentOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(1), "The parentOrganization's aggregate workflow step's second aggregate note was not as expected!");
        assertEquals(n2, parentOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(2), "The parentOrganization's aggregate workflow step's third aggregate note was not as expected!");

        assertEquals(n3, organization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(0), "The organization's aggregate workflow step's first aggregate note was not as expected!");
        assertEquals(n1, organization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(1), "The organization's aggregate workflow step's second aggregate note was not as expected!");
        assertEquals(n2, organization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(2), "The organization's aggregate workflow step's third aggregate note was not as expected!");

        assertEquals(n3, grandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(0), "The grandChildOrganization's aggregate workflow step's first aggregate note was not as expected!");
        assertEquals(n1, grandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(1), "The grandChildOrganization's aggregate workflow step's second aggregate note was not as expected!");
        assertEquals(n2, grandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(2), "The grandChildOrganization's aggregate workflow step's third aggregate note was not as expected!");

        assertEquals(n3, greatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(0), "The greatGrandChildOrganization's aggregate workflow step's first aggregate note was not as expected!");
        assertEquals(n1, greatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(1), "The greatGrandChildOrganization's aggregate workflow step's second aggregate note was not as expected!");
        assertEquals(n2, greatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(2), "The greatGrandChildOrganization's aggregate workflow step's third aggregate note was not as expected!");

        assertEquals(n3, anotherGreatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(0), "The anotherGreatGrandChildOrganization's aggregate workflow step's first aggregate note was not as expected!");
        assertEquals(n1, anotherGreatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(1), "The anotherGreatGrandChildOrganization's aggregate workflow step's second aggregate note was not as expected!");
        assertEquals(n2, anotherGreatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(2), "The anotherGreatGrandChildOrganization's aggregate workflow step's third aggregate note was not as expected!");

        n1 = noteRepo.getById(n1Id);
        n3 = noteRepo.getById(n3Id);

        // creates a new workflow step
        workflowStepRepo.swapNotes(organization, workflowStep, n1, n3);

        parentOrganization = organizationRepo.getById(parentOrganization.getId());
        organization = organizationRepo.getById(organization.getId());
        grandChildOrganization = organizationRepo.getById(grandChildOrganization.getId());
        greatGrandChildOrganization = organizationRepo.getById(greatGrandChildOrganization.getId());
        anotherGreatGrandChildOrganization = organizationRepo.getById(anotherGreatGrandChildOrganization.getId());

        WorkflowStep newWorkflowStep = organization.getOriginalWorkflowSteps().get(0);

        assertEquals(n3, parentOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(0), "The parentOrganization's aggregate workflow step's first aggregate note was not as expected!");
        assertEquals(n1, parentOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(1), "The parentOrganization's aggregate workflow step's second aggregate note was not as expected!");
        assertEquals(n2, parentOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(2), "The parentOrganization's aggregate workflow step's third aggregate note was not as expected!");

        // make sure new workflow step contains all notes
        assertTrue(organization.getOriginalWorkflowSteps().get(0).getAggregateNotes().contains(n1), "The organization's original workflow step's contains first note!");
        assertTrue(organization.getOriginalWorkflowSteps().get(0).getAggregateNotes().contains(n2), "The organization's original workflow step's contains second note!!");
        assertTrue(organization.getOriginalWorkflowSteps().get(0).getAggregateNotes().contains(n3), "The organization's original workflow step's contains third note!!");

        assertEquals(newWorkflowStep, organization.getAggregateWorkflowSteps().get(0), "The organization aggregate workflow steps does not have new workflow step from reorder on non originating organization!");

        assertEquals(n1, organization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(0), "The organization's aggregate workflow step's first aggregate note was not as expected!");
        assertEquals(n3, organization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(1), "The organization's aggregate workflow step's second aggregate note was not as expected!");
        assertEquals(n2, organization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(2), "The organization's aggregate workflow step's third aggregate note was not as expected!");

        assertEquals(newWorkflowStep, grandChildOrganization.getAggregateWorkflowSteps().get(0), "The grandChildOrganization did not inherit new workflow step from reorder on non originating organization!");

        assertEquals(n1, grandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(0), "The grandChildOrganization's aggregate workflow step's first aggregate note was not as expected!");
        assertEquals(n3, grandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(1), "The grandChildOrganization's aggregate workflow step's second aggregate note was not as expected!");
        assertEquals(n2, grandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(2), "The grandChildOrganization's aggregate workflow step's third aggregate note was not as expected!");

        assertEquals(newWorkflowStep, greatGrandChildOrganization.getAggregateWorkflowSteps().get(0), "The greatGrandChildOrganization did not inherit new workflow step from reorder on non originating organization!");

        assertEquals(n1, greatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(0), "The greatGrandChildOrganization's aggregate workflow step's first aggregate note was not as expected!");
        assertEquals(n3, greatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(1), "The greatGrandChildOrganization's aggregate workflow step's second aggregate note was not as expected!");
        assertEquals(n2, greatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(2), "The greatGrandChildOrganization's aggregate workflow step's third aggregate note was not as expected!");

        assertEquals(newWorkflowStep, anotherGreatGrandChildOrganization.getAggregateWorkflowSteps().get(0), "The anotherGreatGrandChildOrganization did not inherit new workflow step from reorder on non originating organization!");

        assertEquals(n1, anotherGreatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(0), "The anotherGreatGrandChildOrganization's aggregate workflow step's first aggregate note was not as expected!");
        assertEquals(n3, anotherGreatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(1), "The anotherGreatGrandChildOrganization's aggregate workflow step's second aggregate note was not as expected!");
        assertEquals(n2, anotherGreatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(2), "The anotherGreatGrandChildOrganization's aggregate workflow step's third aggregate note was not as expected!");

    }

    @Test
    public void testNoteChangeAtChildOrg() throws HeritableModelNonOverrideableException, WorkflowStepNonOverrideableException, ComponentNotPresentOnOrgException {

        // this test calls for adding a single workflowstep to the parent organization
        workflowStepRepo.delete(workflowStep);

        organization = organizationRepo.getById(organization.getId());

        Organization parentOrganization = organizationRepo.create(TEST_PARENT_ORGANIZATION_NAME, parentCategory);
        parentCategory = organizationCategoryRepo.getById(parentCategory.getId());

        parentOrganization.addChildOrganization(organization);
        parentOrganization = organizationRepo.save(parentOrganization);

        organization = organizationRepo.getById(organization.getId());

        Organization grandChildOrganization = organizationRepo.create(TEST_GRAND_CHILD_ORGANIZATION_NAME, parentCategory);
        parentCategory = organizationCategoryRepo.getById(parentCategory.getId());

        grandChildOrganization = organizationRepo.getById(grandChildOrganization.getId());
        organization = organizationRepo.getById(organization.getId());

        organization.addChildOrganization(grandChildOrganization);
        organization = organizationRepo.save(organization);

        Organization greatGrandChildOrganization = organizationRepo.create("TestGreatGrandchildOrganizationName", parentCategory);
        parentCategory = organizationCategoryRepo.getById(parentCategory.getId());

        greatGrandChildOrganization = organizationRepo.getById(greatGrandChildOrganization.getId());
        grandChildOrganization = organizationRepo.getById(grandChildOrganization.getId());

        grandChildOrganization.addChildOrganization(greatGrandChildOrganization);
        grandChildOrganization = organizationRepo.save(grandChildOrganization);

        Organization anotherGreatGrandChildOrganization = organizationRepo.create("AnotherTestGreatGrandchildOrganizationName", parentCategory);
        parentCategory = organizationCategoryRepo.getById(parentCategory.getId());

        anotherGreatGrandChildOrganization = organizationRepo.getById(anotherGreatGrandChildOrganization.getId());
        grandChildOrganization = organizationRepo.getById(grandChildOrganization.getId());

        grandChildOrganization.addChildOrganization(anotherGreatGrandChildOrganization);
        grandChildOrganization = organizationRepo.save(grandChildOrganization);

        parentOrganization = organizationRepo.getById(parentOrganization.getId());

        WorkflowStep workflowStep = workflowStepRepo.create(TEST_WORKFLOW_STEP_NAME, parentOrganization);

        Note note = noteRepo.create(workflowStep, TEST_NOTE_NAME, TEST_NOTE_TEXT);

        workflowStep = workflowStepRepo.getById(workflowStep.getId());

        Long originalNoteId = note.getId();

        parentOrganization = organizationRepo.getById(parentOrganization.getId());
        organization = organizationRepo.getById(organization.getId());
        grandChildOrganization = organizationRepo.getById(grandChildOrganization.getId());
        greatGrandChildOrganization = organizationRepo.getById(greatGrandChildOrganization.getId());
        anotherGreatGrandChildOrganization = organizationRepo.getById(anotherGreatGrandChildOrganization.getId());

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        assertEquals(1, parentOrganization.getOriginalWorkflowSteps().size(), "Parent organization has the incorrect number of workflow steps!");
        assertEquals(1, parentOrganization.getAggregateWorkflowSteps().size(), "Parent organization has wrong size of workflow!");

        assertEquals(0, organization.getOriginalWorkflowSteps().size(), "organization has the incorrect number of workflow steps!");
        assertEquals(1, organization.getAggregateWorkflowSteps().size(), "organization has wrong size of workflow!");

        assertEquals(0, grandChildOrganization.getOriginalWorkflowSteps().size(), "Grand child organization has the incorrect number of workflow steps!");
        assertEquals(1, grandChildOrganization.getAggregateWorkflowSteps().size(), "Grand child organization has wrong size of workflow!");

        assertEquals(0, greatGrandChildOrganization.getOriginalWorkflowSteps().size(), "Great grand child organization has the incorrect number of workflow steps!");
        assertEquals(1, greatGrandChildOrganization.getAggregateWorkflowSteps().size(), "Great grand child organization has wrong size of workflow!");

        assertEquals(0, anotherGreatGrandChildOrganization.getOriginalWorkflowSteps().size(), "Another great grand child organization has the incorrect number of workflow steps!");
        assertEquals(1, anotherGreatGrandChildOrganization.getAggregateWorkflowSteps().size(), "Another great grand child organization has wrong size of workflow!");

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        note.setName("Updated Name");

        // request the change at the level of the child organization
        Note updatedNote = noteRepo.update(note, organization);

        parentOrganization = organizationRepo.getById(parentOrganization.getId());
        organization = organizationRepo.getById(organization.getId());
        grandChildOrganization = organizationRepo.getById(grandChildOrganization.getId());
        greatGrandChildOrganization = organizationRepo.getById(greatGrandChildOrganization.getId());
        anotherGreatGrandChildOrganization = organizationRepo.getById(anotherGreatGrandChildOrganization.getId());

        // pointer to note became updatedNote, must fetch it agains
        note = noteRepo.getById(originalNoteId);

        // There should be a new workflow step on the child organization that is distinct from the original workflowStep
        WorkflowStep updatedWorkflowStep = organization.getAggregateWorkflowSteps().get(0);
        assertFalse(workflowStep.getId().equals(updatedWorkflowStep.getId()), "The updatedWorkflowStep was just the same as the original from which it was derived when its note was updated!");

        // The new workflow step should contain the new updatedNote
        assertTrue(updatedWorkflowStep.getAggregateNotes().contains(updatedNote), "The updatedWorkflowStep didn't contain the new updatedNote");

        // The updatedNote should be distinct from the original note
        assertFalse(note.getId().equals(updatedNote.getId()), "The updatedNote was just the same as the original from which it was derived!");

        // the grandchild and great grandchildren should all be using the new workflow step and the updatedNote
        assertTrue(grandChildOrganization.getAggregateWorkflowSteps().contains(updatedWorkflowStep), "The grandchild org didn't have the updatedWorkflowStep!");
        assertTrue(grandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().contains(updatedNote), "The grandchild org didn't have the updatedNote on the updatedWorkflowStep!");
        assertTrue(greatGrandChildOrganization.getAggregateWorkflowSteps().contains(updatedWorkflowStep), "The great grandchild org didn't have the updatedWorkflowStep!");
        assertTrue(greatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().contains(updatedNote), "The great grandchild org didn't have the updatedNote on the updatedWorkflowStep!");
        assertTrue(anotherGreatGrandChildOrganization.getAggregateWorkflowSteps().contains(updatedWorkflowStep), "Another great grandchild org didn't have the updatedWorkflowStep!");
        assertTrue(anotherGreatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().contains(updatedNote), "Another great grandchild org didn't have the updatedNote on the updatedWorkflowStep!");
    }

    @Test
    public void testMaintainNoteOrderWhenOverriding() throws HeritableModelNonOverrideableException, WorkflowStepNonOverrideableException, ComponentNotPresentOnOrgException {

        // this test calls for adding a single workflowstep to the parent organization
        workflowStepRepo.delete(workflowStep);

        organization = organizationRepo.getById(organization.getId());

        Organization parentOrganization = organizationRepo.create(TEST_PARENT_ORGANIZATION_NAME, parentCategory);
        parentCategory = organizationCategoryRepo.getById(parentCategory.getId());

        parentOrganization.addChildOrganization(organization);
        parentOrganization = organizationRepo.save(parentOrganization);

        organization = organizationRepo.getById(organization.getId());

        Organization grandChildOrganization = organizationRepo.create(TEST_GRAND_CHILD_ORGANIZATION_NAME, organization, parentCategory);
        parentCategory = organizationCategoryRepo.getById(parentCategory.getId());

        grandChildOrganization = organizationRepo.getById(grandChildOrganization.getId());
        organization = organizationRepo.getById(organization.getId());

        Organization greatGrandChildOrganization = organizationRepo.create("TestGreatGrandchildOrganizationName", grandChildOrganization, parentCategory);
        parentCategory = organizationCategoryRepo.getById(parentCategory.getId());

        greatGrandChildOrganization = organizationRepo.getById(greatGrandChildOrganization.getId());
        grandChildOrganization = organizationRepo.getById(grandChildOrganization.getId());

        Organization anotherGreatGrandChildOrganization = organizationRepo.create("AnotherTestGreatGrandchildOrganizationName", grandChildOrganization, parentCategory);
        parentCategory = organizationCategoryRepo.getById(parentCategory.getId());

        anotherGreatGrandChildOrganization = organizationRepo.getById(anotherGreatGrandChildOrganization.getId());
        grandChildOrganization = organizationRepo.getById(grandChildOrganization.getId());

        parentOrganization = organizationRepo.getById(parentOrganization.getId());

        WorkflowStep workflowStep = workflowStepRepo.create(TEST_WORKFLOW_STEP_NAME, parentOrganization);

        String note1Name = TEST_NOTE_NAME + " 1", note2Name = TEST_NOTE_NAME + " 2", note3Name = TEST_NOTE_NAME + " 3";

        /* Note n1 = */ noteRepo.create(workflowStep, note1Name, TEST_NOTE_TEXT);
        workflowStep = workflowStepRepo.getById(workflowStep.getId());

        Note n2 = noteRepo.create(workflowStep, note2Name, TEST_NOTE_TEXT);
        workflowStep = workflowStepRepo.getById(workflowStep.getId());

        /* Note n3 = */ noteRepo.create(workflowStep, note3Name, TEST_NOTE_TEXT);
        workflowStep = workflowStepRepo.getById(workflowStep.getId());

        n2.setName("Updated Name!");

        grandChildOrganization = organizationRepo.getById(grandChildOrganization.getId());

        Note n2Updated = noteRepo.update(n2, grandChildOrganization);

        grandChildOrganization = organizationRepo.getById(grandChildOrganization.getId());

        WorkflowStep newWSWithNewNoteViaOriginals = grandChildOrganization.getOriginalWorkflowSteps().get(0);
        WorkflowStep newWSWithNewNoteViaAggregation = grandChildOrganization.getAggregateWorkflowSteps().get(0);

        assertEquals(newWSWithNewNoteViaOriginals, newWSWithNewNoteViaAggregation, "The new aggregated workflow step on the grandchild org was not the one the grandchild org just originated!  Original had id " + newWSWithNewNoteViaOriginals.getId() + " while aggregate had id " + newWSWithNewNoteViaAggregation.getId());

        assertEquals(n2Updated.getId(), grandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().get(1).getId(), "Updated note was in the wrong order!");

    }

    /**
     * Test that when a note is overridden at a child org, the override is removed and the original is reattached when the note is made non-overrideable at a parent org.
     *
     * @throws WorkflowStepNonOverrideableException
     * @throws HeritableModelNonOverrideableException
     * @throws ComponentNotPresentOnOrgException
     */
    @Test
    public void testReInheritOverriddenNote() throws WorkflowStepNonOverrideableException, HeritableModelNonOverrideableException, ComponentNotPresentOnOrgException {
        // this test calls for adding a single workflowstep to the parent organization, so get rid of the one at the middle org
        workflowStepRepo.delete(workflowStep);

        organization = organizationRepo.getById(organization.getId());

        Organization parentOrganization = organizationRepo.create(TEST_PARENT_ORGANIZATION_NAME, parentCategory);
        parentCategory = organizationCategoryRepo.getById(parentCategory.getId());

        parentOrganization.addChildOrganization(organization);
        parentOrganization = organizationRepo.save(parentOrganization);

        organization = organizationRepo.getById(organization.getId());

        Organization grandChildOrganization = organizationRepo.create(TEST_GRAND_CHILD_ORGANIZATION_NAME, organization, parentCategory);
        parentCategory = organizationCategoryRepo.getById(parentCategory.getId());

        grandChildOrganization = organizationRepo.getById(grandChildOrganization.getId());
        organization = organizationRepo.getById(organization.getId());

        parentOrganization = organizationRepo.getById(parentOrganization.getId());

        WorkflowStep workflowStep = workflowStepRepo.create(TEST_WORKFLOW_STEP_NAME, parentOrganization);

        parentOrganization = organizationRepo.getById(parentOrganization.getId());
        organization = organizationRepo.getById(organization.getId());
        grandChildOrganization = organizationRepo.getById(grandChildOrganization.getId());

        // now we have an org hierarchy with a parent, child, and grandchild
        /////////////////////////////////////////////////////////////////////////
        String note1Name = TEST_NOTE_NAME + " 1", note2Name = TEST_NOTE_NAME + " 2", note3Name = TEST_NOTE_NAME + " 3";

        /* Note n1 = */ noteRepo.create(workflowStep, note1Name, TEST_NOTE_TEXT);
        workflowStep = workflowStepRepo.getById(workflowStep.getId());

        Note n2 = noteRepo.create(workflowStep, note2Name, TEST_NOTE_TEXT);
        workflowStep = workflowStepRepo.getById(workflowStep.getId());

        /* Note n3 = */ noteRepo.create(workflowStep, note3Name, TEST_NOTE_TEXT);
        workflowStep = workflowStepRepo.getById(workflowStep.getId());

        grandChildOrganization = organizationRepo.getById(grandChildOrganization.getId());

        // now we have three notes originating at the topmost org
        //////////////////////////////////////////////////////////////////////

        // make a note non-overrideable at the grandchild org
        long n2Id = n2.getId();

        n2.setOverrideable(false);

        Note n2updatedAtGrandchild = noteRepo.update(n2, grandChildOrganization);

        grandChildOrganization = organizationRepo.getById(grandChildOrganization.getId());

        n2 = noteRepo.getById(n2Id);

        // ensure that a new note got made at the grandchild after this override (to non-overrideable :) )
        // old n2 should still be overrideable
        assertTrue(n2.getOverrideable(), "Note updated at grandchild changed the note at the parent!");

        // old n2 should be different from the new n2 updated at the grandchild
        assertFalse(n2.getId().equals(n2updatedAtGrandchild.getId()), "Note updated at grandchild didn't get duplicated!");
        assertEquals(1, grandChildOrganization.getAggregateWorkflowSteps().get(0).getOriginalNotes().size(), "A new Note didn't get originated at an org that overrode it!");
        assertTrue(grandChildOrganization.getAggregateWorkflowSteps().get(0).getOriginalNotes().contains(n2updatedAtGrandchild), "A new Note didn't get originated at an org that overrode it!");

        organization = organizationRepo.getById(organization.getId());

        // TODO: make the note non-overrideable at the child org
        n2.setOverrideable(false);
        Note n2updatedAtChild = noteRepo.update(n2, organization);

        n2 = noteRepo.getById(n2Id);

        // ensure that a new note got made at the child after this override (to non-overrideable :) )
        // old n2 should still be overrideable
        assertTrue(n2.getOverrideable(), "Note updated at child didn't get duplicated!");
        // old n2 should be different from the new n2 updated at the grandchild
        assertFalse(n2.getId().equals(n2updatedAtChild.getId()), "Note updated at child didn't get duplicated!");

        grandChildOrganization = organizationRepo.getById(grandChildOrganization.getId());

        assertFalse(grandChildOrganization.getAggregateWorkflowSteps().get(0).getOriginalNotes().contains(n2updatedAtChild), "Grand child inherited original note from child organization");

        // ensure that the grandchild's new note is replaced by the child's non-overrideable one
        assertTrue(grandChildOrganization.getAggregateWorkflowSteps().get(0).getOriginalNotes().isEmpty(), "Note made non-overrideable didn't blow away an inferior override at descendant org!");
        assertFalse(grandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateNotes().contains(n2updatedAtGrandchild), "Note made non-overrideable still stuck around at the grandchild who'd overridden it!");

        // TODO: make the note non-overrideable at the parent org
        // TOOO: ensure that the child's note is replaced by the original, non-overrideable at both the child and grandchild

        // now all is restored, except one of the notes is non-overrideable at the parent

        // TODO: remove a note at the grandchild
        // TODO: make the removed note non-overrideable at the parent
        // TODO: ensure that the removed note is reaggregated upon the grandchild who removed it

    }

    // TODO: this test is not done, development of the full feature (indefinitely) deferred for now
    @Test
    public void testDeleteNoteAtDescendantOrganizationAndDuplicateWorkflowStepIsDeletedToo() throws HeritableModelNonOverrideableException, WorkflowStepNonOverrideableException, ComponentNotPresentOnOrgException {
        // this test calls for adding a single workflowstep to the parent organization
        workflowStepRepo.delete(workflowStep);

        organization = organizationRepo.getById(organization.getId());

        Organization parentOrganization = organizationRepo.create(TEST_PARENT_ORGANIZATION_NAME, parentCategory);
        parentCategory = organizationCategoryRepo.getById(parentCategory.getId());

        parentOrganization.addChildOrganization(organization);
        parentOrganization = organizationRepo.save(parentOrganization);

        organization = organizationRepo.getById(organization.getId());

        Organization grandChildOrganization = organizationRepo.create(TEST_GRAND_CHILD_ORGANIZATION_NAME, organization, parentCategory);
        parentCategory = organizationCategoryRepo.getById(parentCategory.getId());

        grandChildOrganization = organizationRepo.getById(grandChildOrganization.getId());
        organization = organizationRepo.getById(organization.getId());

        Organization greatGrandChildOrganization = organizationRepo.create("TestGreatGrandchildOrganizationName", grandChildOrganization, parentCategory);
        parentCategory = organizationCategoryRepo.getById(parentCategory.getId());

        greatGrandChildOrganization = organizationRepo.getById(greatGrandChildOrganization.getId());
        grandChildOrganization = organizationRepo.getById(grandChildOrganization.getId());

        Organization anotherGreatGrandChildOrganization = organizationRepo.create("AnotherTestGreatGrandchildOrganizationName", grandChildOrganization, parentCategory);
        parentCategory = organizationCategoryRepo.getById(parentCategory.getId());

        anotherGreatGrandChildOrganization = organizationRepo.getById(anotherGreatGrandChildOrganization.getId());
        grandChildOrganization = organizationRepo.getById(grandChildOrganization.getId());

        parentOrganization = organizationRepo.getById(parentOrganization.getId());

        WorkflowStep workflowStep = workflowStepRepo.create(TEST_WORKFLOW_STEP_NAME, parentOrganization);

        String note1Name = TEST_NOTE_NAME + " 1", note2Name = TEST_NOTE_NAME + " 2", note3Name = TEST_NOTE_NAME + " 3";

        Note n1 = noteRepo.create(workflowStep, note1Name, TEST_NOTE_TEXT);
        workflowStep = workflowStepRepo.getById(workflowStep.getId());

        /* Note n2 = */ noteRepo.create(workflowStep, note2Name, TEST_NOTE_TEXT);
        workflowStep = workflowStepRepo.getById(workflowStep.getId());

        /* Note n3 = */ noteRepo.create(workflowStep, note3Name, TEST_NOTE_TEXT);
        workflowStep = workflowStepRepo.getById(workflowStep.getId());

        organization = organizationRepo.getById(organization.getId());

        // override the note at the child
        n1.setName("Updated Name!");
        /* Note nPrime = */ noteRepo.update(n1, organization);

        // TODO: make note non-overrideable, check that fpPrime goes away and the new derivative step goes away

    }

    @AfterEach
    public void cleanUp() {
        noteRepo.findAll().forEach(note -> {
            noteRepo.delete(note);
        });

        workflowStepRepo.findAll().forEach(workflowStep -> {
            workflowStepRepo.delete(workflowStep);
        });

        organizationRepo.deleteAll();

        organizationCategoryRepo.deleteAll();
    }

}
