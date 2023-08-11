package org.tdl.vireo.model;

import java.util.stream.Stream;
import org.junit.jupiter.params.provider.Arguments;

public class FilterActionTest extends AbstractEnumTest<FilterAction> {

    protected static Stream<Arguments> provideEnumParameters() {
        return Stream.of(
            Arguments.of(FilterAction.CLEAR, "CLEAR", 0),
            Arguments.of(FilterAction.REFRESH, "REFRESH", 1),
            Arguments.of(FilterAction.REMOVE, "REMOVE", 2),
            Arguments.of(FilterAction.SAVE, "SAVE", 3),
            Arguments.of(FilterAction.SET, "SET", 4),
            Arguments.of(FilterAction.SORT, "SORT", 5)
        );
    }

}
