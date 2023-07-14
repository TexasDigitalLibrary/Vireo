package org.tdl.vireo.model;

import java.util.stream.Stream;
import org.junit.jupiter.params.provider.Arguments;
import org.mockito.InjectMocks;

public class NoteTest extends AbstractModelCustomMethodTest<Note> {

    @InjectMocks
    private Note note;

    @Override
    protected Note getInstance() {
        return note;
    }

    protected static Stream<Arguments> provideGetterParameters() {
        return getParameterStream();
    }

    protected static Stream<Arguments> provideSetterParameters() {
        return getParameterStream();
    }

    protected static Stream<Arguments> provideGetterMethodParameters() {
        return Stream.of(
            Arguments.of("getOverrideable", "overrideable", true),
            Arguments.of("getOverrideable", "overrideable", false)
        );
    }

    protected static Stream<Arguments> provideSetterMethodParameters() {
        return Stream.of(
            Arguments.of("setOverrideable", "overrideable", true),
            Arguments.of("setOverrideable", "overrideable", false)
        );
    }

    private static Stream<Arguments> getParameterStream() {
        WorkflowStep workflowStep = new WorkflowStep();

        return Stream.of(
            Arguments.of("name", "value"),
            Arguments.of("text", "value"),
            Arguments.of("originating", new Note(workflowStep, "name", "text", false)),
            Arguments.of("originatingWorkflowStep", workflowStep)
        );
    }

}
