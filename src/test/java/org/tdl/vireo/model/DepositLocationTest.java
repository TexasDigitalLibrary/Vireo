package org.tdl.vireo.model;

import java.util.stream.Stream;
import org.junit.jupiter.params.provider.Arguments;
import org.mockito.InjectMocks;
import org.tdl.vireo.model.packager.DSpaceMetsPackager;
import org.tdl.vireo.model.packager.DSpaceSimplePackager;

public class DepositLocationTest extends AbstractModelTest<DepositLocation> {

    @InjectMocks
    private DepositLocation depositLocation;

    @Override
    protected DepositLocation getInstance() {
        return depositLocation;
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
            Arguments.of("repository", "value"),
            Arguments.of("collection", "value"),
            Arguments.of("username", "value"),
            Arguments.of("password", "value"),
            Arguments.of("onBehalfOf", "value"),
            Arguments.of("packager", new DSpaceMetsPackager()),
            Arguments.of("packager", new DSpaceSimplePackager()),
            Arguments.of("depositorName", "value"),
            Arguments.of("timeout", 123)
        );
    }

}
