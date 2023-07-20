package org.tdl.vireo.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class EmbargoGuarantorTest extends AbstractEnumTest<EmbargoGuarantor> {

    @ParameterizedTest
    @MethodSource("provideEnumParameters")
    public void testGetValue(EmbargoGuarantor enumeration, String name, int ordinal) {
        assertEquals(ordinal, enumeration.getValue(), "Value is incorrect.");
    }

    @ParameterizedTest
    @MethodSource("provideEnumParameters")
    public void testToString(EmbargoGuarantor enumeration, String name, int ordinal) {
        assertEquals(name, enumeration.toString(), "Name is incorrect.");
    }

    @ParameterizedTest
    @MethodSource("provideEnumParameters")
    public void testFromString(EmbargoGuarantor enumeration, String name, int ordinal) {
        assertEquals(enumeration, EmbargoGuarantor.fromString(name), "Enumeration is incorrect.");
    }

    @Test
    public void testFromStringReturnsNull() {
        assertNull(EmbargoGuarantor.fromString("This cannot exist."), "Did not return null.");
    }

    protected static Stream<Arguments> provideEnumParameters() {
        return Stream.of(
            Arguments.of(EmbargoGuarantor.DEFAULT, "DEFAULT", 0),
            Arguments.of(EmbargoGuarantor.PROQUEST, "PROQUEST", 1)
        );
    }

}
