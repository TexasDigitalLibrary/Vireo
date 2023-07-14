package org.tdl.vireo.model.export.enums;

import java.util.stream.Stream;
import org.junit.jupiter.params.provider.Arguments;
import org.tdl.vireo.model.AbstractEnumTest;

public class DSpaceSimpleKeyTest extends AbstractEnumTest<DSpaceSimpleKey> {

    protected static Stream<Arguments> provideEnumParameters() {
        return Stream.of(
            Arguments.of(DSpaceSimpleKey.STUDENT_FULL_NAME_WITH_BIRTH_YEAR, "STUDENT_FULL_NAME_WITH_BIRTH_YEAR", 0),
            Arguments.of(DSpaceSimpleKey.USER_ORCID, "USER_ORCID", 1),
            Arguments.of(DSpaceSimpleKey.TITLE, "TITLE", 2),
            Arguments.of(DSpaceSimpleKey.ABSTRACT, "ABSTRACT", 3),
            Arguments.of(DSpaceSimpleKey.ABSTRACT_LINES, "ABSTRACT_LINES", 4),
            Arguments.of(DSpaceSimpleKey.SUBJECT_FIELD_VALUES, "SUBJECT_FIELD_VALUES", 5),
            Arguments.of(DSpaceSimpleKey.COMMITTEE_CHAIR_FIELD_VALUES, "COMMITTEE_CHAIR_FIELD_VALUES", 6),
            Arguments.of(DSpaceSimpleKey.COMMITTEE_MEMBER_FIELD_VALUES, "COMMITTEE_MEMBER_FIELD_VALUES", 7),
            Arguments.of(DSpaceSimpleKey.GRADUATION_DATE_YEAR_MONTH_STRING, "GRADUATION_DATE_YEAR_MONTH_STRING", 8),
            Arguments.of(DSpaceSimpleKey.GRADUATION_DATE_MONTH_YEAR_STRING, "GRADUATION_DATE_MONTH_YEAR_STRING", 9),
            Arguments.of(DSpaceSimpleKey.APPROVAL_DATE_STRING, "APPROVAL_DATE_STRING", 10),
            Arguments.of(DSpaceSimpleKey.PRIMARY_DOCUMENT_MIMETYPE, "PRIMARY_DOCUMENT_MIMETYPE", 11),
            Arguments.of(DSpaceSimpleKey.PROQUEST_LANGUAGE_CODE, "PROQUEST_LANGUAGE_CODE", 12),
            Arguments.of(DSpaceSimpleKey.SUBMISSION_TYPE, "SUBMISSION_TYPE", 13),
            Arguments.of(DSpaceSimpleKey.DEPOSIT_URL, "DEPOSIT_URL", 14),
            Arguments.of(DSpaceSimpleKey.STUDENT_SHORT_NAME, "STUDENT_SHORT_NAME", 15),
            Arguments.of(DSpaceSimpleKey.DEGREE_NAME, "DEGREE_NAME", 16),
            Arguments.of(DSpaceSimpleKey.DEGREE_LEVEL, "DEGREE_LEVEL", 17),
            Arguments.of(DSpaceSimpleKey.DEGREE_MAJOR, "DEGREE_MAJOR", 18),
            Arguments.of(DSpaceSimpleKey.DEPARTMENT, "DEPARTMENT", 19),
            Arguments.of(DSpaceSimpleKey.DEGREE_COLLEGE, "DEGREE_COLLEGE", 20),
            Arguments.of(DSpaceSimpleKey.DEGREE_SCHOOL, "DEGREE_SCHOOL", 21),
            Arguments.of(DSpaceSimpleKey.DEGREE_PROGRAM, "DEGREE_PROGRAM", 22),
            Arguments.of(DSpaceSimpleKey.FORMATTED_COMMITTEE_APPROVED_EMBARGO_LIFT_DATE, "FORMATTED_COMMITTEE_APPROVED_EMBARGO_LIFT_DATE", 23)
        );
    }

}
