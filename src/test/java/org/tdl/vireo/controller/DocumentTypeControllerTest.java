package org.tdl.vireo.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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
import org.tdl.vireo.model.DocumentType;
import org.tdl.vireo.model.FieldPredicate;
import org.tdl.vireo.model.repo.DocumentTypeRepo;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class DocumentTypeControllerTest extends AbstractControllerTest {

    @Mock
    private DocumentTypeRepo documentTypeRepo;

    @InjectMocks
    private DocumentTypeController documentTypeController;

    private DocumentType mockDocumentType1;
    private DocumentType mockDocumentType2;

    private FieldPredicate mockFieldPredicate1;
    private FieldPredicate mockFieldPredicate2;

    private static List<DocumentType> mockDocumentTypes;

    @BeforeEach
    public void setup() {
        mockFieldPredicate1 = new FieldPredicate("FieldPredicate 1", true);
        mockFieldPredicate1.setId(1L);

        mockFieldPredicate2 = new FieldPredicate("FieldPredicate 2", false);
        mockFieldPredicate2.setId(2L);

        mockDocumentType1 = new DocumentType("DocumentType 1", mockFieldPredicate1);
        mockDocumentType1.setId(1L);
        mockDocumentType1.setPosition(1L);

        mockDocumentType2 = new DocumentType("DocumentType 2", mockFieldPredicate2);
        mockDocumentType2.setId(2L);
        mockDocumentType2.setPosition(2L);

        mockDocumentTypes = new ArrayList<DocumentType>(Arrays.asList(new DocumentType[] { mockDocumentType1 }));
    }

    @Test
    public void testAllDocumentTypes() {
        when(documentTypeRepo.findAllByOrderByPositionAsc()).thenReturn(mockDocumentTypes);

        ApiResponse response = documentTypeController.allDocumentTypes();
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        List<?> list = (ArrayList<?>) response.getPayload().get("ArrayList<DocumentType>");
        assertEquals(mockDocumentTypes.size(), list.size());
    }

    @Test
    public void testCreateDocumentType() {
        when(documentTypeRepo.create(any(String.class))).thenReturn(mockDocumentType2);

        ApiResponse response = documentTypeController.createDocumentType(mockDocumentType1);
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        DocumentType documentType = (DocumentType) response.getPayload().get("DocumentType");
        assertEquals(mockDocumentType2.getId(), documentType.getId());
    }

    @Test
    public void testUpdateDocumentType() {
        when(documentTypeRepo.update(any(DocumentType.class))).thenReturn(mockDocumentType2);

        ApiResponse response = documentTypeController.updateDocumentType(mockDocumentType1);
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        DocumentType documentType = (DocumentType) response.getPayload().get("DocumentType");
        assertEquals(mockDocumentType2.getId(), documentType.getId());
    }

    @Test
    public void testRemoveDocumentType() {
        doNothing().when(documentTypeRepo).remove(any(DocumentType.class));

        ApiResponse response = documentTypeController.removeDocumentType(mockDocumentType2);
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        verify(documentTypeRepo, times(1)).remove(any(DocumentType.class));
    }

    @Test
    public void testRemoveDocumentTypeIsDenied() {
        ApiResponse response = documentTypeController.removeDocumentType(mockDocumentType1);
        assertEquals(ApiStatus.INVALID, response.getMeta().getStatus());
    }

    @Test
    public void testReorderDocumentTypes() {
        doNothing().when(documentTypeRepo).reorder(any(Long.class), any(Long.class));

        ApiResponse response = documentTypeController.reorderDocumentTypes(1L, 2L);
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        verify(documentTypeRepo, times(1)).reorder(any(Long.class), any(Long.class));
    }

}
