package org.tdl.vireo.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public abstract class AbstractEnumTest<E extends Enum<?>> {

    public static final String NAME_MESSAGE = "The enumeration name is different than expected.";
    public static final String ORDINAL_MESSAGE = "The enumeration ordinal is different than expected.";

    /**
     * Perform the test for the given enum.
     *
     * The implementing class must implement the provideEnumParameters() static method, returning Stream<Arguments>.
     *
     * @param enumeration The enumeration to test.
     * @param name The expected enumeration name value.
     * @param ordinal The ordinal of the enumeration.
     */
    @ParameterizedTest
    @MethodSource("provideEnumParameters")
    public void testEnum(Enum<?> enumeration, String name, int ordinal) {
        assertEquals(enumeration.name(), name, NAME_MESSAGE);
        assertEquals(enumeration.ordinal(), ordinal, ORDINAL_MESSAGE);
    }

}
