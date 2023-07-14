package org.tdl.vireo.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.springframework.test.util.ReflectionTestUtils;

public class FieldValueTest extends AbstractModelTest<FieldValue> {

    @InjectMocks
    private FieldValue fieldValue;

    @Test
    public void testFieldValueInstantiation() {
        ReflectionTestUtils.setField(fieldValue, "fieldPredicate", new FieldPredicate());
        ReflectionTestUtils.setField(fieldValue, "contacts", new ArrayList<String>());

        FieldValue newFieldValue = new FieldValue(fieldValue.getFieldPredicate(), fieldValue.getContacts());

        assertEquals(newFieldValue.getFieldPredicate(), fieldValue.getFieldPredicate(), "Field Predicate does not match.");
        assertEquals(newFieldValue.getContacts(), fieldValue.getContacts(), "Contacts does not match.");
    }

    @ParameterizedTest
    @MethodSource("provideGetFileParameters")
    public void testGetFileName(String value, String expect, int row) {
        List<VocabularyWord> dictionary = new ArrayList<>();
        VocabularyWord vocabularyWord = new VocabularyWord();

        vocabularyWord.setId(1L);
        dictionary.add(vocabularyWord);

        ReflectionTestUtils.setField(fieldValue, "value", value);

        assertEquals(expect, fieldValue.getFileName(), GETTER_MESSAGE + "value on row " + row + ".");
    }

    @ParameterizedTest
    @MethodSource("provideGetExportFileParameters")
    public void testGetExportFileName(String value, String expect, int row) {
        List<VocabularyWord> dictionary = new ArrayList<>();
        VocabularyWord vocabularyWord = new VocabularyWord();

        vocabularyWord.setId(1L);
        dictionary.add(vocabularyWord);

        ReflectionTestUtils.setField(fieldValue, "value", value);

        assertEquals(expect, fieldValue.getExportFileName(), GETTER_MESSAGE + "value on row " + row + ".");
    }

    @Override
    protected FieldValue getInstance() {
        return fieldValue;
    }

    protected static Stream<Arguments> provideGetterParameters() {
        return getParameterStream();
    }

    protected static Stream<Arguments> provideSetterParameters() {
        return getParameterStream();
    }

    private static Stream<Arguments> getParameterStream() {
        List<String> contactsEmpty = new ArrayList<>();
        List<String> contactsNotEmpty = new ArrayList<>();
        contactsNotEmpty.add("contact");

        return Stream.of(
            Arguments.of("value", "value"),
            Arguments.of("identifier", "value"),
            Arguments.of("definition", "value"),
            Arguments.of("contacts", contactsEmpty),
            Arguments.of("contacts", contactsNotEmpty),
            Arguments.of("fieldPredicate", new FieldPredicate())
        );
    }

    private static Stream<Arguments> provideGetFileParameters() {
        // Warning: This matches the behavior of getFileName(), some of these, such as 'PRIMARYno_slash' which may or may not be incorrect.  
        return Stream.of(
            Arguments.of("no_slash", "no_slash", 0),
            Arguments.of("with-dash", "dash", 1),
            Arguments.of("with_slash/no_dash", "no_dash", 2),
            Arguments.of("with_slash/with-dash", "dash", 3),
            Arguments.of("/many//-/slashes/with_slash/no_dash", "no_dash", 4),
            Arguments.of("/many//-/slashes/with_slash/with-dash", "dash", 5),
            Arguments.of("PRIMARYno_slash", "PRIMARYno_slash", 6),
            Arguments.of("PRIMARYwith-dash", "dash", 7),
            Arguments.of("PRIMARY-no_slash", "no_slash", 8),
            Arguments.of("PRIMARY-with-dash", "with-dash", 9),
            Arguments.of("with_slash/PRIMARYno_slash", "PRIMARYno_slash", 10),
            Arguments.of("with_slash/PRIMARYwith-dash", "dash", 11),
            Arguments.of("with_slash/PRIMARY-no_slash", "no_slash", 12),
            Arguments.of("with_slash/PRIMARY-with-dash", "with-dash", 13),
            Arguments.of("/many//-/slashes/with_slash/PRIMARYno_slash", "PRIMARYno_slash", 14),
            Arguments.of("/many//-/slashes/with_slash/PRIMARYwith-dash", "dash", 15),
            Arguments.of("/many//-/slashes/with_slash/PRIMARY-no_slash", "no_slash", 16),
            Arguments.of("/many//-/slashes/with_slash/PRIMARY-with-dash", "with-dash", 17),
            Arguments.of("archivedPRIMARYno_slash", "archivedPRIMARYno_slash", 18),
            Arguments.of("archivedPRIMARYwith-dash", "dash", 19),
            Arguments.of("archivedPRIMARY-no_slash", "no_slash", 20),
            Arguments.of("archivedPRIMARY-with-dash", "with-dash", 21),
            Arguments.of("archivedwith_slash/PRIMARYno_slash", "PRIMARYno_slash", 22),
            Arguments.of("archivedwith_slash/PRIMARYwith-dash", "dash", 23),
            Arguments.of("archivedwith_slash/PRIMARY-no_slash", "no_slash", 24),
            Arguments.of("archivedwith_slash/PRIMARY-with-dash", "with-dash", 25),
            Arguments.of("/archivedmany//-/slashes/with_slash/PRIMARYno_slash", "PRIMARYno_slash", 26),
            Arguments.of("/archivedmany//-/slashes/with_slash/PRIMARYwith-dash", "dash", 27),
            Arguments.of("/archivedmany//-/slashes/with_slash/PRIMARY-no_slash", "no_slash", 28),
            Arguments.of("/archivedmany//-/slashes/with_slash/PRIMARY-with-dash", "with-dash", 29),
            Arguments.of("no_slash.txt", "no_slash.txt", 30),
            Arguments.of("with-dash.txt", "dash.txt", 31),
            Arguments.of("with_slash/no_dash.txt", "no_dash.txt", 32),
            Arguments.of("with_slash/with-dash.txt", "dash.txt", 33),
            Arguments.of("/many//-/slashes/with_slash/no_dash.txt", "no_dash.txt", 34),
            Arguments.of("/many//-/slashes/with_slash/with-dash.txt", "dash.txt", 35)
        );
    }

