package org.tdl.vireo.model;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;

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
        
    }

    @Override
    public void testDelete() {
        Note note = noteRepo.create(workflowStep, TEST_NOTE_NAME, TEST_NOTE_TEXT);
        noteRepo.delete(note);
        assertEquals("The entity was not deleted!", 0, noteRepo.count());
    }

    @Override
    public void testCascade() {

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
