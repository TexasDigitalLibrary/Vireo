package org.tdl.vireo.model.request;

import java.util.stream.Stream;
import org.junit.jupiter.params.provider.Arguments;
import org.mockito.InjectMocks;
import org.springframework.data.domain.Sort;
import org.tdl.vireo.model.AbstractModelTest;

public class DirectionSortTest extends AbstractModelTest<DirectionSort> {

    @InjectMocks
    private DirectionSort directionSort;

    @Override
    protected DirectionSort getInstance() {
        return directionSort;
    }

    protected static Stream<Arguments> provideGetterParameters() {
        return getParameterStream();
    }

    protected static Stream<Arguments> provideSetterParameters() {
        return getParameterStream();
    }

    private static Stream<Arguments> getParameterStream() {
        return Stream.of(
            Arguments.of("property", "value"),
            Arguments.of("direction", Sort.Direction.ASC),
            Arguments.of("direction", Sort.Direction.DESC)
        );
    }

}
