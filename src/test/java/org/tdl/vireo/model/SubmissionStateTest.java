package org.tdl.vireo.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class SubmissionStateTest extends AbstractEnumTest<SubmissionState> {

    @ParameterizedTest
    @MethodSource("provideEnumParameters")
    public void testGetValue(SubmissionState enumeration, String name, int ordinal) {
        assertEquals(ordinal, enumeration.getValue(), "Value is incorrect.");
    }

    @ParameterizedTest
    @MethodSource("provideEnumParameters")
    public void testToString(SubmissionState enumeration, String name, int ordinal) {
        assertEquals(name, enumeration.toString(), "Name is incorrect.");
    }

    protected static Stream<Arguments> provideEnumParameters() {
        return Stream.of(
            Arguments.of(SubmissionState.NONE, "NONE", 0),
            Arguments.of(SubmissionState.IN_PROGRESS, "IN_PROGRESS", 1),
            Arguments.of(SubmissionState.SUBMITTED, "SUBMITTED", 2),
            Arguments.of(SubmissionState.UNDER_REVIEW, "UNDER_REVIEW", 3),
            Arguments.of(SubmissionState.NEEDS_CORRECTIONS, "NEEDS_CORRECTIONS", 4),
            Arguments.of(SubmissionState.CORRECTIONS_RECIEVED, "CORRECTIONS_RECIEVED", 5),
            Arguments.of(SubmissionState.WAITING_ON_REQUIREMENTS, "WAITING_ON_REQUIREMENTS", 6),
            Arguments.of(SubmissionState.APPROVED, "APPROVED", 7),
            Arguments.of(SubmissionState.PENDING_PUBLICATION, "PENDING_PUBLICATION", 8),
            Arguments.of(SubmissionState.PUBLISHED, "PUBLISHED", 9),
            Arguments.of(SubmissionState.ON_HOLD, "ON_HOLD", 10),
            Arguments.of(SubmissionState.WITHDRAWN, "WITHDRAWN", 11),
            Arguments.of(SubmissionState.CANCELED, "CANCELED", 12)
        );
    }

}
