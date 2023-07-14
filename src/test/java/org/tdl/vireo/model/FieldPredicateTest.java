package org.tdl.vireo.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.springframework.test.util.ReflectionTestUtils;

public class FieldPredicateTest extends AbstractModelCustomMethodTest<FieldPredicate> {

    @InjectMocks
    private FieldPredicate fieldPredicate;

    @ParameterizedTest
    @MethodSource("provideSchemaTest")
    public void testGetSchema(String value, String expect) {
        ReflectionTestUtils.setField(getInstance(), "value", value);

        assertEquals(expect, fieldPredicate.getSchema(), GETTER_MESSAGE);
    }

    @ParameterizedTest
    @MethodSource("provideElementTest")
    public void testGetElement(String value, String expect) {
        ReflectionTestUtils.setField(getInstance(), "value", value);

        assertEquals(expect, fieldPredicate.getElement(), GETTER_MESSAGE);
    }

    @ParameterizedTest
    @MethodSource("provideQualifierTest")
    public void testGetQualifier(String value, String expect) {
        ReflectionTestUtils.setField(getInstance(), "value", value);

        assertEquals(expect, fieldPredicate.getQualifier(), GETTER_MESSAGE);
    }

    @Override
    protected FieldPredicate getInstance() {
        return fieldPredicate;
    }

    protected static Stream<Arguments> provideGetterParameters() {
        return getParameterStream();
    }

    protected static Stream<Arguments> provideSetterParameters() {
        return getParameterStream();
    }

    protected static Stream<Arguments> provideGetterMethodParameters() {
        return Stream.of(
            Arguments.of("getDocumentTypePredicate", "documentTypePredicate", true),
            Arguments.of("getDocumentTypePredicate", "documentTypePredicate", false)
        );
    }

    protected static Stream<Arguments> provideSetterMethodParameters() {
        return Stream.of(
            Arguments.of("setDocumentTypePredicate", "documentTypePredicate", true),
            Arguments.of("setDocumentTypePredicate", "documentTypePredicate", false)
        );
    }

    private static Stream<Arguments> getParameterStream() {
        return Stream.of(
            Arguments.of("value", "value")
        );
    }

    private static Stream<Arguments> provideSchemaTest() {
        return Stream.of(
            Arguments.of("has.all.three", "has"),
            Arguments.of("has.two", "has"),
            Arguments.of("one", "one"),
            Arguments.of("", "")
        );
    }

    private static Stream<Arguments> provideElementTest() {
        return Stream.of(
            Arguments.of("has.all.three", "all"),
            Arguments.of("has.two", "two"),
            Arguments.of("one", null),
            Arguments.of("", null)
        );
    }

    private static Stream<Arguments> provideQualifierTest() {
        return Stream.of(
            Arguments.of("has.all.three", "three"),
            Arguments.of("has.two", null),
            Arguments.of("one", null),
            Arguments.of("", null)
        );
    }

}
