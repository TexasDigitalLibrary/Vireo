package org.tdl.vireo.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.Arguments;
import org.mockito.InjectMocks;
import org.springframework.test.util.ReflectionTestUtils;

public class DegreeTest extends AbstractModelTest<Degree> {

    @InjectMocks
    private Degree degree;

    @Test
    public void testGetControlledName() {
        String name = "name";

        ReflectionTestUtils.setField(getInstance(), "name", name);

        assertEquals(name, degree.getControlledName(), "Controlled Name does not match.");
    }

    @Test
    public void testGetControlledDefinition() {
        String degreeCode = "degreeCode";

        ReflectionTestUtils.setField(getInstance(), "degreeCode", degreeCode);

        assertEquals(degreeCode, degree.getControlledDefinition(), "Controlled Definition does not match.");
    }

    @Test
    public void testGetControlledIdentifier() {
        DegreeLevel degreeLevel = new DegreeLevel("level");

        ReflectionTestUtils.setField(getInstance(), "level", degreeLevel);

        assertEquals(degreeLevel.getName(), degree.getControlledIdentifier(), "Controlled Identifier does not match.");
    }

    @Test
    public void testGetControlledContacts() {
        assertNotNull(degree.getControlledContacts(), "Controlled Contacts is null.");
    }

    @Override
    protected Degree getInstance() {
        return degree;
    }

    protected static Stream<Arguments> provideGetterParameters() {
        return getParameterStream();
    }

    protected static Stream<Arguments> provideSetterParameters() {
        return getParameterStream();
    }

    private static Stream<Arguments> getParameterStream() {
        return Stream.of(
            Arguments.of("name", "value"),
            Arguments.of("degreeCode", "value"),
            Arguments.of("level", new DegreeLevel())
        );
    }

}
