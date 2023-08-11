package org.tdl.vireo.model;

import java.util.stream.Stream;
import org.junit.jupiter.params.provider.Arguments;
import org.mockito.InjectMocks;

public class SubmissionNoteTest extends AbstractModelTest<SubmissionNote> {

    @InjectMocks
    private SubmissionNote submissionNote;

    @Override
    protected SubmissionNote getInstance() {
        return submissionNote;
    }

    protected static Stream<Arguments> provideGetterParameters() {
        return getParameterStream();
    }

    protected static Stream<Arguments> provideSetterParameters() {
        return getParameterStream();
    }

    private static Stream<Arguments> getParameterStream() {
        return Stream.of(
            Arguments.of("name", "value"),
            Arguments.of("text", "value")
        );
    }

}
