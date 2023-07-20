package org.tdl.vireo.model.export.enums;

import java.util.stream.Stream;
import org.junit.jupiter.params.provider.Arguments;
import org.tdl.vireo.model.AbstractEnumTest;

public class SubmissionPropertyKeyTest extends AbstractEnumTest<SubmissionPropertyKey> {

    protected static Stream<Arguments> provideEnumParameters() {
        return Stream.of(
            Arguments.of(SubmissionPropertyKey.SUBMISSION_ID, "SUBMISSION_ID", 0),
            Arguments.of(SubmissionPropertyKey.FIELD_VALUES, "FIELD_VALUES", 1),
            Arguments.of(SubmissionPropertyKey.FORMATTED_APPLICATION_APPROVAL_DATE, "FORMATTED_APPLICATION_APPROVAL_DATE", 2),
            Arguments.of(SubmissionPropertyKey.LICENSE_AGREEMENT_DATE, "LICENSE_AGREEMENT_DATE", 3),
            Arguments.of(SubmissionPropertyKey.FORMATTED_LICENSE_AGREEMENT_DATE, "FORMATTED_LICENSE_AGREEMENT_DATE", 4),
            Arguments.of(SubmissionPropertyKey.SUBMISSION_DATE, "SUBMISSION_DATE", 5),
            Arguments.of(SubmissionPropertyKey.FORMATTED_SUBMISSION_DATE, "FORMATTED_SUBMISSION_DATE", 6),
            Arguments.of(SubmissionPropertyKey.COMMITTEE_APPROVAL_DATE, "COMMITTEE_APPROVAL_DATE", 7),
            Arguments.of(SubmissionPropertyKey.FORMATTED_COMMITTEE_APPROVAL_DATE, "FORMATTED_COMMITTEE_APPROVAL_DATE", 8),
            Arguments.of(SubmissionPropertyKey.COMMITTEE_EMBARGO_APPROVAL_DATE, "COMMITTEE_EMBARGO_APPROVAL_DATE", 9),
            Arguments.of(SubmissionPropertyKey.FORMATTED_COMMITTEE_EMBARGO_APPROVAL_DATE, "FORMATTED_COMMITTEE_EMBARGO_APPROVAL_DATE", 10),
            Arguments.of(SubmissionPropertyKey.APPROVAL_DATE, "APPROVAL_DATE", 11),
            Arguments.of(SubmissionPropertyKey.FORMATTED_APPROVAL_DATE, "FORMATTED_APPROVAL_DATE", 12)
        );
    }

}
