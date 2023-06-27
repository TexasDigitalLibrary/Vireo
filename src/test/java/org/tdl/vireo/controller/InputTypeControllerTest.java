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
import org.tdl.vireo.model.InputType;
import org.tdl.vireo.model.repo.InputTypeRepo;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class InputTypeControllerTest extends AbstractControllerTest {

    @Mock
    private InputTypeRepo inputTypeRepo;

    @InjectMocks
    private InputTypeController inputTypeController;

    private InputType mockInputType1;

    private static List<InputType> mockInputTypes;

    @BeforeEach
    public void setup() {
        mockInputType1 = new InputType("InputType 1");
        mockInputType1.setId(1L);

        mockInputTypes = new ArrayList<InputType>(Arrays.asList(new InputType[] { mockInputType1 }));
    }

    @Test
    public void testAllInputTypes() {
        when(inputTypeRepo.findAll()).thenReturn(mockInputTypes);

        ApiResponse response = inputTypeController.getAllInputTypes();
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        List<?> list = (ArrayList<?>) response.getPayload().get("ArrayList<InputType>");
        assertEquals(mockInputTypes.size(), list.size());
    }

}
