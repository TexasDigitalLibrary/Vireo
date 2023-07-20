package org.tdl.vireo.model;

import java.util.stream.Stream;
import org.junit.jupiter.params.provider.Arguments;
import org.mockito.InjectMocks;

public class EntityCVFilterTest extends AbstractModelTest<EntityCVFilter> {

    @InjectMocks
    private EntityCVFilter entityCVFilter;

    @Override
    protected EntityCVFilter getInstance() {
        return entityCVFilter;
    }

    protected static Stream<Arguments> provideGetterParameters() {
        return getParameterStream();
    }

    protected static Stream<Arguments> provideSetterParameters() {
        return getParameterStream();
    }

    private static Stream<Arguments> getParameterStream() {
        return Stream.of(
            Arguments.of("path", "value"),
            Arguments.of("value", "value")
        );
    }

}
