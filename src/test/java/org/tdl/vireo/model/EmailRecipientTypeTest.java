package org.tdl.vireo.model;

import java.util.stream.Stream;
import org.junit.jupiter.params.provider.Arguments;

public class EmailRecipientTypeTest extends AbstractEnumTest<EmailRecipientType> {

    protected static Stream<Arguments> provideEnumParameters() {
        return Stream.of(
            Arguments.of(EmailRecipientType.ASSIGNEE, "ASSIGNEE", 0),
            Arguments.of(EmailRecipientType.ADVISOR, "ADVISOR", 1),
            Arguments.of(EmailRecipientType.CONTACT, "CONTACT", 2),
            Arguments.of(EmailRecipientType.ORGANIZATION, "ORGANIZATION", 3),
            Arguments.of(EmailRecipientType.PLAIN_ADDRESS, "PLAIN_ADDRESS", 4),
            Arguments.of(EmailRecipientType.SUBMITTER, "SUBMITTER", 5)
        );
    }

}
