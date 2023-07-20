package org.tdl.vireo.model;

import java.util.stream.Stream;
import org.junit.jupiter.params.provider.Arguments;
import org.mockito.InjectMocks;

public class EmailTemplateTest extends AbstractModelCustomMethodTest<EmailTemplate> {

    @InjectMocks
    private EmailTemplate emailTemplate;

    @Override
    protected EmailTemplate getInstance() {
        return emailTemplate;
    }

    protected static Stream<Arguments> provideGetterParameters() {
        return getParameterStream();
    }

    protected static Stream<Arguments> provideSetterParameters() {
        return getParameterStream();
    }

    protected static Stream<Arguments> provideGetterMethodParameters() {
        return Stream.of(
            Arguments.of("getSystemRequired", "systemRequired", true),
            Arguments.of("getSystemRequired", "systemRequired", false)
        );
    }

    protected static Stream<Arguments> provideSetterMethodParameters() {
        return Stream.of(
            Arguments.of("setSystemRequired", "systemRequired", true),
            Arguments.of("setSystemRequired", "systemRequired", false)
        );
    }

    private static Stream<Arguments> getParameterStream() {
        return Stream.of(
            Arguments.of("name", "value"),
            Arguments.of("subject", "value"),
            Arguments.of("message", "value")
        );
    }

}