    private static Stream<Arguments> provideGetExportFileParameters() {
        // Warning: This matches the behavior of getFileName(), some of these, such as 'PRIMARYno_slash' which may or may not be incorrect.  
        return Stream.of(
            Arguments.of("no_slash", "no_slash", 0),
            Arguments.of("with-dash", "with-dash", 1),
            Arguments.of("with_slash/no_dash", "no_dash", 2),
            Arguments.of("with_slash/with-dash", "with-dash", 3),
            Arguments.of("/many//-/slashes/with_slash/no_dash", "no_dash", 4),
            Arguments.of("/many//-/slashes/with_slash/with-dash", "with-dash", 5),
            Arguments.of("PRIMARYno_slash", "PRIMARYno_slash", 6),
            Arguments.of("PRIMARYwith-dash", "dash", 7),
            Arguments.of("PRIMARY-no_slash", "no_slash", 8),
            Arguments.of("PRIMARY-with-dash", "with-dash", 9),
            Arguments.of("with_slash/PRIMARYno_slash", "PRIMARYno_slash", 10),
            Arguments.of("with_slash/PRIMARYwith-dash", "dash", 11),
            Arguments.of("with_slash/PRIMARY-no_slash", "no_slash", 12),
            Arguments.of("with_slash/PRIMARY-with-dash", "with-dash", 13),
            Arguments.of("/many//-/slashes/with_slash/PRIMARYno_slash", "PRIMARYno_slash", 14),
            Arguments.of("/many//-/slashes/with_slash/PRIMARYwith-dash", "dash", 15),
            Arguments.of("/many//-/slashes/with_slash/PRIMARY-no_slash", "no_slash", 16),
            Arguments.of("/many//-/slashes/with_slash/PRIMARY-with-dash", "with-dash", 17),
            Arguments.of("archivedPRIMARYno_slash", "archivedPRIMARYno_slash", 18),
            Arguments.of("archivedPRIMARYwith-dash", "archivedPRIMARYwith-dash", 19),
            Arguments.of("archivedPRIMARY-no_slash", "archivedPRIMARY-no_slash", 20),
            Arguments.of("archivedPRIMARY-with-dash", "archivedPRIMARY-with-dash", 21),
            Arguments.of("archivedwith_slash/PRIMARYno_slash", "PRIMARYno_slash", 22),
            Arguments.of("archivedwith_slash/PRIMARYwith-dash", "dash", 23),
            Arguments.of("archivedwith_slash/PRIMARY-no_slash", "no_slash", 24),
            Arguments.of("archivedwith_slash/PRIMARY-with-dash", "with-dash", 25),
            Arguments.of("/archivedmany//-/slashes/with_slash/PRIMARYno_slash", "PRIMARYno_slash", 26),
            Arguments.of("/archivedmany//-/slashes/with_slash/PRIMARYwith-dash", "dash", 27),
            Arguments.of("/archivedmany//-/slashes/with_slash/PRIMARY-no_slash", "no_slash", 28),
            Arguments.of("/archivedmany//-/slashes/with_slash/PRIMARY-with-dash", "with-dash", 29),
            Arguments.of("no_slash.txt", "no_slash.txt", 30),
            Arguments.of("with-dash.txt", "dash.txt", 31),
            Arguments.of("with_slash/no_dash.txt", "no_dash.txt", 32),
            Arguments.of("with_slash/with-dash.txt", "dash.txt", 33),
            Arguments.of("/many//-/slashes/with_slash/no_dash.txt", "no_dash.txt", 34),
            Arguments.of("/many//-/slashes/with_slash/with-dash.txt", "dash.txt", 35)
        );
    }

}
