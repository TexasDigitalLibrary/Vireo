package org.tdl.vireo.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

@ActiveProfiles(value = { "test", "isolated-test" })
@ExtendWith(MockitoExtension.class)
public abstract class AbstractModelTest<M> {

    public static final String GETTER_MESSAGE = "The getter must return the correct response for property: ";
    public static final String SETTER_MESSAGE = "The setter must assign the correct value for property: ";

    /**
     * Perform the test for the given getter.
     *
     * The implementing class must implement the provideGetterParameters() static method, returning Stream<Arguments>.
     *
     * @param property The name of the property on the model.
     * @param value The expected value to get (and set).
     */
    @ParameterizedTest
    @MethodSource("provideGetterParameters")
    public void testGetter(String property, Object value) {
        ReflectionTestUtils.setField(getInstance(), property, value);

        assertEquals(value, ReflectionTestUtils.invokeGetterMethod(getInstance(), property), GETTER_MESSAGE + property + ".");
    }

    /**
     * Perform the test for the given setter.
     *
     * The implementing class must implement the provideSetterParameters() static method, returning Stream<Arguments>.
     *
     * @param property The name of the property on the model.
     * @param value The expected value to set (and get).
     */
    @ParameterizedTest
    @MethodSource("provideSetterParameters")
    public void testSetter(String property, Object value) {
        ReflectionTestUtils.invokeSetterMethod(getInstance(), property, value);

        assertEquals(value, ReflectionTestUtils.getField(getInstance(), property), SETTER_MESSAGE + property + ".");
    }

    /**
     * Provide the instance of the given model M.
     *
     * @return The instance for the testGetter() and the testSetter() methods to use.
     */
    abstract protected M getInstance();

}
