package org.tdl.vireo.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.params.provider.Arguments;
import org.mockito.InjectMocks;

public class ControlledVocabularyCacheTest extends AbstractModelTest<ControlledVocabularyCache> {

    @InjectMocks
    private ControlledVocabularyCache controlledVocabularyCache;

    @Override
    protected ControlledVocabularyCache getInstance() {
        return controlledVocabularyCache;
    }

    protected static Stream<Arguments> provideGetterParameters() {
        return getParameterStream();
    }

    protected static Stream<Arguments> provideSetterParameters() {
        return getParameterStream();
    }

    private static Stream<Arguments> getParameterStream() {
        List<VocabularyWord> words = new ArrayList<>();
        words.add(new VocabularyWord());

        List<VocabularyWord[]> wordArrayList = new ArrayList<>();
        VocabularyWord[] wordsArray = { new VocabularyWord() }; 
        wordArrayList.add(wordsArray);

        return Stream.of(
            Arguments.of("timestamp", 123L),
            Arguments.of("controlledVocabularyName", "name"),
            Arguments.of("newVocabularyWords", words),
            Arguments.of("updatingVocabularyWords", wordArrayList),
            Arguments.of("duplicateVocabularyWords", words),
            Arguments.of("removedVocabularyWords", words)
        );
    }

}
