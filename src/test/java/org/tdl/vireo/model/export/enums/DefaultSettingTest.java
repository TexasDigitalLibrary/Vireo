package org.tdl.vireo.model.export.enums;

import java.util.stream.Stream;
import org.junit.jupiter.params.provider.Arguments;
import org.tdl.vireo.model.AbstractEnumTest;

public class DefaultSettingTest extends AbstractEnumTest<DefaultSettingKey> {

    protected static Stream<Arguments> provideEnumParameters() {
        return Stream.of(
            Arguments.of(DefaultSettingKey.APPLICATION_GRANTOR, "APPLICATION_GRANTOR", 0),
            Arguments.of(DefaultSettingKey.EXPORT_RELEASE_STUDENT_CONTACT_INFORMATION, "EXPORT_RELEASE_STUDENT_CONTACT_INFORMATION", 1),
            Arguments.of(DefaultSettingKey.PROQUEST_INDEXING, "PROQUEST_INDEXING", 2),
            Arguments.of(DefaultSettingKey.PROQUEST_EXTERNAL_ID, "PROQUEST_EXTERNAL_ID", 3),
            Arguments.of(DefaultSettingKey.PROQUEST_INSTITUTION_CODE, "PROQUEST_INSTITUTION_CODE", 4),
            Arguments.of(DefaultSettingKey.PROQUEST_APPLY_FOR_COPYRIGHT, "PROQUEST_APPLY_FOR_COPYRIGHT", 5),
            Arguments.of(DefaultSettingKey.PROQUEST_SALE_RESTRICTION_CODE, "PROQUEST_SALE_RESTRICTION_CODE", 6),
            Arguments.of(DefaultSettingKey.PROQUEST_SALE_RESTRICTION_REMOVE, "PROQUEST_SALE_RESTRICTION_REMOVE", 7),
            Arguments.of(DefaultSettingKey.PROQUEST_FORMAT_RESTRICTION_CODE, "PROQUEST_FORMAT_RESTRICTION_CODE", 8),
            Arguments.of(DefaultSettingKey.PROQUEST_FORMAT_RESTRICTION_REMOVE, "PROQUEST_FORMAT_RESTRICTION_REMOVE", 9)
        );
    }

}
