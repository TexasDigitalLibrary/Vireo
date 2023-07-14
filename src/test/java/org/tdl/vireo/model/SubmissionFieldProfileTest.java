package org.tdl.vireo.model;

import java.util.stream.Stream;
import org.junit.jupiter.params.provider.Arguments;
import org.mockito.InjectMocks;

public class SubmissionFieldProfileTest extends AbstractModelCustomMethodTest<SubmissionFieldProfile> {

    @InjectMocks
    private SubmissionFieldProfile submissionFieldProfile;

    @Override
    protected SubmissionFieldProfile getInstance() {
        return submissionFieldProfile;
    }

    protected static Stream<Arguments> provideGetterParameters() {
        return getParameterStream();
    }

    protected static Stream<Arguments> provideSetterParameters() {
        return getParameterStream();
    }

    protected static Stream<Arguments> provideGetterMethodParameters() {
        return Stream.of(
            Arguments.of("getRepeatable", "repeatable", true),
            Arguments.of("getRepeatable", "repeatable", false),
            Arguments.of("getOptional", "optional", true),
            Arguments.of("getOptional", "optional", false),
            Arguments.of("getHidden", "hidden", true),
            Arguments.of("getHidden", "hidden", false),
            Arguments.of("getLogged", "logged", true),
            Arguments.of("getLogged", "logged", false),
            Arguments.of("getFlagged", "flagged", true),
            Arguments.of("getFlagged", "flagged", false),
            Arguments.of("getEnabled", "enabled", true),
            Arguments.of("getEnabled", "enabled", false)
        );
    }

    protected static Stream<Arguments> provideSetterMethodParameters() {
        return Stream.of(
            Arguments.of("setRepeatable", "repeatable", true),
            Arguments.of("setRepeatable", "repeatable", false),
            Arguments.of("setOptional", "optional", true),
            Arguments.of("setOptional", "optional", false),
            Arguments.of("setHidden", "hidden", true),
            Arguments.of("setHidden", "hidden", false),
            Arguments.of("setLogged", "logged", true),
            Arguments.of("setLogged", "logged", false),
            Arguments.of("setFlagged", "flagged", true),
            Arguments.of("setFlagged", "flagged", false),
            Arguments.of("setEnabled", "enabled", true),
            Arguments.of("setEnabled", "enabled", false)
        );
    }

    private static Stream<Arguments> getParameterStream() {
        return Stream.of(
            Arguments.of("fieldPredicate", new FieldPredicate()),
            Arguments.of("inputType", new InputType()),
            Arguments.of("usage", "value"),
            Arguments.of("help", "value"),
            Arguments.of("gloss", "value"),
            Arguments.of("controlledVocabulary", new ControlledVocabulary()),
            Arguments.of("mappedShibAttribute", new ManagedConfiguration())
        );
    }

}
