package org.tdl.vireo.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.Arguments;
import org.mockito.InjectMocks;
import org.springframework.test.util.ReflectionTestUtils;

public class LanguageTest extends AbstractModelTest<Language> {

    @InjectMocks
    private Language language;

    @Test
    public void testGetControlledName() {
        String name = "name";

        ReflectionTestUtils.setField(getInstance(), "name", name);

        assertEquals(name, language.getControlledName(), "Controlled Name does not match.");
    }

    @Test
    public void testGetControlledDefinition() {
        assertEquals("", language.getControlledDefinition(), "Controlled Definition does not match.");
    }

    @Test
    public void testGetControlledIdentifier() {
        assertEquals("", language.getControlledIdentifier(), "Controlled Identifier does not match.");
    }

    @Test
    public void testGetControlledContacts() {
        assertNotNull(language.getControlledContacts(), "Controlled Contacts is null.");
    }

    @Override
    protected Language getInstance() {
        return language;
    }

    protected static Stream<Arguments> provideGetterParameters() {
        return getParameterStream();
    }

    protected static Stream<Arguments> provideSetterParameters() {
        return getParameterStream();
    }

    private static Stream<Arguments> getParameterStream() {
        return Stream.of(
            Arguments.of("name", "value")
        );
    }

}
