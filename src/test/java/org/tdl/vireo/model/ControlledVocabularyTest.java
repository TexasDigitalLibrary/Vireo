package org.tdl.vireo.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.Arguments;
import org.mockito.InjectMocks;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.tdl.vireo.Application;
import org.tdl.vireo.service.EntityControlledVocabularyService;

@SpringBootTest(classes = { Application.class }, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ControlledVocabularyTest extends AbstractModelCustomMethodTest<ControlledVocabulary> {

    @InjectMocks
    private ControlledVocabulary controlledVocabulary;

    @MockBean
    EntityControlledVocabularyService entityControlledVocabularyService;

    @Test
    public void testGetDictionary() {
        List<VocabularyWord> dictionary = new ArrayList<>();
        VocabularyWord vocabularyWord = new VocabularyWord();

        vocabularyWord.setId(1L);
        dictionary.add(vocabularyWord);

        ReflectionTestUtils.setField(controlledVocabulary, "dictionary", dictionary);
        ReflectionTestUtils.setField(controlledVocabulary, "isEntityProperty", false);

        List<VocabularyWord> got = controlledVocabulary.getDictionary();

        assertEquals(1, got.size(), GETTER_MESSAGE);
    }

    @Test
    public void testGetDictionaryThrowsClassNotFoundException() throws ClassNotFoundException {
        List<VocabularyWord> dictionary = new ArrayList<>();
        VocabularyWord vocabularyWord = new VocabularyWord();

        vocabularyWord.setId(1L);
        dictionary.add(vocabularyWord);

        ReflectionTestUtils.setField(controlledVocabulary, "dictionary", dictionary);
        ReflectionTestUtils.setField(controlledVocabulary, "isEntityProperty", true);
        ReflectionTestUtils.setField(controlledVocabulary, "name", "Should not be found.");

        when(entityControlledVocabularyService.getControlledVocabularyWords(anyString())).thenThrow(ClassNotFoundException.class);

        List<VocabularyWord> got = controlledVocabulary.getDictionary();

        assertEquals(0, got.size(), "The Vocabulary Word array should be empty when ClassNotFoundException is thrown.");
    }

    @Test
    public void testSetDictionary() {
        List<VocabularyWord> dictionary = new ArrayList<>();
        VocabularyWord vocabularyWord = new VocabularyWord();

        vocabularyWord.setId(1L);
        dictionary.add(vocabularyWord);

        ReflectionTestUtils.setField(controlledVocabulary, "isEntityProperty", false);

        controlledVocabulary.setDictionary(dictionary);

        assertEquals(dictionary, ReflectionTestUtils.getField(controlledVocabulary, "dictionary"), SETTER_MESSAGE + "dictionary.");
    }

    @Override
    protected ControlledVocabulary getInstance() {
        return controlledVocabulary;
    }

    protected static Stream<Arguments> provideGetterParameters() {
        return getParameterStream();
    }

    protected static Stream<Arguments> provideSetterParameters() {
        return getParameterStream();
    }

    protected static Stream<Arguments> provideGetterMethodParameters() {
        return Stream.of(
            Arguments.of("getIsEntityProperty", "isEntityProperty", true),
            Arguments.of("getIsEntityProperty", "isEntityProperty", false)
        );
    }

    protected static Stream<Arguments> provideSetterMethodParameters() {
        return Stream.of(
            Arguments.of("setIsEntityProperty", "isEntityProperty", true),
            Arguments.of("setIsEntityProperty", "isEntityProperty", false)
        );
    }

    private static Stream<Arguments> getParameterStream() {
        return Stream.of(
            Arguments.of("name", "value")
        );
    }

}
