package org.tdl.vireo.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.springframework.test.util.ReflectionTestUtils;

public class DefaultConfigurationTest {

    // Warning: The tests fail here due to strange initialization problems and so an explicit assignment is provided.
    @InjectMocks
    private DefaultConfiguration defaultConfiguration = new DefaultConfiguration("a", "b", "c");

    @ParameterizedTest
    @MethodSource("provideGetterParameters")
    public void testGetter(String property, Object value) {
        ReflectionTestUtils.setField(defaultConfiguration, property, value);

        assertEquals(value, ReflectionTestUtils.invokeGetterMethod(defaultConfiguration, property), AbstractModelTest.GETTER_MESSAGE);
    }

    private static Stream<Arguments> provideGetterParameters() {
        return Stream.of(
            Arguments.of("name", "value"),
            Arguments.of("value", "value"),
            Arguments.of("type", "value")
        );
    }

}
