package org.tdl.vireo.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.Arguments;
import org.mockito.InjectMocks;
import org.springframework.test.util.ReflectionTestUtils;

public class GraduationMonthTest extends AbstractModelTest<GraduationMonth> {

    @InjectMocks
    private GraduationMonth graduationMonth;

    @Test
    public void testGetControlledName() {
        int month = 123;

        ReflectionTestUtils.setField(getInstance(), "month", month);

        assertEquals(String.valueOf(month), graduationMonth.getControlledName(), "Controlled Name does not match.");
    }

    @Test
    public void testGetControlledDefinition() {
        assertEquals("", graduationMonth.getControlledDefinition(), "Controlled Definition does not match.");
    }

    @Test
    public void testGetControlledIdentifier() {
        assertEquals("", graduationMonth.getControlledIdentifier(), "Controlled Identifier does not match.");
    }

    @Test
    public void testGetControlledContacts() {
        assertNotNull(graduationMonth.getControlledContacts(), "Controlled Contacts is null.");
    }

    @Override
    protected GraduationMonth getInstance() {
        return graduationMonth;
    }

    protected static Stream<Arguments> provideGetterParameters() {
        return getParameterStream();
    }

    protected static Stream<Arguments> provideSetterParameters() {
        return getParameterStream();
    }

    private static Stream<Arguments> getParameterStream() {
        return Stream.of(
            Arguments.of("month", 123)
        );
    }

}
