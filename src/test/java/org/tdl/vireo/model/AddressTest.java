package org.tdl.vireo.model;

import java.util.stream.Stream;
import org.junit.jupiter.params.provider.Arguments;
import org.mockito.InjectMocks;

public class AddressTest extends AbstractModelTest<Address> {

    @InjectMocks
    private Address address;

    @Override
    protected Address getInstance() {
        return address;
    }

    protected static Stream<Arguments> provideGetterParameters() {
        return getParameterStream();
    }

    protected static Stream<Arguments> provideSetterParameters() {
        return getParameterStream();
    }

    private static Stream<Arguments> getParameterStream() {
        return Stream.of(
            Arguments.of("address1", "value"),
            Arguments.of("address2", "value"),
            Arguments.of("city", "value"),
            Arguments.of("state", "value"),
            Arguments.of("postalCode", "value"),
            Arguments.of("country", "value")
        );
    }

}
