package org.tdl.vireo.model;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.repo.NoteRepo;

public class NoteTest extends AbstractEntityTest {

    @Autowired
    private NoteRepo noteRepo;

    @Override
    public void testCreate() {
        Note note = noteRepo.create(TEST_NOTE_NAME, TEST_NOTE_TEXT);
        assertEquals("The entity was not created!", 1, noteRepo.count());
        assertEquals("The entity did not have the correct name!", TEST_NOTE_NAME, note.getName());
        assertEquals("The entity did not have the correct text!", TEST_NOTE_TEXT, note.getText());
    }

    @Override
    public void testDuplication() {
        
    }

    @Override
    public void testDelete() {
        Note note = noteRepo.create(TEST_NOTE_NAME, TEST_NOTE_TEXT);
        noteRepo.delete(note);
        assertEquals("The entity was not deleted!", 0, noteRepo.count());
    }

    @Override
    public void testCascade() {

    }

    @After
    public void cleanUp() {
        noteRepo.deleteAll();
    }

}
