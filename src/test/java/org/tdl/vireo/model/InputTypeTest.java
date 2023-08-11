package org.tdl.vireo.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.Arguments;
import org.mockito.InjectMocks;
import org.springframework.test.util.ReflectionTestUtils;

public class InputTypeTest extends AbstractModelTest<InputType> {

    @InjectMocks
    private InputType inputType;

    @Test
    public void testGetValidationMessage() {
        Map<String, Validation> validations = new HashMap<>();
        Validation validation1 = new Validation();
        Validation validation2 = new Validation();
        String name = "name";

        validation1.setPattern("Some Pattern.");
        validation1.setMessage("something");

        validation2.setPattern("Another Pattern.");
        validation2.setMessage("another");

        validations.put("other", validation1);
        validations.put(name, validation2);

        ReflectionTestUtils.invokeSetterMethod(getInstance(), "validation", validations);

        assertEquals(validation2.getMessage(), inputType.getValidationMessage(name), GETTER_MESSAGE + "validation.");
    }

    @Test
    public void testGetValidationMessageReturnsNull() {
        Map<String, Validation> validations = new HashMap<>();
        Validation validation1 = new Validation();
        Validation validation2 = new Validation();
        String name = "name";

        validation1.setPattern("Some Pattern.");
        validation1.setMessage("something");

        validation2.setPattern("Another Pattern.");
        validation2.setMessage("another");

        validations.put("other", validation1);
        validations.put(name, validation2);

        ReflectionTestUtils.invokeSetterMethod(getInstance(), "validation", validations);

        assertNull(inputType.getValidationMessage("Not Found"), "Null must be returned for unknown map name.");
    }

    @Test
    public void testGetValidationPattern() {
        Map<String, Validation> validations = new HashMap<>();
        Validation validation1 = new Validation();
        Validation validation2 = new Validation();
        String name = "name";

        validation1.setPattern("Some Pattern.");
        validation1.setMessage("something");

        validation2.setPattern("Another Pattern.");
        validation2.setMessage("another");

        validations.put("other", validation1);
        validations.put(name, validation2);

        ReflectionTestUtils.invokeSetterMethod(getInstance(), "validation", validations);

        assertEquals(validation2.getPattern(), inputType.getValidationPattern(name), GETTER_MESSAGE + "validation.");
    }

    /**
     * The setValidationMessage() does not work via setParameterStream() and is manually tested here.
     */
    @Test
    public void testSetValidationMessage1() {
        String message = "message";

        inputType.setValidationMessage(message);

        assertEquals(message, ReflectionTestUtils.getField(getInstance(), "validationMessage"), SETTER_MESSAGE + "validationMessage.");
    }

    @Test
    public void testSetValidationMessage2() {
        Map<String, Validation> validations = new HashMap<>();
        Validation validation1 = new Validation();
        Validation validation2 = new Validation();
        String message = "message";
        String name = "name";

        validation1.setPattern("Some Pattern.");
        validation1.setMessage("something");

        validation2.setPattern("Another Pattern.");
        validation2.setMessage("another");

        validations.put("other", validation1);
        validations.put(name, validation2);

        ReflectionTestUtils.invokeSetterMethod(getInstance(), "validation", validations);

        inputType.setValidationMessage(message, name);

        assertEquals(message, validation2.getMessage(), SETTER_MESSAGE + "validation.");
    }

    /**
     * The setValidationPattern() does not work via setParameterStream() and is manually tested here.
     */
    @Test
    public void testSetValidationPattern1() {
        String pattern = "pattern";

        inputType.setValidationPattern(pattern);

        assertEquals(pattern, ReflectionTestUtils.getField(getInstance(), "validationPattern"), SETTER_MESSAGE + "validationPattern.");
    }

    @Test
    public void testSetValidationPattern2() {
        Map<String, Validation> validations = new HashMap<>();
        Validation validation1 = new Validation();
        Validation validation2 = new Validation();
        String pattern = "pattern";
        String name = "name";

        validation1.setMessage("Some Message.");
        validation1.setPattern("something");

        validation2.setMessage("Another Message.");
        validation2.setPattern("another");

        validations.put("other", validation1);
        validations.put(name, validation2);

        ReflectionTestUtils.invokeSetterMethod(getInstance(), "validation", validations);

        inputType.setValidationPattern(pattern, name);

        assertEquals(pattern, validation2.getPattern(), SETTER_MESSAGE + "validation.");
    }

    @Override
    protected InputType getInstance() {
        return inputType;
    }

    protected static Stream<Arguments> provideGetterParameters() {
        Map<String, Validation> validationMap = new HashMap<>();
        Validation validation = new Validation();

        validation.setMessage("message");
        validation.setMessage("pattern");
        validationMap.put("key", validation);

        return Stream.of(
            Arguments.of("name", "value"),
            Arguments.of("validationPattern", "pattern"),
            Arguments.of("validationMessage", "message"),
            Arguments.of("validation", validationMap)
        );
    }

    protected static Stream<Arguments> provideSetterParameters() {
        Map<String, Validation> validationMap = new HashMap<>();
        Validation validation = new Validation();

        validation.setMessage("message");
        validation.setMessage("pattern");
        validationMap.put("key", validation);

        return Stream.of(
            Arguments.of("name", "value"),
            Arguments.of("validation", validationMap)
        );
    }

}
