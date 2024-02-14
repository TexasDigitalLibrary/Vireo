package org.tdl.vireo.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.ActiveProfiles;
import org.tdl.vireo.model.DocumentType;
import org.tdl.vireo.model.FieldPredicate;
import org.tdl.vireo.model.repo.DocumentTypeRepo;

@ActiveProfiles(value = { "test", "isolated-test" })
public class DocumentTypeControllerTest extends AbstractControllerTest {

    @Mock
    private DocumentTypeRepo documentTypeRepo;

    @InjectMocks
    private DocumentTypeController documentTypeController;

    private DocumentType documentType1;
    private DocumentType documentType2;

    private FieldPredicate fieldPredicate1;
    private FieldPredicate fieldPredicate2;

    private static List<DocumentType> documentTypes;

    @BeforeEach
    public void setup() {
        fieldPredicate1 = new FieldPredicate("FieldPredicate 1", true);
        fieldPredicate2 = new FieldPredicate("FieldPredicate 2", false);
        documentType1 = new DocumentType("DocumentType 1", fieldPredicate1);
        documentType2 = new DocumentType("DocumentType 2", fieldPredicate2);

        fieldPredicate1.setId(1L);
        fieldPredicate2.setId(2L);
        documentType1.setId(1L);
        documentType2.setId(2L);

        documentType1.setPosition(1L);
        documentType2.setPosition(2L);

        documentTypes = new ArrayList<DocumentType>(Arrays.asList(new DocumentType[] { documentType1 }));
    }

    @Test
    public void testAllDocumentTypes() {
        when(documentTypeRepo.findAllByOrderByPositionAsc()).thenReturn(documentTypes);

        ApiResponse response = documentTypeController.allDocumentTypes();
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        List<?> list = (ArrayList<?>) response.getPayload().get("ArrayList<DocumentType>");
        assertEquals(documentTypes.size(), list.size());
    }

    @Test
    public void testCreateDocumentType() {
        when(documentTypeRepo.create(any(String.class))).thenReturn(documentType2);

        ApiResponse response = documentTypeController.createDocumentType(documentType1);
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        DocumentType documentType = (DocumentType) response.getPayload().get("DocumentType");
        assertEquals(documentType2.getId(), documentType.getId());
    }

    @Test
    public void testUpdateDocumentType() {
        when(documentTypeRepo.update(any(DocumentType.class))).thenReturn(documentType2);

        ApiResponse response = documentTypeController.updateDocumentType(documentType1);
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        DocumentType documentType = (DocumentType) response.getPayload().get("DocumentType");
        assertEquals(documentType2.getId(), documentType.getId());
    }

    @Test
    public void testRemoveDocumentType() {
        doNothing().when(documentTypeRepo).remove(any(DocumentType.class));

        ApiResponse response = documentTypeController.removeDocumentType(documentType2);
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        verify(documentTypeRepo, times(1)).remove(any(DocumentType.class));
    }

    @Test
    public void testRemoveDocumentTypeIsDenied() {
        ApiResponse response = documentTypeController.removeDocumentType(documentType1);
        assertEquals(ApiStatus.INVALID, response.getMeta().getStatus());
    }

    @Test
    public void testReorderDocumentTypes() {
        doNothing().when(documentTypeRepo).reorder(any(Long.class), any(Long.class));

        ApiResponse response = documentTypeController.reorderDocumentTypes(1L, 2L);
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        verify(documentTypeRepo, times(1)).reorder(any(Long.class), any(Long.class));
    }

    @Test
    public void testSortDocumentTypes() {
        doNothing().when(documentTypeRepo).sort(anyString());

        ApiResponse response = documentTypeController.sortDocumentTypes("column");
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());
    }

}
