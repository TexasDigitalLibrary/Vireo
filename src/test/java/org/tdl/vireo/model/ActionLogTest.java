package org.tdl.vireo.model;

import java.util.Calendar;
import java.util.stream.Stream;
import org.junit.jupiter.params.provider.Arguments;
import org.mockito.InjectMocks;

public class ActionLogTest extends AbstractModelCustomMethodTest<ActionLog> {

    @InjectMocks
    private ActionLog actionLog;

    @Override
    protected ActionLog getInstance() {
        return actionLog;
    }

    protected static Stream<Arguments> provideGetterParameters() {
        return getParameterStream();
    }

    protected static Stream<Arguments> provideSetterParameters() {
        return getParameterStream();
    }

    protected static Stream<Arguments> provideGetterMethodParameters() {
        return Stream.of(
            Arguments.of("isPrivateFlag", "privateFlag", true),
            Arguments.of("isPrivateFlag", "privateFlag", false)
        );
    }

    protected static Stream<Arguments> provideSetterMethodParameters() {
        return Stream.of(
            Arguments.of("setPrivateFlag", "privateFlag", true),
            Arguments.of("setPrivateFlag", "privateFlag", false)
        );
    }

    private static Stream<Arguments> getParameterStream() {
        return Stream.of(
            Arguments.of("submissionStatus", new SubmissionStatus()),
            Arguments.of("user", new User()),
            Arguments.of("actionDate", Calendar.getInstance()),
            Arguments.of("entry", "value") 
        );
    }

}
