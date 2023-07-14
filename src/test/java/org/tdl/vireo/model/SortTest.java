package org.tdl.vireo.model;

import java.util.stream.Stream;
import org.junit.jupiter.params.provider.Arguments;

public class SortTest extends AbstractEnumTest<Sort> {

    protected static Stream<Arguments> provideEnumParameters() {
        return Stream.of(
            Arguments.of(Sort.ASC, "ASC", 0),
            Arguments.of(Sort.DESC, "DESC", 1),
            Arguments.of(Sort.NONE, "NONE", 2) 
        );
    }

}
