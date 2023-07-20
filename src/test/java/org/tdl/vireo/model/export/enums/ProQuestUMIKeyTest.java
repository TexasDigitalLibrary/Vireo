package org.tdl.vireo.model.export.enums;

import java.util.stream.Stream;
import org.junit.jupiter.params.provider.Arguments;
import org.tdl.vireo.model.AbstractEnumTest;

public class ProQuestUMIKeyTest extends AbstractEnumTest<ProQuestUMIKey> {

    protected static Stream<Arguments> provideEnumParameters() {
        return Stream.of(
            Arguments.of(ProQuestUMIKey.AGENT, "AGENT", 0),
            Arguments.of(ProQuestUMIKey.EMBARGO_CODE, "EMBARGO_CODE", 1),
            Arguments.of(ProQuestUMIKey.SUBMITTER_LAST_NAME, "SUBMITTER_LAST_NAME", 2),
            Arguments.of(ProQuestUMIKey.SUBMITTER_FIRST_NAME, "SUBMITTER_FIRST_NAME", 3),
            Arguments.of(ProQuestUMIKey.SUBMITTER_MIDDLE_NAME, "SUBMITTER_MIDDLE_NAME", 4),
            Arguments.of(ProQuestUMIKey.SUBMITTER_CURRENT_PHONE_NUMBER, "SUBMITTER_CURRENT_PHONE_NUMBER", 5),
            Arguments.of(ProQuestUMIKey.SUBMITTER_CURRENT_ADDRESS, "SUBMITTER_CURRENT_ADDRESS", 6),
            Arguments.of(ProQuestUMIKey.SUBMITTER_EMAIL, "SUBMITTER_EMAIL", 7),
            Arguments.of(ProQuestUMIKey.SUBMITTER_GRADUATION_DATE, "SUBMITTER_GRADUATION_DATE", 8),
            Arguments.of(ProQuestUMIKey.SUBMITTER_GRADUATION_YEAR, "SUBMITTER_GRADUATION_YEAR", 9),
            Arguments.of(ProQuestUMIKey.SUBMITTER_PERMANENT_PHONE_NUMBER, "SUBMITTER_PERMANENT_PHONE_NUMBER", 10),
            Arguments.of(ProQuestUMIKey.SUBMITTER_PERMANENT_ADDRESS, "SUBMITTER_PERMANENT_ADDRESS", 11),
            Arguments.of(ProQuestUMIKey.SUBMITTER_PERMANENT_EMAIL, "SUBMITTER_PERMANENT_EMAIL", 12),
            Arguments.of(ProQuestUMIKey.PROQUEST_DEGREE_CODE, "PROQUEST_DEGREE_CODE", 13),
            Arguments.of(ProQuestUMIKey.PROQUEST_LANGUAGE_CODE, "PROQUEST_LANGUAGE_CODE", 14),
            Arguments.of(ProQuestUMIKey.DEGREE_LEVEL, "DEGREE_LEVEL", 15),
            Arguments.of(ProQuestUMIKey.DEPARTMENT, "DEPARTMENT", 16),
            Arguments.of(ProQuestUMIKey.TITLE, "TITLE", 17),
            Arguments.of(ProQuestUMIKey.COMMITTEE_CHAIR_FIELD_VALUES, "COMMITTEE_CHAIR_FIELD_VALUES", 18),
            Arguments.of(ProQuestUMIKey.COMMITTEE_MEMBER_FIELD_VALUES, "COMMITTEE_MEMBER_FIELD_VALUES", 19),
            Arguments.of(ProQuestUMIKey.SUBJECT_FIELD_VALUES, "SUBJECT_FIELD_VALUES", 20),
            Arguments.of(ProQuestUMIKey.KEYWORD_FIELD_VALUES, "KEYWORD_FIELD_VALUES", 21),
            Arguments.of(ProQuestUMIKey.ABSTRACT, "ABSTRACT", 22),
            Arguments.of(ProQuestUMIKey.ABSTRACT_LINES, "ABSTRACT_LINES", 23),
            Arguments.of(ProQuestUMIKey.PRIMARY_DOCUMENT_MIMETYPE, "PRIMARY_DOCUMENT_MIMETYPE", 24),
            Arguments.of(ProQuestUMIKey.PRIMARY_DOCUMENT_FIELD_VALUE, "PRIMARY_DOCUMENT_FIELD_VALUE", 25),
            Arguments.of(ProQuestUMIKey.SUPPLEMENTAL_DOCUMENT_FIELD_VALUES, "SUPPLEMENTAL_DOCUMENT_FIELD_VALUES", 26),
            Arguments.of(ProQuestUMIKey.PROQUEST_PERSON_FILENAME, "PROQUEST_PERSON_FILENAME", 27),
            Arguments.of(ProQuestUMIKey.DEGREE_LEVEL_STR, "DEGREE_LEVEL_STR", 28),
            Arguments.of(ProQuestUMIKey.DEGREE_CODE_STR, "DEGREE_CODE_STR", 29),
            Arguments.of(ProQuestUMIKey.DEGREE_LEVEL_PQ_PROCCODE, "DEGREE_LEVEL_PQ_PROCCODE", 30)
        );
    }

}
