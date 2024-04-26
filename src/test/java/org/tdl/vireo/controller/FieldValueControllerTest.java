package org.tdl.vireo.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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
import org.tdl.vireo.model.FieldValue;
import org.tdl.vireo.model.repo.FieldPredicateRepo;
import org.tdl.vireo.model.repo.FieldValueRepo;

@ActiveProfiles(value = { "test", "isolated-test" })
public class FieldValueControllerTest extends AbstractControllerTest {

    @Mock
    private FieldPredicateRepo fieldPredicateRepo;

    @Mock
    private FieldValueRepo fieldValueRepo;

    @InjectMocks
    private FieldValueController fieldValueController;

    private FieldPredicate mockFieldPredicate1;

    private FieldValue mockFieldValue1;
    private FieldValue mockFieldValue2;

    private static List<FieldValue> mockFieldValues;

    @BeforeEach
    public void setup() {
        mockFieldValue1 = new FieldValue();
        mockFieldValue1.setId(1L);
        mockFieldValue1.setValue("FieldValue 1");

        mockFieldValue2 = new FieldValue();
        mockFieldValue2.setId(2L);
        mockFieldValue2.setValue("FieldValue 2");

        mockFieldPredicate1 = new FieldPredicate("FieldPredicate 1", true);
        mockFieldPredicate1.setId(1L);

        mockFieldValues = new ArrayList<FieldValue>(Arrays.asList(new FieldValue[] { mockFieldValue1 }));
    }

    @Test
    public void testGetFieldValuesByPredicateValue() {
        when(fieldPredicateRepo.findByValue(anyString())).thenReturn(mockFieldPredicate1);
        when(fieldValueRepo.findAllByFieldPredicate(any(FieldPredicate.class))).thenReturn(mockFieldValues);

        ApiResponse response = fieldValueController.getFieldValuesByPredicateValue("value");
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        ArrayList<FieldValue> fieldValues = (ArrayList<FieldValue>) response.getPayload().get("ArrayList<FieldValue>");
        assertEquals(mockFieldValues.size(), fieldValues.size());
        assertEquals(mockFieldValues.get(0).getId(), fieldValues.get(0).getId());
    }

}
