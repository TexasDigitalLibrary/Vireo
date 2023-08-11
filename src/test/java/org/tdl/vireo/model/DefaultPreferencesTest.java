package org.tdl.vireo.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.Arguments;
import org.mockito.InjectMocks;

public class DefaultPreferencesTest extends AbstractModelTest<DefaultPreferences> {

    @InjectMocks
    private DefaultPreferences defaultPreferences;

    @Test
    public void testDefaultPreferencesInstantiation() {
        List<DefaultConfiguration> preferences = new ArrayList<>();
        preferences.add(new DefaultConfiguration("name", "value", "type"));

        defaultPreferences.setPreferences(preferences);
        defaultPreferences.setType("type");

        DefaultPreferences newDefaultPreferences = new DefaultPreferences(defaultPreferences.getType(), defaultPreferences.getPreferences());

        assertEquals(newDefaultPreferences.getPreferences(), defaultPreferences.getPreferences(), "Preferences does not match.");
        assertEquals(newDefaultPreferences.getType(), defaultPreferences.getType(), "Type does not match.");
    }

    @Override
    protected DefaultPreferences getInstance() {
        return defaultPreferences;
    }

    protected static Stream<Arguments> provideGetterParameters() {
        return getParameterStream();
    }

    protected static Stream<Arguments> provideSetterParameters() {
        return getParameterStream();
    }

    private static Stream<Arguments> getParameterStream() {
        List<DefaultConfiguration> preferences = new ArrayList<>();

        return Stream.of(
            Arguments.of("preferences", preferences),
            Arguments.of("type", "value")
        );
    }

}
