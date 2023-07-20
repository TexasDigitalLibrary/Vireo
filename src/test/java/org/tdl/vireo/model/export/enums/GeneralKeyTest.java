package org.tdl.vireo.model.export.enums;

import java.util.stream.Stream;
import org.junit.jupiter.params.provider.Arguments;
import org.tdl.vireo.model.AbstractEnumTest;

public class GeneralKeyTest extends AbstractEnumTest<GeneralKey> {

    protected static Stream<Arguments> provideEnumParameters() {
        return Stream.of(
            Arguments.of(GeneralKey.TIME, "TIME", 0),
            Arguments.of(GeneralKey.FILE_HELPER, "FILE_HELPER", 1),
            Arguments.of(GeneralKey.SUBMISSION_HELPER, "SUBMISSION_HELPER", 2),
            Arguments.of(GeneralKey.SUBMISSION, "SUBMISSION", 3)
        );
    }

}
