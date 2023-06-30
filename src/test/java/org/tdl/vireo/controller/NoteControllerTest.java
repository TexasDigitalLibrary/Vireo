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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.tdl.vireo.model.Note;
import org.tdl.vireo.model.repo.NoteRepo;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class NoteControllerTest extends AbstractControllerTest {

    @Mock
    private NoteRepo noteRepo;

    @InjectMocks
    private NoteController noteController;

    private Note mockNote1;

    private static List<Note> mockNotes;

    @BeforeEach
    public void setup() {
        mockNote1 = new Note("Note 1", "text 1");
        mockNote1.setId(1L);

        mockNotes = new ArrayList<Note>(Arrays.asList(new Note[] { mockNote1 }));
    }

    @Test
    public void testAllNotes() {
        when(noteRepo.findAll()).thenReturn(mockNotes);

        ApiResponse response = noteController.getAllNotes();
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        List<?> list = (ArrayList<?>) response.getPayload().get("ArrayList<Note>");
        assertEquals(mockNotes.size(), list.size());
    }

}
