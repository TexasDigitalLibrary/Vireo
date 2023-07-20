package org.tdl.vireo.model;

import java.util.stream.Stream;
import org.junit.jupiter.params.provider.Arguments;
import org.mockito.InjectMocks;

public class DegreeLevelTest extends AbstractModelTest<DegreeLevel> {

    @InjectMocks
    private DegreeLevel degreeLevel;

    @Override
    protected DegreeLevel getInstance() {
        return degreeLevel;
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
