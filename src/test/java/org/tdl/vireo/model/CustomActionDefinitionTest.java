package org.tdl.vireo.model;

import java.util.stream.Stream;
import org.junit.jupiter.params.provider.Arguments;
import org.mockito.InjectMocks;

public class CustomActionDefinitionTest extends AbstractModelCustomMethodTest<CustomActionDefinition> {

    @InjectMocks
    private CustomActionDefinition customActionDefinition;

    @Override
    protected CustomActionDefinition getInstance() {
        return customActionDefinition;
    }

    protected static Stream<Arguments> provideGetterParameters() {
        return getParameterStream();
    }

    protected static Stream<Arguments> provideSetterParameters() {
        return getParameterStream();
    }

    protected static Stream<Arguments> provideGetterMethodParameters() {
        return getParameterMethodStream();
    }

    protected static Stream<Arguments> provideSetterMethodParameters() {
        return getParameterMethodStream();
    }

    private static Stream<Arguments> getParameterStream() {
        return Stream.of(
            Arguments.of("label", "value")
        );
    }

    private static Stream<Arguments> getParameterMethodStream() {
        return Stream.of(
            Arguments.of("isStudentVisible", "isStudentVisible", true),
            Arguments.of("isStudentVisible", "isStudentVisible", false)
        );
    }

}
