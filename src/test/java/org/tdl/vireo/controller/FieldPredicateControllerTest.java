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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.ActiveProfiles;
import org.tdl.vireo.model.FieldPredicate;
import org.tdl.vireo.model.repo.FieldPredicateRepo;

@ActiveProfiles(value = { "test", "isolated-test" })
public class FieldPredicateControllerTest extends AbstractControllerTest {

    @Mock
    private FieldPredicateRepo fieldPredicateRepo;

    @InjectMocks
    private FieldPredicateController fieldPredicateController;

    private FieldPredicate mockFieldPredicate1;
    private FieldPredicate mockFieldPredicate2;

    private static List<FieldPredicate> mockFieldPredicates;

    @BeforeEach
    public void setup() {
        mockFieldPredicate1 = new FieldPredicate("FieldPredicate 1", true);
        mockFieldPredicate1.setId(1L);

        mockFieldPredicate2 = new FieldPredicate("FieldPredicate 2", false);
        mockFieldPredicate2.setId(2L);

        mockFieldPredicates = new ArrayList<FieldPredicate>(Arrays.asList(new FieldPredicate[] { mockFieldPredicate1 }));
    }

    @Test
    public void testAllFieldPredicates() {
        when(fieldPredicateRepo.findAll()).thenReturn(mockFieldPredicates);

        ApiResponse response = fieldPredicateController.getAllFieldPredicates();
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        List<?> list = (ArrayList<?>) response.getPayload().get("ArrayList<FieldPredicate>");
        assertEquals(mockFieldPredicates.size(), list.size());
    }

    @Test
    public void testGetFieldPredicate() {
        when(fieldPredicateRepo.findByValue(any(String.class))).thenReturn(mockFieldPredicate1);

        ApiResponse response = fieldPredicateController.getFieldPredicateByValue("value");
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        FieldPredicate fieldPredicate = (FieldPredicate) response.getPayload().get("FieldPredicate");
        assertEquals(mockFieldPredicate1.getId(), fieldPredicate.getId());
    }

    @Test
    public void testCreateFieldPredicate() {
        when(fieldPredicateRepo.create(any(String.class), any(Boolean.class))).thenReturn(mockFieldPredicate2);

        ApiResponse response = fieldPredicateController.createFieldPredicate(mockFieldPredicate1);
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        FieldPredicate fieldPredicate = (FieldPredicate) response.getPayload().get("FieldPredicate");
        assertEquals(mockFieldPredicate2.getId(), fieldPredicate.getId());
    }

    @Test
    public void testUpdateFieldPredicate() {
        when(fieldPredicateRepo.update(any(FieldPredicate.class))).thenReturn(mockFieldPredicate2);

        ApiResponse response = fieldPredicateController.updateFieldPredicate(mockFieldPredicate1);
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        FieldPredicate fieldPredicate = (FieldPredicate) response.getPayload().get("FieldPredicate");
        assertEquals(mockFieldPredicate2.getId(), fieldPredicate.getId());
    }

    @Test
    public void testRemoveFieldPredicate() {
        doNothing().when(fieldPredicateRepo).delete(any(FieldPredicate.class));

        ApiResponse response = fieldPredicateController.removeFieldPredicate(mockFieldPredicate1);
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        verify(fieldPredicateRepo, times(1)).delete(any(FieldPredicate.class));
    }

}
