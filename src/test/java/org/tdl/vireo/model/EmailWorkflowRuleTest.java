package org.tdl.vireo.model;

import java.util.stream.Stream;
import org.junit.jupiter.params.provider.Arguments;
import org.mockito.InjectMocks;

public class EmailWorkflowRuleTest extends AbstractModelCustomMethodTest<EmailWorkflowRuleByStatus> {

    @InjectMocks
    private EmailWorkflowRuleByStatus emailWorkflowRule;

    @Override
    protected EmailWorkflowRuleByStatus getInstance() {
        return emailWorkflowRule;
    }

    protected static Stream<Arguments> provideGetterParameters() {
        return getParameterStream();
    }

    protected static Stream<Arguments> provideSetterParameters() {
        return getParameterStream();
    }

    protected static Stream<Arguments> provideGetterMethodParameters() {
        return Stream.of(
            Arguments.of("isSystem", "isSystem", true),
            Arguments.of("isSystem", "isSystem", false),
            Arguments.of("isDisabled", "isDisabled", true),
            Arguments.of("isDisabled", "isDisabled", false)
        );
    }

    protected static Stream<Arguments> provideSetterMethodParameters() {
        return Stream.of(
            Arguments.of("isSystem", "isSystem", true),
            Arguments.of("isSystem", "isSystem", false),
            Arguments.of("isDisabled", "isDisabled", true),
            Arguments.of("isDisabled", "isDisabled", false)
        );
    }

    private static Stream<Arguments> getParameterStream() {
        return Stream.of(
            Arguments.of("emailRecipient", new EmailRecipientOrganization()),
            Arguments.of("emailRecipient", new EmailRecipientPlainAddress()),
            Arguments.of("emailTemplate", new EmailTemplate())
        );
    }

}
