package org.tdl.vireo.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

@ActiveProfiles(value = { "test", "isolated-test" })
public abstract class AbstractModelCustomMethodTest<M> extends AbstractModelTest<M> {

    /**
     * Perform the test for the given getter using a custom function name.
     *
     * The implementing class must implement the provideGetterMethodParameters() static method, returning Stream<Arguments>.
     *
     * @param method The name of the method on the model.
     * @param property The name of the property on the model.
     * @param value The expected value to get (and set).
     */
    @ParameterizedTest
    @MethodSource("provideGetterMethodParameters")
    public void testGetterMethod(String method, String property, Object value) {
        ReflectionTestUtils.setField(getInstance(), property, value);

        assertEquals(value, ReflectionTestUtils.invokeMethod(getInstance(), method), GETTER_MESSAGE + property + " and method: " + method + ".");
    }

    /**
     * Perform the test for the given setter using a custom function name.
     *
     * The implementing class must implement the provideSetterMethodParameters() static method, returning Stream<Arguments>.
     *
     * @param method The name of the method on the model.
     * @param property The name of the property on the model.
     * @param value The expected value to set (and get).
     */
    @ParameterizedTest
    @MethodSource("provideSetterMethodParameters")
    public void testSetterMethod(String method, String property, Object value) {
        ReflectionTestUtils.invokeMethod(getInstance(), method, value);

        assertEquals(value, ReflectionTestUtils.getField(getInstance(), property), SETTER_MESSAGE + property + " and method: " + method + ".");
    }
}
