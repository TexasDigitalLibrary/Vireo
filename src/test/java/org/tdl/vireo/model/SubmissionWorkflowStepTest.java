package org.tdl.vireo.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import edu.tamu.weaver.data.model.WeaverEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.Arguments;
import org.mockito.InjectMocks;
import org.springframework.test.util.ReflectionTestUtils;
import org.tdl.vireo.model.inheritance.HeritableComponent;

public class SubmissionWorkflowStepTest extends AbstractModelCustomMethodTest<SubmissionWorkflowStep> {

    @InjectMocks
    private SubmissionWorkflowStep submissionWorkflowStep;

    @Test
    public void testReorderAggregateFieldProfile() {
        List<FieldProfile> fieldProfiles = new ArrayList<>();
        FieldProfile fieldProfile1 = new FieldProfile();
        FieldProfile fieldProfile2 = new FieldProfile();
        FieldPredicate fieldPredicate1 = new FieldPredicate();
        FieldPredicate fieldPredicate2 = new FieldPredicate();

        fieldProfile1.setId(1L);
        fieldProfile2.setId(2L);

        fieldPredicate1.setId(1L);
        fieldPredicate2.setId(2L);

        fieldPredicate1.setValue("value1");
        fieldPredicate2.setValue("value2");

        fieldProfile1.setFieldPredicate(fieldPredicate1);
        fieldProfile2.setFieldPredicate(fieldPredicate2);

        fieldProfiles.add(fieldProfile1);
        fieldProfiles.add(fieldProfile2);

        ReflectionTestUtils.setField(getInstance(), "aggregateFieldProfiles", fieldProfiles);

        // Warning: The re-order parameters use index + 1 location logic such that 1 represents index 0 and 2 represents index 1.
        submissionWorkflowStep.reorderAggregateFieldProfile(1, 2);

        assertEquals(fieldProfiles.get(0), fieldProfile2, "Did not correctly re-order Aggregate Field Profile.");
        assertEquals(fieldProfiles.get(1), fieldProfile1, "Did not correctly re-order Aggregate Field Profile.");
    }

    @Test
    public void testReorderAggregateNote() {
        List<Note> notes = new ArrayList<>();
        Note note1 = new Note();
        Note note2 = new Note();

        note1.setId(1L);
        note2.setId(2L);

        notes.add(note1);
        notes.add(note2);

        ReflectionTestUtils.setField(getInstance(), "aggregateNotes", notes);

        // Warning: The re-order parameters use index + 1 location logic such that 1 represents index 0 and 2 represents index 1.
        submissionWorkflowStep.reorderAggregateNote(1, 2);

        assertEquals(notes.get(0), note2, "Did not correctly re-order Aggregate Note.");
        assertEquals(notes.get(1), note1, "Did not correctly re-order Aggregate Note.");
    }

    @Override
    protected SubmissionWorkflowStep getInstance() {
        return submissionWorkflowStep;
    }

    protected static Stream<Arguments> provideGetterParameters() {
        return getParameterStream();
    }

    protected static Stream<Arguments> provideSetterParameters() {
        return getParameterStream();
    }

    protected static Stream<Arguments> provideGetterMethodParameters() {
        return Stream.of(
            Arguments.of("getOverrideable", "overrideable", true),
            Arguments.of("getOverrideable", "overrideable", false)
        );
    }

    protected static Stream<Arguments> provideSetterMethodParameters() {
        return Stream.of(
            Arguments.of("setOverrideable", "overrideable", true),
            Arguments.of("setOverrideable", "overrideable", false)
        );
    }

    private static Stream<Arguments> getParameterStream() {
        List<FieldProfile> fieldProfiles = new ArrayList<>();
        fieldProfiles.add(new FieldProfile());

        List<Note> notes = new ArrayList<>();
        notes.add(new Note());

        return Stream.of(
            Arguments.of("name", "value"),
            Arguments.of("aggregateFieldProfiles", fieldProfiles),
            Arguments.of("aggregateNotes", notes),
            Arguments.of("instructions", "instruction")
        );
    }

    protected class OtherComponent implements HeritableComponent<Object> {
        private Long id = 0L;

        @Override
        public int compareTo(WeaverEntity arg0) {
            return 0;
        }

        @Override
        public Long getId() {
            return id;
        }

        @Override
        public void setId(Long arg0) {
            this.id = arg0;
        }

        @Override
        public void setOriginating(Object originatingHeritableModel) {
        }

        @Override
        public Object getOriginating() {
            return null;
        }

        @Override
        public void setOriginatingWorkflowStep(WorkflowStep originatingWorkflowStep) {
        }

        @Override
        public WorkflowStep getOriginatingWorkflowStep() {
            return null;
        }

        @Override
        public Boolean getOverrideable() {
            return null;
        }

        @Override
        public Object clone() {
            return new Object();
        }
    }
}
