package org.tdl.vireo.model;

import java.util.stream.Stream;
import org.junit.jupiter.params.provider.Arguments;
import org.mockito.InjectMocks;

public class ManagedConfigurationTest extends AbstractModelTest<ManagedConfiguration> {

    @InjectMocks
    private ManagedConfiguration managedConfiguration;

    @Override
    protected ManagedConfiguration getInstance() {
        return managedConfiguration;
    }

    protected static Stream<Arguments> provideGetterParameters() {
        return getParameterStream();
    }

    protected static Stream<Arguments> provideSetterParameters() {
        return getParameterStream();
    }

    private static Stream<Arguments> getParameterStream() {
        return Stream.of(
            Arguments.of("value", "value"),
            Arguments.of("type", "value"),
            Arguments.of("name", "value")
        );
    }

}
