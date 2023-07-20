package org.tdl.vireo.model.export.enums;

import java.util.stream.Stream;
import org.junit.jupiter.params.provider.Arguments;
import org.tdl.vireo.model.AbstractEnumTest;

public class DSpaceMetsKeyTest extends AbstractEnumTest<DSpaceMETSKey> {

    protected static Stream<Arguments> provideEnumParameters() {
        return Stream.of(
            Arguments.of(DSpaceMETSKey.AGENT, "AGENT", 0),
            Arguments.of(DSpaceMETSKey.STUDENT_SHORT_NAME, "STUDENT_SHORT_NAME", 1),
            Arguments.of(DSpaceMETSKey.STUDENT_FULL_NAME_WITH_BIRTH_YEAR, "STUDENT_FULL_NAME_WITH_BIRTH_YEAR", 2),
            Arguments.of(DSpaceMETSKey.SUBMISSION_TYPE, "SUBMISSION_TYPE", 3),
            Arguments.of(DSpaceMETSKey.DEPOSIT_URL, "DEPOSIT_URL", 4),
            Arguments.of(DSpaceMETSKey.PRIMARY_DOCUMENT_MIMETYPE, "PRIMARY_DOCUMENT_MIMETYPE", 5),
            Arguments.of(DSpaceMETSKey.PRIMARY_DOCUMENT_FIELD_VALUE, "PRIMARY_DOCUMENT_FIELD_VALUE", 6),
            Arguments.of(DSpaceMETSKey.SUPPLEMENTAL_AND_SOURCE_DOCUMENT_FIELD_VALUES, "SUPPLEMENTAL_AND_SOURCE_DOCUMENT_FIELD_VALUES", 7),
            Arguments.of(DSpaceMETSKey.LICENSE_DOCUMENT_FIELD_VALUES, "LICENSE_DOCUMENT_FIELD_VALUES", 8),
            Arguments.of(DSpaceMETSKey.METS_FIELD_VALUES, "METS_FIELD_VALUES", 9),
            Arguments.of(DSpaceMETSKey.GRADUATION_MONTH_YEAR, "GRADUATION_MONTH_YEAR", 10),
            Arguments.of(DSpaceMETSKey.GRADUATION_YEAR_MONTH, "GRADUATION_YEAR_MONTH", 11),
            Arguments.of(DSpaceMETSKey.GRANTOR, "GRANTOR", 12),
            Arguments.of(DSpaceMETSKey.EMBARGO_LIFT_DATE, "EMBARGO_LIFT_DATE", 13)
        );
    }

}
