package org.tdl.vireo.model;

import java.util.stream.Stream;
import org.junit.jupiter.params.provider.Arguments;
import org.mockito.InjectMocks;

public class ValidationTest extends AbstractModelTest<Validation> {

    @InjectMocks
    private Validation validation;

    @Override
    protected Validation getInstance() {
        return validation;
    }

    protected static Stream<Arguments> provideGetterParameters() {
        return getParameterStream();
    }

    protected static Stream<Arguments> provideSetterParameters() {
        return getParameterStream();
    }

    private static Stream<Arguments> getParameterStream() {
        return Stream.of(
            Arguments.of("pattern", "value"),
            Arguments.of("message", "value")
        );
    }

}
