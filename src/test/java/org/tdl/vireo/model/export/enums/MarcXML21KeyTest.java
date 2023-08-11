package org.tdl.vireo.model.export.enums;

import java.util.stream.Stream;
import org.junit.jupiter.params.provider.Arguments;
import org.tdl.vireo.model.AbstractEnumTest;

public class MarcXML21KeyTest extends AbstractEnumTest<MarcXML21Key> {

    protected static Stream<Arguments> provideEnumParameters() {
        return Stream.of(
            Arguments.of(MarcXML21Key.SUBMITTER_GRADUATION_YEAR, "SUBMITTER_GRADUATION_YEAR", 0),
            Arguments.of(MarcXML21Key.STUDENT_FULL_NAME_WITH_BIRTH_YEAR, "STUDENT_FULL_NAME_WITH_BIRTH_YEAR", 1),
            Arguments.of(MarcXML21Key.TITLE, "TITLE", 2),
            Arguments.of(MarcXML21Key.TITLE_IND2, "TITLE_IND2", 3),
            Arguments.of(MarcXML21Key.DEGREE_LEVEL, "DEGREE_LEVEL", 4),
            Arguments.of(MarcXML21Key.DEGREE_NAME, "DEGREE_NAME", 5),
            Arguments.of(MarcXML21Key.MAJOR, "MAJOR", 6),
            Arguments.of(MarcXML21Key.DEPOSIT_URL, "DEPOSIT_URL", 7),
            Arguments.of(MarcXML21Key.ABSTRACT, "ABSTRACT", 8),
            Arguments.of(MarcXML21Key.KEYWORD_FIELD_VALUES, "KEYWORD_FIELD_VALUES", 9),
            Arguments.of(MarcXML21Key.DEPARTMENT, "DEPARTMENT", 10),
            Arguments.of(MarcXML21Key.COMMITTEE_CHAIR_FIELD_VALUES, "COMMITTEE_CHAIR_FIELD_VALUES", 11),
            Arguments.of(MarcXML21Key.COMMITTEE_MEMBER_FIELD_VALUES, "COMMITTEE_MEMBER_FIELD_VALUES", 12),
            Arguments.of(MarcXML21Key.PRIMARY_DOCUMENT_MIMETYPE, "PRIMARY_DOCUMENT_MIMETYPE", 13)
        );
    }

}
