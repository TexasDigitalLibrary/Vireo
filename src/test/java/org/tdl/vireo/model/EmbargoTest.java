package org.tdl.vireo.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.Arguments;
import org.mockito.InjectMocks;
import org.springframework.test.util.ReflectionTestUtils;

public class EmbargoTest extends AbstractModelCustomMethodTest<Embargo> {

    @InjectMocks
    private Embargo embargo;

    @Test
    public void testGetControlledName() {
        String name = "name";

        ReflectionTestUtils.setField(getInstance(), "name", name);

        assertEquals(name, embargo.getControlledName(), "Controlled Name does not match.");
    }

    @Test
    public void testGetControlledDefinition() {
        String description = "description";

        ReflectionTestUtils.setField(getInstance(), "description", description);

        assertEquals(description, embargo.getControlledDefinition(), "Controlled Definition does not match.");
    }

    @Test
    public void testGetControlledIdentifier() {
        Long id = 1L;

        ReflectionTestUtils.setField(getInstance(), "id", id);

        assertEquals(String.valueOf(id), embargo.getControlledIdentifier(), "Controlled Identifier does not match.");
    }

    @Test
    public void testGetControlledContacts() {
        assertNotNull(embargo.getControlledContacts(), "Controlled Contacts is null.");
    }

    @Test
    public void testInstanceDoesNotEqual() {
        Embargo embargo1 = new Embargo();
        embargo.setId(1L);

        Embargo embargo2 = new Embargo();
        embargo2.setId(2L);

        assertFalse(embargo1.equals(embargo2), "Embargos must not match.");
    }

    @Test
    public void testInstanceEquals() {
        Embargo embargo1 = new Embargo();
        embargo1.setId(1L);
        embargo1.setName("name");
        embargo1.setDescription("description");
        embargo1.setGuarantor(EmbargoGuarantor.DEFAULT);
        embargo1.setDuration(1);

        assertTrue(embargo1.equals((Embargo) embargo1), "Embargos must match.");
    }

    @Override
    protected Embargo getInstance() {
        return embargo;
    }

    protected static Stream<Arguments> provideGetterParameters() {
        return getParameterStream();
    }

    protected static Stream<Arguments> provideSetterParameters() {
        return getParameterStream();
    }

    protected static Stream<Arguments> provideGetterMethodParameters() {
        return Stream.of(
            Arguments.of("isActive", "isActive", true),
            Arguments.of("isActive", "isActive", false),
            Arguments.of("getSystemRequired", "systemRequired", true),
            Arguments.of("getSystemRequired", "systemRequired", false)
        );
    }

    protected static Stream<Arguments> provideSetterMethodParameters() {
        return Stream.of(
            Arguments.of("isActive", "isActive", true),
            Arguments.of("isActive", "isActive", false),
            Arguments.of("setSystemRequired", "systemRequired", true),
            Arguments.of("setSystemRequired", "systemRequired", false)
        );
    }

    private static Stream<Arguments> getParameterStream() {
        return Stream.of(
            Arguments.of("name", "value"),
            Arguments.of("description", "value"),
            Arguments.of("duration", 123),
            Arguments.of("guarantor", EmbargoGuarantor.DEFAULT),
            Arguments.of("guarantor", EmbargoGuarantor.PROQUEST)
        );
    }

}
