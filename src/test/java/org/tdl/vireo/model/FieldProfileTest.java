package org.tdl.vireo.model;

import java.util.stream.Stream;
import org.junit.jupiter.params.provider.Arguments;
import org.mockito.InjectMocks;

public class FieldProfileTest  extends AbstractModelCustomMethodTest<FieldProfile> {

    @InjectMocks
    private FieldProfile fieldProfile;

    @Override
    protected FieldProfile getInstance() {
        return fieldProfile;
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
            Arguments.of("getEnabled", "enabled", false),
            Arguments.of("getOverrideable", "overrideable", true),
            Arguments.of("getOverrideable", "overrideable", false)
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
            Arguments.of("setEnabled", "enabled", false),
            Arguments.of("setOverrideable", "overrideable", true),
            Arguments.of("setOverrideable", "overrideable", false)
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
            Arguments.of("mappedShibAttribute", new ManagedConfiguration()),
            Arguments.of("defaultValue", "value"),
            Arguments.of("originating", new FieldProfile()),
            Arguments.of("originatingWorkflowStep", new WorkflowStep())
        );
    }

}
