package org.tdl.vireo.model;

import java.util.stream.Stream;
import org.junit.jupiter.params.provider.Arguments;
import org.mockito.InjectMocks;

public class CustomActionValueTest extends AbstractModelCustomMethodTest<CustomActionValue> {

    @InjectMocks
    private CustomActionValue customActionValue;

    @Override
    protected CustomActionValue getInstance() {
        return customActionValue;
    }

    protected static Stream<Arguments> provideGetterParameters() {
        return getParameterStream();
    }

    protected static Stream<Arguments> provideSetterParameters() {
        return getParameterStream();
    }

    protected static Stream<Arguments> provideGetterMethodParameters() {
        return Stream.of(
            Arguments.of("getValue", "value", true),
            Arguments.of("getValue", "value", false)
        );
    }

    protected static Stream<Arguments> provideSetterMethodParameters() {
        return Stream.of(
            Arguments.of("setValue", "value", true),
            Arguments.of("setValue", "value", false)
        );
    }

    private static Stream<Arguments> getParameterStream() {
        return Stream.of(
            Arguments.of("definition", new CustomActionDefinition())
        );
    }

}
