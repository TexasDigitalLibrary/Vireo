package org.tdl.vireo.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.Arguments;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

public class VocabularyWordTest extends AbstractModelTest<VocabularyWord> {

    @InjectMocks
    private VocabularyWord vocabularyWord;

    @Test
    public void testVocabularyWordInstantiation1() {
        List<String> contacts = new ArrayList<>();
        contacts.add("contact");

        vocabularyWord.setName("name");
        vocabularyWord.setDefinition("definition");
        vocabularyWord.setIdentifier("identifier");
        vocabularyWord.setContacts(contacts);

        VocabularyWord newVocabularyWord = new VocabularyWord(vocabularyWord.getName(), vocabularyWord.getDefinition(), vocabularyWord.getIdentifier(), vocabularyWord.getContacts());

        assertEquals(newVocabularyWord.getName(), vocabularyWord.getName(), "Name does not match.");
        assertEquals(newVocabularyWord.getDefinition(), vocabularyWord.getDefinition(), "Definition does not match.");
        assertEquals(newVocabularyWord.getIdentifier(), vocabularyWord.getIdentifier(), "Identifier does not match.");
        assertEquals(newVocabularyWord.getContacts(), vocabularyWord.getContacts(), "Contacts does not match.");
    }

    @Test
    public void testVocabularyWordInstantiation2() {
        ControlledVocabulary controlledVocabulary = new ControlledVocabulary();
        List<String> contacts = new ArrayList<>();

        controlledVocabulary.setId(1L);
        contacts.add("contact");

        vocabularyWord.setControlledVocabulary(controlledVocabulary);
        vocabularyWord.setName("name");
        vocabularyWord.setDefinition("definition");
        vocabularyWord.setIdentifier("identifier");
        vocabularyWord.setContacts(contacts);

        VocabularyWord newVocabularyWord = new VocabularyWord(controlledVocabulary, vocabularyWord.getName(), vocabularyWord.getDefinition(), vocabularyWord.getIdentifier(), vocabularyWord.getContacts());

        assertEquals(newVocabularyWord.getControlledVocabulary(), vocabularyWord.getControlledVocabulary(), "Controlled Vocabulary does not match.");
        assertEquals(newVocabularyWord.getName(), vocabularyWord.getName(), "Name does not match.");
        assertEquals(newVocabularyWord.getDefinition(), vocabularyWord.getDefinition(), "Definition does not match.");
        assertEquals(newVocabularyWord.getIdentifier(), vocabularyWord.getIdentifier(), "Identifier does not match.");
        assertEquals(newVocabularyWord.getContacts(), vocabularyWord.getContacts(), "Contacts does not match.");
    }

    @Test
    public void testSetContactsPassingNull() {
        List<String> contacts = Mockito.spy(new ArrayList<>());

        ReflectionTestUtils.setField(vocabularyWord, "contacts", contacts);

        vocabularyWord.setContacts(null);

        Mockito.verifyNoInteractions(contacts);
    }

    @Override
    protected VocabularyWord getInstance() {
        return vocabularyWord;
    }

    protected static Stream<Arguments> provideGetterParameters() {
        return getParameterStream();
    }

    protected static Stream<Arguments> provideSetterParameters() {
        return getParameterStream();
    }

    private static Stream<Arguments> getParameterStream() {
        List<String> contacts = new ArrayList<>();
        contacts.add("contact");

        return Stream.of(
            Arguments.of("name", "value"),
            Arguments.of("definition", "value"),
            Arguments.of("identifier", "value"),
            Arguments.of("contacts", contacts),
            Arguments.of("controlledVocabulary", new ControlledVocabulary())
        );
    }

}
