package org.tdl.vireo.model;

import java.util.stream.Stream;
import org.junit.jupiter.params.provider.Arguments;
import org.mockito.InjectMocks;

public class ContactInfoTest extends AbstractModelTest<ContactInfo> {

    @InjectMocks
    private ContactInfo contactInfo;

    @Override
    protected ContactInfo getInstance() {
        return contactInfo;
    }

    protected static Stream<Arguments> provideGetterParameters() {
        return getParameterStream();
    }

    protected static Stream<Arguments> provideSetterParameters() {
        return getParameterStream();
    }

    private static Stream<Arguments> getParameterStream() {
        return Stream.of(
            Arguments.of("address", new Address()),
            Arguments.of("phone", "value"),
            Arguments.of("email", "value")
        );
    }

}
