package org.tdl.vireo.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import edu.tamu.weaver.response.ApiResponse;
import edu.tamu.weaver.response.ApiStatus;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.ActiveProfiles;
import org.tdl.vireo.model.Note;
import org.tdl.vireo.model.repo.NoteRepo;

@ActiveProfiles(value = { "test", "isolated-test" })
public class NoteControllerTest extends AbstractControllerTest {

    @Mock
    private NoteRepo noteRepo;

    @InjectMocks
    private NoteController noteController;

    private Note note1;

    private static List<Note> notes;

    @BeforeEach
    public void setup() {
        note1 = new Note("Note 1", "text 1");
        note1.setId(1L);

        notes = new ArrayList<Note>(Arrays.asList(new Note[] { note1 }));
    }

    @Test
    public void testAllNotes() {
        when(noteRepo.findAll()).thenReturn(notes);

        ApiResponse response = noteController.getAllNotes();
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        List<?> list = (ArrayList<?>) response.getPayload().get("ArrayList<Note>");
        assertEquals(notes.size(), list.size());
    }

}
